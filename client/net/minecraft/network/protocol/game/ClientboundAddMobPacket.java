package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddMobPacket implements Packet<ClientGamePacketListener> {
   private int id;
   private UUID uuid;
   private int type;
   private double x;
   private double y;
   private double z;
   private int xd;
   private int yd;
   private int zd;
   private byte yRot;
   private byte xRot;
   private byte yHeadRot;
   private SynchedEntityData entityData;
   private List<SynchedEntityData.DataItem<?>> unpack;

   public ClientboundAddMobPacket() {
      super();
   }

   public ClientboundAddMobPacket(LivingEntity var1) {
      super();
      this.id = var1.getId();
      this.uuid = var1.getUUID();
      this.type = Registry.ENTITY_TYPE.getId(var1.getType());
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.yRot = (byte)((int)(var1.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.xRot * 256.0F / 360.0F));
      this.yHeadRot = (byte)((int)(var1.yHeadRot * 256.0F / 360.0F));
      double var2 = 3.9D;
      Vec3 var4 = var1.getDeltaMovement();
      double var5 = Mth.clamp(var4.x, -3.9D, 3.9D);
      double var7 = Mth.clamp(var4.y, -3.9D, 3.9D);
      double var9 = Mth.clamp(var4.z, -3.9D, 3.9D);
      this.xd = (int)(var5 * 8000.0D);
      this.yd = (int)(var7 * 8000.0D);
      this.zd = (int)(var9 * 8000.0D);
      this.entityData = var1.getEntityData();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.type = var1.readVarInt();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
      this.yHeadRot = var1.readByte();
      this.xd = var1.readShort();
      this.yd = var1.readShort();
      this.zd = var1.readShort();
      this.unpack = SynchedEntityData.unpack(var1);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeUUID(this.uuid);
      var1.writeVarInt(this.type);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeByte(this.yRot);
      var1.writeByte(this.xRot);
      var1.writeByte(this.yHeadRot);
      var1.writeShort(this.xd);
      var1.writeShort(this.yd);
      var1.writeShort(this.zd);
      this.entityData.packAll(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddMob(this);
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.unpack;
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public int getType() {
      return this.type;
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

   public int getXd() {
      return this.xd;
   }

   public int getYd() {
      return this.yd;
   }

   public int getZd() {
      return this.zd;
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
