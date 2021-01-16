package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private final int level;
   private final Checksum checksum;

   public FastLzFrameEncoder() {
      this(0, (Checksum)null);
   }

   public FastLzFrameEncoder(int var1) {
      this(var1, (Checksum)null);
   }

   public FastLzFrameEncoder(boolean var1) {
      this(0, var1 ? new Adler32() : null);
   }

   public FastLzFrameEncoder(int var1, Checksum var2) {
      super(false);
      if (var1 != 0 && var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException(String.format("level: %d (expected: %d or %d or %d)", var1, 0, 1, 2));
      } else {
         this.level = var1;
         this.checksum = var2;
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      Checksum var4 = this.checksum;

      while(var2.isReadable()) {
         int var5 = var2.readerIndex();
         int var6 = Math.min(var2.readableBytes(), 65535);
         int var7 = var3.writerIndex();
         var3.setMedium(var7, 4607066);
         int var8 = var7 + 4 + (var4 != null ? 4 : 0);
         byte var9;
         int var10;
         byte[] var11;
         int var12;
         if (var6 < 32) {
            var9 = 0;
            var3.ensureWritable(var8 + 2 + var6);
            var11 = var3.array();
            var12 = var3.arrayOffset() + var8 + 2;
            if (var4 != null) {
               byte[] var13;
               int var14;
               if (var2.hasArray()) {
                  var13 = var2.array();
                  var14 = var2.arrayOffset() + var5;
               } else {
                  var13 = new byte[var6];
                  var2.getBytes(var5, var13);
                  var14 = 0;
               }

               var4.reset();
               var4.update(var13, var14, var6);
               var3.setInt(var7 + 4, (int)var4.getValue());
               System.arraycopy(var13, var14, var11, var12, var6);
            } else {
               var2.getBytes(var5, var11, var12, var6);
            }

            var10 = var6;
         } else {
            if (var2.hasArray()) {
               var11 = var2.array();
               var12 = var2.arrayOffset() + var5;
            } else {
               var11 = new byte[var6];
               var2.getBytes(var5, var11);
               var12 = 0;
            }

            if (var4 != null) {
               var4.reset();
               var4.update(var11, var12, var6);
               var3.setInt(var7 + 4, (int)var4.getValue());
            }

            int var17 = FastLz.calculateOutputBufferLength(var6);
            var3.ensureWritable(var8 + 4 + var17);
            byte[] var18 = var3.array();
            int var15 = var3.arrayOffset() + var8 + 4;
            int var16 = FastLz.compress(var11, var12, var6, var18, var15, this.level);
            if (var16 < var6) {
               var9 = 1;
               var10 = var16;
               var3.setShort(var8, var16);
               var8 += 2;
            } else {
               var9 = 0;
               System.arraycopy(var11, var12, var18, var15 - 2, var6);
               var10 = var6;
            }
         }

         var3.setShort(var8, var6);
         var3.setByte(var7 + 3, var9 | (var4 != null ? 16 : 0));
         var3.writerIndex(var8 + 2 + var10);
         var2.skipBytes(var6);
      }

   }
}
