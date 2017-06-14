package com.zaf.java_network.socket.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadSocketFileClient {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketFileClient.class);

	private static final String sendFileName = "./src/main/java/com/zaf/java_study/socket/file/MultiThreadSocketFileServer.java";

	public static void main(String[] args) throws Exception {
		new Thread(new MyRunnable()).start();
	}

	private static final class MyRunnable implements Runnable {
		public void run() {

			File file = new File(sendFileName);
			if (!file.exists()) {
				logger.error("send file: {} don't exist!exit!", sendFileName);
				System.exit(2);
			}

			SocketChannel socketChannel = null;
			try {
				socketChannel = SocketChannel.open();
				SocketAddress socketAddress = new InetSocketAddress("localhost", 9999);
				socketChannel.connect(socketAddress);

				sendFile(socketChannel, file);
				receiveFile(socketChannel, new File("c:/temp/client_receive.log"));
			} catch (Exception ex) {
				logger.info("Exception:", ex);
			} finally {
				try {
					socketChannel.close();
				} catch (Exception ex) {
				}
			}
		}

		private void sendFile(SocketChannel socketChannel, File file) throws IOException {
			FileInputStream fis = null;
			FileChannel channel = null;

			logger.info("sendFile name: {} size: {}",file.getAbsolutePath(),file.length());
			try {
				fis = new FileInputStream(file);
				channel = fis.getChannel();
				ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
				int size = 0;
				while ((size = channel.read(buffer)) != -1) {
					buffer.rewind();
					buffer.limit(size);
					socketChannel.write(buffer);
					buffer.clear();
				}
				socketChannel.socket().shutdownOutput();
			} finally {
				try {
					channel.close();
				} catch (Exception ex) {
				}
				try {
					fis.close();
				} catch (Exception ex) {
				}
			}
		}

		private void receiveFile(SocketChannel socketChannel, File file) throws IOException {
			FileOutputStream fos = null;
			FileChannel channel = null;

			try {
				fos = new FileOutputStream(file);
				channel = fos.getChannel();
				ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

				int size = 0;
				while ((size = socketChannel.read(buffer)) != -1) {
					buffer.flip();
					if (size > 0) {
						buffer.limit(size);
						channel.write(buffer);
						buffer.clear();
					}
				}
			} finally {
				try {
					channel.close();
				} catch (Exception ex) {
				}
				try {
					fos.close();
				} catch (Exception ex) {
				}
			}
		}
	}
}
