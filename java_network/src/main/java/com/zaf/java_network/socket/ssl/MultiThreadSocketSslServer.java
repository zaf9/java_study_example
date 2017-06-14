package com.zaf.java_network.socket.ssl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class MultiThreadSocketSslServer {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketSslServer.class);

	public static void main(String[] args) {

		File keyFile = new File("./src/main/resources/mysocket.jks");
		if (!keyFile.exists()) {
			logger.info("keyFile: {} don't exists! exit!", keyFile);
			System.exit(2);
		}
		
		 System.setProperty("javax.net.debug", "ssl,handshake"); 		  
         System.setProperty("javax.net.ssl.keyStore", "./src/main/resources/mysocket.jks");  
         System.setProperty("javax.net.ssl.keyStorePassword", "mysocket");  
         System.setProperty("javax.net.ssl.trustStore", "./src/main/resources/mysocket.jks");  
         System.setProperty("javax.net.ssl.trustStorePassword", "mysocket");  
		
		try {
			ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
			ServerSocket server = factory.createServerSocket(9999);

			while (true) {
				Socket socket = server.accept();
				invoke(socket);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
