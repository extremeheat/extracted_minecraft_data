package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase {
   EntityAnimal field_75348_a;
   EntityAnimal field_75346_b;
   double field_75347_c;
   private int field_75345_d;

   public EntityAIFollowParent(EntityAnimal var1, double var2) {
      super();
      this.field_75348_a = var1;
      this.field_75347_c = var2;
   }

   @Override
   public boolean func_75250_a() {
      if (this.field_75348_a.func_70874_b() >= 0) {
         return false;
      } else {
         List var1 = this.field_75348_a
            .field_70170_p
            .func_72872_a(this.field_75348_a.getClass(), this.field_75348_a.field_70121_D.func_72314_b(8.0, 4.0, 8.0));
         EntityAnimal var2 = null;
         double var3 = 1.7976931348623157E308;

         for(EntityAnimal var6 : var1) {
            if (var6.func_70874_b() >= 0) {
               double var7 = this.field_75348_a.func_70068_e(var6);
               if (!(var7 > var3)) {
                  var3 = var7;
                  var2 = var6;
               }
            }
         }

         if (var2 == null) {
            return false;
         } else if (var3 < 9.0) {
            return false;
         } else {
            this.field_75346_b = var2;
            return true;
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      if (!this.field_75346_b.func_70089_S()) {
         return false;
      } else {
         double var1 = this.field_75348_a.func_70068_e(this.field_75346_b);
         return !(var1 < 9.0) && !(var1 > 256.0);
      }
   }

   @Override
   public void func_75249_e() {
      this.field_75345_d = 0;
   }

   @Override
   public void func_75251_c() {
      this.field_75346_b = null;
   }

   @Override
   public void func_75246_d() {
      if (--this.field_75345_d <= 0) {
         this.field_75345_d = 10;
         this.field_75348_a.func_70661_as().func_75497_a(this.field_75346_b, this.field_75347_c);
      }
   }
}
