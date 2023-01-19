package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundSetSimulationDistancePacket(int a) implements Packet<ClientGamePacketListener> {
   private final int simulationDistance;

   public ClientboundSetSimulationDistancePacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundSetSimulationDistancePacket(int var1) {
      super();
      this.simulationDistance = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.simulationDistance);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetSimulationDistance(this);
   }
}
