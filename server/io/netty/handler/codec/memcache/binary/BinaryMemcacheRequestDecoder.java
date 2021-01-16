package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BinaryMemcacheRequestDecoder extends AbstractBinaryMemcacheDecoder<BinaryMemcacheRequest> {
   public BinaryMemcacheRequestDecoder() {
      this(8192);
   }

   public BinaryMemcacheRequestDecoder(int var1) {
      super(var1);
   }

   protected BinaryMemcacheRequest decodeHeader(ByteBuf var1) {
      DefaultBinaryMemcacheRequest var2 = new DefaultBinaryMemcacheRequest();
      var2.setMagic(var1.readByte());
      var2.setOpcode(var1.readByte());
      var2.setKeyLength(var1.readShort());
      var2.setExtrasLength(var1.readByte());
      var2.setDataType(var1.readByte());
      var2.setReserved(var1.readShort());
      var2.setTotalBodyLength(var1.readInt());
      var2.setOpaque(var1.readInt());
      var2.setCas(var1.readLong());
      return var2;
   }

   protected BinaryMemcacheRequest buildInvalidMessage() {
      return new DefaultBinaryMemcacheRequest(Unpooled.EMPTY_BUFFER, Unpooled.EMPTY_BUFFER);
   }
}
