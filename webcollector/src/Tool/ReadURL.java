package Tool;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/*
 * 读取抓取的网址
 * */
public class ReadURL {
  static List<String> urlall=null;
  static String filePath = "url/url.txt";
    public List<String> readTxtFile(){
        try {  urlall=new ArrayList<String>();
                String encoding="GBK";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                     urlall.add(lineTxt);                 
                  }
                    System.out.println(urlall.size());
                    read.close();
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
     return urlall;
    }
}