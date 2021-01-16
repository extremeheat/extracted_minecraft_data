package io.netty.handler.codec.memcache.binary;

public interface BinaryMemcacheRequest extends BinaryMemcacheMessage {
   short reserved();

   BinaryMemcacheRequest setReserved(short var1);

   BinaryMemcacheRequest retain();

   BinaryMemcacheRequest retain(int var1);

   BinaryMemcacheRequest touch();

   BinaryMemcacheRequest touch(Object var1);
}
