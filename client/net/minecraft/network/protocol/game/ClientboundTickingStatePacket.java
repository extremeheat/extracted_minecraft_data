package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.TickRateManager;

public record ClientboundTickingStatePacket(float a, boolean b) implements Packet<ClientGamePacketListener> {
   private final float tickRate;
   private final boolean isFrozen;

   public ClientboundTickingStatePacket(FriendlyByteBuf var1) {
      this(var1.readFloat(), var1.readBoolean());
   }

   public ClientboundTickingStatePacket(float var1, boolean var2) {
      super();
      this.tickRate = var1;
      this.isFrozen = var2;
   }

   public static ClientboundTickingStatePacket from(TickRateManager var0) {
      return new ClientboundTickingStatePacket(var0.tickrate(), var0.isFrozen());
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.tickRate);
      var1.writeBoolean(this.isFrozen);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTickingState(this);
   }
}
