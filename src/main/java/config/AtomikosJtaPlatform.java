package config;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.beans.factory.DisposableBean;

import com.atomikos.icatch.jta.UserTransactionManager;

public class AtomikosJtaPlatform extends AbstractJtaPlatform implements DisposableBean {

	private static final long serialVersionUID = 1L;

	static TransactionManager transactionManager;
	static UserTransaction transaction;

	public AtomikosJtaPlatform() throws SystemException {
		UserTransactionManager usTx = new  UserTransactionManager();
		transaction = (UserTransaction) (transactionManager = usTx);
	}

	protected TransactionManager locateTransactionManager() {
		return transactionManager;
	}

	protected UserTransaction locateUserTransaction() {
		return transaction;
	}

	public void destroy() throws Exception {
		((UserTransactionManager)transaction).close();
	}

}