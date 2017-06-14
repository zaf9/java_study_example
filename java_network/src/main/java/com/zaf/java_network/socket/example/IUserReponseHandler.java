package com.zaf.java_network.socket.example;

public interface IUserReponseHandler<T> {

	T handle(IUserResponse response);
}
