package com.zaf.java_network.socket.commons;

import java.io.Serializable;
import java.util.List;

public class UserPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private int age;

	private List<String> likeThing;

	public UserPojo(String name, int age, List<String> likeThing) {
		super();
		this.name = name;
		this.age = age;
		this.likeThing = likeThing;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<String> getLikeThing() {
		return likeThing;
	}

	public void setLikeThing(List<String> likeThing) {
		this.likeThing = likeThing;
	}

	@Override
	public String toString() {
		return "UserPojo [name=" + name + ", age=" + age + ", likeThing=" + likeThing + "]";
	}

}
