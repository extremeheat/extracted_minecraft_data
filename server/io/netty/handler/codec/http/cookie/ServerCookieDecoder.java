package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public final class ServerCookieDecoder extends CookieDecoder {
   private static final String RFC2965_VERSION = "$Version";
   private static final String RFC2965_PATH = "$Path";
   private static final String RFC2965_DOMAIN = "$Domain";
   private static final String RFC2965_PORT = "$Port";
   public static final ServerCookieDecoder STRICT = new ServerCookieDecoder(true);
   public static final ServerCookieDecoder LAX = new ServerCookieDecoder(false);

   private ServerCookieDecoder(boolean var1) {
      super(var1);
   }

   public Set<Cookie> decode(String var1) {
      int var2 = ((String)ObjectUtil.checkNotNull(var1, "header")).length();
      if (var2 == 0) {
         return Collections.emptySet();
      } else {
         TreeSet var3 = new TreeSet();
         int var4 = 0;
         boolean var5 = false;
         if (var1.regionMatches(true, 0, "$Version", 0, "$Version".length())) {
            var4 = var1.indexOf(59) + 1;
            var5 = true;
         }

         while(true) {
            int var7;
            int var8;
            int var9;
            int var12;
            label81:
            do {
               while(var4 != var2) {
                  char var6 = var1.charAt(var4);
                  if (var6 != '\t' && var6 != '\n' && var6 != 11 && var6 != '\f' && var6 != '\r' && var6 != ' ' && var6 != ',' && var6 != ';') {
                     var12 = var4;

                     do {
                        char var10 = var1.charAt(var4);
                        if (var10 == ';') {
                           var7 = var4;
                           var9 = -1;
                           var8 = -1;
                           continue label81;
                        }

                        if (var10 == '=') {
                           var7 = var4++;
                           if (var4 == var2) {
                              var9 = 0;
                              var8 = 0;
                           } else {
                              var8 = var4;
                              int var11 = var1.indexOf(59, var4);
                              var9 = var4 = var11 > 0 ? var11 : var2;
                           }
                           continue label81;
                        }

                        ++var4;
                     } while(var4 != var2);

                     var7 = var2;
                     var9 = -1;
                     var8 = -1;
                     continue label81;
                  }

                  ++var4;
               }

               return var3;
            } while(var5 && (var1.regionMatches(var12, "$Path", 0, "$Path".length()) || var1.regionMatches(var12, "$Domain", 0, "$Domain".length()) || var1.regionMatches(var12, "$Port", 0, "$Port".length())));

            DefaultCookie var13 = this.initCookie(var1, var12, var7, var8, var9);
            if (var13 != null) {
               var3.add(var13);
            }
         }
      }
   }
}
