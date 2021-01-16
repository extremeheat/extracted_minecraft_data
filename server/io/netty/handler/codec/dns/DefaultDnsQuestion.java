package io.netty.handler.codec.dns;

import io.netty.util.internal.StringUtil;

public class DefaultDnsQuestion extends AbstractDnsRecord implements DnsQuestion {
   public DefaultDnsQuestion(String var1, DnsRecordType var2) {
      super(var1, var2, 0L);
   }

   public DefaultDnsQuestion(String var1, DnsRecordType var2, int var3) {
      super(var1, var2, var3, 0L);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append(StringUtil.simpleClassName((Object)this)).append('(').append(this.name()).append(' ');
      DnsMessageUtil.appendRecordClass(var1, this.dnsClass()).append(' ').append(this.type().name()).append(')');
      return var1.toString();
   }
}
