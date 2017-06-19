package webcollector.test;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import webcollector.htmlUtils.HTMLTextProcessUtils;
import webcollector.htmlUtils.SaveFileUtils;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

public class HtmlCrawlerTest extends BreadthCrawler {
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);

	public HtmlCrawlerTest(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);

		// 处理网页证书问题
		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return urlHostName.equals(session.getPeerHost());
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	/*
	 * 可以往next中添加希望后续爬取的任务，任务可以是URL或者CrawlDatum
	 * 爬虫不会重复爬取任务，从2.20版之后，爬虫根据CrawlDatum的key去重，而不是URL
	 * 因此如果希望重复爬取某个URL，只要将CrawlDatum的key设置为一个历史中不存在的值即可 例如增量爬取，可以使用
	 * 爬取时间+URL作为key。
	 * 
	 * 新版本中，可以直接通过 page.select(css选择器)方法来抽取网页中的信息，等价于
	 * page.getDoc().select(css选择器)方法，page.getDoc()获取到的是Jsoup中的
	 * Document对象，细节请参考Jsoup教程
	 */
	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.response() == null) {
			return;
		} else if (page.response().contentType().contains("html")) {
			
			String url = page.getUrl();
			logger.info("开始处理目标HTML,目标URL:"+url);
			Map<String, Object> htmlComponentMap = new HashMap<String, Object>();
			
			Document doc = page.doc();
			Charset charset = doc.charset();
			
			logger.info("获取目标HTML部分...");
			//标题
			Element titleElement = doc.getElementById("title");
			
			//正文内容简介
			ArrayList<Element> summaryElements= doc.getElementsByClass("summary");
			
			//正文部分
			Element contentElement = doc.getElementById("content");
		
			
			logger.info("开始下载图片及处理图片路径...");
			//正文部分图片资源
			ArrayList<Element> imgElements = contentElement.getElementsByTag("img");
			Map<String, String> imgMap = new HashMap<String, String>();
			
			//下载Img
			for (Element element : imgElements) {
				//<img alt="daria-nepriakhina-198549.jpg" src="http://www.binzz.com/uploads/allimg/170326/1A102K37-4.jpg" title="8716248178507445.jpg" />
				imgMap.put(element.attr("alt"), element.attr("src"));
			}
			Map<String, String> processedHTMLImage = HTMLTextProcessUtils.ProcessHTMLImage(imgMap);
			
			//修改页面Img src 改为本地资源地址
			for (Element element : imgElements) {
				String localImgPath = processedHTMLImage.get(element.attr("alt"));
				logger.info("替换本地图片路径:"+localImgPath);
				element.attr("src",localImgPath);
			}
				
			
			//CSS资源
			logger.info("开始下载CSS资源并处理路径...");
			ArrayList<Element> cssLInkElements = doc.getElementsByTag("link");
			Map<String, String> cssMap = new HashMap<String, String>();
			
			//下载CSS 并修改为本地CSS资源
			try {
				HTMLTextProcessUtils.ProcessHTMLCSS(cssLInkElements,url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			htmlComponentMap.put("css", cssLInkElements);
			htmlComponentMap.put("summary", summaryElements.get(0));
			htmlComponentMap.put("title", titleElement);
			htmlComponentMap.put("content", contentElement);
			
			
			//重新打包HTML文件,并处理字符编码问题
			logger.info("正在重新打包HTML...");
			byte[] packagingHTML = HTMLTextProcessUtils.packagingHTML(htmlComponentMap,charset);
			
			
			//构建层级目录
			logger.info("构建保存层级目录...");
			//开始处理目标HTML,目标URL:http://www.binzz.com/wenzhang/60224.html
			String targetName = url.substring(url.lastIndexOf("/")+1,url.length());
			url = url.substring(url.lastIndexOf("://"),url.lastIndexOf("/"));
			String[] split = url.split("/");
			String targetPath = "";
			for (String pathPart : split) {
				if(pathPart!=null&&!pathPart.equals("")&&!pathPart.contains(":")){
					targetPath+=pathPart+"\\";
				}
			}

			logger.info("targetPath:"+targetPath);
			logger.info("targetName:"+targetName);
			//保存html文件
			SaveFileUtils.saveGeneralFile(packagingHTML, "C:\\Users\\Mick Mo\\Desktop\\temp\\"+targetPath+targetName);
			logger.info("处理完成");
		}

	}

}