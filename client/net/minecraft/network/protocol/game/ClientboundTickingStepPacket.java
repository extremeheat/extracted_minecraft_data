package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStepPacket(int tickSteps) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTickingStepPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundTickingStepPacket>codec(ClientboundTickingStepPacket::write, ClientboundTickingStepPacket::new);

   private ClientboundTickingStepPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt());
   }

   public ClientboundTickingStepPacket(int var1) {
      super();
      this.tickSteps = var1;
   }

   public static ClientboundTickingStepPacket from(TickRateManager var0) {
      return new ClientboundTickingStepPacket(var0.frozenTicksToRun());
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.tickSteps);
   }

   public PacketType<ClientboundTickingStepPacket> type() {
      return GamePacketTypes.CLIENTBOUND_TICKING_STEP;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTickingStep(this);
   }
}
