package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundTeleportEntityPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_423;
   // $FF: renamed from: x double
   private final double field_424;
   // $FF: renamed from: y double
   private final double field_425;
   // $FF: renamed from: z double
   private final double field_426;
   private final byte yRot;
   private final byte xRot;
   private final boolean onGround;

   public ClientboundTeleportEntityPacket(Entity var1) {
      super();
      this.field_423 = var1.getId();
      this.field_424 = var1.getX();
      this.field_425 = var1.getY();
      this.field_426 = var1.getZ();
      this.yRot = (byte)((int)(var1.getYRot() * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.getXRot() * 256.0F / 360.0F));
      this.onGround = var1.isOnGround();
   }

   public ClientboundTeleportEntityPacket(FriendlyByteBuf var1) {
      super();
      this.field_423 = var1.readVarInt();
      this.field_424 = var1.readDouble();
      this.field_425 = var1.readDouble();
      this.field_426 = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
      this.onGround = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_423);
      var1.writeDouble(this.field_424);
      var1.writeDouble(this.field_425);
      var1.writeDouble(this.field_426);
      var1.writeByte(this.yRot);
      var1.writeByte(this.xRot);
      var1.writeBoolean(this.onGround);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTeleportEntity(this);
   }

   public int getId() {
      return this.field_423;
   }

   public double getX() {
      return this.field_424;
   }

   public double getY() {
      return this.field_425;
   }

   public double getZ() {
      return this.field_426;
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
