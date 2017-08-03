package com.orange.paddock.suma.business.factory;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;

public interface IExceptionFactory {

	public void throwException(CcgwClientException e) throws AbstractSumaException;
	
}
