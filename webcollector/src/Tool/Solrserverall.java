package Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Solrserverall {
	  private static Document document = null;
	
	public static List<String> getserverurl(){
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
	        }
	} catch (DocumentException e) {
		
		e.printStackTrace();
	}
		//return document.elementByID(solrname).getTextTrim(); 	 	 
	return serverall;
   }      
public static void main(String args[]){
	getserverurl();
	
	
	
}

}
