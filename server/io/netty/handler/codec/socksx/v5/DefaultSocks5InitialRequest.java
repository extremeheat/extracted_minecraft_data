package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefaultSocks5InitialRequest extends AbstractSocks5Message implements Socks5InitialRequest {
   private final List<Socks5AuthMethod> authMethods;

   public DefaultSocks5InitialRequest(Socks5AuthMethod... var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("authMethods");
      } else {
         ArrayList var2 = new ArrayList(var1.length);
         Socks5AuthMethod[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Socks5AuthMethod var6 = var3[var5];
            if (var6 == null) {
               break;
            }

            var2.add(var6);
         }

         if (var2.isEmpty()) {
            throw new IllegalArgumentException("authMethods is empty");
         } else {
            this.authMethods = Collections.unmodifiableList(var2);
         }
      }
   }

   public DefaultSocks5InitialRequest(Iterable<Socks5AuthMethod> var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("authSchemes");
      } else {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Socks5AuthMethod var4 = (Socks5AuthMethod)var3.next();
            if (var4 == null) {
               break;
            }

            var2.add(var4);
         }

         if (var2.isEmpty()) {
            throw new IllegalArgumentException("authMethods is empty");
         } else {
            this.authMethods = Collections.unmodifiableList(var2);
         }
      }
   }

   public List<Socks5AuthMethod> authMethods() {
      return this.authMethods;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", authMethods: ");
      } else {
         var1.append("(authMethods: ");
      }

      var1.append(this.authMethods());
      var1.append(')');
      return var1.toString();
   }
}
