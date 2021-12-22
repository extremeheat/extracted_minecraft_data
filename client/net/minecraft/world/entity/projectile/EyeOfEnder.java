package net.minecraft.world.entity.projectile;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
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
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;
   // $FF: renamed from: tx double
   private double field_16;
   // $FF: renamed from: ty double
   private double field_17;
   // $FF: renamed from: tz double
   private double field_18;
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
      if (!var1.method_87(Items.ENDER_EYE) || var1.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, (ItemStack)Util.make(var1.copy(), (var0) -> {
            var0.setCount(1);
         }));
      }

   }

   private ItemStack getItemRaw() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack var1 = this.getItemRaw();
      return var1.isEmpty() ? new ItemStack(Items.ENDER_EYE) : var1;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   public void signalTo(BlockPos var1) {
      double var2 = (double)var1.getX();
      int var4 = var1.getY();
      double var5 = (double)var1.getZ();
      double var7 = var2 - this.getX();
      double var9 = var5 - this.getZ();
      double var11 = Math.sqrt(var7 * var7 + var9 * var9);
      if (var11 > 12.0D) {
         this.field_16 = this.getX() + var7 / var11 * 12.0D;
         this.field_18 = this.getZ() + var9 / var11 * 12.0D;
         this.field_17 = this.getY() + 8.0D;
      } else {
         this.field_16 = var2;
         this.field_17 = (double)var4;
         this.field_18 = var5;
      }

      this.life = 0;
      this.surviveAfterDeath = this.random.nextInt(5) > 0;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double var7 = Math.sqrt(var1 * var1 + var5 * var5);
         this.setYRot((float)(Mth.atan2(var1, var5) * 57.2957763671875D));
         this.setXRot((float)(Mth.atan2(var3, var7) * 57.2957763671875D));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }

   }

   public void tick() {
      super.tick();
      Vec3 var1 = this.getDeltaMovement();
      double var2 = this.getX() + var1.field_414;
      double var4 = this.getY() + var1.field_415;
      double var6 = this.getZ() + var1.field_416;
      double var8 = var1.horizontalDistance();
      this.setXRot(Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2(var1.field_415, var8) * 57.2957763671875D)));
      this.setYRot(Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2(var1.field_414, var1.field_416) * 57.2957763671875D)));
      if (!this.level.isClientSide) {
         double var10 = this.field_16 - var2;
         double var12 = this.field_18 - var6;
         float var14 = (float)Math.sqrt(var10 * var10 + var12 * var12);
         float var15 = (float)Mth.atan2(var12, var10);
         double var16 = Mth.lerp(0.0025D, var8, (double)var14);
         double var18 = var1.field_415;
         if (var14 < 1.0F) {
            var16 *= 0.8D;
            var18 *= 0.8D;
         }

         int var20 = this.getY() < this.field_17 ? 1 : -1;
         var1 = new Vec3(Math.cos((double)var15) * var16, var18 + ((double)var20 - var18) * 0.014999999664723873D, Math.sin((double)var15) * var16);
         this.setDeltaMovement(var1);
      }

      float var21 = 0.25F;
      if (this.isInWater()) {
         for(int var11 = 0; var11 < 4; ++var11) {
            this.level.addParticle(ParticleTypes.BUBBLE, var2 - var1.field_414 * 0.25D, var4 - var1.field_415 * 0.25D, var6 - var1.field_416 * 0.25D, var1.field_414, var1.field_415, var1.field_416);
         }
      } else {
         this.level.addParticle(ParticleTypes.PORTAL, var2 - var1.field_414 * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, var4 - var1.field_415 * 0.25D - 0.5D, var6 - var1.field_416 * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, var1.field_414, var1.field_415, var1.field_416);
      }

      if (!this.level.isClientSide) {
         this.setPos(var2, var4, var6);
         ++this.life;
         if (this.life > 80 && !this.level.isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.discard();
            if (this.surviveAfterDeath) {
               this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), this.getItem()));
            } else {
               this.level.levelEvent(2003, this.blockPosition(), 0);
            }
         }
      } else {
         this.setPosRaw(var2, var4, var6);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      ItemStack var2 = this.getItemRaw();
      if (!var2.isEmpty()) {
         var1.put("Item", var2.save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      ItemStack var2 = ItemStack.method_85(var1.getCompound("Item"));
      this.setItem(var2);
   }

   public float getBrightness() {
      return 1.0F;
   }

   public boolean isAttackable() {
      return false;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
   }
}
