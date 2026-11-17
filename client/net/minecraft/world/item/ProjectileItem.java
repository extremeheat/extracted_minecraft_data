package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public interface ProjectileItem {
   Projectile asProjectile(final Level level, final Position position, final ItemStack itemStack, final Direction direction);

   default void shoot(final Projectile projectile, final double xd, final double yd, final double zd, final float pow, final float uncertainty) {
      projectile.shoot(xd, yd, zd, pow, uncertainty);
   }
}
