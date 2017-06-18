package Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import SolrConenction.ImportSolr;
import SolrConenction.SolrServer;

import model.userInfo;

public class Solrlisteners {
private static Document document = null;
static  List<userInfo> solrall=null;
public static void  MyTask(String solrurl){
	System.out.println("---------------------服务地址"+solrurl);
	/*  String end ="21:00:00";//定时器结束的时间范围
      String start="6:00:00";//定时器开始的时间范围
     // String solrname="collection1";
      List<userInfo> solrall=null;
      SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:zz");
    		String  time=df.format(new Date());
    		TimeTool timetool=new TimeTool();
    	*/
	        List<userInfo> solrall=new ArrayList<userInfo>();
	        solrall.addAll(LIKESerch(solrurl));
    		//solrall= LIKESerch(solrname);
    		if(solrall.size()>0){
    		int page=(solrall.get(0).getNumall()-1)/2000+1;
    		System.out.println(page);
    		if(page>1){
    			for(int i=1;i<=page;i++){
    			 solrall.addAll(LIKESerch(solrurl,i));
    			 LIKESerch(solrurl,i);
    			}
    		}
    	}
    			// System.out.println("solor中的数据--------------"+solrall.size()+"---");
    	//if(solrall.size()>0){
    	Readfile  RDF=new Readfile();//
    	ImportSolr  importsolr=new  ImportSolr();//定时更新solr的索引库
    	importsolr.insertBatch(RDF.fileall(solrall),solrurl);
    				
    			//System.out.println("执行查询操作结束----------" + (new Date()));	 
  }

  public static  List<userInfo>  LIKESerch(String solrurl){
		List<userInfo> listall= null;
	    userInfo user= null;
	    SolrServer.getInstance ();
		HttpSolrServer server=SolrServer.getServerall(solrurl);
	
	   SolrQuery sQuery= new SolrQuery();
	     try {	
	    sQuery.setQuery("*:*"); 
	    sQuery.setRows(2000);
	   QueryResponse response;
	   response = server.query(sQuery); 
	   SolrDocumentList slist=response.getResults();
	   System.out.println("---------------查询的记录数"+slist.size());
	  // List<userInfo> LIST=response.getBeans(userInfo.class);
	   //System.out.println(LIST.size()+"**********************LIST");
	 int num=(int) slist.getNumFound(); 
	 if(num>0){
		  listall=new ArrayList<userInfo>();
	   for (SolrDocument solrDocument : slist) {
	    user= new userInfo();
	      user.setUrls(solrDocument.getFirstValue("urls").toString());
	     user.setId(solrDocument.getFirstValue("id").toString());
	      user.setHtmls(solrDocument.getFirstValue("htmls").toString());
	      user.setTitle(solrDocument.getFirstValue("title").toString());
	     user.setFilehtml(solrDocument.getFirstValue("filehtml").toString());
	    user.setNumall(num);
	    listall.add(user); 
	   // System.out.println(listall.size()+"-----"+num+"-------------------------listserch");
	    }
	 }
	    } catch (SolrServerException e) {
	        e.printStackTrace();
	    }
	      return   listall;
	} 
  
  public static  List<userInfo>  LIKESerch(String solrurl,int page){
		List<userInfo> listall= null;
	    userInfo user= null;
	    SolrServer.getInstance ();
	    int start=(page-1)*1000;
	    int end=page*1000;
		HttpSolrServer server=SolrServer.getServerall(solrurl);
	    SolrQuery sQuery= new SolrQuery();
	     try {	
	    sQuery.setQuery("*:*"); 
	    sQuery.setStart(start);
	    sQuery.setStart(end);
	   QueryResponse response;
	   response = server.query(sQuery); 
	   SolrDocumentList slist=response.getResults();
	   System.out.println("---------------查询的记录数"+slist.size());
	  // List<userInfo> LIST=response.getBeans(userInfo.class);
	   //System.out.println(LIST.size()+"**********************LIST");
	 int num=(int) slist.getNumFound(); 
	 if(num>0){
		  listall=new ArrayList<userInfo>();
	   for (SolrDocument solrDocument : slist) {
	    user= new userInfo();
	      user.setUrls(solrDocument.getFirstValue("urls").toString());
	     user.setId(solrDocument.getFirstValue("id").toString());
	      user.setHtmls(solrDocument.getFirstValue("htmls").toString());
	      user.setTitle(solrDocument.getFirstValue("title").toString());
	     user.setFilehtml(solrDocument.getFirstValue("filehtml").toString());
	    user.setNumall(num);
	    listall.add(user); 
	   // System.out.println(listall.size()+"-----"+num+"-------------------------listserch");
	    }
	 }
	    } catch (SolrServerException e) {
	        e.printStackTrace();
	    }
	      return   listall;
	} 
  
  public static List<String> getserverurl(){//获取所有solr服务的url
		List<String>  serverall=null;
 	 SAXReader reader = new SAXReader();  
	   try {
		document = reader.read(new File("src/solrserver.xml"));
		Element  root = document.getRootElement();
		serverall=new ArrayList<String>();
		 for (Iterator<?> iter = root.elementIterator(); iter.hasNext();)
	        {
	            Element e = (Element) iter.next();
	            serverall.add(e.getTextTrim());
	            System.out.println(e.getTextTrim());
	        }
	} catch (DocumentException e) {
		
		e.printStackTrace();
	} 
	return serverall;
 }      
  
	public static void main(String[] args) {
		List<String> serverall=null;
	   serverall=getserverurl();
		for(String url:serverall)
			MyTask(url);	
	
	}
}
