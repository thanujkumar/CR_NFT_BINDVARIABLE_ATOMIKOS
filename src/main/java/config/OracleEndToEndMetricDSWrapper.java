package config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class OracleEndToEndMetricDSWrapper extends DelegatingDataSource
		implements ApplicationContextAware, InitializingBean, ApplicationListener<ContextStartedEvent> {

	@Override
	public void onApplicationEvent(ContextStartedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		
	}

}
