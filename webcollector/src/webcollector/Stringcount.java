package webcollector;

public class Stringcount {
	 public  int searchCount(String shortStr, String longStr) {
         // 定义一个count来存放字符串出现的次数
         int count = 0;
         // 调用String类的indexOf(String str)方法，返回第一个相同字符串出现的下标
         while (longStr.indexOf(shortStr) != -1) {
             // 如果存在相同字符串则次数加1
             count++;
             // 调用String类的substring(int beginIndex)方法，获得第一个相同字符出现后的字符串
             longStr = longStr.substring(longStr.indexOf(shortStr)
                     + shortStr.length());
         }
         // 返回次数
         return count;
     }
 

}
