package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;

public class EntityFlyHelper extends EntityMoveHelper {
   public EntityFlyHelper(EntityLiving var1) {
      super(var1);
   }

   public void func_75641_c() {
      if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
         this.field_188491_h = EntityMoveHelper.Action.WAIT;
         this.field_75648_a.func_189654_d(true);
         double var1 = this.field_75646_b - this.field_75648_a.field_70165_t;
         double var3 = this.field_75647_c - this.field_75648_a.field_70163_u;
         double var5 = this.field_75644_d - this.field_75648_a.field_70161_v;
         double var7 = var1 * var1 + var3 * var3 + var5 * var5;
         if (var7 < 2.500000277905201E-7D) {
            this.field_75648_a.func_70657_f(0.0F);
            this.field_75648_a.func_191989_p(0.0F);
            return;
         }

         float var9 = (float)(MathHelper.func_181159_b(var5, var1) * 57.2957763671875D) - 90.0F;
         this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, var9, 10.0F);
         float var10;
         if (this.field_75648_a.field_70122_E) {
            var10 = (float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
         } else {
            var10 = (float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_193334_e).func_111126_e());
         }

         this.field_75648_a.func_70659_e(var10);
         double var11 = (double)MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         float var13 = (float)(-(MathHelper.func_181159_b(var3, var11) * 57.2957763671875D));
         this.field_75648_a.field_70125_A = this.func_75639_a(this.field_75648_a.field_70125_A, var13, 10.0F);
         this.field_75648_a.func_70657_f(var3 > 0.0D ? var10 : -var10);
      } else {
         this.field_75648_a.func_189654_d(false);
         this.field_75648_a.func_70657_f(0.0F);
         this.field_75648_a.func_191989_p(0.0F);
      }

   }
}
