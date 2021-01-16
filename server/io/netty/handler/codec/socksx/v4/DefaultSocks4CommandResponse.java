package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSocks4CommandResponse extends AbstractSocks4Message implements Socks4CommandResponse {
   private final Socks4CommandStatus status;
   private final String dstAddr;
   private final int dstPort;

   public DefaultSocks4CommandResponse(Socks4CommandStatus var1) {
      this(var1, (String)null, 0);
   }

   public DefaultSocks4CommandResponse(Socks4CommandStatus var1, String var2, int var3) {
      super();
      if (var1 == null) {
         throw new NullPointerException("cmdStatus");
      } else if (var2 != null && !NetUtil.isValidIpV4Address(var2)) {
         throw new IllegalArgumentException("dstAddr: " + var2 + " (expected: a valid IPv4 address)");
      } else if (var3 >= 0 && var3 <= 65535) {
         this.status = var1;
         this.dstAddr = var2;
         this.dstPort = var3;
      } else {
         throw new IllegalArgumentException("dstPort: " + var3 + " (expected: 0~65535)");
      }
   }

   public Socks4CommandStatus status() {
      return this.status;
   }

   public String dstAddr() {
      return this.dstAddr;
   }

   public int dstPort() {
      return this.dstPort;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(96);
      var1.append(StringUtil.simpleClassName((Object)this));
      DecoderResult var2 = this.decoderResult();
      if (!var2.isSuccess()) {
         var1.append("(decoderResult: ");
         var1.append(var2);
         var1.append(", dstAddr: ");
      } else {
         var1.append("(dstAddr: ");
      }

      var1.append(this.dstAddr());
      var1.append(", dstPort: ");
      var1.append(this.dstPort());
      var1.append(')');
      return var1.toString();
   }
}
