package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundMoveVehiclePacket implements Packet<ClientGamePacketListener> {
   private double x;
   private double y;
   private double z;
   private float yRot;
   private float xRot;

   public ClientboundMoveVehiclePacket() {
      super();
   }

   public ClientboundMoveVehiclePacket(Entity var1) {
      super();
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.yRot = var1.yRot;
      this.xRot = var1.xRot;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMoveVehicle(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }
}
