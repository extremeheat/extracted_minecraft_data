package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;

public record ClientboundDebugEventPacket(DebugSubscription.Event<?> event) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDebugEventPacket> STREAM_CODEC;

   public ClientboundDebugEventPacket(DebugSubscription.Event<?> var1) {
      super();
      this.event = var1;
   }

   public PacketType<ClientboundDebugEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDebugEvent(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(DebugSubscription.Event.STREAM_CODEC, ClientboundDebugEventPacket::event, ClientboundDebugEventPacket::new);
   }
}
