package webcollector.test;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

public class HTMLCrawlerTest extends BreadthCrawler {
	private final static Log logger = LogFactory.getLog("HtmlLogger");

	public HTMLCrawlerTest(String crawlPath, boolean autoParse) {
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

	@Override
	public void visit(Page page, CrawlDatums next) {

		if (page.response() == null) {
			return;
		} else if (page.response().contentType().contains("html")) {
//			logger.info("debug pageContenType:" + page.response().contentType());

//			logger.info("-----page.getResponse()---start-----");
//			logger.info(page.getResponse());
//			logger.info("-----page.getResponse()---e n d-----");
//
//			logger.info("");
//			logger.info("");
//			logger.info("");
//
			// 获取网页的所有HTML代码
			logger.info("-----page.getHtml()---start-----");
			logger.info(page.getHtml());
			logger.info("-----page.getHtml()---e n d-----");
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//
//			logger.info("");
//			logger.info("");
//			logger.info("");
//
//			// 获取页面编码
//			logger.info("-----page.getCharset()---start-----");
//			logger.info(page.getCharset());
//			logger.info("-----page.getCharset()---e n d-----");
//
//			logger.info("");
//			logger.info("");
//			logger.info("");
//
//			logger.info("-----page.metaData()---start-----");
//			HashMap<String, String> metaData = page.getMetaData();
//			Set<Entry<String, String>> entrySet = metaData.entrySet();
//			for (Entry<String, String> entry : entrySet) {
//				logger.info(entry.getKey() + "::" + entry.getValue());
//			}
//			logger.info("-----page.metaData()---e n d-----");
//
//			logger.info("");
//			logger.info("");
//			logger.info("");
//
//			// 获取页面内容,与获取HTML一样
//			logger.info("-----page.getContent()---start-----");
//			String string = null;
//			try {
//				string = new String(page.getContent(), page.getCharset());
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//				logger.error("Fail to get page.getContent() maybe the c");
//			}
//			logger.info(string);
//			logger.info("-----page.getContent()---e n d-----");
			
			
//			
//			System.out.println("测试");
//			Document doc = page.doc();
//			Elements elementsByClass = doc.getElementsByClass("t-h4@m- t-h1-b@tp t-h1@tl+ mt-20 mt-15@tp mt-0@m-");
//			System.out.println("t-h4@m- t-h1-b@tp t-h1@tl+ mt-20 mt-15@tp mt-0@m-");
//			for (Element element : elementsByClass) {
//				System.out.println(element.html());
//			}
//			
//			
//			Elements elementsByClass2 = doc.getElementsByClass("t-d7@m- t-d4@tp t-d3-b@tl t-d2@d mt-15 mt-25@tp c-gray-3 c-gray-6@m-");
//			System.out.println("t-d7@m- t-d4@tp t-d3-b@tl t-d2@d mt-15 mt-25@tp c-gray-3 c-gray-6@m-");
//			for (Element element : elementsByClass2) {
//				System.out.println(element.html());
//			}

		}
	}
}