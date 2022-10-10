package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketPlayer implements Packet<INetHandlerPlayServer> {
   protected double field_149479_a;
   protected double field_149477_b;
   protected double field_149478_c;
   protected float field_149476_e;
   protected float field_149473_f;
   protected boolean field_149474_g;
   protected boolean field_149480_h;
   protected boolean field_149481_i;

   public CPacketPlayer() {
      super();
   }

   public CPacketPlayer(boolean var1) {
      super();
      this.field_149474_g = var1;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147347_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149474_g = var1.readUnsignedByte() != 0;
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149474_g ? 1 : 0);
   }

   public double func_186997_a(double var1) {
      return this.field_149480_h ? this.field_149479_a : var1;
   }

   public double func_186996_b(double var1) {
      return this.field_149480_h ? this.field_149477_b : var1;
   }

   public double func_187000_c(double var1) {
      return this.field_149480_h ? this.field_149478_c : var1;
   }

   public float func_186999_a(float var1) {
      return this.field_149481_i ? this.field_149476_e : var1;
   }

   public float func_186998_b(float var1) {
      return this.field_149481_i ? this.field_149473_f : var1;
   }

   public boolean func_149465_i() {
      return this.field_149474_g;
   }

   public static class Rotation extends CPacketPlayer {
      public Rotation() {
         super();
         this.field_149481_i = true;
      }

      public Rotation(float var1, float var2, boolean var3) {
         super();
         this.field_149476_e = var1;
         this.field_149473_f = var2;
         this.field_149474_g = var3;
         this.field_149481_i = true;
      }

      public void func_148837_a(PacketBuffer var1) throws IOException {
         this.field_149476_e = var1.readFloat();
         this.field_149473_f = var1.readFloat();
         super.func_148837_a(var1);
      }

      public void func_148840_b(PacketBuffer var1) throws IOException {
         var1.writeFloat(this.field_149476_e);
         var1.writeFloat(this.field_149473_f);
         super.func_148840_b(var1);
      }
   }

   public static class Position extends CPacketPlayer {
      public Position() {
         super();
         this.field_149480_h = true;
      }

      public Position(double var1, double var3, double var5, boolean var7) {
         super();
         this.field_149479_a = var1;
         this.field_149477_b = var3;
         this.field_149478_c = var5;
         this.field_149474_g = var7;
         this.field_149480_h = true;
      }

      public void func_148837_a(PacketBuffer var1) throws IOException {
         this.field_149479_a = var1.readDouble();
         this.field_149477_b = var1.readDouble();
         this.field_149478_c = var1.readDouble();
         super.func_148837_a(var1);
      }

      public void func_148840_b(PacketBuffer var1) throws IOException {
         var1.writeDouble(this.field_149479_a);
         var1.writeDouble(this.field_149477_b);
         var1.writeDouble(this.field_149478_c);
         super.func_148840_b(var1);
      }
   }

   public static class PositionRotation extends CPacketPlayer {
      public PositionRotation() {
         super();
         this.field_149480_h = true;
         this.field_149481_i = true;
      }

      public PositionRotation(double var1, double var3, double var5, float var7, float var8, boolean var9) {
         super();
         this.field_149479_a = var1;
         this.field_149477_b = var3;
         this.field_149478_c = var5;
         this.field_149476_e = var7;
         this.field_149473_f = var8;
         this.field_149474_g = var9;
         this.field_149481_i = true;
         this.field_149480_h = true;
      }

      public void func_148837_a(PacketBuffer var1) throws IOException {
         this.field_149479_a = var1.readDouble();
         this.field_149477_b = var1.readDouble();
         this.field_149478_c = var1.readDouble();
         this.field_149476_e = var1.readFloat();
         this.field_149473_f = var1.readFloat();
         super.func_148837_a(var1);
      }

      public void func_148840_b(PacketBuffer var1) throws IOException {
         var1.writeDouble(this.field_149479_a);
         var1.writeDouble(this.field_149477_b);
         var1.writeDouble(this.field_149478_c);
         var1.writeFloat(this.field_149476_e);
         var1.writeFloat(this.field_149473_f);
         super.func_148840_b(var1);
      }
   }
}
