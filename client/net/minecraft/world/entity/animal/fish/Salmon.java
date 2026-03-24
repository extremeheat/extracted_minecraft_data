package net.minecraft.world.entity.animal.fish;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
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
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Salmon extends AbstractSchoolingFish {
   private static final String TAG_TYPE = "type";
   private static final EntityDataAccessor<Integer> DATA_TYPE;

   public Salmon(final EntityType<? extends Salmon> type, final Level level) {
      super(type, level);
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

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.SALMON_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.SALMON_FLOP;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_TYPE, Salmon.Variant.DEFAULT.id());
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (DATA_TYPE.equals(accessor)) {
         this.refreshDimensions();
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("type", Salmon.Variant.CODEC, this.getVariant());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setVariant((Variant)input.read("type", Salmon.Variant.CODEC).orElse(Salmon.Variant.DEFAULT));
   }

   public void saveToBucketTag(final ItemStack bucket) {
      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      bucket.copyFrom(DataComponents.SALMON_SIZE, this);
   }

   private void setVariant(final Variant variant) {
      this.entityData.set(DATA_TYPE, variant.id);
   }

   public Variant getVariant() {
      return (Variant)Salmon.Variant.BY_ID.apply((Integer)this.entityData.get(DATA_TYPE));
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.SALMON_SIZE ? castComponentValue(type, this.getVariant()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.SALMON_SIZE);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.SALMON_SIZE) {
         this.setVariant((Variant)castComponentValue(DataComponents.SALMON_SIZE, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      WeightedList.Builder<Variant> builder = WeightedList.<Variant>builder();
      builder.add(Salmon.Variant.SMALL, 30);
      builder.add(Salmon.Variant.MEDIUM, 50);
      builder.add(Salmon.Variant.LARGE, 15);
      builder.build().getRandom(this.random).ifPresent(this::setVariant);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   public float getSalmonScale() {
      return this.getVariant().boundingBoxScale;
   }

   protected EntityDimensions getDefaultDimensions(final Pose pose) {
      return super.getDefaultDimensions(pose).scale(this.getSalmonScale());
   }

   static {
      DATA_TYPE = SynchedEntityData.<Integer>defineId(Salmon.class, EntityDataSerializers.INT);
   }

   public static enum Variant implements StringRepresentable {
      SMALL("small", 0, 0.5F),
      MEDIUM("medium", 1, 1.0F),
      LARGE("large", 2, 1.5F);

      public static final Variant DEFAULT = MEDIUM;
      public static final Codec<Variant> CODEC = StringRepresentable.<Variant>fromEnum(Variant::values);
      private static final IntFunction<Variant> BY_ID = ByIdMap.<Variant>continuous(Variant::id, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      public static final StreamCodec<ByteBuf, Variant> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Variant::id);
      private final String name;
      private final int id;
      private final float boundingBoxScale;

      private Variant(final String name, final int id, final float boundingBoxScale) {
         this.name = name;
         this.id = id;
         this.boundingBoxScale = boundingBoxScale;
      }

      public String getSerializedName() {
         return this.name;
      }

      private int id() {
         return this.id;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{SMALL, MEDIUM, LARGE};
      }
   }
}
