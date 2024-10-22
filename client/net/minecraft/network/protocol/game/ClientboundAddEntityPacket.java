package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddEntityPacket> STREAM_CODEC = Packet.codec(
      ClientboundAddEntityPacket::write, ClientboundAddEntityPacket::new
   );
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

   public ClientboundAddEntityPacket(Entity var1, ServerEntity var2) {
      this(var1, var2, 0);
   }

   public ClientboundAddEntityPacket(Entity var1, ServerEntity var2, int var3) {
      this(
         var1.getId(),
         var1.getUUID(),
         var2.getPositionBase().x(),
         var2.getPositionBase().y(),
         var2.getPositionBase().z(),
         var2.getLastSentXRot(),
         var2.getLastSentYRot(),
         var1.getType(),
         var3,
         var2.getLastSentMovement(),
         (double)var2.getLastSentYHeadRot()
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
         (double)var1.getYHeadRot()
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
      this.xRot = Mth.packDegrees(var9);
      this.yRot = Mth.packDegrees(var10);
      this.yHeadRot = Mth.packDegrees((float)var14);
      this.type = var11;
      this.data = var12;
      this.xa = (int)(Mth.clamp(var13.x, -3.9, 3.9) * 8000.0);
      this.ya = (int)(Mth.clamp(var13.y, -3.9, 3.9) * 8000.0);
      this.za = (int)(Mth.clamp(var13.z, -3.9, 3.9) * 8000.0);
   }

   private ClientboundAddEntityPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.uuid = var1.readUUID();
      this.type = ByteBufCodecs.registry(Registries.ENTITY_TYPE).decode(var1);
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

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeUUID(this.uuid);
      ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(var1, this.type);
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

   @Override
   public PacketType<ClientboundAddEntityPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ADD_ENTITY;
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
      return Mth.unpackDegrees(this.xRot);
   }

   public float getYRot() {
      return Mth.unpackDegrees(this.yRot);
   }

   public float getYHeadRot() {
      return Mth.unpackDegrees(this.yHeadRot);
   }

   public int getData() {
      return this.data;
   }
}
