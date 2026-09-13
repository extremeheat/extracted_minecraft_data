package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C03PacketPlayer extends Packet {
   protected double field_149479_a;
   protected double field_149477_b;
   protected double field_149478_c;
   protected double field_149475_d;
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

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149474_g = var1.readUnsignedByte() != 0;
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
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

   public double func_149471_f() {
      return this.field_149475_d;
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
}
