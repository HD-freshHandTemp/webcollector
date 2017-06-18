package Tool;

public class TimeTool {
	public int  Timespoor(String tiem1,String tiem2){//用于比较两个时间的大小,返回值为0,-1,1,分别代表等于,小于,大于
		java.text.DateFormat df=new java.text.SimpleDateFormat("HH:mm:ss");   
		java.util.Calendar c1=java.util.Calendar.getInstance();   
		java.util.Calendar c2=java.util.Calendar.getInstance();   
		try  
		{   
		c1.setTime(df.parse(tiem1));   
		c2.setTime(df.parse(tiem2));   
		}catch(java.text.ParseException e){   
		System.err.println("格式不正确");   
		}   
		int result=c1.compareTo(c2);   
		if(result==0)   
		System.out.println("c1相等c2");   
		else if(result<0)   
		System.out.println("c1小于c2");   
		else  
		System.out.println("c1大于c2");  
		return result;	
	}
}
