package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetSimulationDistancePacket(int simulationDistance) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetSimulationDistancePacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundSetSimulationDistancePacket>codec(ClientboundSetSimulationDistancePacket::write, ClientboundSetSimulationDistancePacket::new);

   private ClientboundSetSimulationDistancePacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundSetSimulationDistancePacket(int var1) {
      super();
      this.simulationDistance = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.simulationDistance);
   }

   public PacketType<ClientboundSetSimulationDistancePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_SIMULATION_DISTANCE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetSimulationDistance(this);
   }
}
