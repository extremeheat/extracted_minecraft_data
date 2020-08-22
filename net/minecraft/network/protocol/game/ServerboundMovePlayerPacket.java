package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundMovePlayerPacket implements Packet {
   protected double x;
   protected double y;
   protected double z;
   protected float yRot;
   protected float xRot;
   protected boolean onGround;
   protected boolean hasPos;
   protected boolean hasRot;

   public ServerboundMovePlayerPacket() {
   }

   public ServerboundMovePlayerPacket(boolean var1) {
      this.onGround = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleMovePlayer(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.onGround = var1.readUnsignedByte() != 0;
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.onGround ? 1 : 0);
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

   public static class Rot extends ServerboundMovePlayerPacket {
      public Rot() {
         this.hasRot = true;
      }

      public Rot(float var1, float var2, boolean var3) {
         this.yRot = var1;
         this.xRot = var2;
         this.onGround = var3;
         this.hasRot = true;
      }

      public void read(FriendlyByteBuf var1) throws IOException {
         this.yRot = var1.readFloat();
         this.xRot = var1.readFloat();
         super.read(var1);
      }

      public void write(FriendlyByteBuf var1) throws IOException {
         var1.writeFloat(this.yRot);
         var1.writeFloat(this.xRot);
         super.write(var1);
      }
   }

   public static class Pos extends ServerboundMovePlayerPacket {
      public Pos() {
         this.hasPos = true;
      }

      public Pos(double var1, double var3, double var5, boolean var7) {
         this.x = var1;
         this.y = var3;
         this.z = var5;
         this.onGround = var7;
         this.hasPos = true;
      }

      public void read(FriendlyByteBuf var1) throws IOException {
         this.x = var1.readDouble();
         this.y = var1.readDouble();
         this.z = var1.readDouble();
         super.read(var1);
      }

      public void write(FriendlyByteBuf var1) throws IOException {
         var1.writeDouble(this.x);
         var1.writeDouble(this.y);
         var1.writeDouble(this.z);
         super.write(var1);
      }
   }

   public static class PosRot extends ServerboundMovePlayerPacket {
      public PosRot() {
         this.hasPos = true;
         this.hasRot = true;
      }

      public PosRot(double var1, double var3, double var5, float var7, float var8, boolean var9) {
         this.x = var1;
         this.y = var3;
         this.z = var5;
         this.yRot = var7;
         this.xRot = var8;
         this.onGround = var9;
         this.hasRot = true;
         this.hasPos = true;
      }

      public void read(FriendlyByteBuf var1) throws IOException {
         this.x = var1.readDouble();
         this.y = var1.readDouble();
         this.z = var1.readDouble();
         this.yRot = var1.readFloat();
         this.xRot = var1.readFloat();
         super.read(var1);
      }

      public void write(FriendlyByteBuf var1) throws IOException {
         var1.writeDouble(this.x);
         var1.writeDouble(this.y);
         var1.writeDouble(this.z);
         var1.writeFloat(this.yRot);
         var1.writeFloat(this.xRot);
         super.write(var1);
      }
   }
}
