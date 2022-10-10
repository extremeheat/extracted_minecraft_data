package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;

public class EntityDolphinHelper extends EntityLookHelper {
   private final int field_205139_h;

   public EntityDolphinHelper(EntityLiving var1, int var2) {
      super(var1);
      this.field_205139_h = var2;
   }

   public void func_75649_a() {
      if (this.field_75655_d) {
         this.field_75655_d = false;
         double var1 = this.field_75656_e - this.field_75659_a.field_70165_t;
         double var3 = this.field_75653_f - (this.field_75659_a.field_70163_u + (double)this.field_75659_a.func_70047_e());
         double var5 = this.field_75654_g - this.field_75659_a.field_70161_v;
         double var7 = (double)MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         float var9 = (float)(MathHelper.func_181159_b(var5, var1) * 57.2957763671875D) - 90.0F + 20.0F;
         float var10 = (float)(-(MathHelper.func_181159_b(var3, var7) * 57.2957763671875D)) + 10.0F;
         this.field_75659_a.field_70125_A = this.func_75652_a(this.field_75659_a.field_70125_A, var10, this.field_75658_c);
         this.field_75659_a.field_70759_as = this.func_75652_a(this.field_75659_a.field_70759_as, var9, this.field_75657_b);
      } else {
         if (this.field_75659_a.func_70661_as().func_75500_f()) {
            this.field_75659_a.field_70125_A = this.func_75652_a(this.field_75659_a.field_70125_A, 0.0F, 5.0F);
         }

         this.field_75659_a.field_70759_as = this.func_75652_a(this.field_75659_a.field_70759_as, this.field_75659_a.field_70761_aq, this.field_75657_b);
      }

      float var11 = MathHelper.func_76142_g(this.field_75659_a.field_70759_as - this.field_75659_a.field_70761_aq);
      EntityLiving var10000;
      if (var11 < (float)(-this.field_205139_h)) {
         var10000 = this.field_75659_a;
         var10000.field_70761_aq -= 4.0F;
      } else if (var11 > (float)this.field_205139_h) {
         var10000 = this.field_75659_a;
         var10000.field_70761_aq += 4.0F;
      }

   }
}
