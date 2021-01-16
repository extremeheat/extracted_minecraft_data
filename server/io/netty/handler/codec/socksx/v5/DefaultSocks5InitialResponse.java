package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;

public class DefaultSocks5InitialResponse extends AbstractSocks5Message implements Socks5InitialResponse {
   private final Socks5AuthMethod authMethod;

   public DefaultSocks5InitialResponse(Socks5AuthMethod var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("authMethod");
      } else {
         this.authMethod = var1;
      }
   }

   public Socks5AuthMethod authMethod() {
      return this.authMethod;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", authMethod: ");
      } else {
         var1.append("(authMethod: ");
      }

      var1.append(this.authMethod());
      var1.append(')');
      return var1.toString();
   }
}
