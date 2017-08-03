package com.orange.paddock.suma.business.factory;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;

public class CcgwIntegrationErrorExceptionFactory implements IExceptionFactory {

	@Override
	public void throwException(CcgwClientException e) throws AbstractSumaException {
		throw new SumaCcgwIntegrationErrorException(e);		
	}

}
