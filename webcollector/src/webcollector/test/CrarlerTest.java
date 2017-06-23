package webcollector.test;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import webcollector.CarlerUtils.CarlerInitializeUtils;
import webcollector.Crawler.HtmlCrawler;

public class CrarlerTest {
	//自定义Logger
	private final static Log logger = LogFactory.getLog(CrarlerTest.class);
	Long begin;//任务-开始时间
	Long end;//任务-结束时间
	int count = 1;//任务-次数
	
	
	//多线程高级爬虫测试
    @Test
    public void crawlerTest3() throws Exception {
//    	int threadCount = 10;
    	Vector<String> urls = new Vector<String>();
    	begin = System.nanoTime();
    	

    	HtmlCrawler crawler = new HtmlCrawler("crawler", true);
    	//初始化爬虫数据
    	crawler.setBasePath(CarlerInitializeUtils.readBasePath());
    	
    	String[] targetURIList = CarlerInitializeUtils.getTargetURIList();
    	for (String string : targetURIList) {
    		urls.add(string);
		}
    	crawler.setURLs(urls);
    	
    	crawler.setHtmlPhasingModels(CarlerInitializeUtils.readModels());
    	crawler.setKeyWords(CarlerInitializeUtils.readKeyWords());
    	

        crawler.addSeed(urls.get(0));

        crawler.setThreads(30);
        crawler.start(1);//?
        
        end = System.nanoTime();
        
        
        Double time = (double) TimeUnit.MILLISECONDS.convert(end-begin,TimeUnit.NANOSECONDS)/1000;
		System.out.println("任务耗时:"+time+"s  任务数:"+count+",  平均处理时间:"+time/count+"s");
    }
	
	//CSS+正文处理+图片 爬虫测试
    @Test
    public void crawlerTest() throws Exception {
    	begin = System.nanoTime();
    	
    	HTMLCrawlerTest crawler = new HTMLCrawlerTest("crawler", true);
        String targetUrl ="http://www.binzz.com/wenzhang/60224.html";
        logger.info("开始获取资源 From:"+targetUrl);
        crawler.addSeed(targetUrl);
        crawler.setThreads(30);
        crawler.start(2);

        end = System.nanoTime();
       
        
        Double time = (double) TimeUnit.MILLISECONDS.convert(end-begin,TimeUnit.NANOSECONDS)/1000;
		System.out.println("任务耗时:"+time+"s  任务数:"+count+",  平均处理时间:"+time/count+"s");
    }
    

   //爬虫基本测试
    @Test
    public void crawlerTest2() throws Exception {
    	
    	begin = System.nanoTime();
    	
    	HTMLCrawlerTest crawler = new HTMLCrawlerTest("crawler", true);
        String targetUrl ="http://www.binzz.com/wenzhang/60224.html";
        logger.info("开始获取资源 From:"+targetUrl);
        crawler.addSeed(targetUrl);

        crawler.setThreads(30);
        crawler.start(2);
        
        end = System.nanoTime();
        
        Double time = (double) TimeUnit.MILLISECONDS.convert(end-begin,TimeUnit.NANOSECONDS)/1000;
		System.out.println("任务耗时:"+time+"s  任务数:"+count+",  平均处理时间:"+time/count+"s");
    }
}
