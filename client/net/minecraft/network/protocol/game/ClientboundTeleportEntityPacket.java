package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundTeleportEntityPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final double x;
   private final double y;
   private final double z;
   private final byte yRot;
   private final byte xRot;
   private final boolean onGround;

   public ClientboundTeleportEntityPacket(Entity var1) {
      super();
      this.id = var1.getId();
      Vec3 var2 = var1.trackingPosition();
      this.x = var2.x;
      this.y = var2.y;
      this.z = var2.z;
      this.yRot = (byte)((int)(var1.getYRot() * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.getXRot() * 256.0F / 360.0F));
      this.onGround = var1.isOnGround();
   }

   public ClientboundTeleportEntityPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
      this.onGround = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeByte(this.yRot);
      var1.writeByte(this.xRot);
      var1.writeBoolean(this.onGround);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTeleportEntity(this);
   }

   public int getId() {
      return this.id;
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

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }

   public boolean isOnGround() {
      return this.onGround;
   }
}
