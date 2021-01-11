package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class EntityAIMoveTowardsTarget extends EntityAIBase {
   private EntityCreature field_75431_a;
   private EntityLivingBase field_75429_b;
   private double field_75430_c;
   private double field_75427_d;
   private double field_75428_e;
   private double field_75425_f;
   private float field_75426_g;

   public EntityAIMoveTowardsTarget(EntityCreature var1, double var2, float var4) {
      super();
      this.field_75431_a = var1;
      this.field_75425_f = var2;
      this.field_75426_g = var4;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      this.field_75429_b = this.field_75431_a.func_70638_az();
      if (this.field_75429_b == null) {
         return false;
      } else if (this.field_75429_b.func_70068_e(this.field_75431_a) > (double)(this.field_75426_g * this.field_75426_g)) {
         return false;
      } else {
         Vec3 var1 = RandomPositionGenerator.func_75464_a(this.field_75431_a, 16, 7, new Vec3(this.field_75429_b.field_70165_t, this.field_75429_b.field_70163_u, this.field_75429_b.field_70161_v));
         if (var1 == null) {
            return false;
         } else {
            this.field_75430_c = var1.field_72450_a;
            this.field_75427_d = var1.field_72448_b;
            this.field_75428_e = var1.field_72449_c;
            return true;
         }
      }
   }

   public boolean func_75253_b() {
      return !this.field_75431_a.func_70661_as().func_75500_f() && this.field_75429_b.func_70089_S() && this.field_75429_b.func_70068_e(this.field_75431_a) < (double)(this.field_75426_g * this.field_75426_g);
   }

   public void func_75251_c() {
      this.field_75429_b = null;
   }

   public void func_75249_e() {
      this.field_75431_a.func_70661_as().func_75492_a(this.field_75430_c, this.field_75427_d, this.field_75428_e, this.field_75425_f);
   }
}
