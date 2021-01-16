package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;

public class BinaryMemcacheResponseEncoder extends AbstractBinaryMemcacheEncoder<BinaryMemcacheResponse> {
   public BinaryMemcacheResponseEncoder() {
      super();
   }

   protected void encodeHeader(ByteBuf var1, BinaryMemcacheResponse var2) {
      var1.writeByte(var2.magic());
      var1.writeByte(var2.opcode());
      var1.writeShort(var2.keyLength());
      var1.writeByte(var2.extrasLength());
      var1.writeByte(var2.dataType());
      var1.writeShort(var2.status());
      var1.writeInt(var2.totalBodyLength());
      var1.writeInt(var2.opaque());
      var1.writeLong(var2.cas());
   }
}
