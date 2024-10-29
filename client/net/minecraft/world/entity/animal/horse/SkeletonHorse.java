package net.minecraft.world.entity.animal.horse;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class SkeletonHorse extends AbstractHorse {
   private final SkeletonTrapGoal skeletonTrapGoal = new SkeletonTrapGoal(this);
   private static final int TRAP_MAX_LIFE = 18000;
   private static final EntityDimensions BABY_DIMENSIONS;
   private boolean isTrap;
   private int trapTime;

   public SkeletonHorse(EntityType<? extends SkeletonHorse> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   public static boolean checkSkeletonHorseSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      if (!EntitySpawnReason.isSpawner(var2)) {
         return Animal.checkAnimalSpawnRules(var0, var1, var2, var3, var4);
      } else {
         return EntitySpawnReason.ignoresLightRequirements(var2) || isBrightEnoughToSpawn(var1, var3);
      }
   }

   protected void randomizeAttributes(RandomSource var1) {
      AttributeInstance var10000 = this.getAttribute(Attributes.JUMP_STRENGTH);
      Objects.requireNonNull(var1);
      var10000.setBaseValue(generateJumpStrength(var1::nextDouble));
   }

   protected void addBehaviourGoals() {
   }

   protected SoundEvent getAmbientSound() {
      return this.isEyeInFluid(FluidTags.WATER) ? SoundEvents.SKELETON_HORSE_AMBIENT_WATER : SoundEvents.SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.onGround()) {
         if (!this.isVehicle()) {
            return SoundEvents.SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopSoundCounter;
         if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
            return SoundEvents.SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.gallopSoundCounter <= 5) {
            return SoundEvents.SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float var1) {
      if (this.onGround()) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, var1 * 25.0F));
      }

   }

   protected void playJumpSound() {
      if (this.isInWater()) {
         this.playSound(SoundEvents.SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   public void aiStep() {
      super.aiStep();
      if (this.isTrap() && this.trapTime++ >= 18000) {
         this.discard();
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("SkeletonTrap", this.isTrap());
      var1.putInt("SkeletonTrapTime", this.trapTime);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setTrap(var1.getBoolean("SkeletonTrap"));
      this.trapTime = var1.getInt("SkeletonTrapTime");
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.isTrap;
   }

   public void setTrap(boolean var1) {
      if (var1 != this.isTrap) {
         this.isTrap = var1;
         if (var1) {
            this.goalSelector.addGoal(1, this.skeletonTrapGoal);
         } else {
            this.goalSelector.removeGoal(this.skeletonTrapGoal);
         }

      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (AgeableMob)EntityType.SKELETON_HORSE.create(var1, EntitySpawnReason.BREEDING);
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      return (InteractionResult)(!this.isTamed() ? InteractionResult.PASS : super.mobInteract(var1, var2));
   }

   static {
      BABY_DIMENSIONS = EntityType.SKELETON_HORSE.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityType.SKELETON_HORSE.getHeight() - 0.03125F, 0.0F)).scale(0.5F);
   }
}
