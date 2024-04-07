package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface CrossbowAttackMob extends RangedAttackMob {
   void setChargingCrossbow(boolean var1);

   @Nullable
   LivingEntity getTarget();

   void onCrossbowAttackPerformed();

   default void performCrossbowAttack(LivingEntity var1, float var2) {
      InteractionHand var3 = ProjectileUtil.getWeaponHoldingHand(var1, Items.CROSSBOW);
      ItemStack var4 = var1.getItemInHand(var3);
      if (var4.getItem() instanceof CrossbowItem var5) {
         var5.performShooting(var1.level(), var1, var3, var4, var2, (float)(14 - var1.level().getDifficulty().getId() * 4), this.getTarget());
      }

      this.onCrossbowAttackPerformed();
   }
}
