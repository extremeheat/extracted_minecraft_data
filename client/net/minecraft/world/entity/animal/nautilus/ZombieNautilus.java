package net.minecraft.world.entity.animal.nautilus;

import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.ZombieNautilusVariant;
import net.minecraft.world.entity.animal.ZombieNautilusVariants;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ZombieNautilus extends AbstractNautilus {
   private static final EntityDataAccessor<Holder<ZombieNautilusVariant>> DATA_VARIANT_ID;

   public ZombieNautilus(EntityType<? extends ZombieNautilus> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return AbstractNautilus.createAttributes().add(Attributes.MOVEMENT_SPEED, 1.100000023841858);
   }

   public @Nullable ZombieNautilus getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   protected EquipmentSlot sunProtectionSlot() {
      return EquipmentSlot.BODY;
   }

   protected Brain.Provider<ZombieNautilus> brainProvider() {
      return ZombieNautilusAi.brainProvider();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return ZombieNautilusAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<ZombieNautilus> getBrain() {
      return super.getBrain();
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("zombieNautilusBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("zombieNautilusActivityUpdate");
      ZombieNautilusAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIE_NAUTILUS_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_DEATH;
   }

   protected SoundEvent getDashSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_DASH;
   }

   protected SoundEvent getDashReadySound() {
      return SoundEvents.ZOMBIE_NAUTILUS_DASH_READY;
   }

   protected void playEatingSound() {
      this.makeSound(SoundEvents.ZOMBIE_NAUTILUS_EAT);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_SWIM;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), ZombieNautilusVariants.TEMPERATE));
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      VariantUtils.readVariant(var1, Registries.ZOMBIE_NAUTILUS_VARIANT).ifPresent(this::setVariant);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      VariantUtils.writeVariant(var1, this.getVariant());
   }

   public void setVariant(Holder<ZombieNautilusVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public Holder<ZombieNautilusVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   public <T> @Nullable T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.ZOMBIE_NAUTILUS_VARIANT ? castComponentValue(var1, new EitherHolder(this.getVariant())) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.ZOMBIE_NAUTILUS_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.ZOMBIE_NAUTILUS_VARIANT) {
         Optional var3 = ((EitherHolder)castComponentValue(DataComponents.ZOMBIE_NAUTILUS_VARIANT, var2)).unwrap(this.registryAccess());
         if (var3.isPresent()) {
            this.setVariant((Holder)var3.get());
            return true;
         } else {
            return false;
         }
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      VariantUtils.selectVariantToSpawn(SpawnContext.create(var1, this.blockPosition()), Registries.ZOMBIE_NAUTILUS_VARIANT).ifPresent(this::setVariant);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   public @Nullable AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_VARIANT_ID = SynchedEntityData.<Holder<ZombieNautilusVariant>>defineId(ZombieNautilus.class, EntityDataSerializers.ZOMBIE_NAUTILUS_VARIANT);
   }
}
