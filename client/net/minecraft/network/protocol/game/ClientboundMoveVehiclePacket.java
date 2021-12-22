package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundMoveVehiclePacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x double
   private final double field_471;
   // $FF: renamed from: y double
   private final double field_472;
   // $FF: renamed from: z double
   private final double field_473;
   private final float yRot;
   private final float xRot;

   public ClientboundMoveVehiclePacket(Entity var1) {
      super();
      this.field_471 = var1.getX();
      this.field_472 = var1.getY();
      this.field_473 = var1.getZ();
      this.yRot = var1.getYRot();
      this.xRot = var1.getXRot();
   }

   public ClientboundMoveVehiclePacket(FriendlyByteBuf var1) {
      super();
      this.field_471 = var1.readDouble();
      this.field_472 = var1.readDouble();
      this.field_473 = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.field_471);
      var1.writeDouble(this.field_472);
      var1.writeDouble(this.field_473);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMoveVehicle(this);
   }

   public double getX() {
      return this.field_471;
   }

   public double getY() {
      return this.field_472;
   }

   public double getZ() {
      return this.field_473;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }
}
