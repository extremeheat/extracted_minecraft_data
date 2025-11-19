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

   public Cow(EntityType<? extends Cow> var1, Level var2) {
      super(var1, var2);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), CowVariants.TEMPERATE));
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      VariantUtils.writeVariant(var1, this.getVariant());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      VariantUtils.readVariant(var1, Registries.COW_VARIANT).ifPresent(this::setVariant);
   }

   public @Nullable Cow getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Cow var3 = EntityType.COW.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null && var2 instanceof Cow var4) {
         var3.setVariant(this.random.nextBoolean() ? this.getVariant() : var4.getVariant());
      }

      return var3;
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      VariantUtils.selectVariantToSpawn(SpawnContext.create(var1, this.blockPosition()), Registries.COW_VARIANT).ifPresent(this::setVariant);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public void setVariant(Holder<CowVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public Holder<CowVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   public <T> @Nullable T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.COW_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.COW_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.COW_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.COW_VARIANT, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   // $FF: synthetic method
   public @Nullable AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_VARIANT_ID = SynchedEntityData.<Holder<CowVariant>>defineId(Cow.class, EntityDataSerializers.COW_VARIANT);
   }
}
