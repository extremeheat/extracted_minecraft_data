package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;

public class DefaultSocks5PasswordAuthRequest extends AbstractSocks5Message implements Socks5PasswordAuthRequest {
   private final String username;
   private final String password;

   public DefaultSocks5PasswordAuthRequest(String var1, String var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("username");
      } else if (var2 == null) {
         throw new NullPointerException("password");
      } else if (var1.length() > 255) {
         throw new IllegalArgumentException("username: **** (expected: less than 256 chars)");
      } else if (var2.length() > 255) {
         throw new IllegalArgumentException("password: **** (expected: less than 256 chars)");
      } else {
         this.username = var1;
         this.password = var2;
      }
   }

   public String username() {
      return this.username;
   }

   public String password() {
      return this.password;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", username: ");
      } else {
         var1.append("(username: ");
      }

      var1.append(this.username());
      var1.append(", password: ****)");
      return var1.toString();
   }
}
