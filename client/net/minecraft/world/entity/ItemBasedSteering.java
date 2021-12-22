package net.minecraft.world.entity;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public class ItemBasedSteering {
   private static final int MIN_BOOST_TIME = 140;
   private static final int MAX_BOOST_TIME = 700;
   private final SynchedEntityData entityData;
   private final EntityDataAccessor<Integer> boostTimeAccessor;
   private final EntityDataAccessor<Boolean> hasSaddleAccessor;
   public boolean boosting;
   public int boostTime;
   public int boostTimeTotal;

   public ItemBasedSteering(SynchedEntityData var1, EntityDataAccessor<Integer> var2, EntityDataAccessor<Boolean> var3) {
      super();
      this.entityData = var1;
      this.boostTimeAccessor = var2;
      this.hasSaddleAccessor = var3;
   }

   public void onSynced() {
      this.boosting = true;
      this.boostTime = 0;
      this.boostTimeTotal = (Integer)this.entityData.get(this.boostTimeAccessor);
   }

   public boolean boost(Random var1) {
      if (this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.boostTimeTotal = var1.nextInt(841) + 140;
         this.entityData.set(this.boostTimeAccessor, this.boostTimeTotal);
         return true;
      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putBoolean("Saddle", this.hasSaddle());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.setSaddle(var1.getBoolean("Saddle"));
   }

   public void setSaddle(boolean var1) {
      this.entityData.set(this.hasSaddleAccessor, var1);
   }

   public boolean hasSaddle() {
      return (Boolean)this.entityData.get(this.hasSaddleAccessor);
   }
}
