package net.minecraft.world.entity.projectile.hurtingprojectile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SmallFireball extends Fireball {
   public SmallFireball(final EntityType<? extends SmallFireball> type, final Level level) {
      super(type, level);
   }

   public SmallFireball(final Level level, final LivingEntity mob, final Vec3 direction) {
      super(EntityType.SMALL_FIREBALL, mob, direction, level);
   }

   public SmallFireball(final Level level, final double x, final double y, final double z, final Vec3 direction) {
      super(EntityType.SMALL_FIREBALL, x, y, z, direction, level);
   }

   protected void onHitEntity(final EntityHitResult hitResult) {
      super.onHitEntity(hitResult);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel serverLevel) {
         Entity var7 = hitResult.getEntity();
         Entity owner = this.getOwner();
         int remainingFireTicks = var7.getRemainingFireTicks();
         var7.igniteForSeconds(5.0F);
         DamageSource damageSource = this.damageSources().fireball(this, owner);
         if (!var7.hurtServer(serverLevel, damageSource, 5.0F)) {
            var7.setRemainingFireTicks(remainingFireTicks);
         } else {
            EnchantmentHelper.doPostAttackEffects(serverLevel, var7, damageSource);
         }

      }
   }

   protected void onHitBlock(final BlockHitResult hitResult) {
      super.onHitBlock(hitResult);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel serverLevel) {
         Entity owner = this.getOwner();
         if (!(owner instanceof Mob) || (Boolean)serverLevel.getGameRules().get(GameRules.MOB_GRIEFING)) {
            BlockPos pos = hitResult.getBlockPos().relative(hitResult.getDirection());
            if (this.level().isEmptyBlock(pos)) {
               this.level().setBlockAndUpdate(pos, BaseFireBlock.getState(this.level(), pos));
            }
         }

      }
   }

   protected void onHit(final HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide()) {
         this.discard();
      }

   }
}
