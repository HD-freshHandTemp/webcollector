package Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.userInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;


public class SelectList {

	/**
	 * @param 
	 * args
	 * 通过传入带有路径的list集合,查询出所有文章信息(磁盘总查询)
	 */  
	
	static List<Map<String, Object>> pathall=new ArrayList<Map<String, Object>>();
	public static void main(String[] args) {
		//System.out.println(lis.size());
		List<Map<String, Object> >mapList = null;
		Map<String, Object> mapSqlParams = new HashMap<String, Object>();
		mapSqlParams.put("id", "www.jj59.com/jjart303686html");
		mapSqlParams.put("filehtml", "C:/Users/yanfa/Desktop/1.html");
       mapList=new ArrayList<Map<String, Object> >();
       mapList.add(mapSqlParams);
       System.out.println(mapList.size());
       Selectlist(mapList);

	}
	public static List<Map<String, Object>>  Selectlist(List<Map<String, Object>>  mapList){//通过集合进行批量查询文章内容
		Map<String, Object> mapall=null;
		for(Map<String, Object> map:mapList){
			String filepath=map.get("filehtml").toString();
			File file =new File(filepath);
			if(file.isFile()){				
				if(file.getName().indexOf(".html")!=-1&&file.getName().indexOf(".txt")==-1){
					try {
						Document doc = Jsoup.parse(file, "gbk");//jsoup提取文章标题
						String title=doc.title();
						String content = ContentExtractor.getContentByHtml(doc.html());//提取正文内容
						if(title.indexOf("_")!=-1){
							title=title.substring(0,title.indexOf("_"));
						}
						if(title.indexOf("-")!=-1){
							title=title.substring(0,title.indexOf("-"));
						}	
						mapall=new HashMap<String, Object>();
						mapall.put("id",map.get("id").toString());
						mapall.put("title", title);
						mapall.put("filehtml",filepath);
						mapall.put("htmls", content);
						pathall.add(mapall);
						//uss.setHtmls(content) ;
						//uss.setTitle(title);
						//uss.setId(file.getPath().replace(":",""));
						//uss.setFilehtml(file.getPath());
						System.out.println("提取的文章标题内容-----"+title+"----------");
					} catch (Exception e) {		
						e.printStackTrace();
					}
				}
				System.out.println(file.getPath());//文件完整的路径
				System.out.println(file.getName());//文件的名称	
				System.out.println(pathall.size()+"------------------返回的数据集合");
			}	
		}
		return pathall;//通过list集合批量查询	
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
