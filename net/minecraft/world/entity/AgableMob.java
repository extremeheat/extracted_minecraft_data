package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public abstract class AgableMob extends PathfinderMob {
   private static final EntityDataAccessor DATA_BABY_ID;
   protected int age;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgableMob(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (var4 == null) {
         var4 = new AgableMob.AgableMobGroupData();
      }

      AgableMob.AgableMobGroupData var6 = (AgableMob.AgableMobGroupData)var4;
      if (var6.isShouldSpawnBaby() && var6.getGroupSize() > 0 && this.random.nextFloat() <= var6.getBabySpawnChance()) {
         this.setAge(-24000);
      }

      var6.increaseGroupSizeByOne();
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   @Nullable
   public abstract AgableMob getBreedOffspring(AgableMob var1);

   protected void onOffspringSpawnedFromEgg(Player var1, AgableMob var2) {
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      Item var4 = var3.getItem();
      if (var4 instanceof SpawnEggItem && ((SpawnEggItem)var4).spawnsEntity(var3.getTag(), this.getType())) {
         if (!this.level.isClientSide) {
            AgableMob var5 = this.getBreedOffspring(this);
            if (var5 != null) {
               var5.setAge(-24000);
               var5.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
               this.level.addFreshEntity(var5);
               if (var3.hasCustomHoverName()) {
                  var5.setCustomName(var3.getHoverName());
               }

               this.onOffspringSpawnedFromEgg(var1, var5);
               if (!var1.abilities.instabuild) {
                  var3.shrink(1);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, false);
   }

   public int getAge() {
      if (this.level.isClientSide) {
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
      int var2 = this.age;
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

   public void onSyncedDataUpdated(EntityDataAccessor var1) {
      if (DATA_BABY_ID.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
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
   }

   public boolean isBaby() {
      return this.getAge() < 0;
   }

   static {
      DATA_BABY_ID = SynchedEntityData.defineId(AgableMob.class, EntityDataSerializers.BOOLEAN);
   }

   public static class AgableMobGroupData implements SpawnGroupData {
      private int groupSize;
      private boolean shouldSpawnBaby = true;
      private float babySpawnChance = 0.05F;

      public int getGroupSize() {
         return this.groupSize;
      }

      public void increaseGroupSizeByOne() {
         ++this.groupSize;
      }

      public boolean isShouldSpawnBaby() {
         return this.shouldSpawnBaby;
      }

      public void setShouldSpawnBaby(boolean var1) {
         this.shouldSpawnBaby = var1;
      }

      public float getBabySpawnChance() {
         return this.babySpawnChance;
      }

      public void setBabySpawnChance(float var1) {
         this.babySpawnChance = var1;
      }
   }
}
