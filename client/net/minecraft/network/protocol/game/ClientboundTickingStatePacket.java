package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStatePacket(float tickRate, boolean isFrozen) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTickingStatePacket> STREAM_CODEC = Packet.codec(
      ClientboundTickingStatePacket::write, ClientboundTickingStatePacket::new
   );

   private ClientboundTickingStatePacket(FriendlyByteBuf var1) {
      this(var1.readFloat(), var1.readBoolean());
   }

   public ClientboundTickingStatePacket(float tickRate, boolean isFrozen) {
      super();
      this.tickRate = tickRate;
      this.isFrozen = isFrozen;
   }

   public static ClientboundTickingStatePacket from(TickRateManager var0) {
      return new ClientboundTickingStatePacket(var0.tickrate(), var0.isFrozen());
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.tickRate);
      var1.writeBoolean(this.isFrozen);
   }

   @Override
   public PacketType<ClientboundTickingStatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_TICKING_STATE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTickingState(this);
   }
}
