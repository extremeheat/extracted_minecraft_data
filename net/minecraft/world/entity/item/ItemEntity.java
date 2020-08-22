package net.minecraft.world.entity.item;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public class ItemEntity extends Entity {
   private static final EntityDataAccessor DATA_ITEM;
   private int age;
   private int pickupDelay;
   private int health;
   private UUID thrower;
   private UUID owner;
   public final float bobOffs;

   public ItemEntity(EntityType var1, Level var2) {
      super(var1, var2);
      this.health = 5;
      this.bobOffs = (float)(Math.random() * 3.141592653589793D * 2.0D);
   }

   public ItemEntity(Level var1, double var2, double var4, double var6) {
      this(EntityType.ITEM, var1);
      this.setPos(var2, var4, var6);
      this.yRot = this.random.nextFloat() * 360.0F;
      this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
   }

   public ItemEntity(Level var1, double var2, double var4, double var6, ItemStack var8) {
      this(var1, var2, var4, var6);
      this.setItem(var8);
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if (this.getItem().isEmpty()) {
         this.remove();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.xo = this.getX();
         this.yo = this.getY();
         this.zo = this.getZ();
         Vec3 var1 = this.getDeltaMovement();
         if (this.isUnderLiquid(FluidTags.WATER)) {
            this.setUnderwaterMovement();
         } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         if (this.level.isClientSide) {
            this.noPhysics = false;
         } else {
            this.noPhysics = !this.level.noCollision(this);
            if (this.noPhysics) {
               this.checkInBlock(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
            }
         }

         if (!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > 9.999999747378752E-6D || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float var2 = 0.98F;
            if (this.onGround) {
               var2 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)var2, 0.98D, (double)var2));
            if (this.onGround) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.5D, 1.0D));
            }
         }

         boolean var6 = Mth.floor(this.xo) != Mth.floor(this.getX()) || Mth.floor(this.yo) != Mth.floor(this.getY()) || Mth.floor(this.zo) != Mth.floor(this.getZ());
         int var3 = var6 ? 2 : 40;
         if (this.tickCount % var3 == 0) {
            if (this.level.getFluidState(new BlockPos(this)).is(FluidTags.LAVA)) {
               this.setDeltaMovement((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
               this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

            if (!this.level.isClientSide && this.isMergable()) {
               this.mergeWithNeighbours();
            }
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.hasImpulse |= this.updateInWaterState();
         if (!this.level.isClientSide) {
            double var4 = this.getDeltaMovement().subtract(var1).lengthSqr();
            if (var4 > 0.01D) {
               this.hasImpulse = true;
            }
         }

         if (!this.level.isClientSide && this.age >= 6000) {
            this.remove();
         }

      }
   }

   private void setUnderwaterMovement() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x * 0.9900000095367432D, var1.y + (double)(var1.y < 0.05999999865889549D ? 5.0E-4F : 0.0F), var1.z * 0.9900000095367432D);
   }

   private void mergeWithNeighbours() {
      if (this.isMergable()) {
         List var1 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.5D, 0.0D, 0.5D), (var1x) -> {
            return var1x != this && var1x.isMergable();
         });
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ItemEntity var3 = (ItemEntity)var2.next();
            if (var3.isMergable()) {
               this.tryToMerge(var3);
               if (this.removed) {
                  break;
               }
            }
         }

      }
   }

   private boolean isMergable() {
      ItemStack var1 = this.getItem();
      return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && var1.getCount() < var1.getMaxStackSize();
   }

   private void tryToMerge(ItemEntity var1) {
      ItemStack var2 = this.getItem();
      ItemStack var3 = var1.getItem();
      if (Objects.equals(this.getOwner(), var1.getOwner()) && areMergable(var2, var3)) {
         if (var3.getCount() < var2.getCount()) {
            merge(this, var2, var1, var3);
         } else {
            merge(var1, var3, this, var2);
         }

      }
   }

   public static boolean areMergable(ItemStack var0, ItemStack var1) {
      if (var1.getItem() != var0.getItem()) {
         return false;
      } else if (var1.getCount() + var0.getCount() > var1.getMaxStackSize()) {
         return false;
      } else if (var1.hasTag() ^ var0.hasTag()) {
         return false;
      } else {
         return !var1.hasTag() || var1.getTag().equals(var0.getTag());
      }
   }

   public static ItemStack merge(ItemStack var0, ItemStack var1, int var2) {
      int var3 = Math.min(Math.min(var0.getMaxStackSize(), var2) - var0.getCount(), var1.getCount());
      ItemStack var4 = var0.copy();
      var4.grow(var3);
      var1.shrink(var3);
      return var4;
   }

   private static void merge(ItemEntity var0, ItemStack var1, ItemStack var2) {
      ItemStack var3 = merge(var1, var2, 64);
      var0.setItem(var3);
   }

   private static void merge(ItemEntity var0, ItemStack var1, ItemEntity var2, ItemStack var3) {
      merge(var0, var1, var3);
      var0.pickupDelay = Math.max(var0.pickupDelay, var2.pickupDelay);
      var0.age = Math.min(var0.age, var2.age);
      if (var3.isEmpty()) {
         var2.remove();
      }

   }

   protected void burn(int var1) {
      this.hurt(DamageSource.IN_FIRE, (float)var1);
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && var1.isExplosion()) {
         return false;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - var2);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putShort("Health", (short)this.health);
      var1.putShort("Age", (short)this.age);
      var1.putShort("PickupDelay", (short)this.pickupDelay);
      if (this.getThrower() != null) {
         var1.put("Thrower", NbtUtils.createUUIDTag(this.getThrower()));
      }

      if (this.getOwner() != null) {
         var1.put("Owner", NbtUtils.createUUIDTag(this.getOwner()));
      }

      if (!this.getItem().isEmpty()) {
         var1.put("Item", this.getItem().save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.health = var1.getShort("Health");
      this.age = var1.getShort("Age");
      if (var1.contains("PickupDelay")) {
         this.pickupDelay = var1.getShort("PickupDelay");
      }

      if (var1.contains("Owner", 10)) {
         this.owner = NbtUtils.loadUUIDTag(var1.getCompound("Owner"));
      }

      if (var1.contains("Thrower", 10)) {
         this.thrower = NbtUtils.loadUUIDTag(var1.getCompound("Thrower"));
      }

      CompoundTag var2 = var1.getCompound("Item");
      this.setItem(ItemStack.of(var2));
      if (this.getItem().isEmpty()) {
         this.remove();
      }

   }

   public void playerTouch(Player var1) {
      if (!this.level.isClientSide) {
         ItemStack var2 = this.getItem();
         Item var3 = var2.getItem();
         int var4 = var2.getCount();
         if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(var1.getUUID())) && var1.inventory.add(var2)) {
            var1.take(this, var4);
            if (var2.isEmpty()) {
               this.remove();
               var2.setCount(var4);
            }

            var1.awardStat(Stats.ITEM_PICKED_UP.get(var3), var4);
         }

      }
   }

   public Component getName() {
      Component var1 = this.getCustomName();
      return (Component)(var1 != null ? var1 : new TranslatableComponent(this.getItem().getDescriptionId(), new Object[0]));
   }

   public boolean isAttackable() {
      return false;
   }

   @Nullable
   public Entity changeDimension(DimensionType var1) {
      Entity var2 = super.changeDimension(var1);
      if (!this.level.isClientSide && var2 instanceof ItemEntity) {
         ((ItemEntity)var2).mergeWithNeighbours();
      }

      return var2;
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack var1) {
      this.getEntityData().set(DATA_ITEM, var1);
   }

   @Nullable
   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(@Nullable UUID var1) {
      this.owner = var1;
   }

   @Nullable
   public UUID getThrower() {
      return this.thrower;
   }

   public void setThrower(@Nullable UUID var1) {
      this.thrower = var1;
   }

   public int getAge() {
      return this.age;
   }

   public void setDefaultPickUpDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickUpDelay() {
      this.pickupDelay = 0;
   }

   public void setNeverPickUp() {
      this.pickupDelay = 32767;
   }

   public void setPickUpDelay(int var1) {
      this.pickupDelay = var1;
   }

   public boolean hasPickUpDelay() {
      return this.pickupDelay > 0;
   }

   public void setExtendedLifetime() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setNeverPickUp();
      this.age = 5999;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   static {
      DATA_ITEM = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.ITEM_STACK);
   }
}
