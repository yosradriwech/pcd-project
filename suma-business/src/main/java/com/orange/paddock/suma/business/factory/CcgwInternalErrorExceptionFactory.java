package com.orange.paddock.suma.business.factory;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwInternalErrorException;

public class CcgwInternalErrorExceptionFactory implements IExceptionFactory {

	@Override
	public void throwException() throws AbstractSumaException {
		throw new SumaCcgwInternalErrorException();
	}

}
