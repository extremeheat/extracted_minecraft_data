package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SmallFireball extends Fireball {
   public SmallFireball(EntityType<? extends SmallFireball> var1, Level var2) {
      super(var1, var2);
   }

   public SmallFireball(Level var1, LivingEntity var2, double var3, double var5, double var7) {
      super(EntityType.SMALL_FIREBALL, var2, var3, var5, var7, var1);
   }

   public SmallFireball(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.SMALL_FIREBALL, var2, var4, var6, var8, var10, var12, var1);
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (this.level() instanceof ServerLevel var2) {
         Entity var7 = var1.getEntity();
         Entity var4 = this.getOwner();
         int var5 = var7.getRemainingFireTicks();
         var7.igniteForSeconds(5.0F);
         DamageSource var6 = this.damageSources().fireball(this, var4);
         if (!var7.hurt(var6, 5.0F)) {
            var7.setRemainingFireTicks(var5);
         } else {
            EnchantmentHelper.doPostAttackEffects(var2, var7, var6);
         }
      }
   }

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level().isClientSide) {
         Entity var2 = this.getOwner();
         if (!(var2 instanceof Mob) || this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos var3 = var1.getBlockPos().relative(var1.getDirection());
            if (this.level().isEmptyBlock(var3)) {
               this.level().setBlockAndUpdate(var3, BaseFireBlock.getState(this.level(), var3));
            }
         }
      }
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         this.discard();
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }
}
