package net.minecraft.network.play.client;

import net.minecraft.network.PacketBuffer;

public class C03PacketPlayer$C04PacketPlayerPosition extends C03PacketPlayer {
   public C03PacketPlayer$C04PacketPlayerPosition() {
      super();
      this.field_149480_h = true;
   }

   public C03PacketPlayer$C04PacketPlayerPosition(double var1, double var3, double var5, double var7, boolean var9) {
      super();
      this.field_149479_a = var1;
      this.field_149477_b = var3;
      this.field_149475_d = var5;
      this.field_149478_c = var7;
      this.field_149474_g = var9;
      this.field_149480_h = true;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149479_a = var1.readDouble();
      this.field_149477_b = var1.readDouble();
      this.field_149475_d = var1.readDouble();
      this.field_149478_c = var1.readDouble();
      super.func_148837_a(var1);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeDouble(this.field_149479_a);
      var1.writeDouble(this.field_149477_b);
      var1.writeDouble(this.field_149475_d);
      var1.writeDouble(this.field_149478_c);
      super.func_148840_b(var1);
   }
}
