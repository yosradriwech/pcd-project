package com.orange.paddock.suma.business.exception;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class ManagementAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
	 
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementAsyncUncaughtExceptionHandler.class);
    
    @Override
    public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
            LOGGER.error("Exception Async", arg0);
    }
    
}