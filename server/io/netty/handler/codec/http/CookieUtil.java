package io.netty.handler.codec.http;

import java.util.BitSet;

/** @deprecated */
@Deprecated
final class CookieUtil {
   private static final BitSet VALID_COOKIE_VALUE_OCTETS = validCookieValueOctets();
   private static final BitSet VALID_COOKIE_NAME_OCTETS;

   private static BitSet validCookieValueOctets() {
      BitSet var0 = new BitSet(8);

      for(int var1 = 35; var1 < 127; ++var1) {
         var0.set(var1);
      }

      var0.set(34, false);
      var0.set(44, false);
      var0.set(59, false);
      var0.set(92, false);
      return var0;
   }

   private static BitSet validCookieNameOctets(BitSet var0) {
      BitSet var1 = new BitSet(8);
      var1.or(var0);
      var1.set(40, false);
      var1.set(41, false);
      var1.set(60, false);
      var1.set(62, false);
      var1.set(64, false);
      var1.set(58, false);
      var1.set(47, false);
      var1.set(91, false);
      var1.set(93, false);
      var1.set(63, false);
      var1.set(61, false);
      var1.set(123, false);
      var1.set(125, false);
      var1.set(32, false);
      var1.set(9, false);
      return var1;
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

   private CookieUtil() {
      super();
   }

   static {
      VALID_COOKIE_NAME_OCTETS = validCookieNameOctets(VALID_COOKIE_VALUE_OCTETS);
   }
}
