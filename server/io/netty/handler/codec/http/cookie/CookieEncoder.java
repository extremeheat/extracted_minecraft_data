package io.netty.handler.codec.http.cookie;

public abstract class CookieEncoder {
   protected final boolean strict;

   protected CookieEncoder(boolean var1) {
      super();
      this.strict = var1;
   }

   protected void validateCookie(String var1, String var2) {
      if (this.strict) {
         int var3;
         if ((var3 = CookieUtil.firstInvalidCookieNameOctet(var1)) >= 0) {
            throw new IllegalArgumentException("Cookie name contains an invalid char: " + var1.charAt(var3));
         }

         CharSequence var4 = CookieUtil.unwrapValue(var2);
         if (var4 == null) {
            throw new IllegalArgumentException("Cookie value wrapping quotes are not balanced: " + var2);
         }

         if ((var3 = CookieUtil.firstInvalidCookieValueOctet(var4)) >= 0) {
            throw new IllegalArgumentException("Cookie value contains an invalid char: " + var2.charAt(var3));
         }
      }

   }
}
