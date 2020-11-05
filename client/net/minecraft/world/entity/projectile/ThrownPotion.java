package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotion extends ThrowableItemProjectile implements ItemSupplier {
   public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isSensitiveToWater;

   public ThrownPotion(EntityType<? extends ThrownPotion> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownPotion(Level var1, LivingEntity var2) {
      super(EntityType.POTION, var2, var1);
   }

   public ThrownPotion(Level var1, double var2, double var4, double var6) {
      super(EntityType.POTION, var2, var4, var6, var1);
   }

   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   protected float getGravity() {
      return 0.05F;
   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      if (!this.level.isClientSide) {
         ItemStack var2 = this.getItem();
         Potion var3 = PotionUtils.getPotion(var2);
         List var4 = PotionUtils.getMobEffects(var2);
         boolean var5 = var3 == Potions.WATER && var4.isEmpty();
         Direction var6 = var1.getDirection();
         BlockPos var7 = var1.getBlockPos();
         BlockPos var8 = var7.relative(var6);
         if (var5) {
            this.dowseFire(var8, var6);
            this.dowseFire(var8.relative(var6.getOpposite()), var6);
            Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

            while(var9.hasNext()) {
               Direction var10 = (Direction)var9.next();
               this.dowseFire(var8.relative(var10), var10);
            }
         }

      }
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level.isClientSide) {
         ItemStack var2 = this.getItem();
         Potion var3 = PotionUtils.getPotion(var2);
         List var4 = PotionUtils.getMobEffects(var2);
         boolean var5 = var3 == Potions.WATER && var4.isEmpty();
         if (var5) {
            this.applyWater();
         } else if (!var4.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(var2, var3);
            } else {
               this.applySplash(var4, var1.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)var1).getEntity() : null);
            }
         }

         int var6 = var3.hasInstantEffects() ? 2007 : 2002;
         this.level.levelEvent(var6, this.blockPosition(), PotionUtils.getColor(var2));
         this.discard();
      }
   }

   private void applyWater() {
      AABB var1 = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List var2 = this.level.getEntitiesOfClass(LivingEntity.class, var1, WATER_SENSITIVE);
      if (!var2.isEmpty()) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            LivingEntity var4 = (LivingEntity)var3.next();
            double var5 = this.distanceToSqr(var4);
            if (var5 < 16.0D && var4.isSensitiveToWater()) {
               var4.hurt(DamageSource.indirectMagic(var4, this.getOwner()), 1.0F);
            }
         }
      }

   }

   private void applySplash(List<MobEffectInstance> var1, @Nullable Entity var2) {
      AABB var3 = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List var4 = this.level.getEntitiesOfClass(LivingEntity.class, var3);
      if (!var4.isEmpty()) {
         Iterator var5 = var4.iterator();

         while(true) {
            LivingEntity var6;
            double var7;
            do {
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  var6 = (LivingEntity)var5.next();
               } while(!var6.isAffectedByPotions());

               var7 = this.distanceToSqr(var6);
            } while(var7 >= 16.0D);

            double var9 = 1.0D - Math.sqrt(var7) / 4.0D;
            if (var6 == var2) {
               var9 = 1.0D;
            }

            Iterator var11 = var1.iterator();

            while(var11.hasNext()) {
               MobEffectInstance var12 = (MobEffectInstance)var11.next();
               MobEffect var13 = var12.getEffect();
               if (var13.isInstantenous()) {
                  var13.applyInstantenousEffect(this, this.getOwner(), var6, var12.getAmplifier(), var9);
               } else {
                  int var14 = (int)(var9 * (double)var12.getDuration() + 0.5D);
                  if (var14 > 20) {
                     var6.addEffect(new MobEffectInstance(var13, var14, var12.getAmplifier(), var12.isAmbient(), var12.isVisible()));
                  }
               }
            }
         }
      }
   }

   private void makeAreaOfEffectCloud(ItemStack var1, Potion var2) {
      AreaEffectCloud var3 = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
      Entity var4 = this.getOwner();
      if (var4 instanceof LivingEntity) {
         var3.setOwner((LivingEntity)var4);
      }

      var3.setRadius(3.0F);
      var3.setRadiusOnUse(-0.5F);
      var3.setWaitTime(10);
      var3.setRadiusPerTick(-var3.getRadius() / (float)var3.getDuration());
      var3.setPotion(var2);
      Iterator var5 = PotionUtils.getCustomEffects(var1).iterator();

      while(var5.hasNext()) {
         MobEffectInstance var6 = (MobEffectInstance)var5.next();
         var3.addEffect(new MobEffectInstance(var6));
      }

      CompoundTag var7 = var1.getTag();
      if (var7 != null && var7.contains("CustomPotionColor", 99)) {
         var3.setFixedColor(var7.getInt("CustomPotionColor"));
      }

      this.level.addFreshEntity(var3);
   }

   private boolean isLingering() {
      return this.getItem().is(Items.LINGERING_POTION);
   }

   private void dowseFire(BlockPos var1, Direction var2) {
      BlockState var3 = this.level.getBlockState(var1);
      if (var3.is(BlockTags.FIRE)) {
         this.level.removeBlock(var1, false);
      } else if (CampfireBlock.isLitCampfire(var3)) {
         this.level.levelEvent((Player)null, 1009, var1, 0);
         CampfireBlock.dowse(this.level, var1, var3);
         this.level.setBlockAndUpdate(var1, (BlockState)var3.setValue(CampfireBlock.LIT, false));
      }

   }
}
