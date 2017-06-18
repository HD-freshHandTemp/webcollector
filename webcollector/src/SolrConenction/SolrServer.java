package SolrConenction;

import java.io.File;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
public class SolrServer {  
     private static SolrServer  solrServer=null;
     private static HttpSolrServer server=null;
     private static Document document = null;
     private static String url=null;
    
     
     public static synchronized SolrServer getInstance(){
           if(solrServer ==null){
               solrServer=new SolrServer();
          }
           return solrServer ;
     }
     
     public static  HttpSolrServer getServer(String solrname){
           if(server ==null){
              url=getserverurl(solrname);
              server=new HttpSolrServer(url);
              server.setDefaultMaxConnectionsPerHost(1000);
              server.setConnectionTimeout(100000);
              server.setMaxTotalConnections(1000);  
              server.setSoTimeout(1000000);
              server.setFollowRedirects(false);
              server.setAllowCompression(true);
              server.setMaxRetries(1);
          }
           return server ;          
     }   
     
     public static  HttpSolrServer getServerall(String serverurl){//进行索引库更新时,调用此方法
         if(server ==null){
           // url=getserverurl(solrname);
            server=new HttpSolrServer(serverurl);
            server.setDefaultMaxConnectionsPerHost(1000);
            server.setConnectionTimeout(100000);
            server.setMaxTotalConnections(1000);  
            server.setSoTimeout(1000000);
            server.setFollowRedirects(false);
            server.setAllowCompression(true);
            server.setMaxRetries(1);
        }
         return server ;          
   }   
     
     
     public static  String getserverurl(String solrname){
    	 SAXReader reader = new SAXReader();  
  	   try {
		document = reader.read(new File("src/solrserver.xml"));
	} catch (DocumentException e) {
		
		e.printStackTrace();
	}
		return document.elementByID(solrname).getTextTrim(); 	 	 
    }      
}
