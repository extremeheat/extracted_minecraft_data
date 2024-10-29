package net.minecraft.world.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.Iterator;
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
   public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = (var0) -> {
      return var0.isSensitiveToWater() || var0.isOnFire();
   };

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
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               Direction var8 = (Direction)var7.next();
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
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(var4);
            } else {
               this.applySplash(var2, var4.getAllEffects(), var1.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)var1).getEntity() : null);
            }
         }

         int var5 = var4.potion().isPresent() && ((Potion)((Holder)var4.potion().get()).value()).hasInstantEffects() ? 2007 : 2002;
         var2.levelEvent(var5, this.blockPosition(), var4.getColor());
         this.discard();
      }
   }

   private void applyWater(ServerLevel var1) {
      AABB var2 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
      List var3 = this.level().getEntitiesOfClass(LivingEntity.class, var2, WATER_SENSITIVE_OR_ON_FIRE);
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         LivingEntity var5 = (LivingEntity)var4.next();
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

      List var8 = this.level().getEntitiesOfClass(Axolotl.class, var2);
      Iterator var9 = var8.iterator();

      while(var9.hasNext()) {
         Axolotl var10 = (Axolotl)var9.next();
         var10.rehydrate();
      }

   }

   private void applySplash(ServerLevel var1, Iterable<MobEffectInstance> var2, @Nullable Entity var3) {
      AABB var4 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
      List var5 = var1.getEntitiesOfClass(LivingEntity.class, var4);
      if (!var5.isEmpty()) {
         Entity var6 = this.getEffectSource();
         Iterator var7 = var5.iterator();

         while(true) {
            LivingEntity var8;
            double var9;
            do {
               do {
                  if (!var7.hasNext()) {
                     return;
                  }

                  var8 = (LivingEntity)var7.next();
               } while(!var8.isAffectedByPotions());

               var9 = this.distanceToSqr(var8);
            } while(!(var9 < 16.0));

            double var11;
            if (var8 == var3) {
               var11 = 1.0;
            } else {
               var11 = 1.0 - Math.sqrt(var9) / 4.0;
            }

            Iterator var13 = var2.iterator();

            while(var13.hasNext()) {
               MobEffectInstance var14 = (MobEffectInstance)var13.next();
               Holder var15 = var14.getEffect();
               if (((MobEffect)var15.value()).isInstantenous()) {
                  ((MobEffect)var15.value()).applyInstantenousEffect(var1, this, this.getOwner(), var8, var14.getAmplifier(), var11);
               } else {
                  int var16 = var14.mapDuration((var2x) -> {
                     return (int)(var11 * (double)var2x + 0.5);
                  });
                  MobEffectInstance var17 = new MobEffectInstance(var15, var16, var14.getAmplifier(), var14.isAmbient(), var14.isVisible());
                  if (!var17.endsWithin(20)) {
                     var8.addEffect(var17, var6);
                  }
               }
            }
         }
      }
   }

   private void makeAreaOfEffectCloud(PotionContents var1) {
      AreaEffectCloud var2 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
      Entity var4 = this.getOwner();
      if (var4 instanceof LivingEntity var3) {
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
         AbstractCandleBlock.extinguish((Player)null, var2, this.level(), var1);
      } else if (CampfireBlock.isLitCampfire(var2)) {
         this.level().levelEvent((Player)null, 1009, var1, 0);
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
