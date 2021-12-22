package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ServerboundMoveVehiclePacket implements Packet<ServerGamePacketListener> {
   // $FF: renamed from: x double
   private final double field_376;
   // $FF: renamed from: y double
   private final double field_377;
   // $FF: renamed from: z double
   private final double field_378;
   private final float yRot;
   private final float xRot;

   public ServerboundMoveVehiclePacket(Entity var1) {
      super();
      this.field_376 = var1.getX();
      this.field_377 = var1.getY();
      this.field_378 = var1.getZ();
      this.yRot = var1.getYRot();
      this.xRot = var1.getXRot();
   }

   public ServerboundMoveVehiclePacket(FriendlyByteBuf var1) {
      super();
      this.field_376 = var1.readDouble();
      this.field_377 = var1.readDouble();
      this.field_378 = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.field_376);
      var1.writeDouble(this.field_377);
      var1.writeDouble(this.field_378);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleMoveVehicle(this);
   }

   public double getX() {
      return this.field_376;
   }

   public double getY() {
      return this.field_377;
   }

   public double getZ() {
      return this.field_378;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }
}
