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
         this.getEntityData().set(DATA_ITEM_STACK, Util.make(var1.copy(), (var0) -> {
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
      float var11 = Mth.sqrt(var7 * var7 + var9 * var9);
      if (var11 > 12.0F) {
         this.tx = this.getX() + var7 / (double)var11 * 12.0D;
         this.tz = this.getZ() + var9 / (double)var11 * 12.0D;
         this.ty = this.getY() + 8.0D;
      } else {
         this.tx = var2;
         this.ty = (double)var4;
         this.tz = var5;
      }

      this.life = 0;
      this.surviveAfterDeath = this.random.nextInt(5) > 0;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

   }

   public void tick() {
      super.tick();
      Vec3 var1 = this.getDeltaMovement();
      double var2 = this.getX() + var1.x;
      double var4 = this.getY() + var1.y;
      double var6 = this.getZ() + var1.z;
      float var8 = Mth.sqrt(getHorizontalDistanceSqr(var1));
      this.xRot = Projectile.lerpRotation(this.xRotO, (float)(Mth.atan2(var1.y, (double)var8) * 57.2957763671875D));
      this.yRot = Projectile.lerpRotation(this.yRotO, (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875D));
      if (!this.level.isClientSide) {
         double var9 = this.tx - var2;
         double var11 = this.tz - var6;
         float var13 = (float)Math.sqrt(var9 * var9 + var11 * var11);
         float var14 = (float)Mth.atan2(var11, var9);
         double var15 = Mth.lerp(0.0025D, (double)var8, (double)var13);
         double var17 = var1.y;
         if (var13 < 1.0F) {
            var15 *= 0.8D;
            var17 *= 0.8D;
         }

         int var19 = this.getY() < this.ty ? 1 : -1;
         var1 = new Vec3(Math.cos((double)var14) * var15, var17 + ((double)var19 - var17) * 0.014999999664723873D, Math.sin((double)var14) * var15);
         this.setDeltaMovement(var1);
      }

      float var20 = 0.25F;
      if (this.isInWater()) {
         for(int var10 = 0; var10 < 4; ++var10) {
            this.level.addParticle(ParticleTypes.BUBBLE, var2 - var1.x * 0.25D, var4 - var1.y * 0.25D, var6 - var1.z * 0.25D, var1.x, var1.y, var1.z);
         }
      } else {
         this.level.addParticle(ParticleTypes.PORTAL, var2 - var1.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, var4 - var1.y * 0.25D - 0.5D, var6 - var1.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, var1.x, var1.y, var1.z);
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
      ItemStack var2 = ItemStack.of(var1.getCompound("Item"));
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
