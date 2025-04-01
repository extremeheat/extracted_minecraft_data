package net.minecraft.world.entity.pets;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PetArmadillo extends AbstractPet {
   public static final float MAX_HEAD_ROTATION_EXTENT = 32.5F;
   private static final EntityDataAccessor<Armadillo.ArmadilloState> ARMADILLO_STATE;
   private long inStateTicks = 0L;
   public final AnimationState rollOutAnimationState = new AnimationState();
   public final AnimationState rollUpAnimationState = new AnimationState();
   public final AnimationState peekAnimationState = new AnimationState();
   private int scuteTime;
   private boolean peekReceivedClient = false;

   public PetArmadillo(EntityType<? extends PetArmadillo> var1, Level var2) {
      super(var1, var2);
      this.getNavigation().setCanFloat(true);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.MOVEMENT_SPEED, 0.14);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ARMADILLO_STATE, Armadillo.ArmadilloState.IDLE);
   }

   public boolean isScared() {
      return this.entityData.get(ARMADILLO_STATE) != Armadillo.ArmadilloState.IDLE;
   }

   public boolean shouldHideInShell() {
      return this.getState().shouldHideInShell(this.inStateTicks);
   }

   public Armadillo.ArmadilloState getState() {
      return (Armadillo.ArmadilloState)this.entityData.get(ARMADILLO_STATE);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void switchToState(Armadillo.ArmadilloState var1) {
      this.entityData.set(ARMADILLO_STATE, var1);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (ARMADILLO_STATE.equals(var1)) {
         this.inStateTicks = 0L;
      }

      super.onSyncedDataUpdated(var1);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.setupAnimationStates();
      }

      if (this.isScared()) {
         this.clampHeadRotationToBody();
      }

      ++this.inStateTicks;
   }

   public float getAgeScale() {
      return 1.0F;
   }

   private void setupAnimationStates() {
      switch (this.getState()) {
         case IDLE:
            this.rollOutAnimationState.stop();
            this.rollUpAnimationState.stop();
            this.peekAnimationState.stop();
            break;
         case UNROLLING:
            this.rollOutAnimationState.startIfStopped(this.tickCount);
            this.rollUpAnimationState.stop();
            this.peekAnimationState.stop();
            break;
         case ROLLING:
            this.rollOutAnimationState.stop();
            this.rollUpAnimationState.startIfStopped(this.tickCount);
            this.peekAnimationState.stop();
            break;
         case SCARED:
            this.rollOutAnimationState.stop();
            this.rollUpAnimationState.stop();
            if (this.peekReceivedClient) {
               this.peekAnimationState.stop();
               this.peekReceivedClient = false;
            }

            if (this.inStateTicks == 0L) {
               this.peekAnimationState.start(this.tickCount);
               this.peekAnimationState.fastForward(Armadillo.ArmadilloState.SCARED.animationDuration(), 1.0F);
            } else {
               this.peekAnimationState.startIfStopped(this.tickCount);
            }
      }

   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 64 && this.level().isClientSide) {
         this.peekReceivedClient = true;
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMADILLO_PEEK, this.getSoundSource(), 1.0F, 1.0F, false);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.store("state", Armadillo.ArmadilloState.CODEC, this.getState());
      var1.putInt("scute_time", this.scuteTime);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.switchToState((Armadillo.ArmadilloState)var1.read("state", Armadillo.ArmadilloState.CODEC).orElse(Armadillo.ArmadilloState.IDLE));
      var1.getInt("scute_time").ifPresent((var1x) -> this.scuteTime = var1x);
   }

   public boolean canStayRolledUp() {
      return !this.isPanicking() && !this.isInLiquid() && !this.isLeashed() && !this.isPassenger() && !this.isVehicle();
   }

   protected SoundEvent getAmbientSound() {
      return this.isScared() ? null : SoundEvents.ARMADILLO_AMBIENT;
   }

   protected void playEatingSound() {
      this.makeSound(SoundEvents.ARMADILLO_EAT);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.ARMADILLO_STEP, 0.15F, 1.0F);
   }

   public int getMaxHeadYRot() {
      return this.isScared() ? 0 : 32;
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this) {
         public void clientTick() {
            if (!PetArmadillo.this.isScared()) {
               super.clientTick();
            }

         }
      };
   }

   static {
      ARMADILLO_STATE = SynchedEntityData.<Armadillo.ArmadilloState>defineId(PetArmadillo.class, EntityDataSerializers.ARMADILLO_STATE);
   }
}
