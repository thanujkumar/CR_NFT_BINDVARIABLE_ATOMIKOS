package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppMainNoAtomikos {


	private static DataSource dataSource;
	private static ClassPathXmlApplicationContext context;
	
	public static void main(String[] args) throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContextNoAtomikos-original.xml");
		dataSource = context.getBean("dataSource", DataSource.class);
		context.registerShutdownHook();
		
		AppMainNoAtomikos main = new AppMainNoAtomikos();
        Random rand = new Random();
		
        List<Connection> listCon = new ArrayList<>();
		
		new Thread() {

			public void run() {
				for (int i = 0; i < 50; i++) {
					
					Connection leaked = null;
					try {
						Thread.sleep(1000);
						leaked = main.testConnection(rand.nextBoolean());
					} catch (IllegalStateException | SecurityException | NotSupportedException | SystemException
							| SQLException | HeuristicMixedException | HeuristicRollbackException
							| RollbackException | InterruptedException e) {
						e.printStackTrace();
					}
					if (leaked != null) {
						listCon.add(leaked);
					}
					System.out.printf("Leaked (%d) -> %s ", listCon.size(), leaked);
				}
				synchronized (AppMainNoAtomikos.class) {
					AppMainNoAtomikos.class.notify();
				}
				
			}
		}.start();
		
		synchronized(AppMainNoAtomikos.class) {
			AppMainNoAtomikos.class.wait();
		}
		
		
		System.out.println("==============TOTAL LEAKED==================:-"+listCon.size());
		for (Connection con : listCon) {
			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//To Kill
		synchronized(Thread.currentThread()) {
			   Thread.currentThread().wait();
		}
		
	}
	
	public Connection testConnection(boolean leak) throws NotSupportedException, SystemException, SQLException, IllegalStateException, SecurityException, HeuristicMixedException, HeuristicRollbackException, RollbackException {
		Connection con = dataSource.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select TO_CHAR(SYSDATE, 'MM-DD-YYYY HH24:MI:SS') from dual");
		while(rs.next()) {
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++"+rs.getObject(1));
		}
		rs.close();
		stmt.close();
		
		if (leak) {
			//do nothing
		} else {
		  con.close();
		  con = null;
		}
		
		return con;
	}
}
