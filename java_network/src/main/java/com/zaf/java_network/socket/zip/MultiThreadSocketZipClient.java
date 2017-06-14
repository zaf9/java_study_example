package com.zaf.java_network.socket.zip;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class MultiThreadSocketZipClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketZipClient.class);

	   public static void main(String[] args) throws Exception {  
	        for (int i = 0; i < 10; i++) {  
	            Socket socket = null;  
	            GZIPOutputStream gzipos = null;  
	            ObjectOutputStream oos = null;  
	            GZIPInputStream gzipis = null;  
	            ObjectInputStream ois = null;  
	              
	            try {  
	                socket = new Socket();  
	                SocketAddress socketAddress = new InetSocketAddress("localhost", 9999);   
	                socket.connect(socketAddress, 10 * 1000);  
	                socket.setSoTimeout(10 * 1000);  
	                  
	                gzipos = new GZIPOutputStream(socket.getOutputStream());  
	                oos = new ObjectOutputStream(gzipos);  
	                TestUser user = new TestUser("user_" + i, "password_" + i);  
	                oos.writeObject(user);  
	                oos.flush();  
	                gzipos.finish();  
	                  
	                gzipis = new GZIPInputStream(socket.getInputStream());  
	                ois = new ObjectInputStream(gzipis);  
	                Object obj = ois.readObject();  
	                if (obj != null) {  
	                    user = (TestUser)obj;  
	                    logger.info("recevied mssg user name: {} password: {}", user.getName(), user.getPassword());
	                }  
	            } catch (IOException ex) {
					logger.info("IOException: {}", ex);
				} finally {  
	                try {  
	                    ois.close();  
	                } catch(Exception ex) {}  
	                try {  
	                    oos.close();  
	                } catch(Exception ex) {}  
	                try {  
	                    socket.close();  
	                } catch(Exception ex) {}  
	            }  
	        }  
	    }  
}
