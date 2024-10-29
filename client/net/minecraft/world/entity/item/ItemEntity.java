package net.minecraft.world.entity.item;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class ItemEntity extends Entity implements TraceableEntity {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM;
   private static final float FLOAT_HEIGHT = 0.1F;
   public static final float EYE_HEIGHT = 0.2125F;
   private static final int LIFETIME = 6000;
   private static final int INFINITE_PICKUP_DELAY = 32767;
   private static final int INFINITE_LIFETIME = -32768;
   private int age;
   private int pickupDelay;
   private int health;
   @Nullable
   private UUID thrower;
   @Nullable
   private Entity cachedThrower;
   @Nullable
   private UUID target;
   public final float bobOffs;

   public ItemEntity(EntityType<? extends ItemEntity> var1, Level var2) {
      super(var1, var2);
      this.health = 5;
      this.bobOffs = this.random.nextFloat() * 3.1415927F * 2.0F;
      this.setYRot(this.random.nextFloat() * 360.0F);
   }

   public ItemEntity(Level var1, double var2, double var4, double var6, ItemStack var8) {
      this(var1, var2, var4, var6, var8, var1.random.nextDouble() * 0.2 - 0.1, 0.2, var1.random.nextDouble() * 0.2 - 0.1);
   }

   public ItemEntity(Level var1, double var2, double var4, double var6, ItemStack var8, double var9, double var11, double var13) {
      this(EntityType.ITEM, var1);
      this.setPos(var2, var4, var6);
      this.setDeltaMovement(var9, var11, var13);
      this.setItem(var8);
   }

   private ItemEntity(ItemEntity var1) {
      super(var1.getType(), var1.level());
      this.health = 5;
      this.setItem(var1.getItem().copy());
      this.copyPosition(var1);
      this.age = var1.age;
      this.bobOffs = var1.bobOffs;
   }

   public boolean dampensVibrations() {
      return this.getItem().is(ItemTags.DAMPENS_VIBRATIONS);
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedThrower != null && !this.cachedThrower.isRemoved()) {
         return this.cachedThrower;
      } else {
         if (this.thrower != null) {
            Level var2 = this.level();
            if (var2 instanceof ServerLevel) {
               ServerLevel var1 = (ServerLevel)var2;
               this.cachedThrower = var1.getEntity(this.thrower);
               return this.cachedThrower;
            }
         }

         return null;
      }
   }

   public void restoreFrom(Entity var1) {
      super.restoreFrom(var1);
      if (var1 instanceof ItemEntity var2) {
         this.cachedThrower = var2.cachedThrower;
      }

   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM, ItemStack.EMPTY);
   }

   protected double getDefaultGravity() {
      return 0.04;
   }

   public void tick() {
      if (this.getItem().isEmpty()) {
         this.discard();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.xo = this.getX();
         this.yo = this.getY();
         this.zo = this.getZ();
         Vec3 var1 = this.getDeltaMovement();
         if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > 0.10000000149011612) {
            this.setUnderwaterMovement();
         } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > 0.10000000149011612) {
            this.setUnderLavaMovement();
         } else {
            this.applyGravity();
         }

         if (this.level().isClientSide) {
            this.noPhysics = false;
         } else {
            this.noPhysics = !this.level().noCollision(this, this.getBoundingBox().deflate(1.0E-7));
            if (this.noPhysics) {
               this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
         }

         if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > 9.999999747378752E-6 || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            float var2 = 0.98F;
            if (this.onGround()) {
               var2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)var2, 0.98, (double)var2));
            if (this.onGround()) {
               Vec3 var3 = this.getDeltaMovement();
               if (var3.y < 0.0) {
                  this.setDeltaMovement(var3.multiply(1.0, -0.5, 1.0));
               }
            }
         }

         boolean var6 = Mth.floor(this.xo) != Mth.floor(this.getX()) || Mth.floor(this.yo) != Mth.floor(this.getY()) || Mth.floor(this.zo) != Mth.floor(this.getZ());
         int var7 = var6 ? 2 : 40;
         if (this.tickCount % var7 == 0 && !this.level().isClientSide && this.isMergable()) {
            this.mergeWithNeighbours();
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
         if (!this.level().isClientSide) {
            double var4 = this.getDeltaMovement().subtract(var1).lengthSqr();
            if (var4 > 0.01) {
               this.hasImpulse = true;
            }
         }

         if (!this.level().isClientSide && this.age >= 6000) {
            this.discard();
         }

      }
   }

   public BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return this.getOnPos(0.999999F);
   }

   private void setUnderwaterMovement() {
      this.setFluidMovement(0.9900000095367432);
   }

   private void setUnderLavaMovement() {
      this.setFluidMovement(0.949999988079071);
   }

   private void setFluidMovement(double var1) {
      Vec3 var3 = this.getDeltaMovement();
      this.setDeltaMovement(var3.x * var1, var3.y + (double)(var3.y < 0.05999999865889549 ? 5.0E-4F : 0.0F), var3.z * var1);
   }

   private void mergeWithNeighbours() {
      if (this.isMergable()) {
         List var1 = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.5, 0.0, 0.5), (var1x) -> {
            return var1x != this && var1x.isMergable();
         });
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ItemEntity var3 = (ItemEntity)var2.next();
            if (var3.isMergable()) {
               this.tryToMerge(var3);
               if (this.isRemoved()) {
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
      if (Objects.equals(this.target, var1.target) && areMergable(var2, var3)) {
         if (var3.getCount() < var2.getCount()) {
            merge(this, var2, var1, var3);
         } else {
            merge(var1, var3, this, var2);
         }

      }
   }

   public static boolean areMergable(ItemStack var0, ItemStack var1) {
      return var1.getCount() + var0.getCount() > var1.getMaxStackSize() ? false : ItemStack.isSameItemSameComponents(var0, var1);
   }

   public static ItemStack merge(ItemStack var0, ItemStack var1, int var2) {
      int var3 = Math.min(Math.min(var0.getMaxStackSize(), var2) - var0.getCount(), var1.getCount());
      ItemStack var4 = var0.copyWithCount(var0.getCount() + var3);
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
         var2.discard();
      }

   }

   public boolean fireImmune() {
      return !this.getItem().canBeHurtBy(this.damageSources().inFire()) || super.fireImmune();
   }

   protected boolean shouldPlayLavaHurtSound() {
      if (this.health <= 0) {
         return true;
      } else {
         return this.tickCount % 10 == 0;
      }
   }

   public final boolean hurtClient(DamageSource var1) {
      return this.isInvulnerableToBase(var1) ? false : this.getItem().canBeHurtBy(var1);
   }

   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableToBase(var2)) {
         return false;
      } else if (!var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && var2.getEntity() instanceof Mob) {
         return false;
      } else if (!this.getItem().canBeHurtBy(var2)) {
         return false;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - var3);
         this.gameEvent(GameEvent.ENTITY_DAMAGE, var2.getEntity());
         if (this.health <= 0) {
            this.getItem().onDestroyed(this);
            this.discard();
         }

         return true;
      }
   }

   public boolean ignoreExplosion(Explosion var1) {
      return var1.shouldAffectBlocklikeEntities() ? super.ignoreExplosion(var1) : true;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putShort("Health", (short)this.health);
      var1.putShort("Age", (short)this.age);
      var1.putShort("PickupDelay", (short)this.pickupDelay);
      if (this.thrower != null) {
         var1.putUUID("Thrower", this.thrower);
      }

      if (this.target != null) {
         var1.putUUID("Owner", this.target);
      }

      if (!this.getItem().isEmpty()) {
         var1.put("Item", this.getItem().save(this.registryAccess()));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.health = var1.getShort("Health");
      this.age = var1.getShort("Age");
      if (var1.contains("PickupDelay")) {
         this.pickupDelay = var1.getShort("PickupDelay");
      }

      if (var1.hasUUID("Owner")) {
         this.target = var1.getUUID("Owner");
      }

      if (var1.hasUUID("Thrower")) {
         this.thrower = var1.getUUID("Thrower");
         this.cachedThrower = null;
      }

      if (var1.contains("Item", 10)) {
         CompoundTag var2 = var1.getCompound("Item");
         this.setItem((ItemStack)ItemStack.parse(this.registryAccess(), var2).orElse(ItemStack.EMPTY));
      } else {
         this.setItem(ItemStack.EMPTY);
      }

      if (this.getItem().isEmpty()) {
         this.discard();
      }

   }

   public void playerTouch(Player var1) {
      if (!this.level().isClientSide) {
         ItemStack var2 = this.getItem();
         Item var3 = var2.getItem();
         int var4 = var2.getCount();
         if (this.pickupDelay == 0 && (this.target == null || this.target.equals(var1.getUUID())) && var1.getInventory().add(var2)) {
            var1.take(this, var4);
            if (var2.isEmpty()) {
               this.discard();
               var2.setCount(var4);
            }

            var1.awardStat(Stats.ITEM_PICKED_UP.get(var3), var4);
            var1.onItemPickup(this);
         }

      }
   }

   public Component getName() {
      Component var1 = this.getCustomName();
      return var1 != null ? var1 : this.getItem().getItemName();
   }

   public boolean isAttackable() {
      return false;
   }

   @Nullable
   public Entity teleport(TeleportTransition var1) {
      Entity var2 = super.teleport(var1);
      if (!this.level().isClientSide && var2 instanceof ItemEntity var3) {
         var3.mergeWithNeighbours();
      }

      return var2;
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack var1) {
      this.getEntityData().set(DATA_ITEM, var1);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_ITEM.equals(var1)) {
         this.getItem().setEntityRepresentation(this);
      }

   }

   public void setTarget(@Nullable UUID var1) {
      this.target = var1;
   }

   public void setThrower(Entity var1) {
      this.thrower = var1.getUUID();
      this.cachedThrower = var1;
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

   public void setUnlimitedLifetime() {
      this.age = -32768;
   }

   public void setExtendedLifetime() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setNeverPickUp();
      this.age = 5999;
   }

   public static float getSpin(float var0, float var1) {
      return var0 / 20.0F + var1;
   }

   public ItemEntity copy() {
      return new ItemEntity(this);
   }

   public SoundSource getSoundSource() {
      return SoundSource.AMBIENT;
   }

   public float getVisualRotationYInDegrees() {
      return 180.0F - getSpin((float)this.getAge() + 0.5F, this.bobOffs) / 6.2831855F * 360.0F;
   }

   public SlotAccess getSlot(int var1) {
      return var1 == 0 ? SlotAccess.of(this::getItem, this::setItem) : super.getSlot(var1);
   }

   static {
      DATA_ITEM = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.ITEM_STACK);
   }
}
