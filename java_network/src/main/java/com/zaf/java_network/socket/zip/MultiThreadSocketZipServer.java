package com.zaf.java_network.socket.zip;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.TestUser;

public class MultiThreadSocketZipServer {
	
	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketZipServer.class);

    public static void main(String[] args) throws IOException {  
        ServerSocket server = new ServerSocket(9999);  
  
        while (true) {  
            Socket socket = server.accept();  
            socket.setSoTimeout(10 * 1000);  
            invoke(socket);  
        }  
    }  
  
    private static void invoke(final Socket socket) throws IOException {  
        new Thread(new Runnable() {  
            public void run() {  
                GZIPInputStream gzipis = null;  
                ObjectInputStream ois = null;  
                GZIPOutputStream gzipos = null;  
                ObjectOutputStream oos = null;  
                  
                try {  
                    gzipis = new GZIPInputStream(socket.getInputStream());  
                    ois = new ObjectInputStream(gzipis);  
                    gzipos = new GZIPOutputStream(socket.getOutputStream());  
                    oos = new ObjectOutputStream(gzipos);  
  
                    Object obj = ois.readObject();  
                    TestUser user = (TestUser)obj;
					logger.info("recevied mssg user name: {} password: {}", user.getName(), user.getPassword());
  
                    user.setName(user.getName() + "_new");  
                    user.setPassword(user.getPassword() + "_new");  
  
                    oos.writeObject(user);  
                    oos.flush();  
                    gzipos.finish();  
                } catch (IOException ex) {
					logger.info("IOException: {}", ex);
				} catch (ClassNotFoundException ex) {
					logger.info("ClassNotFoundException: {}", ex);
				}  finally {  
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
        }).start();  
    }  
}
