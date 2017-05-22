package org.wonderly.io;

import java.util.*;
import java.io.*;
import org.wonderly.util.*;

public class VectorReader<T> {	
	public @SuppressWarnings("unchecked") Vector<T> read(ObjectInputStream is) 
			throws IOException, ClassNotFoundException {
		Vector<T> iv = new Cast<Vector<T>>(is.readObject()).get();
		return iv;		
	}
}