package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket implements Packet {
   private int id;
   private UUID uuid;
   private double x;
   private double y;
   private double z;
   private int xa;
   private int ya;
   private int za;
   private int xRot;
   private int yRot;
   private EntityType type;
   private int data;

   public ClientboundAddEntityPacket() {
   }

   public ClientboundAddEntityPacket(int var1, UUID var2, double var3, double var5, double var7, float var9, float var10, EntityType var11, int var12, Vec3 var13) {
      this.id = var1;
      this.uuid = var2;
      this.x = var3;
      this.y = var5;
      this.z = var7;
      this.xRot = Mth.floor(var9 * 256.0F / 360.0F);
      this.yRot = Mth.floor(var10 * 256.0F / 360.0F);
      this.type = var11;
      this.data = var12;
      this.xa = (int)(Mth.clamp(var13.x, -3.9D, 3.9D) * 8000.0D);
      this.ya = (int)(Mth.clamp(var13.y, -3.9D, 3.9D) * 8000.0D);
      this.za = (int)(Mth.clamp(var13.z, -3.9D, 3.9D) * 8000.0D);
   }

   public ClientboundAddEntityPacket(Entity var1) {
      this(var1, 0);
   }

   public ClientboundAddEntityPacket(Entity var1, int var2) {
      this(var1.getId(), var1.getUUID(), var1.getX(), var1.getY(), var1.getZ(), var1.xRot, var1.yRot, var1.getType(), var2, var1.getDeltaMovement());
   }

   public ClientboundAddEntityPacket(Entity var1, EntityType var2, int var3, BlockPos var4) {
      this(var1.getId(), var1.getUUID(), (double)var4.getX(), (double)var4.getY(), (double)var4.getZ(), var1.xRot, var1.yRot, var2, var3, var1.getDeltaMovement());
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.type = (EntityType)Registry.ENTITY_TYPE.byId(var1.readVarInt());
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.xRot = var1.readByte();
      this.yRot = var1.readByte();
      this.data = var1.readInt();
      this.xa = var1.readShort();
      this.ya = var1.readShort();
      this.za = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeUUID(this.uuid);
      var1.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeByte(this.xRot);
      var1.writeByte(this.yRot);
      var1.writeInt(this.data);
      var1.writeShort(this.xa);
      var1.writeShort(this.ya);
      var1.writeShort(this.za);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddEntity(this);
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
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

   public double getXa() {
      return (double)this.xa / 8000.0D;
   }

   public double getYa() {
      return (double)this.ya / 8000.0D;
   }

   public double getZa() {
      return (double)this.za / 8000.0D;
   }

   public int getxRot() {
      return this.xRot;
   }

   public int getyRot() {
      return this.yRot;
   }

   public EntityType getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }
}
