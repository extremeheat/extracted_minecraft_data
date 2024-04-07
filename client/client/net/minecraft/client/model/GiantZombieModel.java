package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Giant;

public class GiantZombieModel extends AbstractZombieModel<Giant> {
   public GiantZombieModel(ModelPart var1) {
      super(var1);
   }

   public boolean isAggressive(Giant var1) {
      return false;
   }
}
