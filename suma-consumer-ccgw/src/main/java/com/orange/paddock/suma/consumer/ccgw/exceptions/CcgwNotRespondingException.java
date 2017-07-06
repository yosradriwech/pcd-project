package com.orange.paddock.suma.consumer.ccgw.exceptions;

public class CcgwNotRespondingException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public  CcgwNotRespondingException() {
		super("CCGW timeout exception");
	}
}
