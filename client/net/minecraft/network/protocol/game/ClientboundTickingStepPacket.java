package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStepPacket(int a) implements Packet<ClientGamePacketListener> {
   private final int tickSteps;

   public ClientboundTickingStepPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundTickingStepPacket(int var1) {
      super();
      this.tickSteps = var1;
   }

   public static ClientboundTickingStepPacket from(TickRateManager var0) {
      return new ClientboundTickingStepPacket(var0.frozenTicksToRun());
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.tickSteps);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTickingStep(this);
   }
}
