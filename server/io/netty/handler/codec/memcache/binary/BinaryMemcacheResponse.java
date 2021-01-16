package io.netty.handler.codec.memcache.binary;

public interface BinaryMemcacheResponse extends BinaryMemcacheMessage {
   short status();

   BinaryMemcacheResponse setStatus(short var1);

   BinaryMemcacheResponse retain();

   BinaryMemcacheResponse retain(int var1);

   BinaryMemcacheResponse touch();

   BinaryMemcacheResponse touch(Object var1);
}
