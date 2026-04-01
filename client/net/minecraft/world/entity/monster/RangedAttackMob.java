package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.Entity;

public interface RangedAttackMob {
   void performRangedAttack(Entity target, float power);
}
