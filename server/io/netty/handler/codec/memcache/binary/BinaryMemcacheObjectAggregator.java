package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.memcache.AbstractMemcacheObjectAggregator;
import io.netty.handler.codec.memcache.FullMemcacheMessage;
import io.netty.handler.codec.memcache.MemcacheObject;

public class BinaryMemcacheObjectAggregator extends AbstractMemcacheObjectAggregator<BinaryMemcacheMessage> {
   public BinaryMemcacheObjectAggregator(int var1) {
      super(var1);
   }

   protected boolean isStartMessage(MemcacheObject var1) throws Exception {
      return var1 instanceof BinaryMemcacheMessage;
   }

   protected FullMemcacheMessage beginAggregation(BinaryMemcacheMessage var1, ByteBuf var2) throws Exception {
      if (var1 instanceof BinaryMemcacheRequest) {
         return toFullRequest((BinaryMemcacheRequest)var1, var2);
      } else if (var1 instanceof BinaryMemcacheResponse) {
         return toFullResponse((BinaryMemcacheResponse)var1, var2);
      } else {
         throw new Error();
      }
   }

   private static FullBinaryMemcacheRequest toFullRequest(BinaryMemcacheRequest var0, ByteBuf var1) {
      ByteBuf var2 = var0.key() == null ? null : var0.key().retain();
      ByteBuf var3 = var0.extras() == null ? null : var0.extras().retain();
      DefaultFullBinaryMemcacheRequest var4 = new DefaultFullBinaryMemcacheRequest(var2, var3, var1);
      var4.setMagic(var0.magic());
      var4.setOpcode(var0.opcode());
      var4.setKeyLength(var0.keyLength());
      var4.setExtrasLength(var0.extrasLength());
      var4.setDataType(var0.dataType());
      var4.setTotalBodyLength(var0.totalBodyLength());
      var4.setOpaque(var0.opaque());
      var4.setCas(var0.cas());
      var4.setReserved(var0.reserved());
      return var4;
   }

   private static FullBinaryMemcacheResponse toFullResponse(BinaryMemcacheResponse var0, ByteBuf var1) {
      ByteBuf var2 = var0.key() == null ? null : var0.key().retain();
      ByteBuf var3 = var0.extras() == null ? null : var0.extras().retain();
      DefaultFullBinaryMemcacheResponse var4 = new DefaultFullBinaryMemcacheResponse(var2, var3, var1);
      var4.setMagic(var0.magic());
      var4.setOpcode(var0.opcode());
      var4.setKeyLength(var0.keyLength());
      var4.setExtrasLength(var0.extrasLength());
      var4.setDataType(var0.dataType());
      var4.setTotalBodyLength(var0.totalBodyLength());
      var4.setOpaque(var0.opaque());
      var4.setCas(var0.cas());
      var4.setStatus(var0.status());
      return var4;
   }
}
