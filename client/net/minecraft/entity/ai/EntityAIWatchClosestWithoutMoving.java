package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntityAIWatchClosestWithoutMoving extends EntityAIWatchClosest {
   public EntityAIWatchClosestWithoutMoving(EntityLiving var1, Class<? extends Entity> var2, float var3, float var4) {
      super(var1, var2, var3, var4);
      this.func_75248_a(3);
   }
}
