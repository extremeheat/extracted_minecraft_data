package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultDnsRawRecord extends AbstractDnsRecord implements DnsRawRecord {
   private final ByteBuf content;

   public DefaultDnsRawRecord(String var1, DnsRecordType var2, long var3, ByteBuf var5) {
      this(var1, var2, 1, var3, var5);
   }

   public DefaultDnsRawRecord(String var1, DnsRecordType var2, int var3, long var4, ByteBuf var6) {
      super(var1, var2, var3, var4);
      this.content = (ByteBuf)ObjectUtil.checkNotNull(var6, "content");
   }

   public ByteBuf content() {
      return this.content;
   }

   public DnsRawRecord copy() {
      return this.replace(this.content().copy());
   }

   public DnsRawRecord duplicate() {
      return this.replace(this.content().duplicate());
   }

   public DnsRawRecord retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public DnsRawRecord replace(ByteBuf var1) {
      return new DefaultDnsRawRecord(this.name(), this.type(), this.dnsClass(), this.timeToLive(), var1);
   }

   public int refCnt() {
      return this.content().refCnt();
   }

   public DnsRawRecord retain() {
      this.content().retain();
      return this;
   }

   public DnsRawRecord retain(int var1) {
      this.content().retain(var1);
      return this;
   }

   public boolean release() {
      return this.content().release();
   }

   public boolean release(int var1) {
      return this.content().release(var1);
   }

   public DnsRawRecord touch() {
      this.content().touch();
      return this;
   }

   public DnsRawRecord touch(Object var1) {
      this.content().touch(var1);
      return this;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(64)).append(StringUtil.simpleClassName((Object)this)).append('(');
      DnsRecordType var2 = this.type();
      if (var2 != DnsRecordType.OPT) {
         var1.append(this.name().isEmpty() ? "<root>" : this.name()).append(' ').append(this.timeToLive()).append(' ');
         DnsMessageUtil.appendRecordClass(var1, this.dnsClass()).append(' ').append(var2.name());
      } else {
         var1.append("OPT flags:").append(this.timeToLive()).append(" udp:").append(this.dnsClass());
      }

      var1.append(' ').append(this.content().readableBytes()).append("B)");
      return var1.toString();
   }
}
