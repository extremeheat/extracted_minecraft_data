package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;

public class SpectralArrowItem extends ArrowItem {
   public SpectralArrowItem(Item.Properties var1) {
      super(var1);
   }

   public AbstractArrow createArrow(Level var1, ItemStack var2, LivingEntity var3) {
      return new SpectralArrow(var1, var3);
   }
}
