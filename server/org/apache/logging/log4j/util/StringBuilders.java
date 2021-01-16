package org.apache.logging.log4j.util;

import java.util.Map.Entry;

public final class StringBuilders {
   private StringBuilders() {
      super();
   }

   public static StringBuilder appendDqValue(StringBuilder var0, Object var1) {
      return var0.append('"').append(var1).append('"');
   }

   public static StringBuilder appendKeyDqValue(StringBuilder var0, Entry<String, String> var1) {
      return appendKeyDqValue(var0, (String)var1.getKey(), var1.getValue());
   }

   public static StringBuilder appendKeyDqValue(StringBuilder var0, String var1, Object var2) {
      return var0.append(var1).append('=').append('"').append(var2).append('"');
   }

   public static void appendValue(StringBuilder var0, Object var1) {
      if (var1 != null && !(var1 instanceof String)) {
         if (var1 instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable)var1).formatTo(var0);
         } else if (var1 instanceof CharSequence) {
            var0.append((CharSequence)var1);
         } else if (var1 instanceof Integer) {
            var0.append((Integer)var1);
         } else if (var1 instanceof Long) {
            var0.append((Long)var1);
         } else if (var1 instanceof Double) {
            var0.append((Double)var1);
         } else if (var1 instanceof Boolean) {
            var0.append((Boolean)var1);
         } else if (var1 instanceof Character) {
            var0.append((Character)var1);
         } else if (var1 instanceof Short) {
            var0.append((Short)var1);
         } else if (var1 instanceof Float) {
            var0.append((Float)var1);
         } else {
            var0.append(var1);
         }
      } else {
         var0.append((String)var1);
      }

   }

   public static boolean equals(CharSequence var0, int var1, int var2, CharSequence var3, int var4, int var5) {
      if (var2 == var5) {
         for(int var6 = 0; var6 < var5; ++var6) {
            if (var0.charAt(var6 + var1) != var3.charAt(var6 + var4)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean equalsIgnoreCase(CharSequence var0, int var1, int var2, CharSequence var3, int var4, int var5) {
      if (var2 == var5) {
         for(int var6 = 0; var6 < var5; ++var6) {
            if (Character.toLowerCase(var0.charAt(var6 + var1)) != Character.toLowerCase(var3.charAt(var6 + var4))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
