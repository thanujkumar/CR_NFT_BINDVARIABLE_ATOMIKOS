package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppMain {
	
	private static DataSource dataSource;
	private static UserTransaction uTx;
	private static ClassPathXmlApplicationContext context;
	
	public static void main(String[] args) throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		dataSource = context.getBean("dataSource", DataSource.class);
		uTx = context.getBean("userTransaction", UserTransaction.class);
		context.registerShutdownHook();
		
		AppMain main = new AppMain();

		main.testConnection();
		main.testNonBind();
		main.testBind();
		
		System.out.println("+++++++++++++++++++++++Time taken non-bind : "+ totalNonBind +" ms");
		System.out.println("+++++++++++++++++++++++Time taken bind : "+ totalBind +" ms");
		
	}
	
	public void testConnection() throws NotSupportedException, SystemException, SQLException, IllegalStateException, SecurityException, HeuristicMixedException, HeuristicRollbackException, RollbackException {
		uTx.begin();
		Connection con = dataSource.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select TO_CHAR(SYSDATE, 'MM-DD-YYYY HH24:MI:SS') from dual");
		while(rs.next()) {
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++"+rs.getObject(1));
		}
		rs.close();
		stmt.close();
		con.close();
		uTx.commit();
	}
	
	static long totalNonBind, totalBind;
	static int size = 2000;
	
	public void testNonBind() throws Exception{
		uTx.begin();
		Connection con = dataSource.getConnection();
		ResultSet rs;
		long start = System.currentTimeMillis();
		for (int i = 0; i < size ; i++) {
			Statement stmt = con.createStatement();
		    rs = stmt.executeQuery("select count(*) as nonbind from LED_ACCOUNT where FK_EXT_BANK_ID="+"049890976193784678");
		    rs.next();rs.getObject(1);
		    rs.close();
		    stmt.close();
		}
		long end = System.currentTimeMillis();
		con.close();
		uTx.commit();
		totalNonBind = (end-start);
	}
	
	public void testBind() throws Exception{
		uTx.begin();
		Connection con = dataSource.getConnection();
		ResultSet rs;
		long start = System.currentTimeMillis();
		for (int i = 0; i < size ; i++) {
			PreparedStatement pstmt = con.prepareStatement("select count(*) as bind from LED_ACCOUNT where FK_EXT_BANK_ID= ?");
		    pstmt.setString(1, "049890976193784678");
		    rs = pstmt.executeQuery();
		    rs.next();rs.getObject(1);
		    rs.close();
		    pstmt.close();
		}
		long end = System.currentTimeMillis();
		con.close();
		uTx.commit();
		totalBind = (end-start);
	}

}
