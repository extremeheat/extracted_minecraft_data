package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.Packet;

public interface ProtocolSwapHandler {
   static void handleInboundTerminalPacket(ChannelHandlerContext var0, Packet<?> var1) {
      if (var1.isTerminal()) {
         var0.channel().config().setAutoRead(false);
         var0.pipeline().addBefore(var0.name(), "inbound_config", new UnconfiguredPipelineHandler.Inbound());
         var0.pipeline().remove(var0.name());
      }

   }

   static void handleOutboundTerminalPacket(ChannelHandlerContext var0, Packet<?> var1) {
      if (var1.isTerminal()) {
         var0.pipeline().addAfter(var0.name(), "outbound_config", new UnconfiguredPipelineHandler.Outbound());
         var0.pipeline().remove(var0.name());
      }

   }
}
