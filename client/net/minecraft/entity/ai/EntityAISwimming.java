package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAISwimming extends EntityAIBase {
   private EntityLiving field_75373_a;

   public EntityAISwimming(EntityLiving var1) {
      super();
      this.field_75373_a = var1;
      this.func_75248_a(4);
      var1.func_70661_as().func_75495_e(true);
   }

   @Override
   public boolean func_75250_a() {
      return this.field_75373_a.func_70090_H() || this.field_75373_a.func_70058_J();
   }

   @Override
   public void func_75246_d() {
      if (this.field_75373_a.func_70681_au().nextFloat() < 0.8F) {
         this.field_75373_a.func_70683_ar().func_75660_a();
      }
   }
}
