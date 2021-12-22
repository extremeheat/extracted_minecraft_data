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
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
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
   public static final double SPLASH_RANGE = 4.0D;
   private static final double SPLASH_RANGE_SQ = 16.0D;
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
            this.dowseFire(var8);
            this.dowseFire(var8.relative(var6.getOpposite()));
            Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

            while(var9.hasNext()) {
               Direction var10 = (Direction)var9.next();
               this.dowseFire(var8.relative(var10));
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
               var4.hurt(DamageSource.indirectMagic(this, this.getOwner()), 1.0F);
            }
         }
      }

      List var7 = this.level.getEntitiesOfClass(Axolotl.class, var1);
      Iterator var8 = var7.iterator();

      while(var8.hasNext()) {
         Axolotl var9 = (Axolotl)var8.next();
         var9.rehydrate();
      }

   }

   private void applySplash(List<MobEffectInstance> var1, @Nullable Entity var2) {
      AABB var3 = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List var4 = this.level.getEntitiesOfClass(LivingEntity.class, var3);
      if (!var4.isEmpty()) {
         Entity var5 = this.getEffectSource();
         Iterator var6 = var4.iterator();

         while(true) {
            LivingEntity var7;
            double var8;
            do {
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  var7 = (LivingEntity)var6.next();
               } while(!var7.isAffectedByPotions());

               var8 = this.distanceToSqr(var7);
            } while(!(var8 < 16.0D));

            double var10 = 1.0D - Math.sqrt(var8) / 4.0D;
            if (var7 == var2) {
               var10 = 1.0D;
            }

            Iterator var12 = var1.iterator();

            while(var12.hasNext()) {
               MobEffectInstance var13 = (MobEffectInstance)var12.next();
               MobEffect var14 = var13.getEffect();
               if (var14.isInstantenous()) {
                  var14.applyInstantenousEffect(this, this.getOwner(), var7, var13.getAmplifier(), var10);
               } else {
                  int var15 = (int)(var10 * (double)var13.getDuration() + 0.5D);
                  if (var15 > 20) {
                     var7.addEffect(new MobEffectInstance(var14, var15, var13.getAmplifier(), var13.isAmbient(), var13.isVisible()), var5);
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
      return this.getItem().method_87(Items.LINGERING_POTION);
   }

   private void dowseFire(BlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      if (var2.is(BlockTags.FIRE)) {
         this.level.removeBlock(var1, false);
      } else if (AbstractCandleBlock.isLit(var2)) {
         AbstractCandleBlock.extinguish((Player)null, var2, this.level, var1);
      } else if (CampfireBlock.isLitCampfire(var2)) {
         this.level.levelEvent((Player)null, 1009, var1, 0);
         CampfireBlock.dowse(this.getOwner(), this.level, var1, var2);
         this.level.setBlockAndUpdate(var1, (BlockState)var2.setValue(CampfireBlock.LIT, false));
      }

   }
}
