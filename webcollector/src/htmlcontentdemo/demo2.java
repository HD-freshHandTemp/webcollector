package htmlcontentdemo;

import grab.FileWrite;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;

public class demo2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Element contentElement = null;
		String content=null;
		System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");//			处理网页证书问题
        HostnameVerifier hv = new HostnameVerifier() {
             public boolean verify(String urlHostName, SSLSession session) {
              return urlHostName.equals(session.getPeerHost());
             }	
        };
       
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
		try {
			String url="http://www.szwj72.cn/Article/hsyy/201706/2333.html";
			contentElement = ContentExtractor.getContentElementByUrl(url);
			//System.out.println(contentElement.parentNode().attr("id")+"----------------------正文节点"+contentElement.parentNode().attr("class")+"----");
			//System.out.println(contentElement.parentNode().ownerDocument().html());
		//	contentElement.nextSibling().nodeName();
		//	System.out.println(contentElement.nextElementSibling());
			//System.out.println(contentElement.parents().get(3).className());
			
			//System.out.println(contentElement.equals(contentElement));
			//System.out.println(contentElement.parentNode().outerHtml());
	   String  contentall=contentElement.parentNode().outerHtml();
	   String  contents=contentElement.outerHtml();
	   //String index=contents.substring(contents.length()-10,contents.length());
	  // System.out.println(index);
	 //  System.out.println(contentElement.outerHtml());
		//System.out.println(contentElement.parentNode().toString());
		Element doc = Jsoup.connect("http://www.binzz.com/ganwu/60660.html").get().head();
		//Elements imports = doc.select("link[href]");
	/*	for(Element ement:imports){
			System.out.println(ement.attr("abs:href"));
			String ks=ement.attr("abs:href");
			System.out.println(ks.substring(ks.lastIndexOf("/")+1,ks.length()));
			System.out.println(ks.substring(0,ks.lastIndexOf("/")+1));
			
		}*/
            
		
		//String king=contentall.replaceAll("<a>", "<br><a>");
		
			//System.out.println(contentall.indexOf(index));
			//System.out.println(contentall.substring(0, contentall.indexOf(index)+10));
			//System.out.println(contentElement.siblingIndex());
	          System.out.println(contentElement.parentNode().outerHtml());
	        String contenta=contentElement.parents().html();
	        // File  filepateh=new File("E:/JKL/V/X.html");
	          FileWrite filewrit=new FileWrite();
	          filewrit.bufferedWrite("C:/Users/Mick Mo/Desktop/temp/"+"spiderTest"+new Date().getTime()+".html", "spiderTest"+new Date().getTime()+".html",contenta);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//正文节点
		
	}

}
