package com.orange.paddock.suma.business.factory;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;

public class CcgwIntegrationErrorExceptionFactory implements IExceptionFactory {

	@Override
	public void throwException() throws AbstractSumaException {
		throw new SumaCcgwIntegrationErrorException();		
	}

}
