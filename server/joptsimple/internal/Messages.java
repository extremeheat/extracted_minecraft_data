package joptsimple.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {
   private Messages() {
      super();
      throw new UnsupportedOperationException();
   }

   public static String message(Locale var0, String var1, Class<?> var2, String var3, Object... var4) {
      ResourceBundle var5 = ResourceBundle.getBundle(var1, var0);
      String var6 = var5.getString(var2.getName() + '.' + var3);
      MessageFormat var7 = new MessageFormat(var6);
      var7.setLocale(var0);
      return var7.format(var4);
   }
}
