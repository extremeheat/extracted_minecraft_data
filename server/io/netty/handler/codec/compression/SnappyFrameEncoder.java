package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SnappyFrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final int MIN_COMPRESSIBLE_LENGTH = 18;
   private static final byte[] STREAM_START = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
   private final Snappy snappy = new Snappy();
   private boolean started;

   public SnappyFrameEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      if (var2.isReadable()) {
         if (!this.started) {
            this.started = true;
            var3.writeBytes(STREAM_START);
         }

         int var4 = var2.readableBytes();
         if (var4 <= 18) {
            writeUnencodedChunk(var2, var3, var4);
         } else {
            while(true) {
               int var5 = var3.writerIndex() + 1;
               ByteBuf var6;
               if (var4 < 18) {
                  var6 = var2.readSlice(var4);
                  writeUnencodedChunk(var6, var3, var4);
                  break;
               }

               var3.writeInt(0);
               if (var4 <= 32767) {
                  var6 = var2.readSlice(var4);
                  calculateAndWriteChecksum(var6, var3);
                  this.snappy.encode(var6, var3, var4);
                  setChunkLength(var3, var5);
                  break;
               }

               var6 = var2.readSlice(32767);
               calculateAndWriteChecksum(var6, var3);
               this.snappy.encode(var6, var3, 32767);
               setChunkLength(var3, var5);
               var4 -= 32767;
            }
         }

      }
   }

   private static void writeUnencodedChunk(ByteBuf var0, ByteBuf var1, int var2) {
      var1.writeByte(1);
      writeChunkLength(var1, var2 + 4);
      calculateAndWriteChecksum(var0, var1);
      var1.writeBytes(var0, var2);
   }

   private static void setChunkLength(ByteBuf var0, int var1) {
      int var2 = var0.writerIndex() - var1 - 3;
      if (var2 >>> 24 != 0) {
         throw new CompressionException("compressed data too large: " + var2);
      } else {
         var0.setMediumLE(var1, var2);
      }
   }

   private static void writeChunkLength(ByteBuf var0, int var1) {
      var0.writeMediumLE(var1);
   }

   private static void calculateAndWriteChecksum(ByteBuf var0, ByteBuf var1) {
      var1.writeIntLE(Snappy.calculateChecksum(var0));
   }
}
