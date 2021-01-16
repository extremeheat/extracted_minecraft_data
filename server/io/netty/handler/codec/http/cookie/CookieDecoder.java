package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.CharBuffer;

public abstract class CookieDecoder {
   private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
   private final boolean strict;

   protected CookieDecoder(boolean var1) {
      super();
      this.strict = var1;
   }

   protected DefaultCookie initCookie(String var1, int var2, int var3, int var4, int var5) {
      if (var2 != -1 && var2 != var3) {
         if (var4 == -1) {
            this.logger.debug("Skipping cookie with null value");
            return null;
         } else {
            CharBuffer var6 = CharBuffer.wrap(var1, var4, var5);
            CharSequence var7 = CookieUtil.unwrapValue(var6);
            if (var7 == null) {
               this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", (Object)var6);
               return null;
            } else {
               String var8 = var1.substring(var2, var3);
               int var9;
               if (this.strict && (var9 = CookieUtil.firstInvalidCookieNameOctet(var8)) >= 0) {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", var8, var8.charAt(var9));
                  }

                  return null;
               } else {
                  boolean var10 = var7.length() != var5 - var4;
                  if (this.strict && (var9 = CookieUtil.firstInvalidCookieValueOctet(var7)) >= 0) {
                     if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", var7, var7.charAt(var9));
                     }

                     return null;
                  } else {
                     DefaultCookie var11 = new DefaultCookie(var8, var7.toString());
                     var11.setWrap(var10);
                     return var11;
                  }
               }
            }
         }
      } else {
         this.logger.debug("Skipping cookie with null name");
         return null;
      }
   }
}
