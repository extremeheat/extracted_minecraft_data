package io.netty.handler.codec.dns;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultDnsPtrRecord extends AbstractDnsRecord implements DnsPtrRecord {
   private final String hostname;

   public DefaultDnsPtrRecord(String var1, int var2, long var3, String var5) {
      super(var1, DnsRecordType.PTR, var2, var3);
      this.hostname = (String)ObjectUtil.checkNotNull(var5, "hostname");
   }

   public String hostname() {
      return this.hostname;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(64)).append(StringUtil.simpleClassName((Object)this)).append('(');
      DnsRecordType var2 = this.type();
      var1.append(this.name().isEmpty() ? "<root>" : this.name()).append(' ').append(this.timeToLive()).append(' ');
      DnsMessageUtil.appendRecordClass(var1, this.dnsClass()).append(' ').append(var2.name());
      var1.append(' ').append(this.hostname);
      return var1.toString();
   }
}
