package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface MemcacheContent extends MemcacheObject, ByteBufHolder {
   MemcacheContent copy();

   MemcacheContent duplicate();

   MemcacheContent retainedDuplicate();

   MemcacheContent replace(ByteBuf var1);

   MemcacheContent retain();

   MemcacheContent retain(int var1);

   MemcacheContent touch();

   MemcacheContent touch(Object var1);
}
