package net.minecraft.world.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotion extends ThrowableItemProjectile {
   public static final double SPLASH_RANGE = 4.0;
   private static final double SPLASH_RANGE_SQ = 16.0;
   public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = (var0) -> var0.isSensitiveToWater() || var0.isOnFire();

   public ThrownPotion(EntityType<? extends ThrownPotion> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownPotion(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.POTION, var2, var1, var3);
   }

   public ThrownPotion(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.POTION, var2, var4, var6, var1, var8);
   }

   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   protected double getDefaultGravity() {
      return 0.05;
   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level().isClientSide) {
         ItemStack var2 = this.getItem();
         Direction var3 = var1.getDirection();
         BlockPos var4 = var1.getBlockPos();
         BlockPos var5 = var4.relative(var3);
         PotionContents var6 = (PotionContents)var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         if (var6.is(Potions.WATER)) {
            this.dowseFire(var5);
            this.dowseFire(var5.relative(var3.getOpposite()));

            for(Direction var8 : Direction.Plane.HORIZONTAL) {
               this.dowseFire(var5.relative(var8));
            }
         }

      }
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         ItemStack var6 = this.getItem();
         PotionContents var4 = (PotionContents)var6.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         if (var4.is(Potions.WATER)) {
            this.applyWater(var2);
         } else if (var4.hasEffects()) {
            float var5 = (Float)var6.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(var4, var5);
            } else {
               this.applySplash(var2, var4.getAllEffects(), var5, var1.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)var1).getEntity() : null);
            }
         }

         int var7 = var4.potion().isPresent() && ((Potion)((Holder)var4.potion().get()).value()).hasInstantEffects() ? 2007 : 2002;
         var2.levelEvent(var7, this.blockPosition(), var4.getColor());
         this.discard();
      }
   }

   private void applyWater(ServerLevel var1) {
      AABB var2 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);

      for(LivingEntity var5 : this.level().getEntitiesOfClass(LivingEntity.class, var2, WATER_SENSITIVE_OR_ON_FIRE)) {
         double var6 = this.distanceToSqr(var5);
         if (var6 < 16.0) {
            if (var5.isSensitiveToWater()) {
               var5.hurtServer(var1, this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
            }

            if (var5.isOnFire() && var5.isAlive()) {
               var5.extinguishFire();
            }
         }
      }

      for(Axolotl var10 : this.level().getEntitiesOfClass(Axolotl.class, var2)) {
         var10.rehydrate();
      }

   }

   private void applySplash(ServerLevel var1, Iterable<MobEffectInstance> var2, float var3, @Nullable Entity var4) {
      AABB var5 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
      List var6 = var1.getEntitiesOfClass(LivingEntity.class, var5);
      if (!var6.isEmpty()) {
         Entity var7 = this.getEffectSource();

         for(LivingEntity var9 : var6) {
            if (var9.isAffectedByPotions()) {
               double var10 = this.distanceToSqr(var9);
               if (var10 < 16.0) {
                  double var12;
                  if (var9 == var4) {
                     var12 = 1.0;
                  } else {
                     var12 = 1.0 - Math.sqrt(var10) / 4.0;
                  }

                  for(MobEffectInstance var15 : var2) {
                     Holder var16 = var15.getEffect();
                     if (((MobEffect)var16.value()).isInstantenous()) {
                        ((MobEffect)var16.value()).applyInstantenousEffect(var1, this, this.getOwner(), var9, var15.getAmplifier(), var12);
                     } else {
                        int var17 = var15.mapDuration((var3x) -> (int)((double)var3 * var12 * (double)var3x + 0.5));
                        MobEffectInstance var18 = new MobEffectInstance(var16, var17, var15.getAmplifier(), var15.isAmbient(), var15.isVisible());
                        if (!var18.endsWithin(20)) {
                           var9.addEffect(var18, var7);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(PotionContents var1, float var2) {
      AreaEffectCloud var3 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      Entity var5 = this.getOwner();
      if (var5 instanceof LivingEntity var4) {
         var3.setOwner(var4);
      }

      var3.setRadius(3.0F);
      var3.setRadiusOnUse(-0.5F);
      var3.setWaitTime(10);
      var3.setRadiusPerTick(-var3.getRadius() / (float)var3.getDuration());
      var3.setPotionContents(var1);
      var3.setPotionDurationScale(var2);
      this.level().addFreshEntity(var3);
   }

   private boolean isLingering() {
      return this.getItem().is(Items.LINGERING_POTION);
   }

   private void dowseFire(BlockPos var1) {
      BlockState var2 = this.level().getBlockState(var1);
      if (var2.is(BlockTags.FIRE)) {
         this.level().destroyBlock(var1, false, this);
      } else if (AbstractCandleBlock.isLit(var2)) {
         AbstractCandleBlock.extinguish((Player)null, var2, this.level(), var1);
      } else if (CampfireBlock.isLitCampfire(var2)) {
         this.level().levelEvent((Entity)null, 1009, var1, 0);
         CampfireBlock.dowse(this.getOwner(), this.level(), var1, var2);
         this.level().setBlockAndUpdate(var1, (BlockState)var2.setValue(CampfireBlock.LIT, false));
      }

   }

   public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity var1, DamageSource var2) {
      double var3 = var1.position().x - this.position().x;
      double var5 = var1.position().z - this.position().z;
      return DoubleDoubleImmutablePair.of(var3, var5);
   }
}
