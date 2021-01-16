package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface DnsRawRecord extends DnsRecord, ByteBufHolder {
   DnsRawRecord copy();

   DnsRawRecord duplicate();

   DnsRawRecord retainedDuplicate();

   DnsRawRecord replace(ByteBuf var1);

   DnsRawRecord retain();

   DnsRawRecord retain(int var1);

   DnsRawRecord touch();

   DnsRawRecord touch(Object var1);
}
