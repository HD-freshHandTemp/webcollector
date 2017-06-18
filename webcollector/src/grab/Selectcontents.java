package grab;

import java.util.ArrayList;
import java.util.List;
import model.userInfo;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import SolrConenction.SolrServer;

import com.google.gson.Gson;

public class Selectcontents {//查询文章内容的方法
	private int pagesize=6;
	public   String   Select(String wd,String page0,String solrname){
		 //String wd=request.getParameter("wd");
		  int page=1;
		//  String page0=request.getParameter("pageNum");
		if(page0!=null&&page0!=""){
	       page=Integer.valueOf(page0);
		}
	       System.out.println("现在开始模糊查询");
	  	  List<userInfo> urlall=null;
	        urlall=new ArrayList<userInfo>();
	        urlall=LIKESerch1(wd,page,solrname);
	        Gson gson=new Gson();
	       String urlserch=gson.toJson(urlall);
	     return urlserch;	
	}
	public  List<userInfo>  LIKESerch1(String world,int page,String solrname){
			List<userInfo> listall= null;
		    userInfo user= null;
		    SolrServer.getInstance ();
			HttpSolrServer server=SolrServer.getServer(solrname);
		    SolrQuery sQuery= new SolrQuery();
		    List<String> worlds=null;
		    worlds=new ArrayList<String>();
		    worlds.add(world);	 
		   int startpage=(page-1)*pagesize;
		  // System.out.println(startpage+"------------------------------------------666666666666666");
		   String para="htmls:"+"\""+world+"\"";
		   sQuery.setQuery(para);
		   sQuery.setStart(startpage);//起始的数据位置
		   sQuery.setRows(pagesize);
		   QueryResponse response;
			try {
				response = server.query(sQuery);
				SolrDocumentList slist=response.getResults();
				   System.out.println("---------------查询的记录数");
				 int num=(int) slist.getNumFound(); 
				 if(num>0){
					  listall=new ArrayList<userInfo>();
				   for (SolrDocument solrDocument : slist) {
				    user= new userInfo();
				     user.setUrls(solrDocument.getFirstValue("urls").toString());
				     user.setId(solrDocument.getFirstValue("id").toString());
				     user.setHtmls(solrDocument.getFirstValue("htmls").toString());
				     user.setTitle(solrDocument.getFirstValue("title").toString());
			         user.setWORLDS(worlds);
				     user.setNumall(num);
				    user.setPagesize(pagesize);				  
				    listall.add(user); 
				    System.out.println(listall.size()+"-----"+num+"-------------------------listserch");
				    }
				 }			   
			} catch (SolrServerException e) {			
				e.printStackTrace();
			}	
			 return   listall;    		 
	}  
}
