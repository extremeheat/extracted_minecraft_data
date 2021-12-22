package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level.isClientSide) {
         Entity var2 = var1.getEntity();
         if (!var2.fireImmune()) {
            Entity var3 = this.getOwner();
            int var4 = var2.getRemainingFireTicks();
            var2.setSecondsOnFire(5);
            boolean var5 = var2.hurt(DamageSource.fireball(this, var3), 5.0F);
            if (!var5) {
               var2.setRemainingFireTicks(var4);
            } else if (var3 instanceof LivingEntity) {
               this.doEnchantDamageEffects((LivingEntity)var3, var2);
            }
         }

      }
   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level.isClientSide) {
         Entity var2 = this.getOwner();
         if (!(var2 instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos var3 = var1.getBlockPos().relative(var1.getDirection());
            if (this.level.isEmptyBlock(var3)) {
               this.level.setBlockAndUpdate(var3, BaseFireBlock.getState(this.level, var3));
            }
         }

      }
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level.isClientSide) {
         this.discard();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }
}
