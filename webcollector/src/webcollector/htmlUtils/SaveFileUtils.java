package webcollector.htmlUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import webcollector.test.CrarlerTest;


/**
 * 保存图片文件
 * @author Mick Mo
 *
 */
public class SaveFileUtils {
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);
	
	public SaveFileUtils(){
		//读取propertities文件
	}
	
	public static String saveGeneralFile(byte[] fileData,String path){
		if (path!=null&&!path.equals("")&&fileData.length>0) {
			FileOutputStream fos = null;
			try {
				logger.info("文件将保存到以下目录:"+path);
				File file = new File(path);
		        File p = new File(file.getParent());
		        if(!p.exists()){
		        	logger.error("目标目录不存在,创建:");
		        	logger.error(p.getAbsoluteFile());
		            p.mkdirs();
		        }
		        if(!file.exists()){
		        	logger.error("目标文件不存在,创建:");
		        	logger.error(file.getAbsoluteFile());
		            file.createNewFile();
		        }
				
//				//判断目标文件所在的目录是否存在
//		        if(!file.getParentFile().exists()) {
//		            //如果目标文件所在的目录不存在，则创建父目录
//		        	logger.info("目标文件所在目录不存在");
//		        	String dirPath = path.substring(0,path.lastIndexOf("\\")+1);
//		        	logger.info("dirPath:"+dirPath);
//		        	File tempFile = new File(dirPath);
//		            if(tempFile.isDirectory())  {
//		            	logger.info("将创建以下目录"+dirPath);
//		            	if(tempFile.mkdirs()){
//		            		logger.error("创建成功!");
//		            	}else{
//		            		logger.error("创建失败!");
//			                throw new IOException("创建失败!");
//		            	}
//		            }else{
//		            	logger.error("路径错误,创建失败!");
//		            	throw new IOException("创建失败!");
//		            }
//		        }
				
				fos = new FileOutputStream(file);
				fos.write(fileData);
				fos.flush();
				return file.toURI().getPath();
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
	
	public static boolean deleteLocalTempFile(String targetFilePath){
		return false;
		
	}
}
