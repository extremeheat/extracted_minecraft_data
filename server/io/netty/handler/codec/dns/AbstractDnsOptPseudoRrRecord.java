package io.netty.handler.codec.dns;

import io.netty.util.internal.StringUtil;

public abstract class AbstractDnsOptPseudoRrRecord extends AbstractDnsRecord implements DnsOptPseudoRecord {
   protected AbstractDnsOptPseudoRrRecord(int var1, int var2, int var3) {
      super("", DnsRecordType.OPT, var1, packIntoLong(var2, var3));
   }

   protected AbstractDnsOptPseudoRrRecord(int var1) {
      super("", DnsRecordType.OPT, var1, 0L);
   }

   private static long packIntoLong(int var0, int var1) {
      return (long)((var0 & 255) << 24 | (var1 & 255) << 16 | 0 | 0) & 4294967295L;
   }

   public int extendedRcode() {
      return (short)((int)this.timeToLive() >> 24 & 255);
   }

   public int version() {
      return (short)((int)this.timeToLive() >> 16 & 255);
   }

   public int flags() {
      return (short)((short)((int)this.timeToLive()) & 255);
   }

   public String toString() {
      return this.toStringBuilder().toString();
   }

   final StringBuilder toStringBuilder() {
      return (new StringBuilder(64)).append(StringUtil.simpleClassName((Object)this)).append('(').append("OPT flags:").append(this.flags()).append(" version:").append(this.version()).append(" extendedRecode:").append(this.extendedRcode()).append(" udp:").append(this.dnsClass()).append(')');
   }
}
