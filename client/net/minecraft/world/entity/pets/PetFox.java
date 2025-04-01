package net.minecraft.world.entity.pets;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class PetFox extends AbstractPet {
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID;
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_TRUSTED_ID_0;
   private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_TRUSTED_ID_1;

   public PetFox(EntityType<? extends PetFox> var1, Level var2) {
      super(var1, var2);
      this.lookControl = new FoxLookControl();
      this.moveControl = new FoxMoveControl();
      this.setPathfindingMalus(PathType.DANGER_OTHER, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, 0.0F);
      this.getNavigation().setRequiredPathLength(32.0F);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TRUSTED_ID_0, Optional.empty());
      var1.define(DATA_TRUSTED_ID_1, Optional.empty());
      var1.define(DATA_TYPE_ID, Fox.Variant.DEFAULT.getId());
      var1.define(DATA_FLAGS_ID, (byte)0);
   }

   public void aiStep() {
      super.aiStep();
      if (this.random.nextFloat() < 0.05F) {
         this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
      }

   }

   public void handleEntityEvent(byte var1) {
      super.handleEntityEvent(var1);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.SAFE_FALL_DISTANCE, 5.0).add(Attributes.FOLLOW_RANGE, 32.0);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      Holder var5 = var1.getBiome(this.blockPosition());
      Fox.Variant var6 = Fox.Variant.byBiome(var5);
      this.setVariant(var6);
      this.populateDefaultEquipmentSlots(var1.getRandom(), var2);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1);
   }

   public Fox.Variant getVariant() {
      return Fox.Variant.byId((Integer)this.entityData.get(DATA_TYPE_ID));
   }

   private void setVariant(Fox.Variant var1) {
      this.entityData.set(DATA_TYPE_ID, var1.getId());
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.FOX_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.FOX_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.FOX_VARIANT) {
         this.setVariant((Fox.Variant)castComponentValue(DataComponents.FOX_VARIANT, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.store("Type", Fox.Variant.CODEC, this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant((Fox.Variant)var1.read("Type", Fox.Variant.CODEC).orElse(Fox.Variant.DEFAULT));
   }

   private void setFlag(int var1, boolean var2) {
      if (var2) {
         this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) | var1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) & ~var1));
      }

   }

   private boolean getFlag(int var1) {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & var1) != 0;
   }

   protected boolean canDispenserEquipIntoSlot(EquipmentSlot var1) {
      return var1 == EquipmentSlot.MAINHAND && this.canPickUpLoot();
   }

   public void tick() {
      super.tick();
      if (this.isEffectiveAi()) {
      }

   }

   boolean canMove() {
      return true;
   }

   public void playAmbientSound() {
      SoundEvent var1 = this.getAmbientSound();
      if (var1 == SoundEvents.FOX_SCREECH) {
         this.playSound(var1, 2.0F, this.getVoicePitch());
      } else {
         super.playAmbientSound();
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (!this.level().isBrightOutside() && this.random.nextFloat() < 0.1F) {
         List var1 = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16.0, 16.0, 16.0), EntitySelector.NO_SPECTATORS);
         if (var1.isEmpty()) {
            return SoundEvents.FOX_SCREECH;
         }
      }

      return SoundEvents.FOX_AMBIENT;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.55F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   static {
      DATA_TYPE_ID = SynchedEntityData.<Integer>defineId(PetFox.class, EntityDataSerializers.INT);
      DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(PetFox.class, EntityDataSerializers.BYTE);
      DATA_TRUSTED_ID_0 = SynchedEntityData.<Optional<EntityReference<LivingEntity>>>defineId(PetFox.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
      DATA_TRUSTED_ID_1 = SynchedEntityData.<Optional<EntityReference<LivingEntity>>>defineId(PetFox.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
   }

   class FoxMoveControl extends MoveControl {
      public FoxMoveControl() {
         super(PetFox.this);
      }

      public void tick() {
         if (PetFox.this.canMove()) {
            super.tick();
         }

      }
   }

   public static class FoxGroupData extends AgeableMob.AgeableMobGroupData {
      public final Fox.Variant variant;

      public FoxGroupData(Fox.Variant var1) {
         super(false);
         this.variant = var1;
      }
   }

   public class FoxLookControl extends LookControl {
      public FoxLookControl() {
         super(PetFox.this);
      }

      public void tick() {
         super.tick();
      }

      protected boolean resetXRotOnTick() {
         return !PetFox.this.isCrouching();
      }
   }
}
