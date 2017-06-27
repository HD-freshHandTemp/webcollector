package webcollector.HTMLUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import webcollector.GeneralUtils.IsNullUtils;
import webcollector.IOUtils.FileUtils;
import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * 用于过滤无用信息以及格式化文本 提供对以下元素的处理 文字,表格,图片
 * 已实现基于MD5的文件去重
 * 
 * @author Mick Mo
 *
 */
public class HTMLTextProcessUtils {
	private final static Log logger = LogFactory.getLog(HTMLTextProcessUtils.class);
	
	/**
	 * 检测关键字工具
	 * @param keyWords 关键字数组
	 * @param targetElement 目标网页元素
	 * @return 是否包含关键字
	 */
	public static boolean checkingKeyWord(String[] keyWords,Element targetElement){
		boolean isContainKeyword = false;
		if(IsNullUtils.notNull(keyWords)){
			for (String keyWord : keyWords) {
				if (targetElement.text().contains(keyWord)) {
					logger.info("找到关键字:"+keyWord);
					isContainKeyword = true;
					//评分系统可以在这里添加
					break;
				}
			}
		}
		return isContainKeyword;
	}
	
	
	
	/*
	 * 处理HTML表格
	 */
	public static Object ProcessHTMLTable() {
		return null;
	}


	
	/**
	 * 下载CSS并替换为本地CSS资源
	 * @param cssElements 网页元素
	 * @param url URL
	 * @param basePath basePath+Domain 
	 * @throws IOException
	 */
	public static void ProcessHTMLCSS(Element cssElements,String url,String basePath) throws IOException {
		// 获取页面CSS
		if (cssElements!=null) {

			Connection connect;
			String href = cssElements.attr("href");
			
			logger.info("准备下载:"+href);
			String cssName = href.substring(href.lastIndexOf("/") + 1, href.length());
			logger.info("CSS文件名称:"+cssName);
			logger.info("CSS元素 HTML:"+cssElements.html());
			logger.info("CSS元素 Text:"+cssElements.text());
			logger.info("CSS元素长度:"+cssElements.html().length());
			if (cssName.indexOf(".css") == -1){
				logger.info("非CSS文件,"+cssName+"已被跳过");
				return;
			}
			
			
			//构建css地址
			//这里是临时处理,需要增加一个方法来判断,以匹配更多的网站类型
			url = url.substring(0,url.lastIndexOf(".com")+4);
			href = url +href;
			logger.info("目标CSS地址:"+href);
			
			
			
			//下载CSS
			connect = Jsoup.connect(href);
			connect.userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)");
			connect.timeout(5000);
			Document document = connect.get();
			String cssText = document.text();
			String charsetName = document.charset().toString();
			cssText = document.text();
			//检查是否空文件
			if(cssText.length()<1){
				logger.info("CSS文件内容为空,"+cssName+"已被跳过");
				return;
			}
			
			
			//保存CSS
			String localFilePath = "/"+basePath+"CSS/"+cssName;
			logger.info("cssName:"+cssName);
			logger.info("本地CSS路径:"+localFilePath);
			logger.info("basePath:"+basePath);
			URI localCssURI = FileUtils.saveBytesFile(cssText.getBytes(charsetName),localFilePath);
			
			//替换CSS
			cssElements.attr("href");
			cssElements.attr("href", localCssURI.toASCIIString());

			logger.debug("CSS文件处理完毕");
		}
	}

	/**
	 * 处理并保存图片 Elements imgall = Elementcontent.select("img[src]");
	 * 
	 * @param imgall
	 * @param savePath
	 * @param basePath basePath + domain
	 * @return
	 */
	public static void ProcessHTMLImage(Map<URL, URL>imgMap,String basePath) {
		if (IsNullUtils.notNull(imgMap)) {
			
			//读取支持的图片格式
			String[] supportImageTypr = FileUtils.readPropertiesBykey("supportImageTypr").split(",");
			//全部转化成大写方便对比
			String localtemp = "";
			for (int i = 0; i < supportImageTypr.length; i++) {
				supportImageTypr[i] = (supportImageTypr[i].trim()).toUpperCase();
				localtemp +=supportImageTypr[i]+",";
			}
			logger.info("支持的图片格式有:"+localtemp.substring(0, localtemp.length()-1));

			Map<String, String> localImgMap;
			logger.info("开始下载"+imgMap.size()+"个图片");
			BufferedOutputStream bos = null;
			FileOutputStream fos = null;
			ByteArrayOutputStream outStream = null;
			InputStream cin = null;
			HttpURLConnection httpConn;
			Set<Entry<URL, URL>> entrySet = imgMap.entrySet();
			
			try {
				localImgMap = new HashMap<String, String>();
				for (Entry<URL, URL> entry : entrySet) {
					URL url = entry.getKey();
					String fileNameWithSubfix = url.getPath();
					//后缀
					logger.info("开始下载图片:"+fileNameWithSubfix);
					String subfix = fileNameWithSubfix.substring(fileNameWithSubfix.lastIndexOf(".")+1, fileNameWithSubfix.length()).toUpperCase();
					if(IsNullUtils.isNull(subfix)){
						logger.info("无法获取图片文件后缀,跳过处理该图片.");
						continue;
					}
					//检查是否为支持的文件
					boolean localFlag =  false;
					logger.info("检查文件类型:"+subfix);
					for (String temp : supportImageTypr) {
						if(temp.equals(subfix)){
							localFlag = true;
						}
					}
					if(!localFlag){
						logger.info("不支持的文件类型:"+subfix+",跳过");
						continue;
					}
					logger.info("本地保存路径:"+basePath);
					
					//下载图片
					logger.info("图片URL:"+url);
					httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setConnectTimeout(3000);
					httpConn.setDoInput(true);
					httpConn.setRequestMethod("GET");
					httpConn.connect();
					
					logger.info("连接图片资源....");
					int responseCode = httpConn.getResponseCode();
		            if(responseCode == 200){
		            	logger.info("连接成功");
		
						cin = httpConn.getInputStream();
						outStream = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = cin.read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
						}
						cin.close();
						byte[] fileData = outStream.toByteArray();
						outStream.close();
						
						
						//构建层级目录

				
						//保存到本地
						String filePath = basePath+"img/"+fileNameWithSubfix;
						logger.info("保存图片:"+filePath);
						URI savedFileURI = FileUtils.saveBytesFile(fileData, filePath);
						entry.setValue(savedFileURI.toURL());
					}else {
						logger.info("获取图片资源失败....");
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
//				BufferedOutputStream bos;
//				FileOutputStream fos;
//				ByteArrayOutputStream outStream;
//				InputStream cin;
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outStream != null) {
					try {
						outStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (cin != null) {
					try {
						cin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 处理正文内容
	 * @param page
	 * @return
	 */
	public static String ProcessHTMLContent(Page page) {
		return null;
	}
	
	
	/**
	 * 重新打包HTML
	 * @param htmlComponentMap
	 * @param charset
	 * @return
	 */
	public static byte[] packagingHTML(Map<String, Object> htmlComponentMap){
//		htmlComponentMap.put("css", cssLInkElements);
//		htmlComponentMap.put("summary", summaryElements.get(0));
//		htmlComponentMap.put("title", titleElement);
//		htmlComponentMap.put("content", contentElement);
		
		if(!htmlComponentMap.isEmpty()){
			logger.info("开始重新打包HTML...");
			try {
				StringBuffer htmlData = new StringBuffer();
				Set<Entry<String, Object>> entrySet = htmlComponentMap.entrySet();
				
				
				ArrayList<Element> tempElements;
				Element tempElement;
				//保证顺序
				String key = "style";
				Object object = htmlComponentMap.get(key);
				if(IsNullUtils.notNull(object)){
					tempElements = (ArrayList<Element>)object;
					for (Element element : tempElements) {
						htmlData.append(element.html());
						logger.info("添加"+key+"元素,长度:"+element.html().length());
					}
				}
				
				
				key = "css";
				object = htmlComponentMap.get(key);
					if(IsNullUtils.notNull(object)){
					tempElements = (ArrayList<Element>)object;
					for (Element element : tempElements) {
						//手动构建HTML代码
						String cssElementString = "<link href=\""+element.attr("href")+"\" rel=\"stylesheet\" type=\"text/css\">";
						htmlData.append(cssElementString);
						logger.info("添加"+key+"元素,长度:"+cssElementString.length());
					}
					}
				
				
				key = "title";
				object = htmlComponentMap.get(key);
				if(IsNullUtils.notNull(object)){
					tempElement = (Element)object;
					htmlData.append(tempElement.html());
					logger.info("添加"+key+"元素,长度:"+tempElement.html().length());
				}
				
				
				key = "summary";
				object = htmlComponentMap.get(key);
				if(IsNullUtils.notNull(object)){
					tempElement = (Element)object;
					htmlData.append(tempElement.html());
					logger.info("添加"+key+"元素,长度:"+tempElement.html().length());
				}
				
				
				
				key = "content";
				object = htmlComponentMap.get(key);
				if(IsNullUtils.notNull(object)){
					tempElement = (Element)object;
					htmlData.append(tempElement.html());
					logger.info("添加"+key+"元素,长度:"+tempElement.html().length());
				}


				
				
//				for (Entry<String, Object> entry : entrySet) {
//					String key = entry.getKey();
//					Object value = entry.getValue();
//					if(key.equals("style")){
//						@SuppressWarnings("unchecked")
//						ArrayList<Element> elements = (ArrayList<Element>)value;
//						for (Element element : elements) {
//							htmlData.append(element.html());
//							logger.info("添加"+key+"元素,长度:"+element.html().length());
//						}
//					}else if(key.equals("css")){
//						@SuppressWarnings("unchecked")
//						ArrayList<Element> elements = (ArrayList<Element>)value;
//						for (Element element : elements) {
//							htmlData.append(element.html());
//							logger.info("添加"+key+"元素,长度:"+element.html().length());
//						}
//					}
//					else if(key.equals("title")||key.equals("summary")||key.equals("content")){
//						htmlData.append(((Element)value).html());
//						logger.info("添加"+key+"元素,长度:"+((Element)value).html().length());
//					}else {
//						logger.warn("无法识别的HTML结构:"+key+"  class:"+value.getClass());
//						continue;
//					}
//				}
				return htmlData.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("更换编码错误!");
			}
		}
		return null;
	}
	
	

}
