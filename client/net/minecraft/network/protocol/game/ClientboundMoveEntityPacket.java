package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class ClientboundMoveEntityPacket implements Packet<ClientGamePacketListener> {
   private static final double TRUNCATION_STEPS = 4096.0D;
   protected final int entityId;
   // $FF: renamed from: xa short
   protected final short field_312;
   // $FF: renamed from: ya short
   protected final short field_313;
   // $FF: renamed from: za short
   protected final short field_314;
   protected final byte yRot;
   protected final byte xRot;
   protected final boolean onGround;
   protected final boolean hasRot;
   protected final boolean hasPos;

   public static long entityToPacket(double var0) {
      return Mth.lfloor(var0 * 4096.0D);
   }

   public static double packetToEntity(long var0) {
      return (double)var0 / 4096.0D;
   }

   public Vec3 updateEntityPosition(Vec3 var1) {
      double var2 = this.field_312 == 0 ? var1.field_414 : packetToEntity(entityToPacket(var1.field_414) + (long)this.field_312);
      double var4 = this.field_313 == 0 ? var1.field_415 : packetToEntity(entityToPacket(var1.field_415) + (long)this.field_313);
      double var6 = this.field_314 == 0 ? var1.field_416 : packetToEntity(entityToPacket(var1.field_416) + (long)this.field_314);
      return new Vec3(var2, var4, var6);
   }

   public static Vec3 packetToEntity(long var0, long var2, long var4) {
      return (new Vec3((double)var0, (double)var2, (double)var4)).scale(2.44140625E-4D);
   }

   protected ClientboundMoveEntityPacket(int var1, short var2, short var3, short var4, byte var5, byte var6, boolean var7, boolean var8, boolean var9) {
      super();
      this.entityId = var1;
      this.field_312 = var2;
      this.field_313 = var3;
      this.field_314 = var4;
      this.yRot = var5;
      this.xRot = var6;
      this.onGround = var7;
      this.hasRot = var8;
      this.hasPos = var9;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMoveEntity(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public short getXa() {
      return this.field_312;
   }

   public short getYa() {
      return this.field_313;
   }

   public short getZa() {
      return this.field_314;
   }

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }

   public boolean hasRotation() {
      return this.hasRot;
   }

   public boolean hasPosition() {
      return this.hasPos;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class Rot extends ClientboundMoveEntityPacket {
      public Rot(int var1, byte var2, byte var3, boolean var4) {
         super(var1, (short)0, (short)0, (short)0, var2, var3, var4, true, false);
      }

      public static ClientboundMoveEntityPacket.Rot read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt();
         byte var2 = var0.readByte();
         byte var3 = var0.readByte();
         boolean var4 = var0.readBoolean();
         return new ClientboundMoveEntityPacket.Rot(var1, var2, var3, var4);
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.entityId);
         var1.writeByte(this.yRot);
         var1.writeByte(this.xRot);
         var1.writeBoolean(this.onGround);
      }
   }

   public static class Pos extends ClientboundMoveEntityPacket {
      public Pos(int var1, short var2, short var3, short var4, boolean var5) {
         super(var1, var2, var3, var4, (byte)0, (byte)0, var5, false, true);
      }

      public static ClientboundMoveEntityPacket.Pos read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt();
         short var2 = var0.readShort();
         short var3 = var0.readShort();
         short var4 = var0.readShort();
         boolean var5 = var0.readBoolean();
         return new ClientboundMoveEntityPacket.Pos(var1, var2, var3, var4, var5);
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.entityId);
         var1.writeShort(this.xa);
         var1.writeShort(this.ya);
         var1.writeShort(this.za);
         var1.writeBoolean(this.onGround);
      }
   }

   public static class PosRot extends ClientboundMoveEntityPacket {
      public PosRot(int var1, short var2, short var3, short var4, byte var5, byte var6, boolean var7) {
         super(var1, var2, var3, var4, var5, var6, var7, true, true);
      }

      public static ClientboundMoveEntityPacket.PosRot read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt();
         short var2 = var0.readShort();
         short var3 = var0.readShort();
         short var4 = var0.readShort();
         byte var5 = var0.readByte();
         byte var6 = var0.readByte();
         boolean var7 = var0.readBoolean();
         return new ClientboundMoveEntityPacket.PosRot(var1, var2, var3, var4, var5, var6, var7);
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.entityId);
         var1.writeShort(this.xa);
         var1.writeShort(this.ya);
         var1.writeShort(this.za);
         var1.writeByte(this.yRot);
         var1.writeByte(this.xRot);
         var1.writeBoolean(this.onGround);
      }
   }
}
