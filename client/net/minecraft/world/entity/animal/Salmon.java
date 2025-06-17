package net.minecraft.world.entity.animal;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Salmon extends AbstractSchoolingFish {
   private static final String TAG_TYPE = "type";
   private static final EntityDataAccessor<Integer> DATA_TYPE;

   public Salmon(EntityType<? extends Salmon> var1, Level var2) {
      super(var1, var2);
      this.refreshDimensions();
   }

   public int getMaxSchoolSize() {
      return 5;
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.SALMON_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SALMON_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SALMON_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.SALMON_FLOP;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TYPE, Salmon.Variant.DEFAULT.id());
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_TYPE.equals(var1)) {
         this.refreshDimensions();
      }

   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("type", Salmon.Variant.CODEC, this.getVariant());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant((Variant)var1.read("type", Salmon.Variant.CODEC).orElse(Salmon.Variant.DEFAULT));
   }

   public void saveToBucketTag(ItemStack var1) {
      Bucketable.saveDefaultDataToBucketTag(this, var1);
      var1.copyFrom(DataComponents.SALMON_SIZE, this);
   }

   private void setVariant(Variant var1) {
      this.entityData.set(DATA_TYPE, var1.id);
   }

   public Variant getVariant() {
      return (Variant)Salmon.Variant.BY_ID.apply((Integer)this.entityData.get(DATA_TYPE));
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.SALMON_SIZE ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.SALMON_SIZE);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.SALMON_SIZE) {
         this.setVariant((Variant)castComponentValue(DataComponents.SALMON_SIZE, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      WeightedList.Builder var5 = WeightedList.builder();
      var5.add(Salmon.Variant.SMALL, 30);
      var5.add(Salmon.Variant.MEDIUM, 50);
      var5.add(Salmon.Variant.LARGE, 15);
      var5.build().getRandom(this.random).ifPresent(this::setVariant);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public float getSalmonScale() {
      return this.getVariant().boundingBoxScale;
   }

   protected EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1).scale(this.getSalmonScale());
   }

   static {
      DATA_TYPE = SynchedEntityData.<Integer>defineId(Salmon.class, EntityDataSerializers.INT);
   }

   public static enum Variant implements StringRepresentable {
      SMALL("small", 0, 0.5F),
      MEDIUM("medium", 1, 1.0F),
      LARGE("large", 2, 1.5F);

      public static final Variant DEFAULT = MEDIUM;
      public static final StringRepresentable.EnumCodec<Variant> CODEC = StringRepresentable.<Variant>fromEnum(Variant::values);
      static final IntFunction<Variant> BY_ID = ByIdMap.<Variant>continuous(Variant::id, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      public static final StreamCodec<ByteBuf, Variant> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Variant::id);
      private final String name;
      final int id;
      final float boundingBoxScale;

      private Variant(final String var3, final int var4, final float var5) {
         this.name = var3;
         this.id = var4;
         this.boundingBoxScale = var5;
      }

      public String getSerializedName() {
         return this.name;
      }

      int id() {
         return this.id;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{SMALL, MEDIUM, LARGE};
      }
   }
}
