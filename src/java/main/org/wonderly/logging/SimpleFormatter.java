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
		return dt +" ["+record.getLoggerName()+"] # "+record.getLevel()+" # "+formatMessage(record)+System.lineSeparator();
	}
}
