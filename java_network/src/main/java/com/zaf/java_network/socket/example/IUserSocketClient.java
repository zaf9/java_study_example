package com.zaf.java_network.socket.example;

public interface IUserSocketClient {

	public <T> T execute(IUserRequest request, IUserReponseHandler<T> handler);

	public IUserResponse execute(IUserRequest request);
}
