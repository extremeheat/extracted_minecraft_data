package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LocalFrameDecoder extends ChannelInboundHandlerAdapter {
   public LocalFrameDecoder() {
      super();
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) {
      var1.fireChannelRead(HiddenByteBuf.unpack(var2));
   }
}
