package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;

public class DefaultSocks5PasswordAuthResponse extends AbstractSocks5Message implements Socks5PasswordAuthResponse {
   private final Socks5PasswordAuthStatus status;

   public DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("status");
      } else {
         this.status = var1;
      }
   }

   public Socks5PasswordAuthStatus status() {
      return this.status;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", status: ");
      } else {
         var1.append("(status: ");
      }

      var1.append(this.status());
      var1.append(')');
      return var1.toString();
   }
}
