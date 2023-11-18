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
      if (var4 < this.threshold) {
         VarInt.write(var3, 0);
         var3.writeBytes(var2);
      } else {
         byte[] var5 = new byte[var4];
         var2.readBytes(var5);
         VarInt.write(var3, var5.length);
         this.deflater.setInput(var5, 0, var4);
         this.deflater.finish();

         while(!this.deflater.finished()) {
            int var6 = this.deflater.deflate(this.encodeBuf);
            var3.writeBytes(this.encodeBuf, 0, var6);
         }

         this.deflater.reset();
      }
   }

   public int getThreshold() {
      return this.threshold;
   }

   public void setThreshold(int var1) {
      this.threshold = var1;
   }
}
