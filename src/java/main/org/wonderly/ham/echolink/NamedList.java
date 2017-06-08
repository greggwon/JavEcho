package org.wonderly.ham.echolink;

import java.util.ArrayList;

public class NamedList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;
	String name;

	public NamedList(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}
}