package webcollector.Crawler;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import webcollector.Entity.HTMLPhasingModel;
import webcollector.GeneralUtils.IsNullUtils;
import webcollector.HTMLUtils.HTMLModelDetector;
import webcollector.HTMLUtils.HTMLTextProcessUtils;
import webcollector.HTMLUtils.ReadElementByModel;
import webcollector.IOUtils.FileUtils;
import webcollector.test.CrarlerTest;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

public class HtmlCrawler extends BreadthCrawler {
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);
	private static List<HTMLPhasingModel> htmlPhasingModels;//网络模型
	private static String[] keyWords;//关键字
	private static Vector<String> URLs;//目标URL列表
	private static String basePath;
	
	
	

	public HtmlCrawler(String crawlPath, boolean autoParse) {
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
	
	public static void setHtmlPhasingModels(List<HTMLPhasingModel> htmlPhasingModels) {
		HtmlCrawler.htmlPhasingModels = htmlPhasingModels;
	}
	
	public static void setKeyWords(String[] keyWords) {
		HtmlCrawler.keyWords = keyWords;
	}

	public static void setURLs(Vector<String> uRLs) {
		URLs = uRLs;
	}

	public static void setBasePath(String basePath) {
		HtmlCrawler.basePath = basePath;
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
			
			Charset charset = Charset.forName(page.charset());
			logger.info("当前网页编码:"+charset.displayName());
			
			//匹配模型
			HTMLPhasingModel detectHTMLModel;
			try {
				detectHTMLModel = HTMLModelDetector.detectHTMLModel(url, htmlPhasingModels, doc, charset);
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			if (IsNullUtils.isNull(detectHTMLModel)) {
				logger.info("没有用于解析当前URL的网页模型");
				//屏蔽当前URL
//				URLs.remove(url);
//				return;
			}
		
			//从模型中读取基本信息
			URL modelURL;
			try {
				modelURL = new URL(detectHTMLModel.getUrl());
				logger.info("URL in Model:"+modelURL);
			} catch (MalformedURLException e1) {
				logger.info("无法解析模型内的URL.");
				e1.printStackTrace();
				return;
			}
			//获取URL中的Domain和FileName
			String host = modelURL.getHost();
			logger.info("Host in Model:"+host);
//			String path = modelURL.getPath();
//			path.replace("/", "");
			String domain = host.substring(host.indexOf(".")+1, host.lastIndexOf("."));
			logger.info("Domain:"+domain);
			
			Map<String, Object> readElementByModel = ReadElementByModel.readElementByModel(detectHTMLModel, doc, keyWords);
			
			logger.info("获取目标HTML部分...");
			Object tempElement;//用于储存临时元素
			
			
			//正文部分
			logger.info("开始处理正文...");
			tempElement = readElementByModel.get("content");
			if (IsNullUtils.notNull(tempElement)) {
				OuterFor:
					if(tempElement instanceof Element){
						//Element的情况
						htmlComponentMap.put("content", (Element)tempElement);
						logger.info("找到单个正文元素,已添加至HTML容器.");
						logger.debug("正文元素长度:"+((Element) tempElement).html().length());
					}else{
						//Elements的情况
						if(IsNullUtils.notNull(keyWords)){
							logger.info("找到多个正文元素,进行关键字匹配...");
							for (Element element : ((Elements)tempElement)) {
								for (String keyWord : keyWords) {
									if(element.text().contains(keyWord)){
										htmlComponentMap.put("content", element);
										logger.info("已发现含有关键字的正文元素,并添加至HTML容器.");
										logger.debug("正文元素长度:"+((Element) tempElement).html().length());
										break OuterFor;
									}
								}
							}
							logger.info("未发现含有关键字的正文元素,,取消对当前URL的所有操作");
							return;
						}else{
							htmlComponentMap.put("content", ((Elements)tempElement).get(0));
							logger.info("找到多个正文元素,已将第一个添加至HTML容器.");
							logger.info("正文元素长度:"+(((Elements)tempElement).get(0).html()).length());
						}
					}
			}else{
				logger.error("没有发现正文,取消对当前URL的所有操作");
				return;
			}
			
			

			
			
			//正文部分图片资源
			ArrayList<Element> imgElements;
			logger.info("开始下载图片及处理图片路径...");
//			tempElement = htmlComponentMap.get("content");
			if(IsNullUtils.notNull(tempElement)){
				if(tempElement instanceof Element){
					//Element的情况
					logger.info("从单个正文元素中寻找图片呢元素...");
					imgElements = ((Element)tempElement).getElementsByTag("img");
					
				}else{
					//Elements的情况
					imgElements = new ArrayList<Element>();
					logger.info("从"+((Elements)tempElement).size()+"个正文元素中中寻找图片呢元素...");
					for (Element element : ((Elements)tempElement)) {
						Elements elementsByTag = element.getElementsByTag("img");
						for (Element localTempEmlement : elementsByTag) {
							imgElements.add(localTempEmlement);
						}
					}
				}

				if(imgElements.size()>0){
					logger.info("找到"+imgElements.size()+"个图片元素.");
					Map<URL, URL> imgMap = new HashMap<URL, URL>();
					//下载Img
					for (Element element : imgElements) {
						//处理图片路径和文件名
						try {
							URL imgURL = new URL(element.attr("src"));
							imgMap.put(imgURL, null);
						} catch (MalformedURLException e) {
							logger.error("无法处理图片:"+element.attr("src")+",跳过该图片");
							e.printStackTrace();
							continue;
						}
						
					}
					
					logger.info("已根据模型处理"+imgMap.size()+"个图片元素.");
					HTMLTextProcessUtils.ProcessHTMLImage(imgMap,basePath+"/"+domain+"/");
					//修改页面Img src 改为本地资源地址
					Set<Entry<URL, URL>> entrySet = imgMap.entrySet();
					for (Element element : imgElements) {
						String imgName = element.attr("src");
						for (Entry<URL, URL> entry : entrySet) {
							if(imgName.equals(entry.getKey().toString())){
								element.attr("src",entry.getValue().toString());
							}
						}
					}
				}else{
					
					logger.info("没有找到图片元素.跳过");
				}
				
				
			}else{
				logger.warn("无法下载正文图片,跳过");
			}
			

			
			
			
			//style
			logger.info("开始处理style元素");
			ArrayList<Element> styleElements = doc.getElementsByTag("style");
			if(IsNullUtils.notNull(styleElements)){
				logger.info("已处理"+styleElements.size()+"个style元素");
				htmlComponentMap.put("style", styleElements);
			}else{
				logger.warn("没有发现style元素,跳过");
			}

			
			
			
			//CSS资源
			logger.info("开始下载CSS资源并处理路径...");
			ArrayList<Element> cssLInkElements = doc.getElementsByTag("link");
			if(IsNullUtils.notNull(cssLInkElements)){
				logger.warn("发现"+cssLInkElements.size()+"个CSS元素,开始下载...");
				//下载CSS 并修改为本地CSS资源
				try {
					HTMLTextProcessUtils.ProcessHTMLCSS(cssLInkElements,url,basePath+"/"+domain+"/");
				} catch (IOException e) {
					e.printStackTrace();
				}
				htmlComponentMap.put("css", cssLInkElements);
			}else{
				logger.warn("没有发现CSS元素,跳过");
			}

			
			
			
			
			//标题
			logger.info("开始处理标题...");
			tempElement = readElementByModel.get("title");
			if(IsNullUtils.notNull(tempElement)){
				OuterFor:
				if(tempElement instanceof Element){
					//Element的情况
					htmlComponentMap.put("summary", (Element)tempElement);
				}else{
					//Elements的情况
					if(IsNullUtils.notNull(keyWords)){
						for (Element element : ((Elements)tempElement)) {
							for (String keyWord : keyWords) {
								if(element.text().contains(keyWord)){
									htmlComponentMap.put("summary", element);
									break OuterFor;
								}
							}
						}
					}
					htmlComponentMap.put("summary", ((Elements)tempElement).get(0));
				}
			}else{
				logger.info("没有发现题标题,跳过");
			}
			
			
			//正文内容简介
			logger.info("开始处理正文简介...");
			tempElement = readElementByModel.get("summary");
			if(IsNullUtils.notNull(tempElement)){
				OuterFor:
				if(tempElement instanceof Element){
					//Element的情况
					htmlComponentMap.put("summary", (Element)tempElement);
				}else{
					//Elements的情况
					if(IsNullUtils.notNull(keyWords)){
						for (Element element : ((Elements)tempElement)) {
							for (String keyWord : keyWords) {
								if(element.text().contains(keyWord)){
									htmlComponentMap.put("summary", element);
									break OuterFor;
								}
							}
						}
					}
					htmlComponentMap.put("summary", ((Elements)tempElement).get(0));
				}
			}else{
				logger.info("没有发现正文简介,跳过");
			}
			
			
			
			//重新打包HTML文件,并处理字符编码问题
			logger.info("正在重新打包HTML...");
			byte[] packagingHTML = HTMLTextProcessUtils.packagingHTML(htmlComponentMap);
			
			//构建层级目录
			logger.info("构建保存层级目录...");
			//开始处理目标HTML,目标URL:http://www.binzz.com/wenzhang/60224.html
			String targetName = url.substring(url.lastIndexOf("/")+1,url.length());

			logger.info("保存路径:"+basePath+"/"+domain+"/");
			logger.info("targetName:"+targetName);
			//保存html文件
			FileUtils.saveBytesFile(packagingHTML, basePath+"/"+domain+"/"+targetName);
			logger.info("处理完成");
		}

	}

}