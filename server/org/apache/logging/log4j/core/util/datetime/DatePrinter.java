package org.apache.logging.log4j.core.util.datetime;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public interface DatePrinter {
   String format(long var1);

   String format(Date var1);

   String format(Calendar var1);

   <B extends Appendable> B format(long var1, B var3);

   <B extends Appendable> B format(Date var1, B var2);

   <B extends Appendable> B format(Calendar var1, B var2);

   String getPattern();

   TimeZone getTimeZone();

   Locale getLocale();

   StringBuilder format(Object var1, StringBuilder var2, FieldPosition var3);
}
