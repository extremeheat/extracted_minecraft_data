package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BinaryMemcacheResponseDecoder extends AbstractBinaryMemcacheDecoder<BinaryMemcacheResponse> {
   public BinaryMemcacheResponseDecoder() {
      this(8192);
   }

   public BinaryMemcacheResponseDecoder(int var1) {
      super(var1);
   }

   protected BinaryMemcacheResponse decodeHeader(ByteBuf var1) {
      DefaultBinaryMemcacheResponse var2 = new DefaultBinaryMemcacheResponse();
      var2.setMagic(var1.readByte());
      var2.setOpcode(var1.readByte());
      var2.setKeyLength(var1.readShort());
      var2.setExtrasLength(var1.readByte());
      var2.setDataType(var1.readByte());
      var2.setStatus(var1.readShort());
      var2.setTotalBodyLength(var1.readInt());
      var2.setOpaque(var1.readInt());
      var2.setCas(var1.readLong());
      return var2;
   }

   protected BinaryMemcacheResponse buildInvalidMessage() {
      return new DefaultBinaryMemcacheResponse(Unpooled.EMPTY_BUFFER, Unpooled.EMPTY_BUFFER);
   }
}
