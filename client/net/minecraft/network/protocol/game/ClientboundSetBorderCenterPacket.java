package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderCenterPacket implements Packet<ClientGamePacketListener> {
   private final double newCenterX;
   private final double newCenterZ;

   public ClientboundSetBorderCenterPacket(WorldBorder var1) {
      super();
      this.newCenterX = var1.getCenterX();
      this.newCenterZ = var1.getCenterZ();
   }

   public ClientboundSetBorderCenterPacket(FriendlyByteBuf var1) {
      super();
      this.newCenterX = var1.readDouble();
      this.newCenterZ = var1.readDouble();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.newCenterX);
      var1.writeDouble(this.newCenterZ);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderCenter(this);
   }

   public double getNewCenterZ() {
      return this.newCenterZ;
   }

   public double getNewCenterX() {
      return this.newCenterX;
   }
}
