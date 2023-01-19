package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public abstract class ServerboundMovePlayerPacket implements Packet<ServerGamePacketListener> {
   protected final double x;
   protected final double y;
   protected final double z;
   protected final float yRot;
   protected final float xRot;
   protected final boolean onGround;
   protected final boolean hasPos;
   protected final boolean hasRot;

   protected ServerboundMovePlayerPacket(double var1, double var3, double var5, float var7, float var8, boolean var9, boolean var10, boolean var11) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.yRot = var7;
      this.xRot = var8;
      this.onGround = var9;
      this.hasPos = var10;
      this.hasRot = var11;
   }

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

   public boolean hasPosition() {
      return this.hasPos;
   }

   public boolean hasRotation() {
      return this.hasRot;
   }

   public static class Pos extends ServerboundMovePlayerPacket {
      public Pos(double var1, double var3, double var5, boolean var7) {
         super(var1, var3, var5, 0.0F, 0.0F, var7, true, false);
      }

      public static ServerboundMovePlayerPacket.Pos read(FriendlyByteBuf var0) {
         double var1 = var0.readDouble();
         double var3 = var0.readDouble();
         double var5 = var0.readDouble();
         boolean var7 = var0.readUnsignedByte() != 0;
         return new ServerboundMovePlayerPacket.Pos(var1, var3, var5, var7);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeDouble(this.x);
         var1.writeDouble(this.y);
         var1.writeDouble(this.z);
         var1.writeByte(this.onGround ? 1 : 0);
      }
   }

   public static class PosRot extends ServerboundMovePlayerPacket {
      public PosRot(double var1, double var3, double var5, float var7, float var8, boolean var9) {
         super(var1, var3, var5, var7, var8, var9, true, true);
      }

      public static ServerboundMovePlayerPacket.PosRot read(FriendlyByteBuf var0) {
         double var1 = var0.readDouble();
         double var3 = var0.readDouble();
         double var5 = var0.readDouble();
         float var7 = var0.readFloat();
         float var8 = var0.readFloat();
         boolean var9 = var0.readUnsignedByte() != 0;
         return new ServerboundMovePlayerPacket.PosRot(var1, var3, var5, var7, var8, var9);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeDouble(this.x);
         var1.writeDouble(this.y);
         var1.writeDouble(this.z);
         var1.writeFloat(this.yRot);
         var1.writeFloat(this.xRot);
         var1.writeByte(this.onGround ? 1 : 0);
      }
   }

   public static class Rot extends ServerboundMovePlayerPacket {
      public Rot(float var1, float var2, boolean var3) {
         super(0.0, 0.0, 0.0, var1, var2, var3, false, true);
      }

      public static ServerboundMovePlayerPacket.Rot read(FriendlyByteBuf var0) {
         float var1 = var0.readFloat();
         float var2 = var0.readFloat();
         boolean var3 = var0.readUnsignedByte() != 0;
         return new ServerboundMovePlayerPacket.Rot(var1, var2, var3);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeFloat(this.yRot);
         var1.writeFloat(this.xRot);
         var1.writeByte(this.onGround ? 1 : 0);
      }
   }

   public static class StatusOnly extends ServerboundMovePlayerPacket {
      public StatusOnly(boolean var1) {
         super(0.0, 0.0, 0.0, 0.0F, 0.0F, var1, false, false);
      }

      public static ServerboundMovePlayerPacket.StatusOnly read(FriendlyByteBuf var0) {
         boolean var1 = var0.readUnsignedByte() != 0;
         return new ServerboundMovePlayerPacket.StatusOnly(var1);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeByte(this.onGround ? 1 : 0);
      }
   }
}
