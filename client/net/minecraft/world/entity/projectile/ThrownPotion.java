package net.minecraft.world.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class ThrownPotion extends ThrowableItemProjectile implements ItemSupplier {
   public static final double SPLASH_RANGE = 4.0;
   private static final double SPLASH_RANGE_SQ = 16.0;
   public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = var0 -> var0.isSensitiveToWater() || var0.isOnFire();

   public ThrownPotion(EntityType<? extends ThrownPotion> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownPotion(Level var1, LivingEntity var2) {
      super(EntityType.POTION, var2, var1);
   }

   public ThrownPotion(Level var1, double var2, double var4, double var6) {
      super(EntityType.POTION, var2, var4, var6, var1);
   }

   @Override
   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   @Override
   protected double getDefaultGravity() {
      return 0.05;
   }

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level().isClientSide) {
         ItemStack var2 = this.getItem();
         Direction var3 = var1.getDirection();
         BlockPos var4 = var1.getBlockPos();
         BlockPos var5 = var4.relative(var3);
         PotionContents var6 = var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         if (var6.is(Potions.WATER)) {
            this.dowseFire(var5);
            this.dowseFire(var5.relative(var3.getOpposite()));

            for (Direction var8 : Direction.Plane.HORIZONTAL) {
               this.dowseFire(var5.relative(var8));
            }
         }
      }
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         ItemStack var2 = this.getItem();
         PotionContents var3 = var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         if (var3.is(Potions.WATER)) {
            this.applyWater();
         } else if (var3.hasEffects()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(var3);
            } else {
               this.applySplash(var3.getAllEffects(), var1.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)var1).getEntity() : null);
            }
         }

         int var4 = var3.potion().isPresent() && var3.potion().get().value().hasInstantEffects() ? 2007 : 2002;
         this.level().levelEvent(var4, this.blockPosition(), var3.getColor());
         this.discard();
      }
   }

   private void applyWater() {
      AABB var1 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);

      for (LivingEntity var4 : this.level().getEntitiesOfClass(LivingEntity.class, var1, WATER_SENSITIVE_OR_ON_FIRE)) {
         double var5 = this.distanceToSqr(var4);
         if (var5 < 16.0) {
            if (var4.isSensitiveToWater()) {
               var4.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
            }

            if (var4.isOnFire() && var4.isAlive()) {
               var4.extinguishFire();
            }
         }
      }

      for (Axolotl var9 : this.level().getEntitiesOfClass(Axolotl.class, var1)) {
         var9.rehydrate();
      }
   }

   private void applySplash(Iterable<MobEffectInstance> var1, @Nullable Entity var2) {
      AABB var3 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
      List var4 = this.level().getEntitiesOfClass(LivingEntity.class, var3);
      if (!var4.isEmpty()) {
         Entity var5 = this.getEffectSource();

         for (LivingEntity var7 : var4) {
            if (var7.isAffectedByPotions()) {
               double var8 = this.distanceToSqr(var7);
               if (var8 < 16.0) {
                  double var10;
                  if (var7 == var2) {
                     var10 = 1.0;
                  } else {
                     var10 = 1.0 - Math.sqrt(var8) / 4.0;
                  }

                  for (MobEffectInstance var13 : var1) {
                     Holder var14 = var13.getEffect();
                     if (((MobEffect)var14.value()).isInstantenous()) {
                        ((MobEffect)var14.value()).applyInstantenousEffect(this, this.getOwner(), var7, var13.getAmplifier(), var10);
                     } else {
                        int var15 = var13.mapDuration(var2x -> (int)(var10 * (double)var2x + 0.5));
                        MobEffectInstance var16 = new MobEffectInstance(var14, var15, var13.getAmplifier(), var13.isAmbient(), var13.isVisible());
                        if (!var16.endsWithin(20)) {
                           var7.addEffect(var16, var5);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void makeAreaOfEffectCloud(PotionContents var1) {
      AreaEffectCloud var2 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      if (this.getOwner() instanceof LivingEntity var3) {
         var2.setOwner(var3);
      }

      var2.setRadius(3.0F);
      var2.setRadiusOnUse(-0.5F);
      var2.setWaitTime(10);
      var2.setRadiusPerTick(-var2.getRadius() / (float)var2.getDuration());
      var2.setPotionContents(var1);
      this.level().addFreshEntity(var2);
   }

   private boolean isLingering() {
      return this.getItem().is(Items.LINGERING_POTION);
   }

   private void dowseFire(BlockPos var1) {
      BlockState var2 = this.level().getBlockState(var1);
      if (var2.is(BlockTags.FIRE)) {
         this.level().destroyBlock(var1, false, this);
      } else if (AbstractCandleBlock.isLit(var2)) {
         AbstractCandleBlock.extinguish(null, var2, this.level(), var1);
      } else if (CampfireBlock.isLitCampfire(var2)) {
         this.level().levelEvent(null, 1009, var1, 0);
         CampfireBlock.dowse(this.getOwner(), this.level(), var1, var2);
         this.level().setBlockAndUpdate(var1, var2.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
      }
   }

   @Override
   public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity var1, DamageSource var2) {
      double var3 = var1.position().x - this.position().x;
      double var5 = var1.position().z - this.position().z;
      return DoubleDoubleImmutablePair.of(var3, var5);
   }
}
