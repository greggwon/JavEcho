package org.wonderly.logging;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.LogRecord;

public class SimpleFormatter extends java.util.logging.Formatter {
	GregorianCalendar c = new GregorianCalendar();
	SimpleDateFormat fmt = new SimpleDateFormat( "mm/dd/yyyy HH:MM:SS");

	@Override
	public String format(LogRecord record) {
		String dt = "";
		synchronized(this) {
			c.setTimeInMillis(record.getMillis());
			dt = fmt.format(c.getTime());
		}
		String res = dt +" ["+record.getLoggerName()+"] # "+record.getLevel()+" # "+formatMessage(record);
		if( record.getThrown() != null ) {
			Throwable t = record.getThrown();
			while( t != null ) {
				res += "Exception "+t.getClass().getName()+": "+t.getMessage()+ System.lineSeparator();
				StackTraceElement[]stk = t.getStackTrace();
				for( int i = 0; i < stk.length; ++i ) {
					res += "\tat "+stk[i].toString() + System.lineSeparator();
				}
				t = t.getCause();
			}
		}
				
		return res+System.lineSeparator();
	}
}
