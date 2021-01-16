package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.InternalThreadLocalMap;
import java.util.BitSet;

final class CookieUtil {
   private static final BitSet VALID_COOKIE_NAME_OCTETS = validCookieNameOctets();
   private static final BitSet VALID_COOKIE_VALUE_OCTETS = validCookieValueOctets();
   private static final BitSet VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS = validCookieAttributeValueOctets();

   private static BitSet validCookieNameOctets() {
      BitSet var0 = new BitSet();

      for(int var1 = 32; var1 < 127; ++var1) {
         var0.set(var1);
      }

      int[] var6 = new int[]{40, 41, 60, 62, 64, 44, 59, 58, 92, 34, 47, 91, 93, 63, 61, 123, 125, 32, 9};
      int[] var2 = var6;
      int var3 = var6.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var0.set(var5, false);
      }

      return var0;
   }

   private static BitSet validCookieValueOctets() {
      BitSet var0 = new BitSet();
      var0.set(33);

      int var1;
      for(var1 = 35; var1 <= 43; ++var1) {
         var0.set(var1);
      }

      for(var1 = 45; var1 <= 58; ++var1) {
         var0.set(var1);
      }

      for(var1 = 60; var1 <= 91; ++var1) {
         var0.set(var1);
      }

      for(var1 = 93; var1 <= 126; ++var1) {
         var0.set(var1);
      }

      return var0;
   }

   private static BitSet validCookieAttributeValueOctets() {
      BitSet var0 = new BitSet();

      for(int var1 = 32; var1 < 127; ++var1) {
         var0.set(var1);
      }

      var0.set(59, false);
      return var0;
   }

   static StringBuilder stringBuilder() {
      return InternalThreadLocalMap.get().stringBuilder();
   }

   static String stripTrailingSeparatorOrNull(StringBuilder var0) {
      return var0.length() == 0 ? null : stripTrailingSeparator(var0);
   }

   static String stripTrailingSeparator(StringBuilder var0) {
      if (var0.length() > 0) {
         var0.setLength(var0.length() - 2);
      }

      return var0.toString();
   }

   static void add(StringBuilder var0, String var1, long var2) {
      var0.append(var1);
      var0.append('=');
      var0.append(var2);
      var0.append(';');
      var0.append(' ');
   }

   static void add(StringBuilder var0, String var1, String var2) {
      var0.append(var1);
      var0.append('=');
      var0.append(var2);
      var0.append(';');
      var0.append(' ');
   }

   static void add(StringBuilder var0, String var1) {
      var0.append(var1);
      var0.append(';');
      var0.append(' ');
   }

   static void addQuoted(StringBuilder var0, String var1, String var2) {
      if (var2 == null) {
         var2 = "";
      }

      var0.append(var1);
      var0.append('=');
      var0.append('"');
      var0.append(var2);
      var0.append('"');
      var0.append(';');
      var0.append(' ');
   }

   static int firstInvalidCookieNameOctet(CharSequence var0) {
      return firstInvalidOctet(var0, VALID_COOKIE_NAME_OCTETS);
   }

   static int firstInvalidCookieValueOctet(CharSequence var0) {
      return firstInvalidOctet(var0, VALID_COOKIE_VALUE_OCTETS);
   }

   static int firstInvalidOctet(CharSequence var0, BitSet var1) {
      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (!var1.get(var3)) {
            return var2;
         }
      }

      return -1;
   }

   static CharSequence unwrapValue(CharSequence var0) {
      int var1 = var0.length();
      if (var1 > 0 && var0.charAt(0) == '"') {
         if (var1 >= 2 && var0.charAt(var1 - 1) == '"') {
            return (CharSequence)(var1 == 2 ? "" : var0.subSequence(1, var1 - 1));
         } else {
            return null;
         }
      } else {
         return var0;
      }
   }

   static String validateAttributeValue(String var0, String var1) {
      if (var1 == null) {
         return null;
      } else {
         var1 = var1.trim();
         if (var1.isEmpty()) {
            return null;
         } else {
            int var2 = firstInvalidOctet(var1, VALID_COOKIE_ATTRIBUTE_VALUE_OCTETS);
            if (var2 != -1) {
               throw new IllegalArgumentException(var0 + " contains the prohibited characters: " + var1.charAt(var2));
            } else {
               return var1;
            }
         }
      }
   }

   private CookieUtil() {
      super();
   }
}
