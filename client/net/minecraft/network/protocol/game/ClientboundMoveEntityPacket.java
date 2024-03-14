package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class ClientboundMoveEntityPacket implements Packet<ClientGamePacketListener> {
   protected final int entityId;
   protected final short xa;
   protected final short ya;
   protected final short za;
   protected final byte yRot;
   protected final byte xRot;
   protected final boolean onGround;
   protected final boolean hasRot;
   protected final boolean hasPos;

   protected ClientboundMoveEntityPacket(int var1, short var2, short var3, short var4, byte var5, byte var6, boolean var7, boolean var8, boolean var9) {
      super();
      this.entityId = var1;
      this.xa = var2;
      this.ya = var3;
      this.za = var4;
      this.yRot = var5;
      this.xRot = var6;
      this.onGround = var7;
      this.hasRot = var8;
      this.hasPos = var9;
   }

   @Override
   public abstract PacketType<? extends ClientboundMoveEntityPacket> type();

   public void handle(ClientGamePacketListener var1) {
      var1.handleMoveEntity(this);
   }

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public short getXa() {
      return this.xa;
   }

   public short getYa() {
      return this.ya;
   }

   public short getZa() {
      return this.za;
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

   public static class Pos extends ClientboundMoveEntityPacket {
      public static final StreamCodec<FriendlyByteBuf, ClientboundMoveEntityPacket.Pos> STREAM_CODEC = Packet.codec(
         ClientboundMoveEntityPacket.Pos::write, ClientboundMoveEntityPacket.Pos::read
      );

      public Pos(int var1, short var2, short var3, short var4, boolean var5) {
         super(var1, var2, var3, var4, (byte)0, (byte)0, var5, false, true);
      }

      private static ClientboundMoveEntityPacket.Pos read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt();
         short var2 = var0.readShort();
         short var3 = var0.readShort();
         short var4 = var0.readShort();
         boolean var5 = var0.readBoolean();
         return new ClientboundMoveEntityPacket.Pos(var1, var2, var3, var4, var5);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.entityId);
         var1.writeShort(this.xa);
         var1.writeShort(this.ya);
         var1.writeShort(this.za);
         var1.writeBoolean(this.onGround);
      }

      @Override
      public PacketType<ClientboundMoveEntityPacket.Pos> type() {
         return GamePacketTypes.CLIENTBOUND_MOVE_ENTITY_POS;
      }
   }

   public static class PosRot extends ClientboundMoveEntityPacket {
      public static final StreamCodec<FriendlyByteBuf, ClientboundMoveEntityPacket.PosRot> STREAM_CODEC = Packet.codec(
         ClientboundMoveEntityPacket.PosRot::write, ClientboundMoveEntityPacket.PosRot::read
      );

      public PosRot(int var1, short var2, short var3, short var4, byte var5, byte var6, boolean var7) {
         super(var1, var2, var3, var4, var5, var6, var7, true, true);
      }

      private static ClientboundMoveEntityPacket.PosRot read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt();
         short var2 = var0.readShort();
         short var3 = var0.readShort();
         short var4 = var0.readShort();
         byte var5 = var0.readByte();
         byte var6 = var0.readByte();
         boolean var7 = var0.readBoolean();
         return new ClientboundMoveEntityPacket.PosRot(var1, var2, var3, var4, var5, var6, var7);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.entityId);
         var1.writeShort(this.xa);
         var1.writeShort(this.ya);
         var1.writeShort(this.za);
         var1.writeByte(this.yRot);
         var1.writeByte(this.xRot);
         var1.writeBoolean(this.onGround);
      }

      @Override
      public PacketType<ClientboundMoveEntityPacket.PosRot> type() {
         return GamePacketTypes.CLIENTBOUND_MOVE_ENTITY_POS_ROT;
      }
   }

   public static class Rot extends ClientboundMoveEntityPacket {
      public static final StreamCodec<FriendlyByteBuf, ClientboundMoveEntityPacket.Rot> STREAM_CODEC = Packet.codec(
         ClientboundMoveEntityPacket.Rot::write, ClientboundMoveEntityPacket.Rot::read
      );

      public Rot(int var1, byte var2, byte var3, boolean var4) {
         super(var1, (short)0, (short)0, (short)0, var2, var3, var4, true, false);
      }

      private static ClientboundMoveEntityPacket.Rot read(FriendlyByteBuf var0) {
         int var1 = var0.readVarInt();
         byte var2 = var0.readByte();
         byte var3 = var0.readByte();
         boolean var4 = var0.readBoolean();
         return new ClientboundMoveEntityPacket.Rot(var1, var2, var3, var4);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeVarInt(this.entityId);
         var1.writeByte(this.yRot);
         var1.writeByte(this.xRot);
         var1.writeBoolean(this.onGround);
      }

      @Override
      public PacketType<ClientboundMoveEntityPacket.Rot> type() {
         return GamePacketTypes.CLIENTBOUND_MOVE_ENTITY_ROT;
      }
   }
}
