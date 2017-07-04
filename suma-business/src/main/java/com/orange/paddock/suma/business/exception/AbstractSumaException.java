package com.orange.paddock.suma.business.exception;

public abstract class AbstractSumaException extends Exception {

	private static final long serialVersionUID = 1L;

	// Internal ERROR Codes
	public static final String INTERNAL_SUMA_OK_CODE = "PDK_SUMA_OK";
	public static final String INTERNAL_SUMA_0001_CODE = "PDK_SUMA_0001";
	public static final String INTERNAL_SUMA_0002_CODE = "PDK_SUMA_0002";
	public static final String INTERNAL_SUMA_0003_CODE = "PDK_SUMA_0003";
	public static final String INTERNAL_SUMA_0004_CODE = "PDK_SUMA_0004";
	public static final String INTERNAL_SUMA_0005_CODE = "PDK_SUMA_0005";
	public static final String INTERNAL_SUMA_0006_CODE = "PDK_SUMA_0006";
	public static final String INTERNAL_SUMA_0007_CODE = "PDK_SUMA_0007";
	public static final String INTERNAL_SUMA_0100_CODE = "PDK_SUMA_0100";
	public static final String INTERNAL_SUMA_1000_CODE = "PDK_SUMA_1000";
	public static final String INTERNAL_SUMA_1001_CODE = "PDK_SUMA_1001";
	public static final String INTERNAL_SUMA_1002_CODE = "PDK_SUMA_1002";
	public static final String INTERNAL_SUMA_2000_CODE = "PDK_SUMA_2000";
	public static final String INTERNAL_SUMA_2001_CODE = "PDK_SUMA_2001";
	public static final String INTERNAL_SUMA_3000_CODE = "PDK_SUMA_3000";
	public static final String INTERNAL_SUMA_3001_CODE = "PDK_SUMA_3001";
	public static final String INTERNAL_SUMA_3002_CODE = "PDK_SUMA_3002";

	// Output ERROR Codes
	public static final String SUMA_ERROR_CODE_00001 = "00001";
	public static final String SUMA_ERROR_CODE_00002 = "00002";
	public static final String SUMA_ERROR_CODE_00003 = "00003";
	public static final String SUMA_ERROR_CODE_00004 = "00004";

	protected String internalErrorCode;
	protected String errorCode;
	protected String errorDescription;
	protected int httpStatusCode;

	public AbstractSumaException() {
	}

	public AbstractSumaException(String message) {
		super(message);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getInternalErrorCode() {
		return internalErrorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

}
