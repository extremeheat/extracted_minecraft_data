package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;

public interface DnsRecordEncoder {
   DnsRecordEncoder DEFAULT = new DefaultDnsRecordEncoder();

   void encodeQuestion(DnsQuestion var1, ByteBuf var2) throws Exception;

   void encodeRecord(DnsRecord var1, ByteBuf var2) throws Exception;
}
