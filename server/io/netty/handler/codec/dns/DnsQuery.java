package io.netty.handler.codec.dns;

public interface DnsQuery extends DnsMessage {
   DnsQuery setId(int var1);

   DnsQuery setOpCode(DnsOpCode var1);

   DnsQuery setRecursionDesired(boolean var1);

   DnsQuery setZ(int var1);

   DnsQuery setRecord(DnsSection var1, DnsRecord var2);

   DnsQuery addRecord(DnsSection var1, DnsRecord var2);

   DnsQuery addRecord(DnsSection var1, int var2, DnsRecord var3);

   DnsQuery clear(DnsSection var1);

   DnsQuery clear();

   DnsQuery touch();

   DnsQuery touch(Object var1);

   DnsQuery retain();

   DnsQuery retain(int var1);
}
