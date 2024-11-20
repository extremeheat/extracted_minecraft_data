package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MonitoredLocalFrameDecoder extends ChannelInboundHandlerAdapter {
   private final BandwidthDebugMonitor monitor;

   public MonitoredLocalFrameDecoder(BandwidthDebugMonitor var1) {
      super();
      this.monitor = var1;
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) {
      var2 = HiddenByteBuf.unpack(var2);
      if (var2 instanceof ByteBuf var3) {
         this.monitor.onReceive(var3.readableBytes());
      }

      var1.fireChannelRead(var2);
   }
}
