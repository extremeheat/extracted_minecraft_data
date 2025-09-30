package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class EyeOfEnder extends Entity implements ItemSupplier {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;
   private static final float TOO_FAR_SIGNAL_HEIGHT = 8.0F;
   private static final float TOO_FAR_DISTANCE = 12.0F;
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
   @Nullable
   private Vec3 target;
   private int life;
   private boolean surviveAfterDeath;

   public EyeOfEnder(EntityType<? extends EyeOfEnder> var1, Level var2) {
      super(var1, var2);
   }

   public EyeOfEnder(Level var1, double var2, double var4, double var6) {
      this(EntityType.EYE_OF_ENDER, var1);
      this.setPos(var2, var4, var6);
   }

   public void setItem(ItemStack var1) {
      if (var1.isEmpty()) {
         this.getEntityData().set(DATA_ITEM_STACK, this.getDefaultItem());
      } else {
         this.getEntityData().set(DATA_ITEM_STACK, var1.copyWithCount(1));
      }

   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM_STACK, this.getDefaultItem());
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      if (this.tickCount < 2 && var1 < 12.25) {
         return false;
      } else {
         double var3 = this.getBoundingBox().getSize() * 4.0;
         if (Double.isNaN(var3)) {
            var3 = 4.0;
         }

         var3 *= 64.0;
         return var1 < var3 * var3;
      }
   }

   public void signalTo(Vec3 var1) {
      Vec3 var2 = var1.subtract(this.position());
      double var3 = var2.horizontalDistance();
      if (var3 > 12.0) {
         this.target = this.position().add(var2.x / var3 * 12.0, 8.0, var2.z / var3 * 12.0);
      } else {
         this.target = var1;
      }

      this.life = 0;
      this.surviveAfterDeath = this.random.nextInt(5) > 0;
   }

   public void tick() {
      super.tick();
      Vec3 var1 = this.position().add(this.getDeltaMovement());
      if (!this.level().isClientSide() && this.target != null) {
         this.setDeltaMovement(updateDeltaMovement(this.getDeltaMovement(), var1, this.target));
      }

      if (this.level().isClientSide()) {
         Vec3 var2 = var1.subtract(this.getDeltaMovement().scale(0.25));
         this.spawnParticles(var2, this.getDeltaMovement());
      }

      this.setPos(var1);
      if (!this.level().isClientSide()) {
         ++this.life;
         if (this.life > 80 && !this.level().isClientSide()) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.discard();
            if (this.surviveAfterDeath) {
               this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
            } else {
               this.level().levelEvent(2003, this.blockPosition(), 0);
            }
         }
      }

   }

   private void spawnParticles(Vec3 var1, Vec3 var2) {
      if (this.isInWater()) {
         for(int var3 = 0; var3 < 4; ++var3) {
            this.level().addParticle(ParticleTypes.BUBBLE, var1.x, var1.y, var1.z, var2.x, var2.y, var2.z);
         }
      } else {
         this.level().addParticle(ParticleTypes.PORTAL, var1.x + this.random.nextDouble() * 0.6 - 0.3, var1.y - 0.5, var1.z + this.random.nextDouble() * 0.6 - 0.3, var2.x, var2.y, var2.z);
      }

   }

   private static Vec3 updateDeltaMovement(Vec3 var0, Vec3 var1, Vec3 var2) {
      Vec3 var3 = new Vec3(var2.x - var1.x, 0.0, var2.z - var1.z);
      double var4 = var3.length();
      double var6 = Mth.lerp(0.0025, var0.horizontalDistance(), var4);
      double var8 = var0.y;
      if (var4 < 1.0) {
         var6 *= 0.8;
         var8 *= 0.8;
      }

      double var10 = var1.y - var0.y < var2.y ? 1.0 : -1.0;
      return var3.scale(var6 / var4).add(0.0, var8 + (var10 - var8) * 0.015, 0.0);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      var1.store("Item", ItemStack.CODEC, this.getItem());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      this.setItem((ItemStack)var1.read("Item", ItemStack.CODEC).orElse(this.getDefaultItem()));
   }

   private ItemStack getDefaultItem() {
      return new ItemStack(Items.ENDER_EYE);
   }

   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      return false;
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
   }
}
