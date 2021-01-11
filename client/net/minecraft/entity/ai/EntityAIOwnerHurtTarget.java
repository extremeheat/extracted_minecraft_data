package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget {
   EntityTameable field_75314_a;
   EntityLivingBase field_75313_b;
   private int field_142050_e;

   public EntityAIOwnerHurtTarget(EntityTameable var1) {
      super(var1, false);
      this.field_75314_a = var1;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      if (!this.field_75314_a.func_70909_n()) {
         return false;
      } else {
         EntityLivingBase var1 = this.field_75314_a.func_70902_q();
         if (var1 == null) {
            return false;
         } else {
            this.field_75313_b = var1.func_110144_aD();
            int var2 = var1.func_142013_aG();
            return var2 != this.field_142050_e && this.func_75296_a(this.field_75313_b, false) && this.field_75314_a.func_142018_a(this.field_75313_b, var1);
         }
      }
   }

   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75313_b);
      EntityLivingBase var1 = this.field_75314_a.func_70902_q();
      if (var1 != null) {
         this.field_142050_e = var1.func_142013_aG();
      }

      super.func_75249_e();
   }
}
