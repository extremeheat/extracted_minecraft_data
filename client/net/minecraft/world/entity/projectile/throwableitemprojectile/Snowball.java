package net.minecraft.world.entity.projectile.throwableitemprojectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class Snowball extends ThrowableBallProjectile {
   public Snowball(final EntityType<? extends Snowball> type, final Level level) {
      super(type, level);
   }

   public Snowball(final Level level, final LivingEntity mob, final ItemStack itemStack) {
      super(EntityTypes.SNOWBALL, mob, level, itemStack);
   }

   public Snowball(final Level level, final double x, final double y, final double z, final ItemStack itemStack) {
      super(EntityTypes.SNOWBALL, x, y, z, level, itemStack);
   }

   protected Item getDefaultItem() {
      return Items.SNOWBALL;
   }

   protected int getDamage(final EntityHitResult hitResult) {
      return hitResult.getEntity() instanceof Blaze ? 3 : 0;
   }
}
