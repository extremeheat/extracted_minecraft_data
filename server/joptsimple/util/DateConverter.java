package joptsimple.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

public class DateConverter implements ValueConverter<Date> {
   private final DateFormat formatter;

   public DateConverter(DateFormat var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("illegal null formatter");
      } else {
         this.formatter = var1;
      }
   }

   public static DateConverter datePattern(String var0) {
      SimpleDateFormat var1 = new SimpleDateFormat(var0);
      var1.setLenient(false);
      return new DateConverter(var1);
   }

   public Date convert(String var1) {
      ParsePosition var2 = new ParsePosition(0);
      Date var3 = this.formatter.parse(var1, var2);
      if (var2.getIndex() != var1.length()) {
         throw new ValueConversionException(this.message(var1));
      } else {
         return var3;
      }
   }

   public Class<Date> valueType() {
      return Date.class;
   }

   public String valuePattern() {
      return this.formatter instanceof SimpleDateFormat ? ((SimpleDateFormat)this.formatter).toPattern() : "";
   }

   private String message(String var1) {
      String var2;
      Object[] var3;
      if (this.formatter instanceof SimpleDateFormat) {
         var2 = "with.pattern.message";
         var3 = new Object[]{var1, ((SimpleDateFormat)this.formatter).toPattern()};
      } else {
         var2 = "without.pattern.message";
         var3 = new Object[]{var1};
      }

      return Messages.message(Locale.getDefault(), "joptsimple.ExceptionMessages", DateConverter.class, var2, var3);
   }
}
