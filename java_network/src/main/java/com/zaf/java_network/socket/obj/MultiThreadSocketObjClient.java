package com.zaf.java_network.socket.obj;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class MultiThreadSocketObjClient {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketObjClient.class);

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 100; i++) {
			Socket socket = null;
			ObjectOutputStream os = null;
			ObjectInputStream is = null;

			try {
				socket = new Socket("localhost", 9999);

				os = new ObjectOutputStream(socket.getOutputStream());
				TestUser user = new TestUser("user_" + i, "password_" + i);
				os.writeObject(user);
				os.flush();

				is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				Object obj = is.readObject();
				if (obj != null) {
					user = (TestUser) obj;
					System.out.println("user: " + user.getName() + "/" + user.getPassword());
				}
			} catch (IOException ex) {
				logger.info("IOException: {}", ex);
			} finally {
				try {
					is.close();
				} catch (Exception ex) {
				}
				try {
					os.close();
				} catch (Exception ex) {
				}
				try {
					socket.close();
				} catch (Exception ex) {
				}
			}
		}
	}
}
