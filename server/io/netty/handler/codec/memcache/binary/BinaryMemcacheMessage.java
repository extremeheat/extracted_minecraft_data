package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.MemcacheMessage;

public interface BinaryMemcacheMessage extends MemcacheMessage {
   byte magic();

   BinaryMemcacheMessage setMagic(byte var1);

   byte opcode();

   BinaryMemcacheMessage setOpcode(byte var1);

   short keyLength();

   byte extrasLength();

   byte dataType();

   BinaryMemcacheMessage setDataType(byte var1);

   int totalBodyLength();

   BinaryMemcacheMessage setTotalBodyLength(int var1);

   int opaque();

   BinaryMemcacheMessage setOpaque(int var1);

   long cas();

   BinaryMemcacheMessage setCas(long var1);

   ByteBuf key();

   BinaryMemcacheMessage setKey(ByteBuf var1);

   ByteBuf extras();

   BinaryMemcacheMessage setExtras(ByteBuf var1);

   BinaryMemcacheMessage retain();

   BinaryMemcacheMessage retain(int var1);

   BinaryMemcacheMessage touch();

   BinaryMemcacheMessage touch(Object var1);
}
