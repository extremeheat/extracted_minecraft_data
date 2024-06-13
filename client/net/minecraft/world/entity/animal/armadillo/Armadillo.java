package net.minecraft.world.entity.animal.armadillo;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class Armadillo extends Animal {
   public static final float BABY_SCALE = 0.6F;
   public static final float MAX_HEAD_ROTATION_EXTENT = 32.5F;
   public static final int SCARE_CHECK_INTERVAL = 80;
   private static final double SCARE_DISTANCE_HORIZONTAL = 7.0;
   private static final double SCARE_DISTANCE_VERTICAL = 2.0;
   private static final EntityDataAccessor<Armadillo.ArmadilloState> ARMADILLO_STATE = SynchedEntityData.defineId(
      Armadillo.class, EntityDataSerializers.ARMADILLO_STATE
   );
   private long inStateTicks = 0L;
   public final AnimationState rollOutAnimationState = new AnimationState();
   public final AnimationState rollUpAnimationState = new AnimationState();
   public final AnimationState peekAnimationState = new AnimationState();
   private int scuteTime;
   private boolean peekReceivedClient = false;

   public Armadillo(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.getNavigation().setCanFloat(true);
      this.scuteTime = this.pickNextScuteDropTime();
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.ARMADILLO.create(var1);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.MOVEMENT_SPEED, 0.14);
   }

   @Override
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

   public boolean shouldSwitchToScaredState() {
      return this.getState() == Armadillo.ArmadilloState.ROLLING && this.inStateTicks > (long)Armadillo.ArmadilloState.ROLLING.animationDuration();
   }

   public Armadillo.ArmadilloState getState() {
      return this.entityData.get(ARMADILLO_STATE);
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void switchToState(Armadillo.ArmadilloState var1) {
      this.entityData.set(ARMADILLO_STATE, var1);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (ARMADILLO_STATE.equals(var1)) {
         this.inStateTicks = 0L;
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   protected Brain.Provider<Armadillo> brainProvider() {
      return ArmadilloAi.brainProvider();
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return ArmadilloAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("armadilloBrain");
      ((Brain<Armadillo>)this.brain).tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("armadilloActivityUpdate");
      ArmadilloAi.updateActivity(this);
      this.level().getProfiler().pop();
      if (this.isAlive() && !this.isBaby() && --this.scuteTime <= 0) {
         this.playSound(SoundEvents.ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.spawnAtLocation(Items.ARMADILLO_SCUTE);
         this.gameEvent(GameEvent.ENTITY_PLACE);
         this.scuteTime = this.pickNextScuteDropTime();
      }

      super.customServerAiStep();
   }

   private int pickNextScuteDropTime() {
      return this.random.nextInt(20 * TimeUtil.SECONDS_PER_MINUTE * 5) + 20 * TimeUtil.SECONDS_PER_MINUTE * 5;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.setupAnimationStates();
      }

      if (this.isScared()) {
         this.clampHeadRotationToBody();
      }

      this.inStateTicks++;
   }

   @Override
   public float getAgeScale() {
      return this.isBaby() ? 0.6F : 1.0F;
   }

   private void setupAnimationStates() {
      switch (this.getState()) {
         case IDLE:
            this.rollOutAnimationState.stop();
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
            break;
         case UNROLLING:
            this.rollOutAnimationState.startIfStopped(this.tickCount);
            this.rollUpAnimationState.stop();
            this.peekAnimationState.stop();
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 64 && this.level().isClientSide) {
         this.peekReceivedClient = true;
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMADILLO_PEEK, this.getSoundSource(), 1.0F, 1.0F, false);
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.ARMADILLO_FOOD);
   }

   public static boolean checkArmadilloSpawnRules(EntityType<Armadillo> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.ARMADILLO_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   public boolean isScaredBy(LivingEntity var1) {
      if (!this.getBoundingBox().inflate(7.0, 2.0, 7.0).intersects(var1.getBoundingBox())) {
         return false;
      } else if (var1.getType().is(EntityTypeTags.UNDEAD)) {
         return true;
      } else if (this.getLastHurtByMob() == var1) {
         return true;
      } else if (var1 instanceof Player var2) {
         return var2.isSpectator() ? false : var2.isSprinting() || var2.isPassenger();
      } else {
         return false;
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("state", this.getState().getSerializedName());
      var1.putInt("scute_time", this.scuteTime);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.switchToState(Armadillo.ArmadilloState.fromName(var1.getString("state")));
      if (var1.contains("scute_time")) {
         this.scuteTime = var1.getInt("scute_time");
      }
   }

   public void rollUp() {
      if (!this.isScared()) {
         this.stopInPlace();
         this.resetLove();
         this.gameEvent(GameEvent.ENTITY_ACTION);
         this.makeSound(SoundEvents.ARMADILLO_ROLL);
         this.switchToState(Armadillo.ArmadilloState.ROLLING);
      }
   }

   public void rollOut() {
      if (this.isScared()) {
         this.gameEvent(GameEvent.ENTITY_ACTION);
         this.makeSound(SoundEvents.ARMADILLO_UNROLL_FINISH);
         this.switchToState(Armadillo.ArmadilloState.IDLE);
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isScared()) {
         var2 = (var2 - 1.0F) / 2.0F;
      }

      return super.hurt(var1, var2);
   }

   @Override
   protected void actuallyHurt(DamageSource var1, float var2) {
      super.actuallyHurt(var1, var2);
      if (!this.isNoAi()) {
         if (var1.getEntity() instanceof LivingEntity) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, 80L);
            if (this.canStayRolledUp()) {
               this.rollUp();
            }
         } else if (this.shouldPanic()) {
            this.rollOut();
         }
      }
   }

   public boolean shouldPanic() {
      return this.isOnFire() || this.isFreezing();
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.BRUSH) && this.brushOffScute()) {
         var3.hurtAndBreak(16, var1, getSlotForHand(var2));
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Override
   public void ageUp(int var1, boolean var2) {
      if (this.isBaby() && var2) {
         this.makeSound(SoundEvents.ARMADILLO_EAT);
      }

      super.ageUp(var1, var2);
   }

   public boolean brushOffScute() {
      if (this.isBaby()) {
         return false;
      } else {
         this.spawnAtLocation(new ItemStack(Items.ARMADILLO_SCUTE));
         this.gameEvent(GameEvent.ENTITY_INTERACT);
         this.playSound(SoundEvents.ARMADILLO_BRUSH);
         return true;
      }
   }

   public boolean canStayRolledUp() {
      return !this.isPanicking() && !this.isInLiquid() && !this.isLeashed() && !this.isPassenger() && !this.isVehicle();
   }

   @Override
   public void setInLove(@Nullable Player var1) {
      super.setInLove(var1);
      this.makeSound(SoundEvents.ARMADILLO_EAT);
   }

   @Override
   public boolean canFallInLove() {
      return super.canFallInLove() && !this.isScared();
   }

   @Override
   public SoundEvent getEatingSound(ItemStack var1) {
      return SoundEvents.ARMADILLO_EAT;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.isScared() ? null : SoundEvents.ARMADILLO_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMADILLO_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isScared() ? SoundEvents.ARMADILLO_HURT_REDUCED : SoundEvents.ARMADILLO_HURT;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.ARMADILLO_STEP, 0.15F, 1.0F);
   }

   @Override
   public int getMaxHeadYRot() {
      return this.isScared() ? 0 : 32;
   }

   @Override
   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this) {
         @Override
         public void clientTick() {
            if (!Armadillo.this.isScared()) {
               super.clientTick();
            }
         }
      };
   }

   public static enum ArmadilloState implements StringRepresentable {
      IDLE("idle", false, 0, 0) {
         @Override
         public boolean shouldHideInShell(long var1) {
            return false;
         }
      },
      ROLLING("rolling", true, 10, 1) {
         @Override
         public boolean shouldHideInShell(long var1) {
            return var1 > 5L;
         }
      },
      SCARED("scared", true, 50, 2) {
         @Override
         public boolean shouldHideInShell(long var1) {
            return true;
         }
      },
      UNROLLING("unrolling", true, 30, 3) {
         @Override
         public boolean shouldHideInShell(long var1) {
            return var1 < 26L;
         }
      };

      private static final StringRepresentable.EnumCodec<Armadillo.ArmadilloState> CODEC = StringRepresentable.fromEnum(Armadillo.ArmadilloState::values);
      private static final IntFunction<Armadillo.ArmadilloState> BY_ID = ByIdMap.continuous(
         Armadillo.ArmadilloState::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO
      );
      public static final StreamCodec<ByteBuf, Armadillo.ArmadilloState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Armadillo.ArmadilloState::id);
      private final String name;
      private final boolean isThreatened;
      private final int animationDuration;
      private final int id;

      ArmadilloState(String var3, boolean var4, int var5, int var6) {
         this.name = var3;
         this.isThreatened = var4;
         this.animationDuration = var5;
         this.id = var6;
      }

      public static Armadillo.ArmadilloState fromName(String var0) {
         return CODEC.byName(var0, IDLE);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      private int id() {
         return this.id;
      }

      public abstract boolean shouldHideInShell(long var1);

      public boolean isThreatened() {
         return this.isThreatened;
      }

      public int animationDuration() {
         return this.animationDuration;
      }
   }
}
