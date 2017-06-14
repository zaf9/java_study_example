package com.zaf.java_network.socket.example;

public class GenericUserRequestImpl implements IUserRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Class<?> requestClass;
	private String requestMethod;
	private Class<?>[] requestParameterTypes;
	private Object[] requestParameterValues;

	public GenericUserRequestImpl(Class<?> requestClass, String requestMethod, Class<?>[] requestParameterTypes,
			Object[] requestParameterValues) {
		super();
		this.requestClass = requestClass;
		this.requestMethod = requestMethod;
		this.requestParameterTypes = requestParameterTypes;
		this.requestParameterValues = requestParameterValues;
	}

	@Override
	public Class<?> getRequestClass() {
		return requestClass;
	}

	@Override
	public String getRequestMethod() {
		return requestMethod;
	}

	@Override
	public Class<?>[] getRequestParameterTypes() {
		return requestParameterTypes;
	}

	@Override
	public Object[] getRequestParameterValues() {
		return requestParameterValues;
	}

}
