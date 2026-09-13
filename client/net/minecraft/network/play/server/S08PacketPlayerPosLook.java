package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook extends Packet {
   private double field_148940_a;
   private double field_148938_b;
   private double field_148939_c;
   private float field_148936_d;
   private float field_148937_e;
   private boolean field_148935_f;

   public S08PacketPlayerPosLook() {
      super();
   }

   public S08PacketPlayerPosLook(double var1, double var3, double var5, float var7, float var8, boolean var9) {
      super();
      this.field_148940_a = var1;
      this.field_148938_b = var3;
      this.field_148939_c = var5;
      this.field_148936_d = var7;
      this.field_148937_e = var8;
      this.field_148935_f = var9;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148940_a = var1.readDouble();
      this.field_148938_b = var1.readDouble();
      this.field_148939_c = var1.readDouble();
      this.field_148936_d = var1.readFloat();
      this.field_148937_e = var1.readFloat();
      this.field_148935_f = var1.readBoolean();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeDouble(this.field_148940_a);
      var1.writeDouble(this.field_148938_b);
      var1.writeDouble(this.field_148939_c);
      var1.writeFloat(this.field_148936_d);
      var1.writeFloat(this.field_148937_e);
      var1.writeBoolean(this.field_148935_f);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147258_a(this);
   }

   public double func_148932_c() {
      return this.field_148940_a;
   }

   public double func_148928_d() {
      return this.field_148938_b;
   }

   public double func_148933_e() {
      return this.field_148939_c;
   }

   public float func_148931_f() {
      return this.field_148936_d;
   }

   public float func_148930_g() {
      return this.field_148937_e;
   }

   public boolean func_148929_h() {
      return this.field_148935_f;
   }
}
