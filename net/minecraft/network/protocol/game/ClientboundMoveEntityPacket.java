package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundMoveEntityPacket implements Packet {
   protected int entityId;
   protected short xa;
   protected short ya;
   protected short za;
   protected byte yRot;
   protected byte xRot;
   protected boolean onGround;
   protected boolean hasRot;
   protected boolean hasPos;

   public static long entityToPacket(double var0) {
      return Mth.lfloor(var0 * 4096.0D);
   }

   public static Vec3 packetToEntity(long var0, long var2, long var4) {
      return (new Vec3((double)var0, (double)var2, (double)var4)).scale(2.44140625E-4D);
   }

   public ClientboundMoveEntityPacket() {
   }

   public ClientboundMoveEntityPacket(int var1) {
      this.entityId = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMoveEntity(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

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

   public static class Rot extends ClientboundMoveEntityPacket {
      public Rot() {
         this.hasRot = true;
      }

      public Rot(int var1, byte var2, byte var3, boolean var4) {
         super(var1);
         this.yRot = var2;
         this.xRot = var3;
         this.hasRot = true;
         this.onGround = var4;
      }

      public void read(FriendlyByteBuf var1) throws IOException {
         super.read(var1);
         this.yRot = var1.readByte();
         this.xRot = var1.readByte();
         this.onGround = var1.readBoolean();
      }

      public void write(FriendlyByteBuf var1) throws IOException {
         super.write(var1);
         var1.writeByte(this.yRot);
         var1.writeByte(this.xRot);
         var1.writeBoolean(this.onGround);
      }
   }

   public static class Pos extends ClientboundMoveEntityPacket {
      public Pos() {
         this.hasPos = true;
      }

      public Pos(int var1, short var2, short var3, short var4, boolean var5) {
         super(var1);
         this.xa = var2;
         this.ya = var3;
         this.za = var4;
         this.onGround = var5;
         this.hasPos = true;
      }

      public void read(FriendlyByteBuf var1) throws IOException {
         super.read(var1);
         this.xa = var1.readShort();
         this.ya = var1.readShort();
         this.za = var1.readShort();
         this.onGround = var1.readBoolean();
      }

      public void write(FriendlyByteBuf var1) throws IOException {
         super.write(var1);
         var1.writeShort(this.xa);
         var1.writeShort(this.ya);
         var1.writeShort(this.za);
         var1.writeBoolean(this.onGround);
      }
   }

   public static class PosRot extends ClientboundMoveEntityPacket {
      public PosRot() {
         this.hasRot = true;
         this.hasPos = true;
      }

      public PosRot(int var1, short var2, short var3, short var4, byte var5, byte var6, boolean var7) {
         super(var1);
         this.xa = var2;
         this.ya = var3;
         this.za = var4;
         this.yRot = var5;
         this.xRot = var6;
         this.onGround = var7;
         this.hasRot = true;
         this.hasPos = true;
      }

      public void read(FriendlyByteBuf var1) throws IOException {
         super.read(var1);
         this.xa = var1.readShort();
         this.ya = var1.readShort();
         this.za = var1.readShort();
         this.yRot = var1.readByte();
         this.xRot = var1.readByte();
         this.onGround = var1.readBoolean();
      }

      public void write(FriendlyByteBuf var1) throws IOException {
         super.write(var1);
         var1.writeShort(this.xa);
         var1.writeShort(this.ya);
         var1.writeShort(this.za);
         var1.writeByte(this.yRot);
         var1.writeByte(this.xRot);
         var1.writeBoolean(this.onGround);
      }
   }
}
