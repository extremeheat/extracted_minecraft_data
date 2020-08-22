package net.minecraft.client.model;

import net.minecraft.world.entity.monster.Giant;

public class GiantZombieModel extends AbstractZombieModel {
   public GiantZombieModel() {
      this(0.0F, false);
   }

   public GiantZombieModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2 ? 32 : 64);
   }

   public boolean isAggressive(Giant var1) {
      return false;
   }
}
