package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class ArrowItem extends Item implements ProjectileItem {
   public ArrowItem(Item.Properties var1) {
      super(var1);
   }

   public AbstractArrow createArrow(Level var1, ItemStack var2, LivingEntity var3) {
      return new Arrow(var1, var3, var2.copyWithCount(1));
   }

   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      Arrow var5 = new Arrow(var1, var2.x(), var2.y(), var2.z(), var3.copyWithCount(1));
      var5.pickup = AbstractArrow.Pickup.ALLOWED;
      return var5;
   }
}
