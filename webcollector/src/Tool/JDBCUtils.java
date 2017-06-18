package Tool;

import java.sql.Connection;  
import java.sql.SQLException;  
  
import javax.sql.DataSource;  
  
import com.mchange.v2.c3p0.ComboPooledDataSource;  
/** 
 * 注意必须使用c3p0-config.xml的配置方式。将c3p0-config.xml文件放在src根目录下。 
 * 默认配置。 
 *  
 * */  
public class JDBCUtils {  
      
    private static ComboPooledDataSource ds=new ComboPooledDataSource();  
    private static ThreadLocal<Connection> tl=new ThreadLocal<Connection>();  
    /*使用连接池返回一个连接对象 */  
    public static Connection getConnection() throws SQLException{  
        Connection conn=tl.get();  
        if(conn!=null)  
            return conn;  
        else  
            return ds.getConnection();  
    }  
      
      
    /*返回一个连接池对象*/  
    public static DataSource getDataSource(){  
        return ds;  
    }  
        
    /*为conn赋值，并开启事务*/  
    public static void beginTransaction() throws SQLException{  
        Connection conn=tl.get();  
        if(conn!=null)   
            throw new RuntimeException("已经开启事务");  
        conn=getConnection();  
        conn.setAutoCommit(false);  
        tl.set(conn);  
    }  
      
    public static void commitTransaction() throws SQLException{  
        Connection conn=tl.get();  
        if(conn==null)  
            throw new SQLException("还没有开启事务，不能提交");  
        conn.commit();  
        conn.close();  
        tl.remove();  
    }  
      
    public static void rollBackTransaction() throws SQLException{  
        Connection conn=tl.get();  
        if(conn==null)  
            throw new SQLException("还没有开启事务，不能回滚");  
        conn.rollback();  
        conn.close();  
        tl.remove();  
    }  
      
    public static void releaseConnection(Connection connection) throws SQLException{  
        Connection conn=tl.get();  
        if(conn==null)//说明没有开启事务，没有使用事务专用连接  
            connection.close();  
        if(conn!=connection){//虽然有事务专用连接，但是事务连接和传递过来的参数连接不是同一个连接，所以关闭connection连接  
            connection.close();  
        }  
    }  
      
      
}  