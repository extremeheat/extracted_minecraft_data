package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class ArrowItem extends Item {
   public ArrowItem(Item.Properties var1) {
      super(var1);
   }

   public AbstractArrow createArrow(Level var1, ItemStack var2, LivingEntity var3) {
      Arrow var4 = new Arrow(var1, var3);
      var4.setEffectsFromItem(var2);
      return var4;
   }
}
