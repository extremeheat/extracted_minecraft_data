package net.minecraft.world.entity.animal.armadillo;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jspecify.annotations.Nullable;

public class Armadillo extends Animal {
   public static final float BABY_SCALE = 0.6F;
   public static final float MAX_HEAD_ROTATION_EXTENT = 32.5F;
   public static final int SCARE_CHECK_INTERVAL = 80;
   private static final double SCARE_DISTANCE_HORIZONTAL = 7.0;
   private static final double SCARE_DISTANCE_VERTICAL = 2.0;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final Brain.Provider<Armadillo> BRAIN_PROVIDER;
   private static final EntityDataAccessor<ArmadilloState> ARMADILLO_STATE;
   private long inStateTicks = 0L;
   public final AnimationState rollOutAnimationState = new AnimationState();
   public final AnimationState rollUpAnimationState = new AnimationState();
   public final AnimationState peekAnimationState = new AnimationState();
   private int scuteTime;
   private boolean peekReceivedClient = false;

   public Armadillo(final EntityType<? extends Animal> type, final Level level) {
      super(type, level);
      this.getNavigation().setCanFloat(true);
      this.scuteTime = this.pickNextScuteDropTime();
   }

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      return EntityTypes.ARMADILLO.create(level, EntitySpawnReason.BREEDING);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.MOVEMENT_SPEED, 0.14);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(ARMADILLO_STATE, Armadillo.ArmadilloState.IDLE);
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

   public ArmadilloState getState() {
      return (ArmadilloState)this.entityData.get(ARMADILLO_STATE);
   }

   public void switchToState(final ArmadilloState state) {
      this.entityData.set(ARMADILLO_STATE, state);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (ARMADILLO_STATE.equals(accessor)) {
         this.inStateTicks = 0L;
      }

      super.onSyncedDataUpdated(accessor);
   }

   protected Brain<Armadillo> makeBrain(final Brain.Packed packedBrain) {
      return BRAIN_PROVIDER.makeBrain(this, packedBrain);
   }

   public Brain<Armadillo> getBrain() {
      return super.getBrain();
   }

   protected void customServerAiStep(final ServerLevel level) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("armadilloBrain");
      this.getBrain().tick(level, this);
      profiler.pop();
      profiler.push("armadilloActivityUpdate");
      ArmadilloAi.updateActivity(this);
      profiler.pop();
      if (this.isAlive() && --this.scuteTime <= 0 && this.shouldDropLoot(level)) {
         if (this.dropFromGiftLootTable(level, BuiltInLootTables.ARMADILLO_SHED, this::spawnAtLocation)) {
            this.playSound(SoundEvents.ARMADILLO_SCUTE_DROP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.gameEvent(GameEvent.ENTITY_PLACE);
         }

         this.scuteTime = this.pickNextScuteDropTime();
      }

      super.customServerAiStep(level);
   }

   private int pickNextScuteDropTime() {
      return this.random.nextInt(20 * TimeUtil.SECONDS_PER_MINUTE * 5) + 20 * TimeUtil.SECONDS_PER_MINUTE * 5;
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
      return this.isBaby() ? 0.6F : 1.0F;
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   private void setupAnimationStates() {
      switch (this.getState().ordinal()) {
         case 0:
            this.rollOutAnimationState.stop();
            this.rollUpAnimationState.stop();
            this.peekAnimationState.stop();
            break;
         case 1:
            this.rollOutAnimationState.stop();
            this.rollUpAnimationState.startIfStopped(this.tickCount);
            this.peekAnimationState.stop();
            break;
         case 2:
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
         case 3:
            this.rollOutAnimationState.startIfStopped(this.tickCount);
            this.rollUpAnimationState.stop();
            this.peekAnimationState.stop();
      }

   }

   public void handleEntityEvent(final byte id) {
      if (id == 64 && this.level().isClientSide()) {
         this.peekReceivedClient = true;
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMADILLO_PEEK, this.getSoundSource(), 1.0F, 1.0F, false);
      } else {
         super.handleEntityEvent(id);
      }

   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.ARMADILLO_FOOD);
   }

   public static boolean checkArmadilloSpawnRules(final EntityType<Armadillo> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return level.getBlockState(pos.below()).is(BlockTags.ARMADILLO_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
   }

   public boolean isScaredBy(final LivingEntity livingEntity) {
      if (!this.getBoundingBox().inflate(7.0, 2.0, 7.0).intersects(livingEntity.getBoundingBox())) {
         return false;
      } else if (livingEntity.is(EntityTypeTags.UNDEAD)) {
         return true;
      } else if (this.getLastHurtByMob() == livingEntity) {
         return true;
      } else if (livingEntity instanceof Player) {
         Player player = (Player)livingEntity;
         if (player.isSpectator()) {
            return false;
         } else {
            return player.isSprinting() || player.isPassenger();
         }
      } else {
         return false;
      }
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("state", Armadillo.ArmadilloState.CODEC, this.getState());
      output.putInt("scute_time", this.scuteTime);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.switchToState((ArmadilloState)input.read("state", Armadillo.ArmadilloState.CODEC).orElse(Armadillo.ArmadilloState.IDLE));
      input.getInt("scute_time").ifPresent((time) -> this.scuteTime = time);
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

   public boolean hurtServer(final ServerLevel level, final DamageSource source, float damage) {
      if (this.isScared()) {
         damage = (damage - 1.0F) / 2.0F;
      }

      return super.hurtServer(level, source, damage);
   }

   protected void actuallyHurt(final ServerLevel level, final DamageSource source, final float dmg) {
      super.actuallyHurt(level, source, dmg);
      if (!this.isNoAi() && !this.isDeadOrDying()) {
         if (source.getEntity() instanceof LivingEntity) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, 80L);
            if (this.canStayRolledUp()) {
               this.rollUp();
            }
         } else if (source.is(DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES)) {
            this.rollOut();
         }

      }
   }

   public InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.is(Items.BRUSH) && this.brushOffScute(player, itemStack)) {
         itemStack.hurtAndBreak(16, player, (EquipmentSlot)hand.asEquipmentSlot());
         return InteractionResult.SUCCESS;
      } else {
         return (InteractionResult)(this.isScared() ? InteractionResult.FAIL : super.mobInteract(player, hand));
      }
   }

   public boolean brushOffScute(final @Nullable Entity interactingEntity, final ItemStack tool) {
      if (this.isBaby()) {
         return false;
      } else {
         Level var4 = this.level();
         if (var4 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var4;
            this.dropFromEntityInteractLootTable(level, BuiltInLootTables.ARMADILLO_BRUSH, interactingEntity, tool, this::spawnAtLocation);
            this.playSound(SoundEvents.ARMADILLO_BRUSH);
            this.gameEvent(GameEvent.ENTITY_INTERACT);
         }

         return true;
      }
   }

   public boolean canStayRolledUp() {
      return !this.isPanicking() && !this.isInLiquid() && !this.isLeashed() && !this.isPassenger() && !this.isVehicle();
   }

   public boolean canFallInLove() {
      return super.canFallInLove() && !this.isScared();
   }

   protected SoundEvent getAmbientSound() {
      return this.isScared() ? null : SoundEvents.ARMADILLO_AMBIENT;
   }

   protected void playEatingSound() {
      this.makeSound(SoundEvents.ARMADILLO_EAT);
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMADILLO_DEATH;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return this.isScared() ? SoundEvents.ARMADILLO_HURT_REDUCED : SoundEvents.ARMADILLO_HURT;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.ARMADILLO_STEP, 0.15F, 1.0F);
   }

   public int getMaxHeadYRot() {
      return this.isScared() ? 0 : 32;
   }

   protected BodyRotationControl createBodyControl() {
      return new BodyRotationControl(this) {
         {
            Objects.requireNonNull(Armadillo.this);
         }

         public void clientTick() {
            if (!Armadillo.this.isScared()) {
               super.clientTick();
            }

         }
      };
   }

   static {
      BABY_DIMENSIONS = EntityTypes.ARMADILLO.getDimensions().scale(0.6F).withEyeHeight(0.21875F);
      BRAIN_PROVIDER = Brain.<Armadillo>provider(List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FOOD_TEMPTATIONS, SensorType.NEAREST_ADULT, SensorType.ARMADILLO_SCARE_DETECTED), (var0) -> ArmadilloAi.getActivities());
      ARMADILLO_STATE = SynchedEntityData.<ArmadilloState>defineId(Armadillo.class, EntityDataSerializers.ARMADILLO_STATE);
   }

   public static enum ArmadilloState implements StringRepresentable {
      IDLE("idle", false, 0, 0) {
         public boolean shouldHideInShell(final long ticksInState) {
            return false;
         }
      },
      ROLLING("rolling", true, 10, 1) {
         public boolean shouldHideInShell(final long ticksInState) {
            return ticksInState > 5L;
         }
      },
      SCARED("scared", true, 50, 2) {
         public boolean shouldHideInShell(final long ticksInState) {
            return true;
         }
      },
      UNROLLING("unrolling", true, 30, 3) {
         public boolean shouldHideInShell(final long ticksInState) {
            return ticksInState < 26L;
         }
      };

      private static final Codec<ArmadilloState> CODEC = StringRepresentable.<ArmadilloState>fromEnum(ArmadilloState::values);
      private static final IntFunction<ArmadilloState> BY_ID = ByIdMap.<ArmadilloState>continuous(ArmadilloState::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, ArmadilloState> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ArmadilloState::id);
      private final String name;
      private final boolean isThreatened;
      private final int animationDuration;
      private final int id;

      private ArmadilloState(final String name, final boolean isThreatened, final int animationDuration, final int id) {
         this.name = name;
         this.isThreatened = isThreatened;
         this.animationDuration = animationDuration;
         this.id = id;
      }

      public String getSerializedName() {
         return this.name;
      }

      private int id() {
         return this.id;
      }

      public abstract boolean shouldHideInShell(final long ticksInState);

      public boolean isThreatened() {
         return this.isThreatened;
      }

      public int animationDuration() {
         return this.animationDuration;
      }

      // $FF: synthetic method
      private static ArmadilloState[] $values() {
         return new ArmadilloState[]{IDLE, ROLLING, SCARED, UNROLLING};
      }
   }
}
