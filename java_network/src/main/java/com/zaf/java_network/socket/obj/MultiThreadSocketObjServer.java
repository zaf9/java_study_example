package com.zaf.java_network.socket.obj;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class MultiThreadSocketObjServer {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketObjServer.class);

	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(9999);

		while (true) {
			Socket socket = server.accept();
			invoke(socket);
		}
	}

	private static void invoke(final Socket socket) throws IOException {
		new Thread(new Runnable() {
			public void run() {
				ObjectInputStream is = null;
				ObjectOutputStream os = null;
				try {
					is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					os = new ObjectOutputStream(socket.getOutputStream());

					Object obj = is.readObject();
					TestUser user = (TestUser) obj;
					logger.info("recevied mssg user name: {} password: {}", user.getName(), user.getPassword());

					user.setName(user.getName() + "_new");
					user.setPassword(user.getPassword() + "_new");

					os.writeObject(user);
					os.flush();
				} catch (IOException ex) {
					logger.info("IOException: {}", ex);
				} catch (ClassNotFoundException ex) {
					logger.info("ClassNotFoundException: {}", ex);
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
		}).start();
	}
}
