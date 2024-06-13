package net.minecraft.world.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class OminousItemSpawner extends Entity {
   private static final int SPAWN_ITEM_DELAY_MIN = 60;
   private static final int SPAWN_ITEM_DELAY_MAX = 120;
   private static final String TAG_SPAWN_ITEM_AFTER_TICKS = "spawn_item_after_ticks";
   private static final String TAG_ITEM = "item";
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(OminousItemSpawner.class, EntityDataSerializers.ITEM_STACK);
   public static final int TICKS_BEFORE_ABOUT_TO_SPAWN_SOUND = 36;
   private long spawnItemAfterTicks;

   public OminousItemSpawner(EntityType<? extends OminousItemSpawner> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public static OminousItemSpawner create(Level var0, ItemStack var1) {
      OminousItemSpawner var2 = new OminousItemSpawner(EntityType.OMINOUS_ITEM_SPAWNER, var0);
      var2.spawnItemAfterTicks = (long)var0.random.nextIntBetweenInclusive(60, 120);
      var2.setItem(var1);
      return var2;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.tickClient();
      } else {
         this.tickServer();
      }
   }

   private void tickServer() {
      if ((long)this.tickCount == this.spawnItemAfterTicks - 36L) {
         this.level().playSound(null, this.blockPosition(), SoundEvents.TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, SoundSource.NEUTRAL);
      }

      if ((long)this.tickCount >= this.spawnItemAfterTicks) {
         this.spawnItem();
         this.kill();
      }
   }

   private void tickClient() {
      if (this.level().getGameTime() % 5L == 0L) {
         this.addParticles();
      }
   }

   private void spawnItem() {
      Level var1 = this.level();
      ItemStack var2 = this.getItem();
      if (!var2.isEmpty()) {
         Object var3;
         if (var2.getItem() instanceof ProjectileItem var4) {
            Direction var8 = Direction.DOWN;
            Projectile var6 = var4.asProjectile(var1, this.position(), var2, var8);
            var6.setOwner(this);
            ProjectileItem.DispenseConfig var7 = var4.createDispenseConfig();
            var4.shoot(var6, (double)var8.getStepX(), (double)var8.getStepY(), (double)var8.getStepZ(), var7.power(), var7.uncertainty());
            var7.overrideDispenseEvent().ifPresent(var2x -> var1.levelEvent(var2x, this.blockPosition(), 0));
            var3 = var6;
         } else {
            var3 = new ItemEntity(var1, this.getX(), this.getY(), this.getZ(), var2);
         }

         var1.addFreshEntity((Entity)var3);
         var1.levelEvent(3021, this.blockPosition(), 1);
         var1.gameEvent((Entity)var3, GameEvent.ENTITY_PLACE, this.position());
         this.setItem(ItemStack.EMPTY);
      }
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM, ItemStack.EMPTY);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      ItemStack var2 = var1.contains("item", 10) ? ItemStack.parse(this.registryAccess(), var1.getCompound("item")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
      this.setItem(var2);
      var1.getLong("spawn_item_after_ticks");
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      if (!this.getItem().isEmpty()) {
         var1.put("item", this.getItem().save(this.registryAccess()).copy());
      }

      var1.putLong("spawn_item_after_ticks", this.spawnItemAfterTicks);
   }

   @Override
   protected boolean canAddPassenger(Entity var1) {
      return false;
   }

   @Override
   protected boolean couldAcceptPassenger() {
      return false;
   }

   @Override
   protected void addPassenger(Entity var1) {
      throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
   }

   @Override
   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public void addParticles() {
      Vec3 var1 = this.position();
      int var2 = this.random.nextIntBetweenInclusive(1, 3);

      for (int var3 = 0; var3 < var2; var3++) {
         double var4 = 0.4;
         Vec3 var6 = new Vec3(
            this.getX() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
            this.getY() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
            this.getZ() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian())
         );
         Vec3 var7 = var1.vectorTo(var6);
         this.level().addParticle(ParticleTypes.OMINOUS_SPAWNING, var1.x(), var1.y(), var1.z(), var7.x(), var7.y(), var7.z());
      }
   }

   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM);
   }

   private void setItem(ItemStack var1) {
      this.getEntityData().set(DATA_ITEM, var1);
   }
}
