package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;

final class CompressionUtil {
   private CompressionUtil() {
      super();
   }

   static void checkChecksum(ByteBufChecksum var0, ByteBuf var1, int var2) {
      var0.reset();
      var0.update(var1, var1.readerIndex(), var1.readableBytes());
      int var3 = (int)var0.getValue();
      if (var3 != var2) {
         throw new DecompressionException(String.format("stream corrupted: mismatching checksum: %d (expected: %d)", var3, var2));
      }
   }

   static ByteBuffer safeNioBuffer(ByteBuf var0) {
      return var0.nioBufferCount() == 1 ? var0.internalNioBuffer(var0.readerIndex(), var0.readableBytes()) : var0.nioBuffer();
   }
}
