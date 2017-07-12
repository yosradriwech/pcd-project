package com.orange.paddock.suma.business.factory;

import com.orange.paddock.suma.business.exception.AbstractSumaException;

public interface IExceptionFactory {

	public void throwException() throws AbstractSumaException;
	
}
