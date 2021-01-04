package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

   protected void onHit(HitResult var1) {
      if (!this.level.isClientSide) {
         if (var1.getType() == HitResult.Type.ENTITY) {
            Entity var5 = ((EntityHitResult)var1).getEntity();
            if (!var5.fireImmune()) {
               int var6 = var5.getRemainingFireTicks();
               var5.setSecondsOnFire(5);
               boolean var4 = var5.hurt(DamageSource.fireball(this, this.owner), 5.0F);
               if (var4) {
                  this.doEnchantDamageEffects(this.owner, var5);
               } else {
                  var5.setRemainingFireTicks(var6);
               }
            }
         } else if (this.owner == null || !(this.owner instanceof Mob) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockHitResult var2 = (BlockHitResult)var1;
            BlockPos var3 = var2.getBlockPos().relative(var2.getDirection());
            if (this.level.isEmptyBlock(var3)) {
               this.level.setBlockAndUpdate(var3, Blocks.FIRE.defaultBlockState());
            }
         }

         this.remove();
      }

   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }
}
