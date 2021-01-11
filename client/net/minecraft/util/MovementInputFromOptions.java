package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
   private final GameSettings field_78903_e;

   public MovementInputFromOptions(GameSettings var1) {
      super();
      this.field_78903_e = var1;
   }

   public void func_78898_a() {
      this.field_78902_a = 0.0F;
      this.field_78900_b = 0.0F;
      if (this.field_78903_e.field_74351_w.func_151470_d()) {
         ++this.field_78900_b;
      }

      if (this.field_78903_e.field_74368_y.func_151470_d()) {
         --this.field_78900_b;
      }

      if (this.field_78903_e.field_74370_x.func_151470_d()) {
         ++this.field_78902_a;
      }

      if (this.field_78903_e.field_74366_z.func_151470_d()) {
         --this.field_78902_a;
      }

      this.field_78901_c = this.field_78903_e.field_74314_A.func_151470_d();
      this.field_78899_d = this.field_78903_e.field_74311_E.func_151470_d();
      if (this.field_78899_d) {
         this.field_78902_a = (float)((double)this.field_78902_a * 0.3D);
         this.field_78900_b = (float)((double)this.field_78900_b * 0.3D);
      }

   }
}
