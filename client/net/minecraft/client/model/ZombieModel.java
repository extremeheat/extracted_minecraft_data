package net.minecraft.client.model;

import net.minecraft.world.entity.monster.Zombie;

public class ZombieModel<T extends Zombie> extends AbstractZombieModel<T> {
   public ZombieModel(float var1, boolean var2) {
      this(var1, 0.0F, 64, var2 ? 32 : 64);
   }

   protected ZombieModel(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public boolean isAggressive(T var1) {
      return var1.isAggressive();
   }
}
