package grab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.userInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import webcollector.HTMLUtils.HtmlCrawlerBackup;
import SolrConenction.ImportSolr;
import Tool.JDBCUtils;
import Tool.ReadURL;

public class HtmlSerch {
	private final static Log logger = LogFactory.getLog(HtmlSerch.class);
	private String path = "C:/Users/Mick Mo/Desktop/temp/";// 抓取数据存放路径
	private List<userInfo> listall = null;// 抓取数据的集合
	private String urls = null;
//	private String Solrname = "collection1";

	public void Htmlgrab(String solrname) {
		try {
//			if (solrname != null && solrname != "") {
//				Solrname = solrname;
//			}
//			System.out.println(999 + "------------------" + Solrname);
			HtmlCrawlerBackup htmlCrawler = new HtmlCrawlerBackup(path, path);// path+"/crawl"
			ReadURL readurl = new ReadURL();
			List<String> urlall = readurl.readTxtFile();
			if (urlall.size() > 0) {
				for (String url : urlall) {
					if (url.substring(url.length() - 1, url.length()).indexOf(
							"/") == -1) {
						urls = url + "/";
					} else {
						urls = url;
					}
					System.out.println("目标URL:"+url);
					htmlCrawler.addSeed(url);
					htmlCrawler.addRegex(urls + ".*");
				}
			}
			htmlCrawler.setResumable(false);
			htmlCrawler.setThreads(30);
			htmlCrawler.setTopN(20);
			htmlCrawler.start(1);
			htmlCrawler.setExecuteInterval(500);
			
			listall = htmlCrawler.getlistall();
			for (userInfo temp : listall) {
				List<String> worlds = temp.getWORLDS();
				for (String string : worlds) {
					System.out.println(string);
				}
			}
			System.out.println("符合条件的数据 " + htmlCrawler.getlistall().size());
			// SimpleDateFormat df = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss:zz");
			// String time1=df.format(new Date());
			
//			if (htmlCrawler.getlistall().size() > 0) {
//				// Connection conn = jdbcUtils.getConnection();
//				Connection conn = JDBCUtils.getConnection();
//				// JDBCUtils
//				// String
//				// sql="INSERT ignore INTO tb_content (id,title,urls,filehtml)VALUE(?,?,?,?)";
//				String sql1 = "merge  INTO tb_content u using(select ? as id,? as title,? as urls,? filehtml,? as nowdtime from dual) t on "
//						+ "(u.id=t.id) when matched then update set u.nowdtime=t.nowdtime when not matched then insert (id,title,urls,filehtml,nowdtime)values(t.id,t.title,t.urls,t.filehtml,t.nowdtime)";
//				conn.setAutoCommit(false);
//				PreparedStatement ps = conn.prepareStatement(sql1);
//				int count = 0;
//				for (userInfo userinfo : listall) {
//					System.out.println("id的值为---" + userinfo.getId() + "  "
//							+ userinfo.getFilehtml());
//					SimpleDateFormat df = new SimpleDateFormat(
//							"yyyy-MM-dd HH:mm:ss:zz");
//					String times = df.format(new Date());
//					times = times.substring(0, times.lastIndexOf(":"));
//					ps.setString(1, userinfo.getId());
//					ps.setString(2, userinfo.getTitle());
//					ps.setString(3, userinfo.getUrls());
//					ps.setString(4, userinfo.getFilehtml());
//					ps.setString(5, times);
//					ps.addBatch();
//					count++;
//					if (count % 50 == 0) {
//						ps.executeBatch();
//						conn.commit();
//						ps.clearBatch();
//						count = 0;
//					}
//				}
//				ps.executeBatch();
//				conn.commit();
//				ps.clearBatch();
//
////				ImportSolr importSolr = new ImportSolr();
////				// System.out.println("0000000---------------"+listall.size());
////				importSolr.insertBatch(listall, Solrname);
//			}
		} catch (Exception e) {

			logger.error("插入数据错误" + e.getMessage() + e.toString());
		}
	}

}
