package io.netty.handler.codec.dns;

import io.netty.channel.socket.InternetProtocolFamily;
import java.util.Arrays;

public final class DefaultDnsOptEcsRecord extends AbstractDnsOptPseudoRrRecord implements DnsOptEcsRecord {
   private final int srcPrefixLength;
   private final byte[] address;

   public DefaultDnsOptEcsRecord(int var1, int var2, int var3, int var4, byte[] var5) {
      super(var1, var2, var3);
      this.srcPrefixLength = var4;
      this.address = (byte[])verifyAddress(var5).clone();
   }

   public DefaultDnsOptEcsRecord(int var1, int var2, byte[] var3) {
      this(var1, 0, 0, var2, var3);
   }

   public DefaultDnsOptEcsRecord(int var1, InternetProtocolFamily var2) {
      this(var1, 0, 0, 0, var2.localhost().getAddress());
   }

   private static byte[] verifyAddress(byte[] var0) {
      if (var0.length != 4 && var0.length != 16) {
         throw new IllegalArgumentException("bytes.length must either 4 or 16");
      } else {
         return var0;
      }
   }

   public int sourcePrefixLength() {
      return this.srcPrefixLength;
   }

   public int scopePrefixLength() {
      return 0;
   }

   public byte[] address() {
      return (byte[])this.address.clone();
   }

   public String toString() {
      StringBuilder var1 = this.toStringBuilder();
      var1.setLength(var1.length() - 1);
      return var1.append(" address:").append(Arrays.toString(this.address)).append(" sourcePrefixLength:").append(this.sourcePrefixLength()).append(" scopePrefixLength:").append(this.scopePrefixLength()).append(')').toString();
   }
}
