package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
   boolean field_75312_a;
   private int field_142052_b;

   public EntityAIHurtByTarget(EntityCreature var1, boolean var2) {
      super(var1, false);
      this.field_75312_a = var2;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      int var1 = this.field_75299_d.func_142015_aE();
      return var1 != this.field_142052_b && this.func_75296_a(this.field_75299_d.func_70643_av(), false);
   }

   @Override
   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75299_d.func_70643_av());
      this.field_142052_b = this.field_75299_d.func_142015_aE();
      if (this.field_75312_a) {
         double var1 = this.func_111175_f();

         for(EntityCreature var5 : this.field_75299_d
            .field_70170_p
            .func_72872_a(
               this.field_75299_d.getClass(),
               AxisAlignedBB.func_72330_a(
                     this.field_75299_d.field_70165_t,
                     this.field_75299_d.field_70163_u,
                     this.field_75299_d.field_70161_v,
                     this.field_75299_d.field_70165_t + 1.0,
                     this.field_75299_d.field_70163_u + 1.0,
                     this.field_75299_d.field_70161_v + 1.0
                  )
                  .func_72314_b(var1, 10.0, var1)
            )) {
            if (this.field_75299_d != var5 && var5.func_70638_az() == null && !var5.func_142014_c(this.field_75299_d.func_70643_av())) {
               var5.func_70624_b(this.field_75299_d.func_70643_av());
            }
         }
      }

      super.func_75249_e();
   }
}
