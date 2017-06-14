package com.zaf.java_network.socket.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class UserSocketClientImpl implements IUserSocketClient {

	private String host;
	private int port;

	public UserSocketClientImpl(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public <T> T execute(IUserRequest request, IUserReponseHandler<T> handler) {
		IUserResponse response = execute(request);
		return handler.handle(response);
	}

	@Override
	public IUserResponse execute(IUserRequest request) {

		IUserResponse response = null;
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		try {
			socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(host, port);
			socket.connect(socketAddress, 10 * 1000);

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(request);
			oos.flush();

			ois = new ObjectInputStream(socket.getInputStream());
			Object obj = ois.readObject();
			if (obj != null) {
				response = (IUserResponse) obj;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			IOUtil.closeQuietly(ois);
			IOUtil.closeQuietly(oos);
			IOUtil.closeQuietly(socket);
		}
		return response;
	}
}
