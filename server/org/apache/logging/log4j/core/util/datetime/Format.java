package org.apache.logging.log4j.core.util.datetime;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

public abstract class Format {
   public Format() {
      super();
   }

   public final String format(Object var1) {
      return this.format(var1, new StringBuilder(), new FieldPosition(0)).toString();
   }

   public abstract StringBuilder format(Object var1, StringBuilder var2, FieldPosition var3);

   public abstract Object parseObject(String var1, ParsePosition var2);

   public Object parseObject(String var1) throws ParseException {
      ParsePosition var2 = new ParsePosition(0);
      Object var3 = this.parseObject(var1, var2);
      if (var2.getIndex() == 0) {
         throw new ParseException("Format.parseObject(String) failed", var2.getErrorIndex());
      } else {
         return var3;
      }
   }
}
