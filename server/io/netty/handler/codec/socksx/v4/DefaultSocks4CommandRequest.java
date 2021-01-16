package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public class DefaultSocks4CommandRequest extends AbstractSocks4Message implements Socks4CommandRequest {
   private final Socks4CommandType type;
   private final String dstAddr;
   private final int dstPort;
   private final String userId;

   public DefaultSocks4CommandRequest(Socks4CommandType var1, String var2, int var3) {
      this(var1, var2, var3, "");
   }

   public DefaultSocks4CommandRequest(Socks4CommandType var1, String var2, int var3, String var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("type");
      } else if (var2 == null) {
         throw new NullPointerException("dstAddr");
      } else if (var3 > 0 && var3 < 65536) {
         if (var4 == null) {
            throw new NullPointerException("userId");
         } else {
            this.userId = var4;
            this.type = var1;
            this.dstAddr = IDN.toASCII(var2);
            this.dstPort = var3;
         }
      } else {
         throw new IllegalArgumentException("dstPort: " + var3 + " (expected: 1~65535)");
      }
   }

   public Socks4CommandType type() {
      return this.type;
   }

   public String dstAddr() {
      return this.dstAddr;
   }

   public int dstPort() {
      return this.dstPort;
   }

   public String userId() {
      return this.userId;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", type: ");
      } else {
         var1.append("(type: ");
      }

      var1.append(this.type());
      var1.append(", dstAddr: ");
      var1.append(this.dstAddr());
      var1.append(", dstPort: ");
      var1.append(this.dstPort());
      var1.append(", userId: ");
      var1.append(this.userId());
      var1.append(')');
      return var1.toString();
   }
}
