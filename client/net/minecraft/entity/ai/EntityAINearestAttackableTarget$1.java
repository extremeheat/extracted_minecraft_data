package net.minecraft.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

class EntityAINearestAttackableTarget$1 implements IEntitySelector {
   EntityAINearestAttackableTarget$1(EntityAINearestAttackableTarget var1, IEntitySelector var2) {
      super();
      this.field_111102_d = var1;
      this.field_111103_c = var2;
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      if (!(var1 instanceof EntityLivingBase)) {
         return false;
      } else {
         return this.field_111103_c != null && !this.field_111103_c.func_82704_a(var1)
            ? false
            : this.field_111102_d.func_75296_a((EntityLivingBase)var1, false);
      }
   }
}
