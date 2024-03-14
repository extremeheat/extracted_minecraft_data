package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDisconnectPacket(Component b) implements Packet<ClientCommonPacketListener> {
   private final Component reason;
   public static final StreamCodec<ByteBuf, ClientboundDisconnectPacket> STREAM_CODEC = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC
      .map(ClientboundDisconnectPacket::new, ClientboundDisconnectPacket::reason);

   public ClientboundDisconnectPacket(Component var1) {
      super();
      this.reason = var1;
   }

   @Override
   public PacketType<ClientboundDisconnectPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_DISCONNECT;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleDisconnect(this);
   }
}
