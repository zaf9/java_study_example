package com.zaf.java_network.socket.example;

public class GenericUserResponseHandlerImpl<T> implements IUserReponseHandler<T> {

	@SuppressWarnings("unchecked")
	@Override
	public T handle(IUserResponse response) {
		return (T)response.getResult();
	}

}
