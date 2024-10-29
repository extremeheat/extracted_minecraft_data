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
import net.minecraft.world.phys.Vec3;

public class SmallFireball extends Fireball {
   public SmallFireball(EntityType<? extends SmallFireball> var1, Level var2) {
      super(var1, var2);
   }

   public SmallFireball(Level var1, LivingEntity var2, Vec3 var3) {
      super(EntityType.SMALL_FIREBALL, var2, var3, var1);
   }

   public SmallFireball(Level var1, double var2, double var4, double var6, Vec3 var8) {
      super(EntityType.SMALL_FIREBALL, var2, var4, var6, var8, var1);
   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         Entity var7 = var1.getEntity();
         Entity var4 = this.getOwner();
         int var5 = var7.getRemainingFireTicks();
         var7.igniteForSeconds(5.0F);
         DamageSource var6 = this.damageSources().fireball(this, var4);
         if (!var7.hurtServer(var2, var6, 5.0F)) {
            var7.setRemainingFireTicks(var5);
         } else {
            EnchantmentHelper.doPostAttackEffects(var2, var7, var6);
         }

      }
   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         Entity var5 = this.getOwner();
         if (!(var5 instanceof Mob) || var2.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos var4 = var1.getBlockPos().relative(var1.getDirection());
            if (this.level().isEmptyBlock(var4)) {
               this.level().setBlockAndUpdate(var4, BaseFireBlock.getState(this.level(), var4));
            }
         }

      }
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         this.discard();
      }

   }
}
