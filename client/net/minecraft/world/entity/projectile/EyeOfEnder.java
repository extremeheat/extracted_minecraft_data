package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EyeOfEnder extends Entity implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
   private double tx;
   private double ty;
   private double tz;
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
      if (!var1.is(Items.ENDER_EYE) || var1.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, var1.copyWithCount(1));
      }
   }

   private ItemStack getItemRaw() {
      return this.getEntityData().get(DATA_ITEM_STACK);
   }

   @Override
   public ItemStack getItem() {
      ItemStack var1 = this.getItemRaw();
      return var1.isEmpty() ? new ItemStack(Items.ENDER_EYE) : var1;
   }

   @Override
   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0;
      if (Double.isNaN(var3)) {
         var3 = 4.0;
      }

      var3 *= 64.0;
      return var1 < var3 * var3;
   }

   public void signalTo(BlockPos var1) {
      double var2 = (double)var1.getX();
      int var4 = var1.getY();
      double var5 = (double)var1.getZ();
      double var7 = var2 - this.getX();
      double var9 = var5 - this.getZ();
      double var11 = Math.sqrt(var7 * var7 + var9 * var9);
      if (var11 > 12.0) {
         this.tx = this.getX() + var7 / var11 * 12.0;
         this.tz = this.getZ() + var9 / var11 * 12.0;
         this.ty = this.getY() + 8.0;
      } else {
         this.tx = var2;
         this.ty = (double)var4;
         this.tz = var5;
      }

      this.life = 0;
      this.surviveAfterDeath = this.random.nextInt(5) > 0;
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double var7 = Math.sqrt(var1 * var1 + var5 * var5);
         this.setYRot((float)(Mth.atan2(var1, var5) * 57.2957763671875));
         this.setXRot((float)(Mth.atan2(var3, var7) * 57.2957763671875));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }
   }

   @Override
   public void tick() {
      super.tick();
      Vec3 var1 = this.getDeltaMovement();
      double var2 = this.getX() + var1.x;
      double var4 = this.getY() + var1.y;
      double var6 = this.getZ() + var1.z;
      double var8 = var1.horizontalDistance();
      this.setXRot(Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2(var1.y, var8) * 57.2957763671875)));
      this.setYRot(Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875)));
      if (!this.level().isClientSide) {
         double var10 = this.tx - var2;
         double var12 = this.tz - var6;
         float var14 = (float)Math.sqrt(var10 * var10 + var12 * var12);
         float var15 = (float)Mth.atan2(var12, var10);
         double var16 = Mth.lerp(0.0025, var8, (double)var14);
         double var18 = var1.y;
         if (var14 < 1.0F) {
            var16 *= 0.8;
            var18 *= 0.8;
         }

         int var20 = this.getY() < this.ty ? 1 : -1;
         var1 = new Vec3(Math.cos((double)var15) * var16, var18 + ((double)var20 - var18) * 0.014999999664723873, Math.sin((double)var15) * var16);
         this.setDeltaMovement(var1);
      }

      float var21 = 0.25F;
      if (this.isInWater()) {
         for(int var11 = 0; var11 < 4; ++var11) {
            this.level().addParticle(ParticleTypes.BUBBLE, var2 - var1.x * 0.25, var4 - var1.y * 0.25, var6 - var1.z * 0.25, var1.x, var1.y, var1.z);
         }
      } else {
         this.level()
            .addParticle(
               ParticleTypes.PORTAL,
               var2 - var1.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
               var4 - var1.y * 0.25 - 0.5,
               var6 - var1.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
               var1.x,
               var1.y,
               var1.z
            );
      }

      if (!this.level().isClientSide) {
         this.setPos(var2, var4, var6);
         ++this.life;
         if (this.life > 80 && !this.level().isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.discard();
            if (this.surviveAfterDeath) {
               this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
            } else {
               this.level().levelEvent(2003, this.blockPosition(), 0);
            }
         }
      } else {
         this.setPosRaw(var2, var4, var6);
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      ItemStack var2 = this.getItemRaw();
      if (!var2.isEmpty()) {
         var1.put("Item", var2.save(new CompoundTag()));
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      ItemStack var2 = ItemStack.of(var1.getCompound("Item"));
      this.setItem(var2);
   }

   @Override
   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   @Override
   public boolean isAttackable() {
      return false;
   }
}
