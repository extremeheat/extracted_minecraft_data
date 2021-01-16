package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;

public interface DnsRecordDecoder {
   DnsRecordDecoder DEFAULT = new DefaultDnsRecordDecoder();

   DnsQuestion decodeQuestion(ByteBuf var1) throws Exception;

   <T extends DnsRecord> T decodeRecord(ByteBuf var1) throws Exception;
}
