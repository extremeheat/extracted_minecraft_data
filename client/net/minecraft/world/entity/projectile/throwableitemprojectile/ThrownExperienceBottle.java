package net.minecraft.world.entity.projectile.throwableitemprojectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownExperienceBottle extends ThrowableItemProjectile {
   public ThrownExperienceBottle(final EntityType<? extends ThrownExperienceBottle> type, final Level level) {
      super(type, level);
   }

   public ThrownExperienceBottle(final Level level, final LivingEntity mob, final ItemStack itemStack) {
      super(EntityType.EXPERIENCE_BOTTLE, mob, level, itemStack);
   }

   public ThrownExperienceBottle(final Level level, final double x, final double y, final double z, final ItemStack itemStack) {
      super(EntityType.EXPERIENCE_BOTTLE, x, y, z, level, itemStack);
   }

   protected Item getDefaultItem() {
      return Items.EXPERIENCE_BOTTLE;
   }

   protected double getDefaultGravity() {
      return 0.07;
   }

   protected void onHit(final HitResult hitResult) {
      super.onHit(hitResult);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel level) {
         level.levelEvent(2002, this.blockPosition(), -13083194);
         int xpCount = 3 + this.random.nextInt(5) + this.random.nextInt(5);
         if (hitResult instanceof BlockHitResult blockHitResult) {
            Vec3 blockNormalHit = blockHitResult.getDirection().getUnitVec3();
            ExperienceOrb.awardWithDirection(level, hitResult.getLocation(), blockNormalHit, xpCount);
         } else {
            ExperienceOrb.awardWithDirection(level, hitResult.getLocation(), this.getDeltaMovement().scale(-1.0), xpCount);
         }

         this.discard();
      }

   }
}
