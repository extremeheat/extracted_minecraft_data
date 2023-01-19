package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderLerpSizePacket implements Packet<ClientGamePacketListener> {
   private final double oldSize;
   private final double newSize;
   private final long lerpTime;

   public ClientboundSetBorderLerpSizePacket(WorldBorder var1) {
      super();
      this.oldSize = var1.getSize();
      this.newSize = var1.getLerpTarget();
      this.lerpTime = var1.getLerpRemainingTime();
   }

   public ClientboundSetBorderLerpSizePacket(FriendlyByteBuf var1) {
      super();
      this.oldSize = var1.readDouble();
      this.newSize = var1.readDouble();
      this.lerpTime = var1.readVarLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.oldSize);
      var1.writeDouble(this.newSize);
      var1.writeVarLong(this.lerpTime);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderLerpSize(this);
   }

   public double getOldSize() {
      return this.oldSize;
   }

   public double getNewSize() {
      return this.newSize;
   }

   public long getLerpTime() {
      return this.lerpTime;
   }
}
