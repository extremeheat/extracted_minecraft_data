package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] encodeBuf = new byte[8192];
   private final Deflater deflater;
   private int threshold;

   public CompressionEncoder(int var1) {
      super();
      this.threshold = var1;
      this.deflater = new Deflater();
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) {
      int var4 = var2.readableBytes();
      FriendlyByteBuf var5 = new FriendlyByteBuf(var3);
      if (var4 < this.threshold) {
         var5.writeVarInt(0);
         var5.writeBytes(var2);
      } else {
         byte[] var6 = new byte[var4];
         var2.readBytes(var6);
         var5.writeVarInt(var6.length);
         this.deflater.setInput(var6, 0, var4);
         this.deflater.finish();

         while(!this.deflater.finished()) {
            int var7 = this.deflater.deflate(this.encodeBuf);
            var5.writeBytes((byte[])this.encodeBuf, 0, var7);
         }

         this.deflater.reset();
      }

   }

   public void setThreshold(int var1) {
      this.threshold = var1;
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext var1, Object var2, ByteBuf var3) throws Exception {
      this.encode(var1, (ByteBuf)var2, var3);
   }
}
