package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2APacketParticles extends Packet {
   private String field_149236_a;
   private float field_149234_b;
   private float field_149235_c;
   private float field_149232_d;
   private float field_149233_e;
   private float field_149230_f;
   private float field_149231_g;
   private float field_149237_h;
   private int field_149238_i;

   public S2APacketParticles() {
      super();
   }

   public S2APacketParticles(String var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      super();
      this.field_149236_a = var1;
      this.field_149234_b = var2;
      this.field_149235_c = var3;
      this.field_149232_d = var4;
      this.field_149233_e = var5;
      this.field_149230_f = var6;
      this.field_149231_g = var7;
      this.field_149237_h = var8;
      this.field_149238_i = var9;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149236_a = var1.func_150789_c(64);
      this.field_149234_b = var1.readFloat();
      this.field_149235_c = var1.readFloat();
      this.field_149232_d = var1.readFloat();
      this.field_149233_e = var1.readFloat();
      this.field_149230_f = var1.readFloat();
      this.field_149231_g = var1.readFloat();
      this.field_149237_h = var1.readFloat();
      this.field_149238_i = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149236_a);
      var1.writeFloat(this.field_149234_b);
      var1.writeFloat(this.field_149235_c);
      var1.writeFloat(this.field_149232_d);
      var1.writeFloat(this.field_149233_e);
      var1.writeFloat(this.field_149230_f);
      var1.writeFloat(this.field_149231_g);
      var1.writeFloat(this.field_149237_h);
      var1.writeInt(this.field_149238_i);
   }

   public String func_149228_c() {
      return this.field_149236_a;
   }

   public double func_149220_d() {
      return (double)this.field_149234_b;
   }

   public double func_149226_e() {
      return (double)this.field_149235_c;
   }

   public double func_149225_f() {
      return (double)this.field_149232_d;
   }

   public float func_149221_g() {
      return this.field_149233_e;
   }

   public float func_149224_h() {
      return this.field_149230_f;
   }

   public float func_149223_i() {
      return this.field_149231_g;
   }

   public float func_149227_j() {
      return this.field_149237_h;
   }

   public int func_149222_k() {
      return this.field_149238_i;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147289_a(this);
   }
}
