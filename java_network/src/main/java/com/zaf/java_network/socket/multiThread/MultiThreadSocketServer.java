package com.zaf.java_network.socket.multiThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.JavaNetworkApplication;

public class MultiThreadSocketServer {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketServer.class);

	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(9999);

		while (true) {
			Socket socket = server.accept();
			invoke(socket);
		}
	}

	private static void invoke(final Socket client) throws IOException {
		new Thread(new Runnable() {
			public void run() {
				BufferedReader in = null;
				PrintWriter out = null;
				try {
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					out = new PrintWriter(client.getOutputStream());

					while (true) {
						String msg = in.readLine();
						logger.info("received msg: {}",msg);
						out.println("Server received " + msg);
						out.flush();
						if (msg.equals("bye")) {
							break;
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (Exception e) {
					}
					try {
						out.close();
					} catch (Exception e) {
					}
					try {
						client.close();
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}
}