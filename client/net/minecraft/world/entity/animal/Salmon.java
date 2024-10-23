package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class Salmon extends AbstractSchoolingFish implements VariantHolder<Salmon.Variant> {
   private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(Salmon.class, EntityDataSerializers.STRING);

   public Salmon(EntityType<? extends Salmon> var1, Level var2) {
      super(var1, var2);
      this.refreshDimensions();
   }

   @Override
   public int getMaxSchoolSize() {
      return 5;
   }

   @Override
   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.SALMON_BUCKET);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SALMON_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.SALMON_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SALMON_HURT;
   }

   @Override
   protected SoundEvent getFlopSound() {
      return SoundEvents.SALMON_FLOP;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TYPE, Salmon.Variant.MEDIUM.type);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_TYPE.equals(var1)) {
         this.refreshDimensions();
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("type", this.getVariant().getSerializedName());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant(Salmon.Variant.byName(var1.getString("type")));
   }

   @Override
   public void saveToBucketTag(ItemStack var1) {
      Bucketable.saveDefaultDataToBucketTag(this, var1);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, var1, var1x -> var1x.putString("type", this.getVariant().getSerializedName()));
   }

   @Override
   public void loadFromBucketTag(CompoundTag var1) {
      Bucketable.loadDefaultDataFromBucketTag(this, var1);
      this.setVariant(Salmon.Variant.byName(var1.getString("type")));
   }

   public void setVariant(Salmon.Variant var1) {
      this.entityData.set(DATA_TYPE, var1.type);
   }

   public Salmon.Variant getVariant() {
      return Salmon.Variant.byName(this.entityData.get(DATA_TYPE));
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      SimpleWeightedRandomList.Builder var5 = SimpleWeightedRandomList.builder();
      var5.add(Salmon.Variant.SMALL, 30);
      var5.add(Salmon.Variant.MEDIUM, 50);
      var5.add(Salmon.Variant.LARGE, 15);
      var5.build().getRandomValue(this.random).ifPresent(this::setVariant);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public float getSalmonScale() {
      return this.getVariant().boundingBoxScale;
   }

   @Override
   protected EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1).scale(this.getSalmonScale());
   }

   public static enum Variant implements StringRepresentable {
      SMALL("small", 0.5F),
      MEDIUM("medium", 1.0F),
      LARGE("large", 1.5F);

      public static final StringRepresentable.EnumCodec<Salmon.Variant> CODEC = StringRepresentable.fromEnum(Salmon.Variant::values);
      final String type;
      final float boundingBoxScale;

      private Variant(final String nullxx, final float nullxxx) {
         this.type = nullxx;
         this.boundingBoxScale = nullxxx;
      }

      @Override
      public String getSerializedName() {
         return this.type;
      }

      static Salmon.Variant byName(String var0) {
         return CODEC.byName(var0, MEDIUM);
      }
   }
}
