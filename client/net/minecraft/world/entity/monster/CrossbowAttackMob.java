package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public interface CrossbowAttackMob {
   void setChargingCrossbow(boolean var1);

   void shootProjectile(LivingEntity var1, ItemStack var2, Projectile var3, float var4);

   @Nullable
   LivingEntity getTarget();
}
