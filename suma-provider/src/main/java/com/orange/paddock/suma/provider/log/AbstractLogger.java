package com.orange.paddock.suma.provider.log;

public abstract class AbstractLogger {
	
	protected static final String NORTH_SUBSCRIPTION_LOGGER = "north-subscription-logger";
	protected static final String NORTH_UNSUBSCRIPTION_LOGGER = "north-unsubscription-logger";
	protected static final String NORTH_GET_STATUS_LOGGER = "north-get-status-logger";
	protected static final String NORTH_NOTIFICATION_LOGGER = "north-notification-logger";
	
	public abstract void write(LogFields logFields);
	
}
