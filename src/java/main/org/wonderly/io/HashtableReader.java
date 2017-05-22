package org.wonderly.io;

import java.util.*;
import java.io.*;
import org.wonderly.util.*;

public class HashtableReader<K,V> {
	ObjectInputStream os;
	public HashtableReader( ObjectInputStream os ) {
		this.os = os;
	}
	
	public Hashtable<K,V> read() 
			throws IOException,ClassNotFoundException {
		return (Hashtable<K,V>)os.readObject();
	}
}