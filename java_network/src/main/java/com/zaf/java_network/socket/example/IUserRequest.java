package com.zaf.java_network.socket.example;

import java.io.Serializable;

public interface IUserRequest extends Serializable {

	Class<?> getRequestClass();  
	  
    String getRequestMethod();  
  
    Class<?>[] getRequestParameterTypes();  
  
    Object[] getRequestParameterValues();  
}
