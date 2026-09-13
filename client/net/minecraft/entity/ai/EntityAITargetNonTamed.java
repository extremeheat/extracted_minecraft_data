package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget {
   private EntityTameable field_75310_g;

   public EntityAITargetNonTamed(EntityTameable var1, Class var2, int var3, boolean var4) {
      super(var1, var2, var3, var4);
      this.field_75310_g = var1;
   }

   @Override
   public boolean func_75250_a() {
      return !this.field_75310_g.func_70909_n() && super.func_75250_a();
   }
}
