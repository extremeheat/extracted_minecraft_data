package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface Http2GoAwayFrame extends Http2Frame, ByteBufHolder {
   long errorCode();

   int extraStreamIds();

   Http2GoAwayFrame setExtraStreamIds(int var1);

   int lastStreamId();

   ByteBuf content();

   Http2GoAwayFrame copy();

   Http2GoAwayFrame duplicate();

   Http2GoAwayFrame retainedDuplicate();

   Http2GoAwayFrame replace(ByteBuf var1);

   Http2GoAwayFrame retain();

   Http2GoAwayFrame retain(int var1);

   Http2GoAwayFrame touch();

   Http2GoAwayFrame touch(Object var1);
}
