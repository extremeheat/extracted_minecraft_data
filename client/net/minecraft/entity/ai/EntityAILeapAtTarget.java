package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   EntityLiving field_75328_a;
   EntityLivingBase field_75326_b;
   float field_75327_c;

   public EntityAILeapAtTarget(EntityLiving var1, float var2) {
      super();
      this.field_75328_a = var1;
      this.field_75327_c = var2;
      this.func_75248_a(5);
   }

   public boolean func_75250_a() {
      this.field_75326_b = this.field_75328_a.func_70638_az();
      if (this.field_75326_b == null) {
         return false;
      } else {
         double var1 = this.field_75328_a.func_70068_e(this.field_75326_b);
         if (var1 >= 4.0D && var1 <= 16.0D) {
            if (!this.field_75328_a.field_70122_E) {
               return false;
            } else {
               return this.field_75328_a.func_70681_au().nextInt(5) == 0;
            }
         } else {
            return false;
         }
      }
   }

   public boolean func_75253_b() {
      return !this.field_75328_a.field_70122_E;
   }

   public void func_75249_e() {
      double var1 = this.field_75326_b.field_70165_t - this.field_75328_a.field_70165_t;
      double var3 = this.field_75326_b.field_70161_v - this.field_75328_a.field_70161_v;
      float var5 = MathHelper.func_76133_a(var1 * var1 + var3 * var3);
      EntityLiving var10000 = this.field_75328_a;
      var10000.field_70159_w += var1 / (double)var5 * 0.5D * 0.800000011920929D + this.field_75328_a.field_70159_w * 0.20000000298023224D;
      var10000 = this.field_75328_a;
      var10000.field_70179_y += var3 / (double)var5 * 0.5D * 0.800000011920929D + this.field_75328_a.field_70179_y * 0.20000000298023224D;
      this.field_75328_a.field_70181_x = (double)this.field_75327_c;
   }
}
