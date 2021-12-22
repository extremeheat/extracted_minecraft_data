package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddMobPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_253;
   private final UUID uuid;
   private final int type;
   // $FF: renamed from: x double
   private final double field_254;
   // $FF: renamed from: y double
   private final double field_255;
   // $FF: renamed from: z double
   private final double field_256;
   // $FF: renamed from: xd int
   private final int field_257;
   // $FF: renamed from: yd int
   private final int field_258;
   // $FF: renamed from: zd int
   private final int field_259;
   private final byte yRot;
   private final byte xRot;
   private final byte yHeadRot;

   public ClientboundAddMobPacket(LivingEntity var1) {
      super();
      this.field_253 = var1.getId();
      this.uuid = var1.getUUID();
      this.type = Registry.ENTITY_TYPE.getId(var1.getType());
      this.field_254 = var1.getX();
      this.field_255 = var1.getY();
      this.field_256 = var1.getZ();
      this.yRot = (byte)((int)(var1.getYRot() * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.getXRot() * 256.0F / 360.0F));
      this.yHeadRot = (byte)((int)(var1.yHeadRot * 256.0F / 360.0F));
      double var2 = 3.9D;
      Vec3 var4 = var1.getDeltaMovement();
      double var5 = Mth.clamp(var4.field_414, -3.9D, 3.9D);
      double var7 = Mth.clamp(var4.field_415, -3.9D, 3.9D);
      double var9 = Mth.clamp(var4.field_416, -3.9D, 3.9D);
      this.field_257 = (int)(var5 * 8000.0D);
      this.field_258 = (int)(var7 * 8000.0D);
      this.field_259 = (int)(var9 * 8000.0D);
   }

   public ClientboundAddMobPacket(FriendlyByteBuf var1) {
      super();
      this.field_253 = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.type = var1.readVarInt();
      this.field_254 = var1.readDouble();
      this.field_255 = var1.readDouble();
      this.field_256 = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
      this.yHeadRot = var1.readByte();
      this.field_257 = var1.readShort();
      this.field_258 = var1.readShort();
      this.field_259 = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_253);
      var1.writeUUID(this.uuid);
      var1.writeVarInt(this.type);
      var1.writeDouble(this.field_254);
      var1.writeDouble(this.field_255);
      var1.writeDouble(this.field_256);
      var1.writeByte(this.yRot);
      var1.writeByte(this.xRot);
      var1.writeByte(this.yHeadRot);
      var1.writeShort(this.field_257);
      var1.writeShort(this.field_258);
      var1.writeShort(this.field_259);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddMob(this);
   }

   public int getId() {
      return this.field_253;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public int getType() {
      return this.type;
   }

   public double getX() {
      return this.field_254;
   }

   public double getY() {
      return this.field_255;
   }

   public double getZ() {
      return this.field_256;
   }

   public int getXd() {
      return this.field_257;
   }

   public int getYd() {
      return this.field_258;
   }

   public int getZd() {
      return this.field_259;
   }

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }

   public byte getyHeadRot() {
      return this.yHeadRot;
   }
}
