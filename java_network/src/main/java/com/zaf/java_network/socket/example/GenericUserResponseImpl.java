package com.zaf.java_network.socket.example;

public class GenericUserResponseImpl implements IUserResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object obj = null;

	public GenericUserResponseImpl(Object obj) {
		super();
		this.obj = obj;
	}

	@Override
	public Object getResult() {
		return this.obj;
	}

}
