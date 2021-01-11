package net.minecraft.entity;

import net.minecraft.util.MathHelper;

public class EntityBodyHelper {
   private EntityLivingBase field_75668_a;
   private int field_75666_b;
   private float field_75667_c;

   public EntityBodyHelper(EntityLivingBase var1) {
      super();
      this.field_75668_a = var1;
   }

   public void func_75664_a() {
      double var1 = this.field_75668_a.field_70165_t - this.field_75668_a.field_70169_q;
      double var3 = this.field_75668_a.field_70161_v - this.field_75668_a.field_70166_s;
      if (var1 * var1 + var3 * var3 > 2.500000277905201E-7D) {
         this.field_75668_a.field_70761_aq = this.field_75668_a.field_70177_z;
         this.field_75668_a.field_70759_as = this.func_75665_a(this.field_75668_a.field_70761_aq, this.field_75668_a.field_70759_as, 75.0F);
         this.field_75667_c = this.field_75668_a.field_70759_as;
         this.field_75666_b = 0;
      } else {
         float var5 = 75.0F;
         if (Math.abs(this.field_75668_a.field_70759_as - this.field_75667_c) > 15.0F) {
            this.field_75666_b = 0;
            this.field_75667_c = this.field_75668_a.field_70759_as;
         } else {
            ++this.field_75666_b;
            boolean var6 = true;
            if (this.field_75666_b > 10) {
               var5 = Math.max(1.0F - (float)(this.field_75666_b - 10) / 10.0F, 0.0F) * 75.0F;
            }
         }

         this.field_75668_a.field_70761_aq = this.func_75665_a(this.field_75668_a.field_70759_as, this.field_75668_a.field_70761_aq, var5);
      }
   }

   private float func_75665_a(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76142_g(var1 - var2);
      if (var4 < -var3) {
         var4 = -var3;
      }

      if (var4 >= var3) {
         var4 = var3;
      }

      return var1 - var4;
   }
}
