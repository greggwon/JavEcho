package org.wonderly.util;

public class Cast<T> {
	Object nv;
	public Cast( Object v ) {
		nv = v;
	}
	@SuppressWarnings("unchecked")
	public T get() {
		return (T)nv;
	}
}