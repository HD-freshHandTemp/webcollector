package Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;

import model.userInfo;
/*
 * 定时器调用返回的list集合,读取磁盘文件内容的集合
 * 
 * 
 * 
 */
public class Readfile {
	List<userInfo> listall=null;
	public  List<userInfo> fileall(List<userInfo> solrall){//定时器调用返回读取磁盘文件内容的集合
		 listall=solrall;
		 System.out.println("读取传入的solr数据----------"+solrall.size());
		for(int i=0;i<solrall.size();i++){
			String filepath=solrall.get(i).getFilehtml();
		//	File file=new File(kk);
			try {
					File FILE=new File(filepath);
					String content = null;
					if(FILE.isFile()){
						if(FILE.getName().indexOf(".html")!=-1){
							Document doc = Jsoup.parse(FILE, "gbk");
							content = ContentExtractor.getContentByHtml(doc.html());
							listall.get(i).setHtmls(content);
						}
						else{			
							listall.get(i).setHtmls(BufferedReader(filepath));				
						}				
				}			 	
			} catch (Exception e) {	
				e.printStackTrace();
			}
		}
		System.out.println("更新的数据为------------"+listall.size());                    
		return listall;	
	}
	
	public  String BufferedReader( String filepath) throws IOException{//读取文件内容
		String content=null;
		 try {
			FileInputStream fis = new FileInputStream(filepath);
        	BufferedReader br = new BufferedReader(new InputStreamReader(fis,"utf-8"));
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();
        while(temp!=null){
            sb.append(temp+" ");
            temp=br.readLine();
        }
        	content=sb.toString();
		} 
		 catch (Exception e) {
			
			e.printStackTrace();
		}
        return content;     
    }      
}
