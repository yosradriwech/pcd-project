package com.orange.paddock.suma.business.factory;

import java.util.Objects;

import com.orange.paddock.suma.business.exception.AbstractSumaException;
import com.orange.paddock.suma.business.exception.ccgw.SumaCcgwIntegrationErrorException;
import com.orange.paddock.suma.consumer.ccgw.exceptions.CcgwClientException;

public class CcgwIntegrationErrorExceptionFactory implements IExceptionFactory {

	@Override
	public void throwException(CcgwClientException e) throws AbstractSumaException {

		if (Objects.isNull(e.getCcgwFaultStatusErrorParams()) || e.getCcgwFaultStatusErrorParams().size() < 0) {
			throw new SumaCcgwIntegrationErrorException("Integration fault");
		} else
			throw new SumaCcgwIntegrationErrorException(String.join("-", e.getCcgwFaultStatusErrorParams()));
	}

}
