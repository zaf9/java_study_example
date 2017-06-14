package com.zaf.java_network.socket.example;

import java.util.List;

import com.zaf.java_network.socket.commons.TestUser;

public interface ITestUserService {

	List<TestUser> list(int size);

	TestUser findByName(String name);

	void test();
}
