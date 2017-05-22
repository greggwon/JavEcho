package org.wonderly.prefs;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import org.wonderly.awt.*;
import org.wonderly.swing.*;
import java.util.*;
import java.util.prefs.*;
import org.wonderly.ham.echolink.*;

public class PrefsAccessGenerator extends JFrame {
	public static void main( String args[] ) throws Exception {
		new PrefsAccessGenerator();
	}
	
	public PrefsAccessGenerator() throws BackingStoreException {
		Preferences node = Preferences.userNodeForPackage( SysopSettings.class );		
		node = node.node("SysopSettings/W5GGW");
		System.out.println("\tprivate Preferences curPrefs() {");
		System.out.println("\t\tPreferences pr = Preferences.userNodeForPackage( SysopSettings.class );");		
		System.out.println("\t\treturn pr.node(\"SysopSettings\").node(getCallSign());");
		System.out.println("\t}");
		decend( "", node );
	}
	
	protected void decend( String pref, Preferences node ) throws BackingStoreException {
		String arr[] = node.childrenNames();
		String keys[] = node.keys();
		for( int i = 0; i < keys.length; ++i ) {
			String type;
			String nkey = "";
			String pkey = pref+"/"+keys[i];
			for( int j = 0; j < pkey.length(); ++j ) {
				if( pkey.charAt(j) != '/' && pkey.charAt(j) > ' ') {
					nkey += pkey.charAt(j);
				} else {
					++j;
					char ch = pkey.charAt(j);
					if( Character.isLowerCase( ch ) ) {
						ch = Character.toUpperCase(ch);
					}
					nkey += ch;
				}
			}
			pkey = nkey;
			String typeQual;
			String def;
			String getQual="get";
			String val = node.get(keys[i],"");
			if( val.equals("false") || val.equals("true") ) {
				type = "boolean";
				typeQual = "Boolean";
				getQual="is";
				def = "false";
			} else {
				try {
					Integer.parseInt( val );
					type = "int";
					typeQual = "Int";
					def = "0";
				} catch( Exception exxx ) {
					type = "String";
					typeQual = "";
					def = "\"\"";
				}
			}
			System.out.println("\tpublic "+type+" "+getQual+pkey+"() {" ); //throws BackingStoreException {");
			System.out.print("\t\treturn curPrefs()");
			String tarr[] = pref.substring(1).split("/");
			for( int j = 0; j < tarr.length; ++j ) {
				System.out.print(".node(\""+tarr[j]+"\")");
			}
			System.out.println(".get"+typeQual+"( \""+keys[i]+"\", "+def+");");
			System.out.println("\t}");
			System.out.println("\tpublic void set"+pkey+"( "+type+" val ) {"); //throws BackingStoreException {");
			System.out.print("\t\tcurPrefs()");
			tarr = pref.substring(1).split("/");
			for( int j = 0; j < tarr.length; ++j ) {
				System.out.print(".node(\""+tarr[j]+"\")");
			}
			System.out.println(".put"+typeQual+"( \""+keys[i]+"\", val );");
			System.out.println("\t}");
		}
		for( int i = 0; i < arr.length; ++i ) {
//			System.out.println("arr["+i+"]: "+pref+"/"+arr[i]);
			decend( pref+"/"+arr[i], node.node(arr[i]));
		}
	}
}