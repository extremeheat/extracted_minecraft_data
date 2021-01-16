package io.netty.handler.codec.memcache;

import io.netty.util.ReferenceCounted;

public interface MemcacheMessage extends MemcacheObject, ReferenceCounted {
   MemcacheMessage retain();

   MemcacheMessage retain(int var1);

   MemcacheMessage touch();

   MemcacheMessage touch(Object var1);
}
