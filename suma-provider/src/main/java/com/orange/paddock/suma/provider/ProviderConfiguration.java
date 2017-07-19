package com.orange.paddock.suma.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.orange.paddock.suma.provider.log.NorthGetSubUnsubStatusLogger;
import com.orange.paddock.suma.provider.log.NorthNotificationLogger;
import com.orange.paddock.suma.provider.log.NorthSubscriptionLogger;
import com.orange.paddock.suma.provider.log.NorthUnsubscriptionLogger;

@Configuration
public class ProviderConfiguration {
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NorthSubscriptionLogger northSubscriptionLogger() {
		return new NorthSubscriptionLogger();
	}
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NorthUnsubscriptionLogger northUnsubscriptionLogger() {
		return new NorthUnsubscriptionLogger();
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NorthGetSubUnsubStatusLogger northGetSubUnsubStatusLogger() {
		return new NorthGetSubUnsubStatusLogger();
	}
	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NorthNotificationLogger northNotificationLogger() {
		return new NorthNotificationLogger();
	}
	
}
