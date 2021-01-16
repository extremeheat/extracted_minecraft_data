package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.memcache.AbstractMemcacheObjectEncoder;

public abstract class AbstractBinaryMemcacheEncoder<M extends BinaryMemcacheMessage> extends AbstractMemcacheObjectEncoder<M> {
   private static final int MINIMUM_HEADER_SIZE = 24;

   public AbstractBinaryMemcacheEncoder() {
      super();
   }

   protected ByteBuf encodeMessage(ChannelHandlerContext var1, M var2) {
      ByteBuf var3 = var1.alloc().buffer(24 + var2.extrasLength() + var2.keyLength());
      this.encodeHeader(var3, var2);
      encodeExtras(var3, var2.extras());
      encodeKey(var3, var2.key());
      return var3;
   }

   private static void encodeExtras(ByteBuf var0, ByteBuf var1) {
      if (var1 != null && var1.isReadable()) {
         var0.writeBytes(var1);
      }
   }

   private static void encodeKey(ByteBuf var0, ByteBuf var1) {
      if (var1 != null && var1.isReadable()) {
         var0.writeBytes(var1);
      }
   }

   protected abstract void encodeHeader(ByteBuf var1, M var2);
}
