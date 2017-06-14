package com.zaf.java_network.socket.ssl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class MultiThreadSocketSslClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketSslClient.class);

    public static void main(String[] args) throws Exception {  
    	
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
		
		
        for (int i = 0; i < 100; i++) {  
            Socket socket = null;  
            ObjectOutputStream os = null;  
            ObjectInputStream is = null;  
              
            try {  
                SocketFactory factory = SSLSocketFactory.getDefault();  
                socket = factory.createSocket("localhost", 9999);  
      
                os = new ObjectOutputStream(socket.getOutputStream());  
                TestUser user = new TestUser("user_" + i, "password_" + i);  
                os.writeObject(user);  
                os.flush();  
                  
                is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));  
                Object obj = is.readObject();  
                if (obj != null) {  
                    user = (TestUser)obj; 
					logger.info("recevied mssg user name: {} password: {}", user.getName(), user.getPassword());  
                }  
            } catch (IOException ex) {
				logger.info("IOException: {}", ex);
			}finally {  
                try {  
                    is.close();  
                } catch(Exception ex) {}  
                try {  
                    os.close();  
                } catch(Exception ex) {}  
                try {  
                    socket.close();  
                } catch(Exception ex) {}  
            }  
        }  
    }  
}
