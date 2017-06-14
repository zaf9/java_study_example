package com.zaf.java_network.socket.example;

import java.util.ArrayList;
import java.util.List;

import com.zaf.java_network.socket.commons.TestUser;

public class TestUserServiceImpl implements ITestUserService {

	@Override
	public List<TestUser> list(int size) {
		List<TestUser> users = new ArrayList<TestUser>();  
        for (int i = 0; i < size; i++) {  
            users.add(new TestUser("user_" + i, "password_" + i));  
        }  
        return users;  
	}

	@Override
	public TestUser findByName(String name) {
		return new TestUser(name, null); 
	}

	@Override
	public void test() {
		// TODO Auto-generated method stub

	}

}
