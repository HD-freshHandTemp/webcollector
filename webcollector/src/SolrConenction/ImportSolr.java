package SolrConenction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import model.userInfo;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

	public class ImportSolr {//导入数据到solr索引库
	    private static Logger log = Logger.getLogger(ImportSolr.class);

	    private static HttpSolrServer solrServer;
	    public void insertBatch(List<userInfo> dataList,String solrname) {
	        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	     
	       try {
	    	   SolrServer.getInstance();
		    	solrServer =SolrServer.getServer(solrname);
	        for (userInfo data : dataList) {
	            
	            
	            SolrInputDocument doc = new SolrInputDocument();
	            doc.addField("id",data.getId());
	            doc.addField("title",data.getTitle());
	            doc.addField("urls",data.getUrls());
	            doc.addField("htmls",data.getHtmls());
	            doc.addField("filehtml",data.getFilehtml());
	           // doc.addField("nowdtime", data.getNowdtime());
	            docs.add(doc);
	            if(docs.size()==500){
	            	solrServer.add(docs);
					solrServer.commit();
					//solrServer.optimize();
					docs.clear();
	            }
	        }
					solrServer.add(docs);
					solrServer.commit();
					//solrServer.optimize();
					docs.clear();
					
	            }catch (Exception e) {
					 log.error("向solr批量添加文档时遇到错误", e);
				}
	            
	           
	       
	    }
	 
	                                                                           
	}