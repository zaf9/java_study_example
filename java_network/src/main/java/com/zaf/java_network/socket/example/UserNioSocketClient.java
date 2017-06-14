package com.zaf.java_network.socket.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserNioSocketClient implements IUserSocketClient {

	private static final Logger logger = LoggerFactory.getLogger(UserNioSocketClient.class);

	private String host;
	private int port;

	public UserNioSocketClient(String host, int port) {
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
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress socketAddress = new InetSocketAddress(host, port);
			socketChannel.connect(socketAddress);
			sendData(socketChannel, request);
			response = receiveData(socketChannel);
		} catch (Exception ex) {
			logger.info("Exception:", ex);
		} finally {
			try {
				socketChannel.close();
			} catch (Exception ex) {
			}
		}
		return response;
	}

	private void sendData(SocketChannel socketChannel, IUserRequest myRequest) throws IOException {
		byte[] bytes = SerializableUtil.toBytes(myRequest);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		socketChannel.write(buffer);
		socketChannel.socket().shutdownOutput();
	}

	private IUserResponse receiveData(SocketChannel socketChannel) throws IOException {
		IUserResponse myResponse = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
			byte[] bytes;
			int count = 0;
			while ((count = socketChannel.read(buffer)) >= 0) {
				buffer.flip();
				bytes = new byte[count];
				buffer.get(bytes);
				baos.write(bytes);
				buffer.clear();
			}
			bytes = baos.toByteArray();
			Object obj = SerializableUtil.toObject(bytes);
			myResponse = (IUserResponse) obj;
			socketChannel.close();
		} finally {
			try {
				baos.close();
			} catch (Exception ex) {
			}
		}
		return myResponse;
	}

}
