package webcollector.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import webcollector.CarlerUtils.CarlerInitializeUtils;
import webcollector.Crawler.HtmlCrawler;
import webcollector.Entity.HTMLPhasingModel;

public class CrarlerTest {
	private static Vector<String> urlBlackList;
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
    	urlBlackList = new Vector<String>();
    	
    	begin = System.nanoTime();
    	//读取初始化爬虫数据
    	String[] targetURIList = CarlerInitializeUtils.getTargetURIList();
    	for (String string : targetURIList) {
    		urls.add(string);
		}
    	String readBasePath = CarlerInitializeUtils.readBasePath();
    	List<HTMLPhasingModel> readModels = CarlerInitializeUtils.readModels();
    	String[] readKeyWords = CarlerInitializeUtils.readKeyWords();
    	List<String> blackKeyWords = new ArrayList<String>();
    	blackKeyWords.add("google");
    	blackKeyWords.add("facebook");
    	blackKeyWords.add("Twitter");
    	blackKeyWords.add("sina");
    	blackKeyWords.add("qq");
    	blackKeyWords.add("youku");
    	blackKeyWords.add("renren");
    	
    	for (String string : urls) {
    		HtmlCrawler crawler = new HtmlCrawler("crawler", true);
        	crawler.setBasePath(readBasePath);
        	crawler.setHtmlPhasingModels(readModels);
        	crawler.setKeyWords(readKeyWords);
        	crawler.setUrlBlackList(urlBlackList);
        	crawler.setUrlBlackKeyWords(blackKeyWords);
            crawler.addSeed(string);
            crawler.setThreads(50);
            crawler.start(1);//深度
		}
    	
    	
        
        end = System.nanoTime();
        
        
        Double time = (double) TimeUnit.MILLISECONDS.convert(end-begin,TimeUnit.NANOSECONDS)/1000;
		System.out.println("任务耗时:"+time+"s  任务数:"+count+",  平均处理时间:"+time/count+"s");
    }
	
	//CSS+正文处理+图片 爬虫测试
    @Test
    public void crawlerTest() throws Exception {
    	begin = System.nanoTime();
    	
    	HTMLCrawlerTest crawler = new HTMLCrawlerTest("crawler", true);
        String targetUrl ="http://cn.engadget.com/2017/06/23/motorola-moto-e4-hands-on-preview/";
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
