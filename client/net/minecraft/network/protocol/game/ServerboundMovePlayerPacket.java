package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public abstract class ServerboundMovePlayerPacket implements Packet<ServerGamePacketListener> {
   private static final int FLAG_ON_GROUND = 1;
   private static final int FLAG_HORIZONTAL_COLLISION = 2;
   protected final double x;
   protected final double y;
   protected final double z;
   protected final float yRot;
   protected final float xRot;
   protected final boolean onGround;
   protected final boolean horizontalCollision;
   protected final boolean hasPos;
   protected final boolean hasRot;

   static int packFlags(boolean var0, boolean var1) {
      byte var2 = 0;
      if (var0) {
         var2 |= 1;
      }

      if (var1) {
         var2 |= 2;
      }

      return var2;
   }

   static boolean unpackOnGround(int var0) {
      return (var0 & 1) != 0;
   }

   static boolean unpackHorizontalCollision(int var0) {
      return (var0 & 2) != 0;
   }

   protected ServerboundMovePlayerPacket(
      double var1, double var3, double var5, float var7, float var8, boolean var9, boolean var10, boolean var11, boolean var12
   ) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.yRot = var7;
      this.xRot = var8;
      this.onGround = var9;
      this.horizontalCollision = var10;
      this.hasPos = var11;
      this.hasRot = var12;
   }

   @Override
   public abstract PacketType<? extends ServerboundMovePlayerPacket> type();

   public void handle(ServerGamePacketListener var1) {
      var1.handleMovePlayer(this);
   }

   public double getX(double var1) {
      return this.hasPos ? this.x : var1;
   }

   public double getY(double var1) {
      return this.hasPos ? this.y : var1;
   }

   public double getZ(double var1) {
      return this.hasPos ? this.z : var1;
   }

   public float getYRot(float var1) {
      return this.hasRot ? this.yRot : var1;
   }

   public float getXRot(float var1) {
      return this.hasRot ? this.xRot : var1;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public boolean horizontalCollision() {
      return this.horizontalCollision;
   }

   public boolean hasPosition() {
      return this.hasPos;
   }

   public boolean hasRotation() {
      return this.hasRot;
   }

   public static class Pos extends ServerboundMovePlayerPacket {
      public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.Pos> STREAM_CODEC = Packet.codec(
         ServerboundMovePlayerPacket.Pos::write, ServerboundMovePlayerPacket.Pos::read
      );

      public Pos(double var1, double var3, double var5, boolean var7, boolean var8) {
         super(var1, var3, var5, 0.0F, 0.0F, var7, var8, true, false);
      }

      private static ServerboundMovePlayerPacket.Pos read(FriendlyByteBuf var0) {
         double var1 = var0.readDouble();
         double var3 = var0.readDouble();
         double var5 = var0.readDouble();
         short var7 = var0.readUnsignedByte();
         boolean var8 = ServerboundMovePlayerPacket.unpackOnGround(var7);
         boolean var9 = ServerboundMovePlayerPacket.unpackHorizontalCollision(var7);
         return new ServerboundMovePlayerPacket.Pos(var1, var3, var5, var8, var9);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeDouble(this.x);
         var1.writeDouble(this.y);
         var1.writeDouble(this.z);
         var1.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
      }

      @Override
      public PacketType<ServerboundMovePlayerPacket.Pos> type() {
         return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_POS;
      }
   }

   public static class PosRot extends ServerboundMovePlayerPacket {
      public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.PosRot> STREAM_CODEC = Packet.codec(
         ServerboundMovePlayerPacket.PosRot::write, ServerboundMovePlayerPacket.PosRot::read
      );

      public PosRot(double var1, double var3, double var5, float var7, float var8, boolean var9, boolean var10) {
         super(var1, var3, var5, var7, var8, var9, var10, true, true);
      }

      private static ServerboundMovePlayerPacket.PosRot read(FriendlyByteBuf var0) {
         double var1 = var0.readDouble();
         double var3 = var0.readDouble();
         double var5 = var0.readDouble();
         float var7 = var0.readFloat();
         float var8 = var0.readFloat();
         short var9 = var0.readUnsignedByte();
         boolean var10 = ServerboundMovePlayerPacket.unpackOnGround(var9);
         boolean var11 = ServerboundMovePlayerPacket.unpackHorizontalCollision(var9);
         return new ServerboundMovePlayerPacket.PosRot(var1, var3, var5, var7, var8, var10, var11);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeDouble(this.x);
         var1.writeDouble(this.y);
         var1.writeDouble(this.z);
         var1.writeFloat(this.yRot);
         var1.writeFloat(this.xRot);
         var1.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
      }

      @Override
      public PacketType<ServerboundMovePlayerPacket.PosRot> type() {
         return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_POS_ROT;
      }
   }

   public static class Rot extends ServerboundMovePlayerPacket {
      public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.Rot> STREAM_CODEC = Packet.codec(
         ServerboundMovePlayerPacket.Rot::write, ServerboundMovePlayerPacket.Rot::read
      );

      public Rot(float var1, float var2, boolean var3, boolean var4) {
         super(0.0, 0.0, 0.0, var1, var2, var3, var4, false, true);
      }

      private static ServerboundMovePlayerPacket.Rot read(FriendlyByteBuf var0) {
         float var1 = var0.readFloat();
         float var2 = var0.readFloat();
         short var3 = var0.readUnsignedByte();
         boolean var4 = ServerboundMovePlayerPacket.unpackOnGround(var3);
         boolean var5 = ServerboundMovePlayerPacket.unpackHorizontalCollision(var3);
         return new ServerboundMovePlayerPacket.Rot(var1, var2, var4, var5);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeFloat(this.yRot);
         var1.writeFloat(this.xRot);
         var1.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
      }

      @Override
      public PacketType<ServerboundMovePlayerPacket.Rot> type() {
         return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_ROT;
      }
   }

   public static class StatusOnly extends ServerboundMovePlayerPacket {
      public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.StatusOnly> STREAM_CODEC = Packet.codec(
         ServerboundMovePlayerPacket.StatusOnly::write, ServerboundMovePlayerPacket.StatusOnly::read
      );

      public StatusOnly(boolean var1, boolean var2) {
         super(0.0, 0.0, 0.0, 0.0F, 0.0F, var1, var2, false, false);
      }

      private static ServerboundMovePlayerPacket.StatusOnly read(FriendlyByteBuf var0) {
         short var1 = var0.readUnsignedByte();
         boolean var2 = ServerboundMovePlayerPacket.unpackOnGround(var1);
         boolean var3 = ServerboundMovePlayerPacket.unpackHorizontalCollision(var1);
         return new ServerboundMovePlayerPacket.StatusOnly(var2, var3);
      }

      private void write(FriendlyByteBuf var1) {
         var1.writeByte(ServerboundMovePlayerPacket.packFlags(this.onGround, this.horizontalCollision));
      }

      @Override
      public PacketType<ServerboundMovePlayerPacket.StatusOnly> type() {
         return GamePacketTypes.SERVERBOUND_MOVE_PLAYER_STATUS_ONLY;
      }
   }
}
