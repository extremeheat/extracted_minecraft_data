package net.minecraft.world.entity;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class AgeableMob extends PathfinderMob {
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
   public static final int BABY_START_AGE = -24000;
   private static final int FORCED_AGE_PARTICLE_TICKS = 40;
   protected int age;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgeableMob(EntityType<? extends AgeableMob> var1, Level var2) {
      super(var1, var2);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (var4 == null) {
         var4 = new AgeableMobGroupData(true);
      }

      AgeableMobGroupData var5 = (AgeableMobGroupData)var4;
      if (var5.isShouldSpawnBaby() && var5.getGroupSize() > 0 && var1.getRandom().nextFloat() <= var5.getBabySpawnChance()) {
         this.setAge(-24000);
      }

      var5.increaseGroupSizeByOne();
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   @Nullable
   public abstract AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2);

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_BABY_ID, false);
   }

   public boolean canBreed() {
      return false;
   }

   public int getAge() {
      if (this.level().isClientSide) {
         return (Boolean)this.entityData.get(DATA_BABY_ID) ? -1 : 1;
      } else {
         return this.age;
      }
   }

   public void ageUp(int var1, boolean var2) {
      int var3 = this.getAge();
      int var4 = var3;
      var3 += var1 * 20;
      if (var3 > 0) {
         var3 = 0;
      }

      int var5 = var3 - var4;
      this.setAge(var3);
      if (var2) {
         this.forcedAge += var5;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getAge() == 0) {
         this.setAge(this.forcedAge);
      }

   }

   public void ageUp(int var1) {
      this.ageUp(var1, false);
   }

   public void setAge(int var1) {
      int var2 = this.getAge();
      this.age = var1;
      if (var2 < 0 && var1 >= 0 || var2 >= 0 && var1 < 0) {
         this.entityData.set(DATA_BABY_ID, var1 < 0);
         this.ageBoundaryReached();
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Age", this.getAge());
      var1.putInt("ForcedAge", this.forcedAge);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setAge(var1.getInt("Age"));
      this.forcedAge = var1.getInt("ForcedAge");
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_BABY_ID.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level().isClientSide) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            }

            --this.forcedAgeTimer;
         }
      } else if (this.isAlive()) {
         int var1 = this.getAge();
         if (var1 < 0) {
            ++var1;
            this.setAge(var1);
         } else if (var1 > 0) {
            --var1;
            this.setAge(var1);
         }
      }

   }

   protected void ageBoundaryReached() {
      if (!this.isBaby() && this.isPassenger()) {
         Entity var2 = this.getVehicle();
         if (var2 instanceof AbstractBoat) {
            AbstractBoat var1 = (AbstractBoat)var2;
            if (!var1.hasEnoughSpaceFor(this)) {
               this.stopRiding();
            }
         }
      }

   }

   public boolean isBaby() {
      return this.getAge() < 0;
   }

   public void setBaby(boolean var1) {
      this.setAge(var1 ? -24000 : 0);
   }

   public static int getSpeedUpSecondsWhenFeeding(int var0) {
      return (int)((float)(var0 / 20) * 0.1F);
   }

   @VisibleForTesting
   public int getForcedAge() {
      return this.forcedAge;
   }

   @VisibleForTesting
   public int getForcedAgeTimer() {
      return this.forcedAgeTimer;
   }

   static {
      DATA_BABY_ID = SynchedEntityData.defineId(AgeableMob.class, EntityDataSerializers.BOOLEAN);
   }

   public static class AgeableMobGroupData implements SpawnGroupData {
      private int groupSize;
      private final boolean shouldSpawnBaby;
      private final float babySpawnChance;

      public AgeableMobGroupData(boolean var1, float var2) {
         super();
         this.shouldSpawnBaby = var1;
         this.babySpawnChance = var2;
      }

      public AgeableMobGroupData(boolean var1) {
         this(var1, 0.05F);
      }

      public AgeableMobGroupData(float var1) {
         this(true, var1);
      }

      public int getGroupSize() {
         return this.groupSize;
      }

      public void increaseGroupSizeByOne() {
         ++this.groupSize;
      }

      public boolean isShouldSpawnBaby() {
         return this.shouldSpawnBaby;
      }

      public float getBabySpawnChance() {
         return this.babySpawnChance;
      }
   }
}
