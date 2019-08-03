package main;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.stream.IntStream;

import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppMainAAN {
	
	private static DataSource dataSource;
	private static UserTransaction uTx;
	private static ClassPathXmlApplicationContext context;

	public static void main(String[] args) throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContext_AAN.xml");
		dataSource = context.getBean("dataSource", DataSource.class);
		uTx = context.getBean("userTransaction", UserTransaction.class);
		context.registerShutdownHook();
		
		AppMainAAN main = new AppMainAAN();

		main.testConnection();
		
		IntStream i = IntStream.range(0,10);
		i.parallel().forEach(x ->  {
			try {
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> "+main.getAANNumber("049890975415975598"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		});

//		System.out.println("++++++++++++++++++++++++"+main.getAANNumber());
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
	
	
	public String getAANNumber(String orgId) throws Exception{
		uTx.begin();
		Connection con = dataSource.getConnection();
		CallableStatement cStmt = con.prepareCall("{call PROC_RESERVE_ACCOUNT_NUMBER(?,?)}");
		cStmt.setString(1, orgId);
		cStmt.registerOutParameter(2, Types.NVARCHAR);
		
		boolean hasResult = cStmt.execute();
		String acctNo = null;

	    acctNo = cStmt.getString(2);	
		
		cStmt.close();
		con.close();
		uTx.rollback();
		
		return acctNo;
		
	}
}
