package org.wonderly.io;

import java.util.*;
import java.io.*;
import org.wonderly.util.*;

public class VectorReader<T> {	
	public @SuppressWarnings("unchecked") List<T> read(ObjectInputStream is) 
			throws IOException, ClassNotFoundException {
		List<T> iv = new Cast<List<T>>(is.readObject()).get();
		return iv;		
	}
}