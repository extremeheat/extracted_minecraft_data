package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import javax.annotation.Nullable;

public class Varint21FrameDecoder extends ByteToMessageDecoder {
   private static final int MAX_VARINT21_BYTES = 3;
   private final ByteBuf helperBuf = Unpooled.directBuffer(3);
   @Nullable
   private final BandwidthDebugMonitor monitor;

   public Varint21FrameDecoder(@Nullable BandwidthDebugMonitor var1) {
      super();
      this.monitor = var1;
   }

   protected void handlerRemoved0(ChannelHandlerContext var1) {
      this.helperBuf.release();
   }

   private static boolean copyVarint(ByteBuf var0, ByteBuf var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         if (!var0.isReadable()) {
            return false;
         }

         byte var3 = var0.readByte();
         var1.writeByte(var3);
         if (!VarInt.hasContinuationBit(var3)) {
            return true;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) {
      var2.markReaderIndex();
      this.helperBuf.clear();
      if (!copyVarint(var2, this.helperBuf)) {
         var2.resetReaderIndex();
      } else {
         int var4 = VarInt.read(this.helperBuf);
         if (var2.readableBytes() < var4) {
            var2.resetReaderIndex();
         } else {
            if (this.monitor != null) {
               this.monitor.onReceive(var4 + VarInt.getByteSize(var4));
            }

            var3.add(var2.readBytes(var4));
         }
      }
   }
}
