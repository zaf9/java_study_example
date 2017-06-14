package com.zaf.java_network.socket.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class UserSocketApplication {

	private static final Logger logger = LoggerFactory.getLogger(UserSocketApplication.class);

	private static int port = 9999;

	public static void main(String[] args) throws Exception {
		IUserSocketServer myServer = new UserSocketServerImpl(port);
		myServer.startup();
		Thread.sleep(3000);

		IUserSocketClient myClient = new UserSocketClientImpl("localhost", port);

		IUserRequest request = null;
		IUserResponse response = null;

		request = new GenericUserRequestImpl(TestUserServiceImpl.class, "list", new Class<?>[] { int.class },
				new Object[] { 2 });
		response = myClient.execute(request);
		logger.info("first response result: {}", response.getResult());
		List<TestUser> users = myClient.execute(request,
				new GenericUserResponseHandlerImpl<List<TestUser>>());
		logger.info("first users: {}", users);

		request = new GenericUserRequestImpl(TestUserServiceImpl.class, "findByName", new Class<?>[] { String.class },
				new Object[] { "kongxx" });
		response = myClient.execute(request);
		logger.info("second response result: {}", response.getResult());
		TestUser user = myClient.execute(request, new GenericUserResponseHandlerImpl<TestUser>());
		logger.info("second user: {}", user);

		response = myClient.execute(
				new GenericUserRequestImpl(TestUserServiceImpl.class, "test", new Class<?>[] {}, new Object[] {}));
		logger.info("third response result: {}", response.getResult());
	}

}
