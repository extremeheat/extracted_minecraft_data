package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundTeleportEntityPacket implements Packet<ClientGamePacketListener> {
   private int id;
   private double x;
   private double y;
   private double z;
   private byte yRot;
   private byte xRot;
   private boolean onGround;

   public ClientboundTeleportEntityPacket() {
      super();
   }

   public ClientboundTeleportEntityPacket(Entity var1) {
      super();
      this.id = var1.getId();
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.yRot = (byte)((int)(var1.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.xRot * 256.0F / 360.0F));
      this.onGround = var1.isOnGround();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
      this.onGround = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
