package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.util.internal.ObjectUtil;
import java.util.Date;

public final class ClientCookieDecoder extends CookieDecoder {
   public static final ClientCookieDecoder STRICT = new ClientCookieDecoder(true);
   public static final ClientCookieDecoder LAX = new ClientCookieDecoder(false);

   private ClientCookieDecoder(boolean var1) {
      super(var1);
   }

   public Cookie decode(String var1) {
      int var2 = ((String)ObjectUtil.checkNotNull(var1, "header")).length();
      if (var2 == 0) {
         return null;
      } else {
         ClientCookieDecoder.CookieBuilder var3 = null;
         int var4 = 0;

         while(var4 != var2) {
            char var5 = var1.charAt(var4);
            if (var5 == ',') {
               break;
            }

            if (var5 != '\t' && var5 != '\n' && var5 != 11 && var5 != '\f' && var5 != '\r' && var5 != ' ' && var5 != ';') {
               int var11 = var4;

               int var6;
               int var7;
               int var8;
               while(true) {
                  char var9 = var1.charAt(var4);
                  if (var9 == ';') {
                     var6 = var4;
                     var8 = -1;
                     var7 = -1;
                     break;
                  }

                  if (var9 == '=') {
                     var6 = var4++;
                     if (var4 == var2) {
                        var8 = 0;
                        var7 = 0;
                     } else {
                        var7 = var4;
                        int var10 = var1.indexOf(59, var4);
                        var8 = var4 = var10 > 0 ? var10 : var2;
                     }
                     break;
                  }

                  ++var4;
                  if (var4 == var2) {
                     var6 = var2;
                     var8 = -1;
                     var7 = -1;
                     break;
                  }
               }

               if (var8 > 0 && var1.charAt(var8 - 1) == ',') {
                  --var8;
               }

               if (var3 == null) {
                  DefaultCookie var12 = this.initCookie(var1, var11, var6, var7, var8);
                  if (var12 == null) {
                     return null;
                  }

                  var3 = new ClientCookieDecoder.CookieBuilder(var12, var1);
               } else {
                  var3.appendAttribute(var11, var6, var7, var8);
               }
            } else {
               ++var4;
            }
         }

         return var3 != null ? var3.cookie() : null;
      }
   }

   private static class CookieBuilder {
      private final String header;
      private final DefaultCookie cookie;
      private String domain;
      private String path;
      private long maxAge = -9223372036854775808L;
      private int expiresStart;
      private int expiresEnd;
      private boolean secure;
      private boolean httpOnly;

      CookieBuilder(DefaultCookie var1, String var2) {
         super();
         this.cookie = var1;
         this.header = var2;
      }

      private long mergeMaxAgeAndExpires() {
         if (this.maxAge != -9223372036854775808L) {
            return this.maxAge;
         } else {
            if (isValueDefined(this.expiresStart, this.expiresEnd)) {
               Date var1 = DateFormatter.parseHttpDate(this.header, this.expiresStart, this.expiresEnd);
               if (var1 != null) {
                  long var2 = var1.getTime() - System.currentTimeMillis();
                  return var2 / 1000L + (long)(var2 % 1000L != 0L ? 1 : 0);
               }
            }

            return -9223372036854775808L;
         }
      }

      Cookie cookie() {
         this.cookie.setDomain(this.domain);
         this.cookie.setPath(this.path);
         this.cookie.setMaxAge(this.mergeMaxAgeAndExpires());
         this.cookie.setSecure(this.secure);
         this.cookie.setHttpOnly(this.httpOnly);
         return this.cookie;
      }

      void appendAttribute(int var1, int var2, int var3, int var4) {
         int var5 = var2 - var1;
         if (var5 == 4) {
            this.parse4(var1, var3, var4);
         } else if (var5 == 6) {
            this.parse6(var1, var3, var4);
         } else if (var5 == 7) {
            this.parse7(var1, var3, var4);
         } else if (var5 == 8) {
            this.parse8(var1);
         }

      }

      private void parse4(int var1, int var2, int var3) {
         if (this.header.regionMatches(true, var1, "Path", 0, 4)) {
            this.path = this.computeValue(var2, var3);
         }

      }

      private void parse6(int var1, int var2, int var3) {
         if (this.header.regionMatches(true, var1, "Domain", 0, 5)) {
            this.domain = this.computeValue(var2, var3);
         } else if (this.header.regionMatches(true, var1, "Secure", 0, 5)) {
            this.secure = true;
         }

      }

      private void setMaxAge(String var1) {
         try {
            this.maxAge = Math.max(Long.parseLong(var1), 0L);
         } catch (NumberFormatException var3) {
         }

      }

      private void parse7(int var1, int var2, int var3) {
         if (this.header.regionMatches(true, var1, "Expires", 0, 7)) {
            this.expiresStart = var2;
            this.expiresEnd = var3;
         } else if (this.header.regionMatches(true, var1, "Max-Age", 0, 7)) {
            this.setMaxAge(this.computeValue(var2, var3));
         }

      }

      private void parse8(int var1) {
         if (this.header.regionMatches(true, var1, "HTTPOnly", 0, 8)) {
            this.httpOnly = true;
         }

      }

      private static boolean isValueDefined(int var0, int var1) {
         return var0 != -1 && var0 != var1;
      }

      private String computeValue(int var1, int var2) {
         return isValueDefined(var1, var2) ? this.header.substring(var1, var2) : null;
      }
   }
}
