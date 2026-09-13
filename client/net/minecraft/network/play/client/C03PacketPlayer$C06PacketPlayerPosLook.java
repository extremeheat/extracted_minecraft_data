package net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;

public class C03PacketPlayer$C06PacketPlayerPosLook extends C03PacketPlayer {
   public C03PacketPlayer$C06PacketPlayerPosLook() {
      super();
      this.field_149480_h = true;
      this.field_149481_i = true;
   }

   public C03PacketPlayer$C06PacketPlayerPosLook(double var1, double var3, double var5, double var7, float var9, float var10, boolean var11) {
      super();
      this.field_149479_a = var1;
      this.field_149477_b = var3;
      this.field_149475_d = var5;
      this.field_149478_c = var7;
      this.field_149476_e = var9;
      this.field_149473_f = var10;
      this.field_149474_g = var11;
      this.field_149481_i = true;
      this.field_149480_h = true;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149479_a = var1.readDouble();
      this.field_149477_b = var1.readDouble();
      this.field_149475_d = var1.readDouble();
      this.field_149478_c = var1.readDouble();
      this.field_149476_e = var1.readFloat();
      this.field_149473_f = var1.readFloat();
      super.func_148837_a(var1);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeDouble(this.field_149479_a);
      var1.writeDouble(this.field_149477_b);
      var1.writeDouble(this.field_149475_d);
      var1.writeDouble(this.field_149478_c);
      var1.writeFloat(this.field_149476_e);
      var1.writeFloat(this.field_149473_f);
      super.func_148840_b(var1);
   }
}
