package io.netty.handler.codec.dns;

public class DefaultDnsQuery extends AbstractDnsMessage implements DnsQuery {
   public DefaultDnsQuery(int var1) {
      super(var1);
   }

   public DefaultDnsQuery(int var1, DnsOpCode var2) {
      super(var1, var2);
   }

   public DnsQuery setId(int var1) {
      return (DnsQuery)super.setId(var1);
   }

   public DnsQuery setOpCode(DnsOpCode var1) {
      return (DnsQuery)super.setOpCode(var1);
   }

   public DnsQuery setRecursionDesired(boolean var1) {
      return (DnsQuery)super.setRecursionDesired(var1);
   }

   public DnsQuery setZ(int var1) {
      return (DnsQuery)super.setZ(var1);
   }

   public DnsQuery setRecord(DnsSection var1, DnsRecord var2) {
      return (DnsQuery)super.setRecord(var1, var2);
   }

   public DnsQuery addRecord(DnsSection var1, DnsRecord var2) {
      return (DnsQuery)super.addRecord(var1, var2);
   }

   public DnsQuery addRecord(DnsSection var1, int var2, DnsRecord var3) {
      return (DnsQuery)super.addRecord(var1, var2, var3);
   }

   public DnsQuery clear(DnsSection var1) {
      return (DnsQuery)super.clear(var1);
   }

   public DnsQuery clear() {
      return (DnsQuery)super.clear();
   }

   public DnsQuery touch() {
      return (DnsQuery)super.touch();
   }

   public DnsQuery touch(Object var1) {
      return (DnsQuery)super.touch(var1);
   }

   public DnsQuery retain() {
      return (DnsQuery)super.retain();
   }

   public DnsQuery retain(int var1) {
      return (DnsQuery)super.retain(var1);
   }

   public String toString() {
      return DnsMessageUtil.appendQuery(new StringBuilder(128), this).toString();
   }
}
