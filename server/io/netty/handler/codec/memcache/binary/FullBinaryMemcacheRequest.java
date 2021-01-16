package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.FullMemcacheMessage;

public interface FullBinaryMemcacheRequest extends BinaryMemcacheRequest, FullMemcacheMessage {
   FullBinaryMemcacheRequest copy();

   FullBinaryMemcacheRequest duplicate();

   FullBinaryMemcacheRequest retainedDuplicate();

   FullBinaryMemcacheRequest replace(ByteBuf var1);

   FullBinaryMemcacheRequest retain(int var1);

   FullBinaryMemcacheRequest retain();

   FullBinaryMemcacheRequest touch();

   FullBinaryMemcacheRequest touch(Object var1);
}
