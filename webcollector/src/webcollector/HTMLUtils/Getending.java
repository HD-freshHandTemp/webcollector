package webcollector.HTMLUtils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
//编码探测器，处理无法识别页面的编码

public class Getending{
public String getFileEncoding(String url)  {
    CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance();
    
    codepageDetectorProxy.add(JChardetFacade.getInstance());
    codepageDetectorProxy.add(ASCIIDetector.getInstance());
    codepageDetectorProxy.add(UnicodeDetector.getInstance());
    codepageDetectorProxy.add(new ParsingDetector(false));
    codepageDetectorProxy.add(new ByteOrderMarkDetector());
    String ending=null;
    Charset charset;
	try {
		URL urls=null;
		 urls=new URL(url);
		charset = codepageDetectorProxy.detectCodepage(urls);
		ending=charset.name();
		if(ending.equals("GB2312")){
			ending="GBK";
		}
		if(ending.equals("gb2312")){
			ending="GBK";
		}
		if(ending.equals("Big5")){
			ending="GBK";
		}
		if(ending.equals("EUC-JP")){
			ending="GBK";
		}
		if(ending.equals("windows-1252")){
			ending="UTF-8";
		}
		 //System.out.println(charset.name());
			return ending;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		return "utf-8";
	}
    
    }
}
