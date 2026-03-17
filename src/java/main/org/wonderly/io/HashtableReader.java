package org.wonderly.io;

import java.util.*;
import java.io.*;

public class HashtableReader<K,V> {
	ObjectInputStream os;
	public HashtableReader( ObjectInputStream os ) {
		this.os = os;
	}
	
	public HashMap<K,V> read() 
			throws IOException,ClassNotFoundException {
		return (HashMap<K,V>)os.readObject();
	}
}