package io.netty.handler.codec.dns;

public interface DnsResponse extends DnsMessage {
   boolean isAuthoritativeAnswer();

   DnsResponse setAuthoritativeAnswer(boolean var1);

   boolean isTruncated();

   DnsResponse setTruncated(boolean var1);

   boolean isRecursionAvailable();

   DnsResponse setRecursionAvailable(boolean var1);

   DnsResponseCode code();

   DnsResponse setCode(DnsResponseCode var1);

   DnsResponse setId(int var1);

   DnsResponse setOpCode(DnsOpCode var1);

   DnsResponse setRecursionDesired(boolean var1);

   DnsResponse setZ(int var1);

   DnsResponse setRecord(DnsSection var1, DnsRecord var2);

   DnsResponse addRecord(DnsSection var1, DnsRecord var2);

   DnsResponse addRecord(DnsSection var1, int var2, DnsRecord var3);

   DnsResponse clear(DnsSection var1);

   DnsResponse clear();

   DnsResponse touch();

   DnsResponse touch(Object var1);

   DnsResponse retain();

   DnsResponse retain(int var1);
}
