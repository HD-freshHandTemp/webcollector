package Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import model.userInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
/*
 * 
 * 通过指定本地磁盘的路径获取路径下的所有html文件,并且提取其中的标题,正文内容
 * 
 * 
 * /
 */
public  class Selectfiles{//通过指定路径
 static List<userInfo> pathall=new ArrayList<userInfo>();
	public static   List<userInfo> Fileall(File file){//通过指定的路径读取所有的文件 
		File[] fil=file.listFiles();
		System.out.println(fil.length);
		for(int i=0;i<fil.length;i++){	
			if(fil[i].isFile()){				
				if(fil[i].getName().indexOf(".html")!=-1&&fil[i].getName().indexOf(".txt")==-1){
					try {
						userInfo uss=new userInfo();
						Document doc = Jsoup.parse(fil[i], "gbk");//jsoup提取文章标题
						String title=doc.title();
						String content = ContentExtractor.getContentByHtml(doc.html());//提取正文内容
						if(title.indexOf("_")!=-1){
							title=title.substring(0,title.indexOf("_"));
						}
						if(title.indexOf("-")!=-1){
							title=title.substring(0,title.indexOf("-"));
						}						                           
						uss.setHtmls(content) ;
						uss.setTitle(title);
						uss.setId(fil[i].getPath().replace(":",""));
						uss.setFilehtml(fil[i].getPath());
						pathall.add(uss);
						System.out.println("提取的文章标题内容-----"+title);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
				}
				System.out.println(fil[i].getPath());//文件完整的路径
				System.out.println(fil[i].getName());//文件的名称			
			}
			if(fil[i].isDirectory()){
				//System.out.println("文件夹,目录是------>"+fil[i].getParent()); //当前目录的路径
				Fileall(fil[i]);
			}
		}	
		System.out.println(pathall.get(1).getFilehtml());
		System.out.println(pathall.get(1).getId());
		return pathall;
	}
	                                      
	public static void main(String args[]){
		String files="E:/12";
			File file =new File(files);
			Fileall(file);
			
			
			                                
	}
	
	public  String BufferedReader( File file) throws IOException{//读取文件内容
        //File file=new File(path);
		String content=null;
        if(!file.exists()||file.isDirectory())
            throw new FileNotFoundException();
        InputStreamReader read = new InputStreamReader(new FileInputStream(file),"gbk");
        BufferedReader br=new BufferedReader(read);
      
        String temp=null;
        StringBuffer sb=new StringBuffer();
        temp=br.readLine();
        while(temp!=null){
            sb.append(temp+" ");
            temp=br.readLine();
        }
        try {
		content = ContentExtractor.getContentByHtml(sb.toString());
        	
		} catch (Exception e) {
			e.printStackTrace();
		}
        return content;
       
    }
                                     
}