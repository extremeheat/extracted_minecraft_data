package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;

public interface FullMemcacheMessage extends MemcacheMessage, LastMemcacheContent {
   FullMemcacheMessage copy();

   FullMemcacheMessage duplicate();

   FullMemcacheMessage retainedDuplicate();

   FullMemcacheMessage replace(ByteBuf var1);

   FullMemcacheMessage retain(int var1);

   FullMemcacheMessage retain();

   FullMemcacheMessage touch();

   FullMemcacheMessage touch(Object var1);
}
