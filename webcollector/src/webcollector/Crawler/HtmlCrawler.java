package webcollector.Crawler;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
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
	private static List<String> urlBlackKeyWords;//屏蔽关键字
	private static List<String> contentBlackKeyWords;//正文屏蔽关键字
	private static Vector<String> urlBlackList;//URL屏蔽列表
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
	/**
	 * 用于过滤垂直爬取资源时的特定URL,例如社交网站分享链接等
	 * @param urlBlackKeyWords
	 */
	public static void setUrlBlackKeyWords(List<String> urlBlackKeyWords) {
		HtmlCrawler.urlBlackKeyWords = urlBlackKeyWords;
	}
	/**
	 * 用于过滤正文关键字
	 * @param contentBlackKeyWords
	 */
	public static void setContentBlackKeyWords(List<String> contentBlackKeyWords) {
		HtmlCrawler.contentBlackKeyWords = contentBlackKeyWords;
	}
	/**
	 * 爬取失败的URL list,
	 * 用于爬虫系统内部加速
	 * @param urlBlackList
	 */
	public static void setUrlBlackList(Vector<String> urlBlackList) {
		HtmlCrawler.urlBlackList = urlBlackList;
	}
	/**
	 * 储存所有可用的网页模型
	 * @param htmlPhasingModels
	 */
	public static void setHtmlPhasingModels(List<HTMLPhasingModel> htmlPhasingModels) {
		HtmlCrawler.htmlPhasingModels = htmlPhasingModels;
	}
	
	/**
	 * 储存检测关键字
	 * @param keyWords
	 */
	public static void setKeyWords(String[] keyWords) {
		HtmlCrawler.keyWords = keyWords;
	}

	/**
	 * 储存爬虫资源储存的基础目录
	 * @param basePath
	 */
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
		String localPhasing;
		if (page.response() == null) {
			return;
		} else if (page.response().contentType().contains("html")) {
			
			String url = page.getUrl();
			//URL黑名单检测
			//检测URL黑名单List
			//这里需要的是二叉树实现,vector虽然线程安全,但是效率比不过二叉树
//			for (String blackURL : urlBlackList) {
//				if ((url.toLowerCase()).contains((blackURL.toLowerCase()))) {
//					logger.warn("当前URL:"+url+"在黑名单内,终止当前网页的处理...");
//					return;
//				}
//			}
			//检测URL黑名单
			for (String blackKeyWord : urlBlackKeyWords) {
				if ((url.toLowerCase()).contains((blackKeyWord.toLowerCase()))) {
					logger.warn("URL:"+url+"包含有黑名单关键字:"+blackKeyWord+"终止当前网页的处理...");
					return;
				}
			}


			logger.info("开始处理目标HTML,目标URL:"+url);
			Map<String, Object> htmlComponentMap = new HashMap<String, Object>();
			Document doc = page.doc();
			Charset charset = Charset.forName(page.charset());
			logger.info("当前网页编码:"+charset.displayName());
			
			//匹配模型
			HTMLPhasingModel detectHTMLModel;
			URL modelURL;
			try {
				detectHTMLModel = HTMLModelDetector.detectHTMLModel(url, htmlPhasingModels, doc, charset);
				
				//检查解析后的模型
				if (IsNullUtils.isNull(detectHTMLModel)) {
					logger.info("没有用于解析当前URL的网页模型");
					//屏蔽当前URL
//					URLs.remove(url);
					return;
				}
				
				System.out.println("检测更改数据:");
				System.out.println(detectHTMLModel.getModifiedDocumentHTML());
				
				
				
				
				//检查Document是否有更新
				if (IsNullUtils.notNull(detectHTMLModel.getModifiedDocumentHTML())) {
					FileUtils.saveBytesFile(detectHTMLModel.getModifiedDocumentHTML().getBytes(), "C:/Users/Mick Mo/Desktop/temp/"+new Date().getTime()+".html");
					doc = new Document(detectHTMLModel.getModifiedDocumentHTML());
					logger.info("Document更新完毕");
				}else{
					logger.info("没有检测到Document更改");
				}
				
				System.exit(1);
				
				//从模型中读取基本信息
				modelURL = new URL(detectHTMLModel.getUrl());
				//无法获取模型域名
				if(IsNullUtils.isNull(modelURL)){
					logger.info("当前URL没有模型能够正确解析");
					return;
				}
				logger.info("URL in Model:"+modelURL);
				
			} catch (Exception e1) {
				logger.error("匹配模型发生错误!");
				e1.printStackTrace();
				return;
			}

			
		
			
			
			try {
				modelURL = new URL(detectHTMLModel.getUrl());
				logger.info("URL in Model:"+modelURL);
			} catch (MalformedURLException e1) {
				
			}
			//获取URL中的Domain和FileName
			String host = modelURL.getHost();
			logger.info("Host in Model:"+host);
//			String path = modelURL.getPath();
//			path.replace("/", "");
			String domain = host.substring(host.indexOf(".")+1, host.lastIndexOf("."));
			logger.info("Domain:"+domain);
			
			
			//通过模型解析网页获取,目标元素Map
			logger.info("通过模型解析网页:"+url+"....");
			Map<String, Object> readElementByModel = ReadElementByModel.readElementByModel(detectHTMLModel, doc, keyWords);
			logger.info("ReadElementByModelMap's Size:"+readElementByModel.size());
			

			//处理网页链接,
			logger.info("处理后续链接,以便实现垂直爬取...");
			int urlCount = 0;
			int urlBlockCount = 0;
			Elements selectHref = page.select("a[href]");
			if (selectHref.size() > 0) {// 提取网页中的链接,放入后续的队列中
				for (Element link : selectHref) {
					String href = link.attr("href");
					if(IsNullUtils.isNull(href)){
						href = link.attr("src");
					}
					layer1st:
					if(IsNullUtils.notNull(href)&&href.contains(domain)){
						//检测URL屏蔽关键字
						for (String blackKeyWord : urlBlackKeyWords) {
							if ((href.toLowerCase()).contains((blackKeyWord.toLowerCase()))) {
//								logger.info("URL:"+href+"包含有黑名单关键字:"+blackKeyWord+"跳过..");
								urlBlockCount++;
								break layer1st;
							}
						}
						if (href.indexOf("#") != -1) {
							href = href.substring(0,href.lastIndexOf("#"));
						}
						next.add(href);
						urlCount++;
					}
				}
			}
			logger.info(urlCount+"个,"+domain+"下的后续链接已加入后续链接列表,"+urlBlockCount+"个连接已被跳过.");
			

			logger.info("开始处理HTML数据...");
			Object tempElement;//用于储存临时元素
			
			
			//正文部分
			localPhasing = "正文元素";
			logger.info("开始处理"+localPhasing+"...");
			tempElement = readElementByModel.get("content");
			if (IsNullUtils.notNull(tempElement)) {
				OuterFor:
					if(tempElement instanceof Element){
						//Element的情况
						htmlComponentMap.put("content", (Element)tempElement);
						logger.info("找到单个"+localPhasing+",已添加至HTML容器.");
						logger.debug(localPhasing+"长度:"+((Element) tempElement).html().length());
					}else{
						//Elements的情况
						if(IsNullUtils.notNull(keyWords)){
							logger.info("找到多个"+localPhasing+",进行关键字匹配...");
							for (Element element : ((Elements)tempElement)) {
								for (String keyWord : keyWords) {
									if(element.text().contains(keyWord)){
										htmlComponentMap.put("content", element);
										logger.info("已发现含有关键字的"+localPhasing+",并添加至HTML容器.");
										logger.debug(localPhasing+"长度:"+((Element) tempElement).html().length());
										break OuterFor;
									}
								}
							}
							logger.info("未发现含有关键字的"+localPhasing+",取消对当前URL的所有操作");
							return;
						}else{
							htmlComponentMap.put("content", ((Elements)tempElement).get(0));
							logger.info("找到多个"+localPhasing+",已将第一个添加至HTML容器.");
							logger.info(localPhasing+"长度:"+(((Elements)tempElement).get(0).html()).length());
						}
					}
			}else{
				logger.error("没有发现"+localPhasing+",取消对当前URL的所有操作");
				return;
			}
			
			
			
			//正文部分图片资源
			localPhasing = "正文图片元素";
			ArrayList<Element> imgElements;
			logger.info("开始下载"+localPhasing+"及处理路径...");
			tempElement = htmlComponentMap.get("content");
			if(IsNullUtils.notNull(tempElement)){
				if(tempElement instanceof Element){
					//Element的情况
					logger.info("从单个正文元素中寻找"+localPhasing+"...");
					imgElements = ((Element)tempElement).getElementsByTag("img");
					
				}else{
					//Elements的情况
					imgElements = new ArrayList<Element>();
					logger.info("从"+((Elements)tempElement).size()+"个正文元素中中寻找"+localPhasing+"...");
					for (Element element : ((Elements)tempElement)) {
						Elements elementsByTag = element.getElementsByTag("img");
						for (Element localTempEmlement : elementsByTag) {
							imgElements.add(localTempEmlement);
						}
					}
				}

				if(imgElements.size()>0){
					logger.info("找到"+imgElements.size()+"个"+localPhasing);
					Map<URL, URL> imgMap = new HashMap<URL, URL>();
					//下载Img
					for (Element element : imgElements) {
						//处理图片路径和文件名
						try {
							URL imgURL = new URL(element.attr("src"));
							imgMap.put(imgURL, null);
						} catch (MalformedURLException e) {
							logger.error("无法处理"+localPhasing+":"+element.attr("src")+",跳过...");
							e.printStackTrace();
							continue;
						}
					}
					logger.info("已根据模型处理"+imgMap.size()+"个"+localPhasing);
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
					logger.info("没有找到"+localPhasing+",跳过...");
				}
			}else{
				logger.warn("无法下载正文图片,跳过...");
			}
			


			//style
			localPhasing = "style元素";
			logger.info("开始处理"+localPhasing+"...");
			ArrayList<Element> styleElements = doc.getElementsByTag("style");
			if(IsNullUtils.notNull(styleElements)){
				htmlComponentMap.put("style", styleElements);
				logger.info("已处理"+styleElements.size()+"个"+localPhasing);
			}else{
				logger.warn("没有发现"+localPhasing+",跳过...");
			}

			
			
			
			//CSS资源
			localPhasing = "CCS元素";
			logger.info("开始下载"+localPhasing+"并处理路径...");
			ArrayList<Element> cssLInkElements = doc.select("link");
			for (Element element : cssLInkElements) {
				System.out.println(element.html());
			}
			ArrayList<Element> validCSSLInkElements = new ArrayList<Element>();
			int localInvalidCSSCounttemp = 0;
			if(IsNullUtils.notNull(cssLInkElements)){
				logger.warn("发现"+cssLInkElements.size()+"个"+localPhasing+",开始校验CSS...");
				for (Element element : cssLInkElements) {
					if ((element.attr("type")).toLowerCase().contains("css")) {
						//下载CSS 并修改为本地CSS资源
						try {
							HTMLTextProcessUtils.ProcessHTMLCSS(element,url,basePath+"/"+domain+"/");
							validCSSLInkElements.add(element);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						localInvalidCSSCounttemp++;
					}
				}
			}else{
				logger.warn("没有发现"+localPhasing+",跳过");
			}
			htmlComponentMap.put("css", cssLInkElements);
			logger.info(localPhasing+"结果:有效CSS元素:"+(cssLInkElements.size()-localInvalidCSSCounttemp)+"个;无效Link元素数:"+localInvalidCSSCounttemp+"个");
			
			
			
			
			//标题
			localPhasing = "标题元素";
			logger.info("开始处理"+localPhasing+"...");
			tempElement = readElementByModel.get("title");
			if(IsNullUtils.notNull(tempElement)){
				OuterFor:
				if(tempElement instanceof Element){
					//Element的情况
					htmlComponentMap.put("title", (Element)tempElement);
					logger.info("已添加单个"+localPhasing);
				}else{
					//Elements的情况
					if(IsNullUtils.notNull(keyWords)){
						logger.info("找到多个"+localPhasing+",开始匹配关键字..");
						for (Element element : ((Elements)tempElement)) {
							for (String keyWord : keyWords) {
								if(element.text().contains(keyWord)){
									htmlComponentMap.put("title", element);
									break OuterFor;
								}
							}
						}
					}
					logger.info("找到多个"+localPhasing+",关键字不存在,添加第一个"+localPhasing);
					htmlComponentMap.put("title", ((Elements)tempElement).get(0));
				}
			}else{
				logger.info("没有发现"+localPhasing+",跳过...");
			}
			
			
			//正文内容简介
			localPhasing = "正文简介元素";
			logger.info("开始处理"+localPhasing+"...");
			tempElement = readElementByModel.get("summary");
			if(IsNullUtils.notNull(tempElement)){
				OuterFor:
				if(tempElement instanceof Element){
					//Element的情况
					htmlComponentMap.put("summary", (Element)tempElement);
					logger.info("已添加单个"+localPhasing);
				}else{
					//Elements的情况
					logger.info("找到多个"+localPhasing);
					if(IsNullUtils.notNull(keyWords)){
						logger.info("开始匹配关键字..");
						for (Element element : ((Elements)tempElement)) {
							for (String keyWord : keyWords) {
								if(element.text().contains(keyWord)){
									htmlComponentMap.put("summary", element);
									logger.info("找到含有关键字的"+localPhasing);
									break OuterFor;
								}
							}
						}
					}
					logger.info("找到多个"+localPhasing+",关键字不存在,添加第一个"+localPhasing);
					htmlComponentMap.put("summary", ((Elements)tempElement).get(0));
				}
			}else{
				logger.info("没有发现"+localPhasing+",跳过...");
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