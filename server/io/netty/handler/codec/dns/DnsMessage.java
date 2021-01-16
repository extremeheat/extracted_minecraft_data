package io.netty.handler.codec.dns;

import io.netty.util.ReferenceCounted;

public interface DnsMessage extends ReferenceCounted {
   int id();

   DnsMessage setId(int var1);

   DnsOpCode opCode();

   DnsMessage setOpCode(DnsOpCode var1);

   boolean isRecursionDesired();

   DnsMessage setRecursionDesired(boolean var1);

   int z();

   DnsMessage setZ(int var1);

   int count(DnsSection var1);

   int count();

   <T extends DnsRecord> T recordAt(DnsSection var1);

   <T extends DnsRecord> T recordAt(DnsSection var1, int var2);

   DnsMessage setRecord(DnsSection var1, DnsRecord var2);

   <T extends DnsRecord> T setRecord(DnsSection var1, int var2, DnsRecord var3);

   DnsMessage addRecord(DnsSection var1, DnsRecord var2);

   DnsMessage addRecord(DnsSection var1, int var2, DnsRecord var3);

   <T extends DnsRecord> T removeRecord(DnsSection var1, int var2);

   DnsMessage clear(DnsSection var1);

   DnsMessage clear();

   DnsMessage touch();

   DnsMessage touch(Object var1);

   DnsMessage retain();

   DnsMessage retain(int var1);
}
