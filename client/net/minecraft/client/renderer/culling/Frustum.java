package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustum implements ICamera {
   private ClippingHelper field_78552_a;
   private double field_78550_b;
   private double field_78551_c;
   private double field_78549_d;

   public Frustum() {
      this(ClippingHelperImpl.func_78558_a());
   }

   public Frustum(ClippingHelper var1) {
      super();
      this.field_78552_a = var1;
   }

   public void func_78547_a(double var1, double var3, double var5) {
      this.field_78550_b = var1;
      this.field_78551_c = var3;
      this.field_78549_d = var5;
   }

   public boolean func_78548_b(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.field_78552_a.func_78553_b(var1 - this.field_78550_b, var3 - this.field_78551_c, var5 - this.field_78549_d, var7 - this.field_78550_b, var9 - this.field_78551_c, var11 - this.field_78549_d);
   }

   public boolean func_78546_a(AxisAlignedBB var1) {
      return this.func_78548_b(var1.field_72340_a, var1.field_72338_b, var1.field_72339_c, var1.field_72336_d, var1.field_72337_e, var1.field_72334_f);
   }
}
