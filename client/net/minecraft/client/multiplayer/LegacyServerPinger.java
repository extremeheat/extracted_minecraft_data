package net.minecraft.client.multiplayer;

import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.server.network.LegacyProtocolUtils;
import net.minecraft.util.Mth;

public class LegacyServerPinger extends SimpleChannelInboundHandler<ByteBuf> {
   private static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
   private final ServerAddress address;
   private final Output output;

   public LegacyServerPinger(ServerAddress var1, Output var2) {
      super();
      this.address = var1;
      this.output = var2;
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      ByteBuf var2 = var1.alloc().buffer();

      try {
         var2.writeByte(254);
         var2.writeByte(1);
         var2.writeByte(250);
         LegacyProtocolUtils.writeLegacyString(var2, "MC|PingHost");
         int var3 = var2.writerIndex();
         var2.writeShort(0);
         int var4 = var2.writerIndex();
         var2.writeByte(127);
         LegacyProtocolUtils.writeLegacyString(var2, this.address.getHost());
         var2.writeInt(this.address.getPort());
         int var5 = var2.writerIndex() - var4;
         var2.setShort(var3, var5);
         var1.channel().writeAndFlush(var2).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      } catch (Exception var6) {
         var2.release();
         throw var6;
      }
   }

   protected void channelRead0(ChannelHandlerContext var1, ByteBuf var2) {
      short var3 = var2.readUnsignedByte();
      if (var3 == 255) {
         String var4 = LegacyProtocolUtils.readLegacyString(var2);
         List var5 = SPLITTER.splitToList(var4);
         if ("\u00a71".equals(var5.get(0))) {
            int var6 = Mth.getInt((String)var5.get(1), 0);
            String var7 = (String)var5.get(2);
            String var8 = (String)var5.get(3);
            int var9 = Mth.getInt((String)var5.get(4), -1);
            int var10 = Mth.getInt((String)var5.get(5), -1);
            this.output.handleResponse(var6, var7, var8, var9, var10);
         }
      }

      var1.close();
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      var1.close();
   }

   // $FF: synthetic method
   protected void channelRead0(ChannelHandlerContext var1, Object var2) throws Exception {
      this.channelRead0(var1, (ByteBuf)var2);
   }

   @FunctionalInterface
   public interface Output {
      void handleResponse(int var1, String var2, String var3, int var4, int var5);
   }
}
