package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class LocalFrameEncoder extends ChannelOutboundHandlerAdapter {
   public LocalFrameEncoder() {
      super();
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) {
      var1.write(HiddenByteBuf.pack(var2), var3);
   }
}
