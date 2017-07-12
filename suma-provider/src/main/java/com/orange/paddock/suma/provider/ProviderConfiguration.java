package com.orange.paddock.suma.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;

import com.orange.paddock.suma.provider.log.AbstractLogger;
import com.orange.paddock.suma.provider.log.LoggerManager;
import com.orange.paddock.suma.provider.log.NorthGetSubUnsubStatusLogger;
import com.orange.paddock.suma.provider.log.NorthNotificationLogger;
import com.orange.paddock.suma.provider.log.NorthSubscriptionLogger;
import com.orange.paddock.suma.provider.log.NorthUnsubscriptionLogger;

public class ProviderConfiguration {
	
	
	@Bean
	public LoggerManager northLoggerManager(){
		LoggerManager loggerManager = new LoggerManager();
		List<AbstractLogger> loggers = new ArrayList<AbstractLogger>();
		loggers.add(new NorthSubscriptionLogger());
		loggers.add(new NorthUnsubscriptionLogger());
		loggers.add(new NorthGetSubUnsubStatusLogger());
		loggers.add(new NorthNotificationLogger());
		loggerManager.setLoggers(loggers);
		
		return loggerManager;
	}


}
