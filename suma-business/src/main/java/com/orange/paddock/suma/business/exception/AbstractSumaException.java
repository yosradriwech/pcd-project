package com.orange.paddock.suma.business.exception;

import java.util.ArrayList;
import java.util.List;

public class AbstractSumaException extends Exception{
	
	public AbstractSumaException(String message) {
		super(message);
	}
	private static final long serialVersionUID = 3992409985427085325L;
	
	protected static final int HTTP_CODE_BAD_REQUEST = 400;
	
	protected String internalErrorCode;
	protected String description;
	protected String oneApiErrorCode;
	protected String oneApiErrorDescription;
	protected List<String> oneApiErrorVariables = new ArrayList<String>();
	protected int httpCode;

}
