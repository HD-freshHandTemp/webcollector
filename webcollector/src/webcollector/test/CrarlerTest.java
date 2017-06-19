package webcollector.test;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.junit.Test;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import webcollector.HtmlCrawler;
import webcollector.HtmlCrawlerBackup;

public class CrarlerTest {
	//自定义Logger
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);
	Long begin;//任务-开始时间
	Long end;//任务-结束时间
	int count = 1;//任务-次数
	
	//爬虫基本测试
    @Test
    public void crawlerTest() throws Exception {
    	begin = System.nanoTime();
    	
    	HtmlCrawlerTest crawler = new HtmlCrawlerTest("crawler", true);
        String targetUrl ="http://www.binzz.com/wenzhang/60224.html";
        logger.info("开始获取资源 From:"+targetUrl);
        crawler.addSeed(targetUrl);
        crawler.setThreads(30);
        crawler.start(2);

        end = System.nanoTime();
       
        
        Double time = (double) TimeUnit.MILLISECONDS.convert(end-begin,TimeUnit.NANOSECONDS)/1000;
		System.out.println("任务耗时:"+time+"s  任务数:"+count+",  平均处理时间:"+time/count+"s");
    }
    

    //CSS+正文处理+图片 爬虫测试
    @Test
    public void crawlerTest2() throws Exception {
    	
    	begin = System.nanoTime();
    	
    	HtmlCrawler crawler = new HtmlCrawler("crawler", true);
        String targetUrl ="http://www.binzz.com/wenzhang/60224.html";
        logger.info("开始获取资源 From:"+targetUrl);
        crawler.addSeed(targetUrl);
//        crawler.addRegex("http://blog.csdn.net/.*/article/details/.*");

        /*可以设置每个线程visit的间隔，这里是毫秒*/
//        crawler.setVisitInterval(1000);
        /*可以设置http请求重试的间隔，这里是毫秒*/
//        crawler.setRetryInterval(1000);

        crawler.setThreads(30);
        crawler.start(2);
        
        end = System.nanoTime();
        
        Double time = (double) TimeUnit.MILLISECONDS.convert(end-begin,TimeUnit.NANOSECONDS)/1000;
		System.out.println("任务耗时:"+time+"s  任务数:"+count+",  平均处理时间:"+time/count+"s");
    }
}
