package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThrownPotion extends ThrowableProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
   private static final Logger LOGGER;
   public static final Predicate<LivingEntity> WATER_SENSITIVE;

   public ThrownPotion(EntityType<? extends ThrownPotion> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownPotion(Level var1, LivingEntity var2) {
      super(EntityType.POTION, var2, var1);
   }

   public ThrownPotion(Level var1, double var2, double var4, double var6) {
      super(EntityType.POTION, var2, var4, var6, var1);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public ItemStack getItem() {
      ItemStack var1 = (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
      if (var1.getItem() != Items.SPLASH_POTION && var1.getItem() != Items.LINGERING_POTION) {
         if (this.level != null) {
            LOGGER.error("ThrownPotion entity {} has no item?!", this.getId());
         }

         return new ItemStack(Items.SPLASH_POTION);
      } else {
         return var1;
      }
   }

   public void setItem(ItemStack var1) {
      this.getEntityData().set(DATA_ITEM_STACK, var1.copy());
   }

   protected float getGravity() {
      return 0.05F;
   }

   protected void onHit(HitResult var1) {
      if (!this.level.isClientSide) {
         ItemStack var2 = this.getItem();
         Potion var3 = PotionUtils.getPotion(var2);
         List var4 = PotionUtils.getMobEffects(var2);
         boolean var5 = var3 == Potions.WATER && var4.isEmpty();
         if (var1.getType() == HitResult.Type.BLOCK && var5) {
            BlockHitResult var6 = (BlockHitResult)var1;
            Direction var7 = var6.getDirection();
            BlockPos var8 = var6.getBlockPos().relative(var7);
            this.dowseFire(var8, var7);
            this.dowseFire(var8.relative(var7.getOpposite()), var7);
            Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

            while(var9.hasNext()) {
               Direction var10 = (Direction)var9.next();
               this.dowseFire(var8.relative(var10), var10);
            }
         }

         if (var5) {
            this.applyWater();
         } else if (!var4.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(var2, var3);
            } else {
               this.applySplash(var4, var1.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)var1).getEntity() : null);
            }
         }

         int var11 = var3.hasInstantEffects() ? 2007 : 2002;
         this.level.levelEvent(var11, new BlockPos(this), PotionUtils.getColor(var2));
         this.remove();
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
            if (var5 < 16.0D && isWaterSensitiveEntity(var4)) {
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
      AreaEffectCloud var3 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
      var3.setOwner(this.getOwner());
      var3.setRadius(3.0F);
      var3.setRadiusOnUse(-0.5F);
      var3.setWaitTime(10);
      var3.setRadiusPerTick(-var3.getRadius() / (float)var3.getDuration());
      var3.setPotion(var2);
      Iterator var4 = PotionUtils.getCustomEffects(var1).iterator();

      while(var4.hasNext()) {
         MobEffectInstance var5 = (MobEffectInstance)var4.next();
         var3.addEffect(new MobEffectInstance(var5));
      }

      CompoundTag var6 = var1.getTag();
      if (var6 != null && var6.contains("CustomPotionColor", 99)) {
         var3.setFixedColor(var6.getInt("CustomPotionColor"));
      }

      this.level.addFreshEntity(var3);
   }

   private boolean isLingering() {
      return this.getItem().getItem() == Items.LINGERING_POTION;
   }

   private void dowseFire(BlockPos var1, Direction var2) {
      BlockState var3 = this.level.getBlockState(var1);
      Block var4 = var3.getBlock();
      if (var4 == Blocks.FIRE) {
         this.level.extinguishFire((Player)null, var1.relative(var2), var2.getOpposite());
      } else if (var4 == Blocks.CAMPFIRE && (Boolean)var3.getValue(CampfireBlock.LIT)) {
         this.level.levelEvent((Player)null, 1009, var1, 0);
         this.level.setBlockAndUpdate(var1, (BlockState)var3.setValue(CampfireBlock.LIT, false));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      ItemStack var2 = ItemStack.of(var1.getCompound("Potion"));
      if (var2.isEmpty()) {
         this.remove();
      } else {
         this.setItem(var2);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      ItemStack var2 = this.getItem();
      if (!var2.isEmpty()) {
         var1.put("Potion", var2.save(new CompoundTag()));
      }

   }

   private static boolean isWaterSensitiveEntity(LivingEntity var0) {
      return var0 instanceof EnderMan || var0 instanceof Blaze;
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.defineId(ThrownPotion.class, EntityDataSerializers.ITEM_STACK);
      LOGGER = LogManager.getLogger();
      WATER_SENSITIVE = ThrownPotion::isWaterSensitiveEntity;
   }
}
