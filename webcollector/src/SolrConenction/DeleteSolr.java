package SolrConenction;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class DeleteSolr {//删除solr索引

	/**
	 * @param args
	 */
	private static HttpSolrServer solrServer;
	static {
		SolrServer.getInstance();
	    solrServer =SolrServer.getServer("collection1");
	}
	public static void main(String args[]){
		
		Deleteall();
		
	}
	public void DeleteId(String id){
		try {
			solrServer.deleteByQuery("id:"+id);
			solrServer.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static  void Deleteall(){
		try {
			solrServer.deleteByQuery("id:*");
			solrServer.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
