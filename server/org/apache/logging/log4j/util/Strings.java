package org.apache.logging.log4j.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public final class Strings {
   public static final String EMPTY = "";
   public static final String LINE_SEPARATOR = PropertiesUtil.getProperties().getStringProperty("line.separator", "\n");

   private Strings() {
      super();
   }

   public static String dquote(String var0) {
      return '"' + var0 + '"';
   }

   public static boolean isBlank(String var0) {
      return var0 == null || var0.trim().isEmpty();
   }

   public static boolean isEmpty(CharSequence var0) {
      return var0 == null || var0.length() == 0;
   }

   public static boolean isNotBlank(String var0) {
      return !isBlank(var0);
   }

   public static boolean isNotEmpty(CharSequence var0) {
      return !isEmpty(var0);
   }

   public static String quote(String var0) {
      return '\'' + var0 + '\'';
   }

   public String toRootUpperCase(String var1) {
      return var1.toUpperCase(Locale.ROOT);
   }

   public static String trimToNull(String var0) {
      String var1 = var0 == null ? null : var0.trim();
      return isEmpty(var1) ? null : var1;
   }

   public static String join(Iterable<?> var0, char var1) {
      return var0 == null ? null : join(var0.iterator(), var1);
   }

   public static String join(Iterator<?> var0, char var1) {
      if (var0 == null) {
         return null;
      } else if (!var0.hasNext()) {
         return "";
      } else {
         Object var2 = var0.next();
         if (!var0.hasNext()) {
            return Objects.toString(var2);
         } else {
            StringBuilder var3 = new StringBuilder(256);
            if (var2 != null) {
               var3.append(var2);
            }

            while(var0.hasNext()) {
               var3.append(var1);
               Object var4 = var0.next();
               if (var4 != null) {
                  var3.append(var4);
               }
            }

            return var3.toString();
         }
      }
   }
}
