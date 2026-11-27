package net.minecraft.world.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ItemBasedSteering {
   private static final int MIN_BOOST_TIME = 140;
   private static final int MAX_BOOST_TIME = 700;
   private static final String ITEM_BASED_STEERING_TAG = "item_based_steering";
   private static final String BOOSTING_TAG = "boosting";
   private static final String BOOST_TIME_TAG = "boost_time";
   private static final String BOOST_TIME_TOTAL_TAG = "boost_time_total";
   private final SynchedEntityData entityData;
   private final EntityDataAccessor<Integer> boostTimeAccessor;
   private boolean boosting;
   private int boostTime;

   public ItemBasedSteering(final SynchedEntityData entityData, final EntityDataAccessor<Integer> boostTimeAccessor) {
      super();
      this.entityData = entityData;
      this.boostTimeAccessor = boostTimeAccessor;
   }

   public void onSynced() {
      if (this.boostTimeTotal() > 0) {
         this.boosting = true;
         this.boostTime = 0;
      } else {
         this.boosting = false;
      }

   }

   public boolean boost(final RandomSource random) {
      if (this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.setBoostTimeTotal(random.nextIntBetweenInclusive(140, 700));
         return true;
      }
   }

   public void tickBoost() {
      if (this.boosting && this.boostTime++ > this.boostTimeTotal()) {
         this.boosting = false;
         this.setBoostTimeTotal(0);
      }

   }

   public float boostFactor() {
      return this.boosting ? 1.0F + 1.15F * Mth.sin((double)((float)this.boostTime / (float)this.boostTimeTotal() * 3.1415927F)) : 1.0F;
   }

   private int boostTimeTotal() {
      return (Integer)this.entityData.get(this.boostTimeAccessor);
   }

   private void setBoostTimeTotal(final int input) {
      this.entityData.set(this.boostTimeAccessor, input);
   }

   public void save(final ValueOutput output) {
      ValueOutput data = output.child("item_based_steering");
      data.putBoolean("boosting", this.boosting);
      data.putInt("boost_time", this.boostTime);
      data.putInt("boost_time_total", this.boostTimeTotal());
   }

   public void load(final ValueInput input) {
      ValueInput data = input.childOrEmpty("item_based_steering");
      this.boosting = data.getBooleanOr("boosting", false);
      this.boostTime = data.getIntOr("boost_time", 0);
      this.setBoostTimeTotal(!this.boosting ? 0 : data.getIntOr("boost_time_total", 0));
   }
}
