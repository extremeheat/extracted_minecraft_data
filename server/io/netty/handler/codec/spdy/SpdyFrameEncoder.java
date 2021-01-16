package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Set;

public class SpdyFrameEncoder {
   private final int version;

   public SpdyFrameEncoder(SpdyVersion var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("spdyVersion");
      } else {
         this.version = var1.getVersion();
      }
   }

   private void writeControlFrameHeader(ByteBuf var1, int var2, byte var3, int var4) {
      var1.writeShort(this.version | '\u8000');
      var1.writeShort(var2);
      var1.writeByte(var3);
      var1.writeMedium(var4);
   }

   public ByteBuf encodeDataFrame(ByteBufAllocator var1, int var2, boolean var3, ByteBuf var4) {
      int var5 = var3 ? 1 : 0;
      int var6 = var4.readableBytes();
      ByteBuf var7 = var1.ioBuffer(8 + var6).order(ByteOrder.BIG_ENDIAN);
      var7.writeInt(var2 & 2147483647);
      var7.writeByte(var5);
      var7.writeMedium(var6);
      var7.writeBytes(var4, var4.readerIndex(), var6);
      return var7;
   }

   public ByteBuf encodeSynStreamFrame(ByteBufAllocator var1, int var2, int var3, byte var4, boolean var5, boolean var6, ByteBuf var7) {
      int var8 = var7.readableBytes();
      int var9 = var5 ? 1 : 0;
      if (var6) {
         var9 = (byte)(var9 | 2);
      }

      int var10 = 10 + var8;
      ByteBuf var11 = var1.ioBuffer(8 + var10).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var11, 1, (byte)var9, var10);
      var11.writeInt(var2);
      var11.writeInt(var3);
      var11.writeShort((var4 & 255) << 13);
      var11.writeBytes(var7, var7.readerIndex(), var8);
      return var11;
   }

   public ByteBuf encodeSynReplyFrame(ByteBufAllocator var1, int var2, boolean var3, ByteBuf var4) {
      int var5 = var4.readableBytes();
      int var6 = var3 ? 1 : 0;
      int var7 = 4 + var5;
      ByteBuf var8 = var1.ioBuffer(8 + var7).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var8, 2, (byte)var6, var7);
      var8.writeInt(var2);
      var8.writeBytes(var4, var4.readerIndex(), var5);
      return var8;
   }

   public ByteBuf encodeRstStreamFrame(ByteBufAllocator var1, int var2, int var3) {
      byte var4 = 0;
      byte var5 = 8;
      ByteBuf var6 = var1.ioBuffer(8 + var5).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var6, 3, var4, var5);
      var6.writeInt(var2);
      var6.writeInt(var3);
      return var6;
   }

   public ByteBuf encodeSettingsFrame(ByteBufAllocator var1, SpdySettingsFrame var2) {
      Set var3 = var2.ids();
      int var4 = var3.size();
      int var5 = var2.clearPreviouslyPersistedSettings() ? 1 : 0;
      int var6 = 4 + 8 * var4;
      ByteBuf var7 = var1.ioBuffer(8 + var6).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var7, 4, (byte)var5, var6);
      var7.writeInt(var4);
      Iterator var8 = var3.iterator();

      while(var8.hasNext()) {
         Integer var9 = (Integer)var8.next();
         byte var10 = 0;
         if (var2.isPersistValue(var9)) {
            var10 = (byte)(var10 | 1);
         }

         if (var2.isPersisted(var9)) {
            var10 = (byte)(var10 | 2);
         }

         var7.writeByte(var10);
         var7.writeMedium(var9);
         var7.writeInt(var2.getValue(var9));
      }

      return var7;
   }

   public ByteBuf encodePingFrame(ByteBufAllocator var1, int var2) {
      byte var3 = 0;
      byte var4 = 4;
      ByteBuf var5 = var1.ioBuffer(8 + var4).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var5, 6, var3, var4);
      var5.writeInt(var2);
      return var5;
   }

   public ByteBuf encodeGoAwayFrame(ByteBufAllocator var1, int var2, int var3) {
      byte var4 = 0;
      byte var5 = 8;
      ByteBuf var6 = var1.ioBuffer(8 + var5).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var6, 7, var4, var5);
      var6.writeInt(var2);
      var6.writeInt(var3);
      return var6;
   }

   public ByteBuf encodeHeadersFrame(ByteBufAllocator var1, int var2, boolean var3, ByteBuf var4) {
      int var5 = var4.readableBytes();
      int var6 = var3 ? 1 : 0;
      int var7 = 4 + var5;
      ByteBuf var8 = var1.ioBuffer(8 + var7).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var8, 8, (byte)var6, var7);
      var8.writeInt(var2);
      var8.writeBytes(var4, var4.readerIndex(), var5);
      return var8;
   }

   public ByteBuf encodeWindowUpdateFrame(ByteBufAllocator var1, int var2, int var3) {
      byte var4 = 0;
      byte var5 = 8;
      ByteBuf var6 = var1.ioBuffer(8 + var5).order(ByteOrder.BIG_ENDIAN);
      this.writeControlFrameHeader(var6, 9, var4, var5);
      var6.writeInt(var2);
      var6.writeInt(var3);
      return var6;
   }
}
