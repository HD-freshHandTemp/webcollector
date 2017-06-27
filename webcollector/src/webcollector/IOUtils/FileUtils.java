package webcollector.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import webcollector.GeneralUtils.IsNullUtils;
import webcollector.test.CrarlerTest;


/**
 * 文件工具
 * @author Mick Mo
 *
 */
public class FileUtils {
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);
	private static InputStream inStream;
	
	
	/**
	 * 读取文件
	 * @param filePath
	 * @return
	 */
	public static byte[] readFile(String filePath){
		File file = null;
		if (IsNullUtils.isNull(filePath)) {
			return null;
		}
		
		file = new File(filePath);
		if(file.isDirectory()||IsNullUtils.isNull(file)){
			return null;
		}
		
		BufferedInputStream bufferedInputStream = null;
		byte[] bytes = null;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			bytes = new byte[(bufferedInputStream.available())];
			bufferedInputStream.read(bytes);
		} catch (Exception e) {
			logger.error("加载失败:"+filePath);
			e.printStackTrace();
		}finally{
			if(bufferedInputStream!=null){
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bytes;
	}
	
	
	/**
	 * 批量读取文件
	 * @param dirPath
	 * @return
	 */
	public static List<byte[]> readFiles(String dirPath){
		File file = null;
		if (IsNullUtils.isNull(dirPath)) {
			return null;
		}
		
		file = new File(dirPath);
		if(file.isFile()||IsNullUtils.isNull(file)){
			return null;
		}
		
		BufferedInputStream bufferedInputStream = null;
		byte[] bytes;
		List<byte[]> files = null;
		try {
			files = new ArrayList<byte[]>();
			File[] listFiles = file.listFiles();
			for (File tempFile : listFiles) {
				if(tempFile.isFile()){
					bufferedInputStream = new BufferedInputStream(new FileInputStream(tempFile));
					bytes = new byte[(bufferedInputStream.available())];
					bufferedInputStream.read(bytes);
					files.add(bytes);
				}
			}
		} catch (Exception e) {
			logger.error("加载失败:"+dirPath);
			e.printStackTrace();
		}finally{
			if(bufferedInputStream!=null){
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return files;
	}
	
	
	
	/**
	 * 保存为字节码的MD5文件
	 * 返回文件的URI
	 * @param fileData byte[] 字节数据
	 * @param path 储存路径
	 * @return
	 */
	public static URI saveBytesFile(byte[] fileData,String path){
		if (path!=null&&!path.equals("")&&fileData.length>0) {
			FileOutputStream fos = null;
			try {
				logger.info("文件将保存到以下目录:"+path);
				File file = new File(path);
				 if(!file.exists()&&file.isDirectory()){
			        	logger.error("目标目录不存在,创建:");
			        	logger.error(file.getAbsoluteFile());
			        	file.mkdirs();
			        }
		        File p = new File(file.getParent());
		        if(!p.exists()){
		        	logger.error("目标目录不存在,创建:");
		        	logger.error(p.getAbsoluteFile());
		            p.mkdirs();
		        }
		        
		        //计算MD5
		        String md5ByFile = MD5Utils.getMD5ByFile(fileData);
		        
		        //重命名
		        String name = file.getName();
		        String extension = name.substring(name.lastIndexOf("."), name.length());
		        String newName = md5ByFile+extension;
		        path=path.replace(name, newName);
		        logger.error("path:"+path);
		        logger.error("name:"+name);
		        logger.error("MD5:"+newName);
		        file = new File(path);
		        
		        if(!file.exists()){
		        	logger.error("目标文件不存在,创建:");
		        	logger.error(file.getAbsoluteFile());
		            file.createNewFile();
		            
		            //写入文件
					fos = new FileOutputStream(file);
					fos.write(fileData);
					fos.flush();
					
					//返回URI
					return file.toURI();
		        }else{
		        	//相同文件已存在直接返回URI
		        	logger.error("相同文件已存在");
		        	return file.toURI();
		        }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 删除文件
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath){
		File file = null;
		if (IsNullUtils.isNull(filePath)) {
			return false;
		}
		
		file = new File(filePath);
		if(file.isDirectory()||IsNullUtils.isNull(file)){
			return false;
		}
		
		boolean delete = false;
		try {
			delete = file.delete();
		} catch (Exception e) {
			logger.error("加载失败:"+filePath);
			e.printStackTrace();
		}
		return delete;
		
	}
	
	/**
	 * 批量删除文件
	 * @param dirPath
	 * @return
	 */
	public static List<String> deleteFiles(String dirPath){
		File file = null;
		if (IsNullUtils.isNull(dirPath)) {
			return null;
		}
		
		file = new File(dirPath);
		if(file.isFile()||IsNullUtils.isNull(file)){
			return null;
		}
		
		List<String> failingList = null;
		try {
			failingList = new ArrayList<String>();
			File[] listFiles = file.listFiles();
			for (File tempFile : listFiles) {
				if(tempFile.isFile()){
					boolean delete = tempFile.delete();
					if (delete) {
						failingList.add(tempFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			logger.error("加载失败:"+dirPath);
			e.printStackTrace();
		}
		
		return failingList;
	}
	
	/**
	 * 根据URL转换文件储存层级目录
	 * @param url target URL
	 * @param domain WebSite's Domain
	 * @return middleDir
	 */
	public static String buildDirStructure(String url,String domain){
		//构建层级目录
		logger.info("构建保存层级目录...");
		//开始处理目标HTML,目标URL:http://www.test.com/wenzhang/60224/13213.html
		url.replace("\\", "/");
		//获取子目录
		//.com/wenzhang/60224/13213.html
		String tempSubstring = url.substring(url.lastIndexOf(domain)+domain.length(), url.length());
		///wenzhang/60224/
		tempSubstring = tempSubstring.substring(tempSubstring.indexOf("/"), tempSubstring.lastIndexOf("/"));
		
		return tempSubstring;
	}
	
	/**
	 * 获取配置文件
	 * @return properties
	 * @throws IOException 
	 */
	public static Properties readProperties() throws IOException{
		//读取匹配度阈值
		InputStream inStream;
		logger.info("正在读取配置文件...");
		File configFile = new File("src/config/HTMLPhasingModels.config");
		logger.info("加载配置文件:"+configFile.getAbsolutePath());
		if (!configFile.exists()||configFile.isDirectory()) {
			logger.error("找不到网站模型配置文件!请确认config/下存在HTMLPhasingModels.config文件");
			throw new IOException("找不到网站模型配置文件!请确认config/下存在HTMLPhasingModels.config文件");
		}else {
			inStream = new BufferedInputStream(new FileInputStream(configFile));
		}
		Properties properties = new Properties();
		properties.load(inStream);
		return properties;
	}
	
	/**
	 * 获取配置文件的某个值
	 * @param properties' key
	 * @return properties' value
	 * @throws IOException 
	 */
	public static String readPropertiesBykey(String key){
		try {
			return readProperties().getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 读取URLs
	 * @param properties' key
	 * @return properties' value
	 * @throws IOException 
	 */
	public static String[] readTargetURLs(){
		try {
			return readFileByKey("URLs");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 读取文件保存目录
	 * @param properties' key
	 * @return properties' value
	 * @throws IOException 
	 */
	public static String readBasePath(){
		String basePath = readPropertiesBykey("BasePath");
		if(basePath.endsWith("/")){
			return basePath.substring(0, basePath.length()-1);
		}
		return basePath;
	}
	
	/**
	 * 读取URL黑名单文件
	 * @param properties' key
	 * @return properties' value
	 */
	public static String[] readKeyWords(){
		try {
			return readFileByKey("KeyWords");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	/**
	 * 从配置文件中获取对应文件路径并读取
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private static String[] readFileByKey(String key) throws IOException {
		if (IsNullUtils.isNull(key)) {
			return null;
		}
		
		String urlsPath = readPropertiesBykey(key);

		if (IsNullUtils.isNull(urlsPath)) {
			logger.info("没有指定"+key+"列表文件");
			return null;
		}
		
		logger.info("正在读取目标"+key+"列表文件...");
		File configFile = new File(urlsPath);
		logger.info("加载文件:"+configFile.getAbsolutePath());
		if (!configFile.exists()||configFile.isDirectory()) {
			logger.error("找不到URL列表文件!请确认"+configFile.getAbsolutePath()+"存在.");
			throw new IOException("找不到"+key+"列表文件!请确认"+configFile.getAbsolutePath()+"存在.");
		}else {
			inStream = new BufferedInputStream(new FileInputStream(configFile));
		}
		byte[] bytes = new byte[(inStream.available())];
		inStream.read(bytes);
		if(bytes.length>0){
			String string = new String(bytes,"UTF-8");
			logger.info("尝试分割:"+string);
			if(string.contains(System.getProperty("line.separator", "\n").toString())){
				String[] split = string.split("[\\r\\n]+");
				logger.info("分割后元素个数:"+split.length);
				if (IsNullUtils.notNull(split)) {
					logger.info("分割成功,分割后元素:"+split.length);
					return split;
				}else {
					logger.info("分割后为空文件.");
					return null;
				}
			}else{
				logger.info("没有发现分割符,直接返回");
				String[] temp = new String[(1)];
				temp[0] = string;
				return temp;
			}
		}else{
			logger.info("文件为空");
			return null;
		}
	}
}
