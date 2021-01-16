package joptsimple.util;

import java.util.Locale;
import java.util.regex.Pattern;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

public class RegexMatcher implements ValueConverter<String> {
   private final Pattern pattern;

   public RegexMatcher(String var1, int var2) {
      super();
      this.pattern = Pattern.compile(var1, var2);
   }

   public static ValueConverter<String> regex(String var0) {
      return new RegexMatcher(var0, 0);
   }

   public String convert(String var1) {
      if (!this.pattern.matcher(var1).matches()) {
         this.raiseValueConversionFailure(var1);
      }

      return var1;
   }

   public Class<String> valueType() {
      return String.class;
   }

   public String valuePattern() {
      return this.pattern.pattern();
   }

   private void raiseValueConversionFailure(String var1) {
      String var2 = Messages.message(Locale.getDefault(), "joptsimple.ExceptionMessages", RegexMatcher.class, "message", var1, this.pattern.pattern());
      throw new ValueConversionException(var2);
   }
}
