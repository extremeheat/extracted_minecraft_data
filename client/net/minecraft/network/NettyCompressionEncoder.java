package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] field_179302_a = new byte[8192];
   private final Deflater field_179300_b;
   private int field_179301_c;

   public NettyCompressionEncoder(int var1) {
      super();
      this.field_179301_c = var1;
      this.field_179300_b = new Deflater();
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var4 = var2.readableBytes();
      PacketBuffer var5 = new PacketBuffer(var3);
      if (var4 < this.field_179301_c) {
         var5.func_150787_b(0);
         var5.writeBytes(var2);
      } else {
         byte[] var6 = new byte[var4];
         var2.readBytes(var6);
         var5.func_150787_b(var6.length);
         this.field_179300_b.setInput(var6, 0, var4);
         this.field_179300_b.finish();

         while(!this.field_179300_b.finished()) {
            int var7 = this.field_179300_b.deflate(this.field_179302_a);
            var5.writeBytes((byte[])this.field_179302_a, 0, var7);
         }

         this.field_179300_b.reset();
      }

   }

   public void func_179299_a(int var1) {
      this.field_179301_c = var1;
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext var1, Object var2, ByteBuf var3) throws Exception {
      this.encode(var1, (ByteBuf)var2, var3);
   }
}
