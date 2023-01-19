package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {
   private static final double MAGICAL_QUANTIZATION = 8000.0;
   private static final double LIMIT = 3.9;
   private final int id;
   private final UUID uuid;
   private final EntityType<?> type;
   private final double x;
   private final double y;
   private final double z;
   private final int xa;
   private final int ya;
   private final int za;
   private final byte xRot;
   private final byte yRot;
   private final byte yHeadRot;
   private final int data;

   public ClientboundAddEntityPacket(LivingEntity var1) {
      this(var1, 0);
   }

   public ClientboundAddEntityPacket(LivingEntity var1, int var2) {
      this(
         var1.getId(),
         var1.getUUID(),
         var1.getX(),
         var1.getY(),
         var1.getZ(),
         var1.getXRot(),
         var1.getYRot(),
         var1.getType(),
         var2,
         var1.getDeltaMovement(),
         (double)var1.yHeadRot
      );
   }

   public ClientboundAddEntityPacket(Entity var1) {
      this(var1, 0);
   }

   public ClientboundAddEntityPacket(Entity var1, int var2) {
      this(
         var1.getId(),
         var1.getUUID(),
         var1.getX(),
         var1.getY(),
         var1.getZ(),
         var1.getXRot(),
         var1.getYRot(),
         var1.getType(),
         var2,
         var1.getDeltaMovement(),
         0.0
      );
   }

   public ClientboundAddEntityPacket(Entity var1, int var2, BlockPos var3) {
      this(
         var1.getId(),
         var1.getUUID(),
         (double)var3.getX(),
         (double)var3.getY(),
         (double)var3.getZ(),
         var1.getXRot(),
         var1.getYRot(),
         var1.getType(),
         var2,
         var1.getDeltaMovement(),
         0.0
      );
   }

   public ClientboundAddEntityPacket(
      int var1, UUID var2, double var3, double var5, double var7, float var9, float var10, EntityType<?> var11, int var12, Vec3 var13, double var14
   ) {
      super();
      this.id = var1;
      this.uuid = var2;
      this.x = var3;
      this.y = var5;
      this.z = var7;
      this.xRot = (byte)Mth.floor(var9 * 256.0F / 360.0F);
      this.yRot = (byte)Mth.floor(var10 * 256.0F / 360.0F);
      this.yHeadRot = (byte)Mth.floor(var14 * 256.0 / 360.0);
      this.type = var11;
      this.data = var12;
      this.xa = (int)(Mth.clamp(var13.x, -3.9, 3.9) * 8000.0);
      this.ya = (int)(Mth.clamp(var13.y, -3.9, 3.9) * 8000.0);
      this.za = (int)(Mth.clamp(var13.z, -3.9, 3.9) * 8000.0);
   }

   public ClientboundAddEntityPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.type = var1.readById(Registry.ENTITY_TYPE);
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.xRot = var1.readByte();
      this.yRot = var1.readByte();
      this.yHeadRot = var1.readByte();
      this.data = var1.readVarInt();
      this.xa = var1.readShort();
      this.ya = var1.readShort();
      this.za = var1.readShort();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeUUID(this.uuid);
      var1.writeId(Registry.ENTITY_TYPE, this.type);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeByte(this.xRot);
      var1.writeByte(this.yRot);
      var1.writeByte(this.yHeadRot);
      var1.writeVarInt(this.data);
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

   public EntityType<?> getType() {
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

   public double getXa() {
      return (double)this.xa / 8000.0;
   }

   public double getYa() {
      return (double)this.ya / 8000.0;
   }

   public double getZa() {
      return (double)this.za / 8000.0;
   }

   public float getXRot() {
      return (float)(this.xRot * 360) / 256.0F;
   }

   public float getYRot() {
      return (float)(this.yRot * 360) / 256.0F;
   }

   public float getYHeadRot() {
      return (float)(this.yHeadRot * 360) / 256.0F;
   }

   public int getData() {
      return this.data;
   }
}
