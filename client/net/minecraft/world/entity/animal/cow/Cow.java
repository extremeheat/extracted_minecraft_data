package net.minecraft.world.entity.animal.cow;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Cow extends AbstractCow {
   private static final EntityDataAccessor<Holder<CowVariant>> DATA_VARIANT_ID;

   public Cow(final EntityType<? extends Cow> type, final Level level) {
      super(type, level);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), CowVariants.TEMPERATE));
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      VariantUtils.writeVariant(output, this.getVariant());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      VariantUtils.readVariant(input, Registries.COW_VARIANT).ifPresent(this::setVariant);
   }

   public @Nullable Cow getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      Cow baby = EntityType.COW.create(level, EntitySpawnReason.BREEDING);
      if (baby != null && partner instanceof Cow partnerCow) {
         baby.setVariant(this.random.nextBoolean() ? this.getVariant() : partnerCow.getVariant());
      }

      return baby;
   }

   public SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      VariantUtils.selectVariantToSpawn(SpawnContext.create(level, this.blockPosition()), Registries.COW_VARIANT).ifPresent(this::setVariant);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   public void setVariant(final Holder<CowVariant> variant) {
      this.entityData.set(DATA_VARIANT_ID, variant);
   }

   public Holder<CowVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.COW_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.COW_VARIANT);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.COW_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.COW_VARIANT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   static {
      DATA_VARIANT_ID = SynchedEntityData.<Holder<CowVariant>>defineId(Cow.class, EntityDataSerializers.COW_VARIANT);
   }
}
