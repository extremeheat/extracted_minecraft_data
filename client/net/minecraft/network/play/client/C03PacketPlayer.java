package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer implements Packet<INetHandlerPlayServer> {
   protected double field_149479_a;
   protected double field_149477_b;
   protected double field_149478_c;
   protected float field_149476_e;
   protected float field_149473_f;
   protected boolean field_149474_g;
   protected boolean field_149480_h;
   protected boolean field_149481_i;

   public C03PacketPlayer() {
      super();
   }

   public C03PacketPlayer(boolean var1) {
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

   public double func_149464_c() {
      return this.field_149479_a;
   }

   public double func_149467_d() {
      return this.field_149477_b;
   }

   public double func_149472_e() {
      return this.field_149478_c;
   }

   public float func_149462_g() {
      return this.field_149476_e;
   }

   public float func_149470_h() {
      return this.field_149473_f;
   }

   public boolean func_149465_i() {
      return this.field_149474_g;
   }

   public boolean func_149466_j() {
      return this.field_149480_h;
   }

   public boolean func_149463_k() {
      return this.field_149481_i;
   }

   public void func_149469_a(boolean var1) {
      this.field_149480_h = var1;
   }

   public static class C05PacketPlayerLook extends C03PacketPlayer {
      public C05PacketPlayerLook() {
         super();
         this.field_149481_i = true;
      }

      public C05PacketPlayerLook(float var1, float var2, boolean var3) {
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

   public static class C04PacketPlayerPosition extends C03PacketPlayer {
      public C04PacketPlayerPosition() {
         super();
         this.field_149480_h = true;
      }

      public C04PacketPlayerPosition(double var1, double var3, double var5, boolean var7) {
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

   public static class C06PacketPlayerPosLook extends C03PacketPlayer {
      public C06PacketPlayerPosLook() {
         super();
         this.field_149480_h = true;
         this.field_149481_i = true;
      }

      public C06PacketPlayerPosLook(double var1, double var3, double var5, float var7, float var8, boolean var9) {
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
