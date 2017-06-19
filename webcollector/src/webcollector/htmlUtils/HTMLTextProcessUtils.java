package webcollector.htmlUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
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

import webcollector.test.CrarlerTest;

import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * 用于过滤无用信息以及格式化文本 提供对以下元素的处理 文字,表格,图片 如何去重?
 * 
 * @author Mick Mo
 *
 */
public class HTMLTextProcessUtils {
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);
	
	
	// 处理表格
	/*
	 * 
	 */
	public static Object ProcessHTMLTable() {
		return null;
	}


	
	/**
	 * 下载并CSS替换为本地CSS资源
	 * @param page
	 * @param savePath
	 * @return
	 * @throws IOException 
	 */
	public static void ProcessHTMLCSS(ArrayList<Element> cssElements,String url) throws IOException {
		// 获取页面CSS
		if (cssElements!=null&&cssElements.size() > 0) {

			Connection connect;

			for (int i = 0;i<cssElements.size();i++) {
				//检查要下载的是否为CSS文件
				Element element = cssElements.get(i);
				String href = element.attr("href");
				
				logger.info("准备下载:"+href);
				String cssName = href.substring(href.lastIndexOf("/") + 1, href.length());
				logger.info("CSS文件名称:"+cssName);
				if (cssName.indexOf(".css") == -1){
					logger.info("非CSS文件,"+cssName+"已被移除");
					cssElements.remove(i);
					continue;
				}
				
				
				//构建css地址
				//这里是临时处理,需要增加一个方法来判断,以匹配更多的网站类型
				url = url.substring(0,url.lastIndexOf(".com")+4);
				String dirPath = url.substring(url.lastIndexOf("www"),url.length());
				logger.info("dirPath:"+dirPath);
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
					logger.info("CSS文件内容为空,"+cssName+"已被移除");
					cssElements.remove(i);
					continue;
				}
				
				//保存CSS
				String localFilePath = "C:\\Users\\Mick Mo\\Desktop\\temp\\"+dirPath+"\\"+new Date().getTime()+cssName;
				logger.info("dirPath:"+dirPath);
				logger.info("cssName:"+cssName);
				logger.info("本地CSS路径:"+localFilePath);
				String localCssURI = SaveFileUtils.saveGeneralFile(cssText.getBytes(charsetName),localFilePath);
				
				//替换CSS
				element.attr("href");
				element.attr("href", localCssURI);
			}
			logger.debug("CSS文件处理完毕");
		}
	}

	/**
	 * 处理并保存图片 Elements imgall = Elementcontent.select("img[src]");
	 * 
	 * @param imgall
	 * @param savePath
	 * @return
	 */
	public static Map<String, String> ProcessHTMLImage(Map<String, String> imgMap) {
		if (imgMap!=null&&imgMap.size()>0) {
			Map<String, String> filePathByType = new HashMap<String, String>();//后面根据properties文件读取该设置
			
			//保存图片
			//模拟读取properties
			//BMP、JPG、JPEG、PNG、GIF
			//<img alt="daria-nepriakhina-198549.jpg" src="http://www.binzz.com/uploads/allimg/170326/1A102K37-4.jpg" title="8716248178507445.jpg" />
			filePathByType.put("img".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\img");
			filePathByType.put("BMP".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\img");
			filePathByType.put("JPG".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\img");
			filePathByType.put("JPEG".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\img");
			filePathByType.put("PNG".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\img");
			filePathByType.put("GIF".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\img");
			
			filePathByType.put("css".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\css");
			filePathByType.put("html".toUpperCase(), "C:\\Users\\Mick Mo\\Desktop\\temp\\html");
			
			
			String targetPath = null;//目标保存路径
			
			
			Map<String, String> localImgMap;
			Set<Entry<String, String>> entrySet = imgMap.entrySet();
			
			BufferedOutputStream bos = null;
			FileOutputStream fos = null;
			ByteArrayOutputStream outStream = null;
			InputStream cin = null;
			HttpURLConnection httpConn;

			try {
				localImgMap = new HashMap<String, String>();
				for (Entry<String, String> entry : entrySet) {
					
					String fileNameWithSubfix = entry.getKey();
					//后缀
					logger.info("开始下载图片:"+fileNameWithSubfix);
					String subfix = fileNameWithSubfix.substring(fileNameWithSubfix.lastIndexOf(".")+1, fileNameWithSubfix.length());
					
					//检查是否为支持的文件
					
					targetPath = filePathByType.get(subfix.toUpperCase());
					logger.info("检查文件类型:"+subfix);
					if(targetPath==null||targetPath.equals("")){
						logger.info("不支持的文件类型");
						return null;
					}
					logger.info("本地保存路径:"+targetPath);
					
					//下载图片
					String urlValue = entry.getValue();
					logger.info("图片URL:"+urlValue);
					URL url = new URL(urlValue);
					httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setConnectTimeout(3000);
					httpConn.setDoInput(true);
					httpConn.setRequestMethod("GET");
					httpConn.connect();
					
					logger.info("连接图片资源....");
					int responseCode = httpConn.getResponseCode();
		            if(responseCode == 200){
		            	logger.info("连接成功");
						//如果响应为“200”，表示成功响应，则返回一个输入流
//						cin = httpConn.getInputStream();
//						//输出流到response中
//						buffer = new byte[1024];// 给个1MB的缓存没问题
//						int bytesWritten = 0;
//						int byteCount = 0;
//
//						while ((byteCount = cin.read(buffer)) != -1) {
//							outStream.write(buffer, bytesWritten, byteCount);
//							bytesWritten+=byteCount;
//						}
//						cin.close();
//						fileData = outStream.toByteArray();
//						outStream.close();
		            	
		            	
						
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
//						
						//保存到本地
						String filePath = targetPath + "\\" + subfix.toUpperCase() + new Date().getTime() + fileNameWithSubfix;
						logger.info("保存图片:"+filePath);
						String savedFileURI = SaveFileUtils.saveGeneralFile(fileData, filePath);
						localImgMap.put(fileNameWithSubfix, savedFileURI);
					}else {
						logger.info("获取图片资源失败....");
					}
				}
				return localImgMap;
				
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
		return null;
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
	public static byte[] packagingHTML(Map<String, Object> htmlComponentMap,Charset charset){
//		htmlComponentMap.put("css", cssLInkElements);
//		htmlComponentMap.put("summary", summaryElements.get(0));
//		htmlComponentMap.put("title", titleElement);
//		htmlComponentMap.put("content", contentElement);
		
		if(!htmlComponentMap.isEmpty()&&charset!=null){
			try {
				StringBuffer htmlData = new StringBuffer();
				Set<Entry<String, Object>> entrySet = htmlComponentMap.entrySet();
				for (Entry<String, Object> entry : entrySet) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if(key.equals("css")){
						@SuppressWarnings("unchecked")
						ArrayList<Element> elements = (ArrayList<Element>)value;
						for (Element element : elements) {
							htmlData.append(new String(element.html().getBytes(charset), "UTF-8"));
						}
					}
					else if(key.equals("title")||key.equals("summary")||key.equals("content")){
						htmlData.append(new String(((Element)value).html().getBytes(charset), "UTF-8"));
					}else {
						logger.warn("无法识别的HTML结构:"+key+"  class:"+value.getClass());
						continue;
					}
				}
				return htmlData.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("更换编码错误!");
			}
		}
		return null;
	}
	
	

}
