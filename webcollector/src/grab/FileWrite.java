package grab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileWrite {//将抓取的信息写入本地磁盘
	 public void bufferedWrite(String filpath,String filename,String str) {
		 File f2 = new File(filpath);
		 File f = new File(filpath.substring(0,filpath.lastIndexOf("/")));
		  try {
			  if(!f2.exists()){
				   f.mkdirs();
		}
			  OutputStreamWriter writer = null;
			  BufferedWriter bw = null;
		   OutputStream os = new FileOutputStream(f2);
		   writer = new OutputStreamWriter(os);
		   bw = new BufferedWriter(writer);
		   //for (int i = 0; i <3; i++) {
		    bw.write(str);
		 //  }
		   bw.flush();
		   bw.close();
		   writer.close(); 
		   os.close();
		  }  catch (Exception e) {
		   e.printStackTrace();
		   System.out.println(e.getMessage());
		  } 
		  }
		 }


