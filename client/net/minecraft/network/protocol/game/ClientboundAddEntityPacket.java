package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {
   public static final double MAGICAL_QUANTIZATION = 8000.0D;
   // $FF: renamed from: id int
   private final int field_202;
   private final UUID uuid;
   // $FF: renamed from: x double
   private final double field_203;
   // $FF: renamed from: y double
   private final double field_204;
   // $FF: renamed from: z double
   private final double field_205;
   // $FF: renamed from: xa int
   private final int field_206;
   // $FF: renamed from: ya int
   private final int field_207;
   // $FF: renamed from: za int
   private final int field_208;
   private final int xRot;
   private final int yRot;
   private final EntityType<?> type;
   private final int data;
   public static final double LIMIT = 3.9D;

   public ClientboundAddEntityPacket(int var1, UUID var2, double var3, double var5, double var7, float var9, float var10, EntityType<?> var11, int var12, Vec3 var13) {
      super();
      this.field_202 = var1;
      this.uuid = var2;
      this.field_203 = var3;
      this.field_204 = var5;
      this.field_205 = var7;
      this.xRot = Mth.floor(var9 * 256.0F / 360.0F);
      this.yRot = Mth.floor(var10 * 256.0F / 360.0F);
      this.type = var11;
      this.data = var12;
      this.field_206 = (int)(Mth.clamp(var13.field_414, -3.9D, 3.9D) * 8000.0D);
      this.field_207 = (int)(Mth.clamp(var13.field_415, -3.9D, 3.9D) * 8000.0D);
      this.field_208 = (int)(Mth.clamp(var13.field_416, -3.9D, 3.9D) * 8000.0D);
   }

   public ClientboundAddEntityPacket(Entity var1) {
      this(var1, 0);
   }

   public ClientboundAddEntityPacket(Entity var1, int var2) {
      this(var1.getId(), var1.getUUID(), var1.getX(), var1.getY(), var1.getZ(), var1.getXRot(), var1.getYRot(), var1.getType(), var2, var1.getDeltaMovement());
   }

   public ClientboundAddEntityPacket(Entity var1, EntityType<?> var2, int var3, BlockPos var4) {
      this(var1.getId(), var1.getUUID(), (double)var4.getX(), (double)var4.getY(), (double)var4.getZ(), var1.getXRot(), var1.getYRot(), var2, var3, var1.getDeltaMovement());
   }

   public ClientboundAddEntityPacket(FriendlyByteBuf var1) {
      super();
      this.field_202 = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.type = (EntityType)Registry.ENTITY_TYPE.byId(var1.readVarInt());
      this.field_203 = var1.readDouble();
      this.field_204 = var1.readDouble();
      this.field_205 = var1.readDouble();
      this.xRot = var1.readByte();
      this.yRot = var1.readByte();
      this.data = var1.readInt();
      this.field_206 = var1.readShort();
      this.field_207 = var1.readShort();
      this.field_208 = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_202);
      var1.writeUUID(this.uuid);
      var1.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
      var1.writeDouble(this.field_203);
      var1.writeDouble(this.field_204);
      var1.writeDouble(this.field_205);
      var1.writeByte(this.xRot);
      var1.writeByte(this.yRot);
      var1.writeInt(this.data);
      var1.writeShort(this.field_206);
      var1.writeShort(this.field_207);
      var1.writeShort(this.field_208);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddEntity(this);
   }

   public int getId() {
      return this.field_202;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public double getX() {
      return this.field_203;
   }

   public double getY() {
      return this.field_204;
   }

   public double getZ() {
      return this.field_205;
   }

   public double getXa() {
      return (double)this.field_206 / 8000.0D;
   }

   public double getYa() {
      return (double)this.field_207 / 8000.0D;
   }

   public double getZa() {
      return (double)this.field_208 / 8000.0D;
   }

   public int getxRot() {
      return this.xRot;
   }

   public int getyRot() {
      return this.yRot;
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }
}
