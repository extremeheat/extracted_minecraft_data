package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

class OldServerPinger$2$1 extends SimpleChannelInboundHandler {
   OldServerPinger$2$1(OldServerPinger$2 var1) {
      super();
      this.field_147220_a = var1;
   }

   public void channelActive(ChannelHandlerContext var1) {
      super.channelActive(var1);
      ByteBuf var2 = Unpooled.buffer();

      try {
         var2.writeByte(254);
         var2.writeByte(1);
         var2.writeByte(250);
         char[] var3 = "MC|PingHost".toCharArray();
         var2.writeShort(var3.length);

         for(char var7 : var3) {
            var2.writeChar(var7);
         }

         var2.writeShort(7 + 2 * this.field_147220_a.field_147218_a.func_78861_a().length());
         var2.writeByte(127);
         var3 = this.field_147220_a.field_147218_a.func_78861_a().toCharArray();
         var2.writeShort(var3.length);

         for(char var15 : var3) {
            var2.writeChar(var15);
         }

         var2.writeInt(this.field_147220_a.field_147218_a.func_78864_b());
         var1.channel().writeAndFlush(var2).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      } finally {
         var2.release();
      }
   }

   protected void channelRead0(ChannelHandlerContext var1, ByteBuf var2) {
      short var3 = var2.readUnsignedByte();
      if (var3 == 255) {
         String var4 = new String(var2.readBytes(var2.readShort() * 2).array(), Charsets.UTF_16BE);
         String[] var5 = (String[])Iterables.toArray(OldServerPinger.access$200().split(var4), String.class);
         if ("\u00a71".equals(var5[0])) {
            int var6 = MathHelper.func_82715_a(var5[1], 0);
            String var7 = var5[2];
            String var8 = var5[3];
            int var9 = MathHelper.func_82715_a(var5[4], -1);
            int var10 = MathHelper.func_82715_a(var5[5], -1);
            this.field_147220_a.field_147216_b.field_82821_f = -1;
            this.field_147220_a.field_147216_b.field_82822_g = var7;
            this.field_147220_a.field_147216_b.field_78843_d = var8;
            this.field_147220_a.field_147216_b.field_78846_c = EnumChatFormatting.GRAY
               + ""
               + var9
               + ""
               + EnumChatFormatting.DARK_GRAY
               + "/"
               + EnumChatFormatting.GRAY
               + var10;
         }
      }

      var1.close();
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      var1.close();
   }
}
