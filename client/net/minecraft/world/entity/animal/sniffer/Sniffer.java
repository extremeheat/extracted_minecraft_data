package net.minecraft.world.entity.animal.sniffer;

import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Sniffer extends Animal {
   private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
   private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
   private static final int DIGGING_PARTICLES_AMOUNT = 30;
   private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
   private static final int SNIFFING_PROXIMITY_DISTANCE = 10;
   private static final int SNIFFER_BABY_AGE_TICKS = 48000;
   private static final EntityDataAccessor<Sniffer.State> DATA_STATE = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.SNIFFER_STATE);
   private static final EntityDataAccessor<Integer> DATA_DROP_SEED_AT_TICK = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.INT);
   public final AnimationState walkingAnimationState = new AnimationState();
   public final AnimationState panicAnimationState = new AnimationState();
   public final AnimationState feelingHappyAnimationState = new AnimationState();
   public final AnimationState scentingAnimationState = new AnimationState();
   public final AnimationState sniffingAnimationState = new AnimationState();
   public final AnimationState searchingAnimationState = new AnimationState();
   public final AnimationState diggingAnimationState = new AnimationState();
   public final AnimationState risingAnimationState = new AnimationState();

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.10000000149011612).add(Attributes.MAX_HEALTH, 14.0);
   }

   public Sniffer(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.entityData.define(DATA_STATE, Sniffer.State.IDLING);
      this.entityData.define(DATA_DROP_SEED_AT_TICK, 0);
      this.getNavigation().setCanFloat(true);
      this.setPathfindingMalus(BlockPathTypes.WATER, -2.0F);
   }

   @Override
   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.getDimensions(var1).height * 0.6F;
   }

   private boolean isMoving() {
      boolean var1 = this.onGround || this.isInWaterOrBubble();
      return var1 && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6;
   }

   private boolean isMovingInWater() {
      return this.isMoving() && this.isInWater() && !this.isUnderWater() && this.getDeltaMovement().horizontalDistanceSqr() > 9.999999999999999E-6;
   }

   private boolean isMovingOnLand() {
      return this.isMoving() && !this.isUnderWater() && !this.isInWater();
   }

   public boolean isPanicking() {
      return this.brain.getMemory(MemoryModuleType.IS_PANICKING).isPresent();
   }

   public boolean canPlayDiggingSound() {
      return this.getState() == Sniffer.State.DIGGING || this.getState() == Sniffer.State.SEARCHING;
   }

   private BlockPos getHeadPosition() {
      Vec3 var1 = this.position().add(this.getForward().scale(2.25));
      return BlockPos.containing(var1.x(), this.getY(), var1.z());
   }

   private Sniffer.State getState() {
      return this.entityData.get(DATA_STATE);
   }

   private Sniffer setState(Sniffer.State var1) {
      this.entityData.set(DATA_STATE, var1);
      return this;
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_STATE.equals(var1)) {
         Sniffer.State var2 = this.getState();
         this.resetAnimations();
         switch(var2) {
            case SCENTING:
               this.scentingAnimationState.startIfStopped(this.tickCount);
               break;
            case SNIFFING:
               this.sniffingAnimationState.startIfStopped(this.tickCount);
               break;
            case SEARCHING:
               this.searchingAnimationState.startIfStopped(this.tickCount);
               break;
            case DIGGING:
               this.diggingAnimationState.startIfStopped(this.tickCount);
               break;
            case RISING:
               this.risingAnimationState.startIfStopped(this.tickCount);
               break;
            case FEELING_HAPPY:
               this.feelingHappyAnimationState.startIfStopped(this.tickCount);
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   private void resetAnimations() {
      this.searchingAnimationState.stop();
      this.diggingAnimationState.stop();
      this.sniffingAnimationState.stop();
      this.risingAnimationState.stop();
      this.feelingHappyAnimationState.stop();
      this.scentingAnimationState.stop();
   }

   public Sniffer transitionTo(Sniffer.State var1) {
      switch(var1) {
         case SCENTING:
            this.playSound(SoundEvents.SNIFFER_SCENTING, 1.0F, 1.0F);
            this.setState(Sniffer.State.SCENTING);
            break;
         case SNIFFING:
            this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0F, 1.0F);
            this.setState(Sniffer.State.SNIFFING);
            break;
         case SEARCHING:
            this.setState(Sniffer.State.SEARCHING);
            break;
         case DIGGING:
            this.setState(Sniffer.State.DIGGING).onDiggingStart();
            break;
         case RISING:
            this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
            this.setState(Sniffer.State.RISING);
            break;
         case FEELING_HAPPY:
            this.playSound(SoundEvents.SNIFFER_HAPPY, 1.0F, 1.0F);
            this.setState(Sniffer.State.FEELING_HAPPY);
            break;
         case IDLING:
            this.setState(Sniffer.State.IDLING);
      }

      return this;
   }

   private Sniffer onDiggingStart() {
      this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + 120);
      this.level.broadcastEntityEvent(this, (byte)63);
      return this;
   }

   public Sniffer onDiggingComplete(boolean var1) {
      if (var1) {
         this.storeExploredPosition(this.getOnPos());
      }

      return this;
   }

   Optional<BlockPos> calculateDigPosition() {
      return IntStream.range(0, 5)
         .mapToObj(var1 -> LandRandomPos.getPos(this, 10 + 2 * var1, 3))
         .filter(Objects::nonNull)
         .map(BlockPos::containing)
         .map(BlockPos::below)
         .filter(this::canDig)
         .findFirst();
   }

   @Override
   protected boolean canRide(Entity var1) {
      return false;
   }

   boolean canDig() {
      return !this.isPanicking() && !this.isBaby() && !this.isInWater() && this.canDig(this.getHeadPosition().below());
   }

   private boolean canDig(BlockPos var1) {
      return this.level.getBlockState(var1).is(BlockTags.SNIFFER_DIGGABLE_BLOCK)
         && this.level.getBlockState(var1.above()).isAir()
         && this.getExploredPositions().noneMatch(var1::equals);
   }

   private void dropSeed() {
      if (!this.level.isClientSide() && this.entityData.get(DATA_DROP_SEED_AT_TICK) == this.tickCount) {
         ItemStack var1 = new ItemStack(Items.TORCHFLOWER_SEEDS);
         BlockPos var2 = this.getHeadPosition();
         ItemEntity var3 = new ItemEntity(this.level, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), var1);
         var3.setDefaultPickUpDelay();
         this.level.addFreshEntity(var3);
         this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
      }
   }

   private Sniffer emitDiggingParticles(AnimationState var1) {
      boolean var2 = var1.getAccumulatedTime() > 1700L && var1.getAccumulatedTime() < 6000L;
      if (var2) {
         BlockState var3 = this.getBlockStateOn();
         BlockPos var4 = this.getHeadPosition();
         if (var3.getRenderShape() != RenderShape.INVISIBLE) {
            for(int var5 = 0; var5 < 30; ++var5) {
               Vec3 var6 = Vec3.atCenterOf(var4);
               this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var3), var6.x, var6.y, var6.z, 0.0, 0.0, 0.0);
            }

            if (this.tickCount % 10 == 0) {
               this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), var3.getSoundType().getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
            }
         }
      }

      return this;
   }

   private Sniffer storeExploredPosition(BlockPos var1) {
      List var2 = this.getExploredPositions().limit(20L).collect(Collectors.toList());
      var2.add(0, var1);
      this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, var2);
      return this;
   }

   private Stream<BlockPos> getExploredPositions() {
      return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
   }

   @Override
   protected void jumpFromGround() {
      super.jumpFromGround();
      double var1 = this.moveControl.getSpeedModifier();
      if (var1 > 0.0) {
         double var3 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var3 < 0.01) {
            this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
         }
      }
   }

   @Override
   public void tick() {
      boolean var1 = this.isInWater() && !this.isUnderWater();
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(var1 ? 0.20000000298023224 : 0.10000000149011612);
      if (!this.isMovingOnLand() && !this.isMovingInWater()) {
         this.panicAnimationState.stop();
         this.walkingAnimationState.stop();
      } else if (this.isPanicking()) {
         this.walkingAnimationState.stop();
         this.panicAnimationState.startIfStopped(this.tickCount);
      } else {
         this.panicAnimationState.stop();
         this.walkingAnimationState.startIfStopped(this.tickCount);
      }

      switch(this.getState()) {
         case SEARCHING:
            this.playSearchingSound();
            break;
         case DIGGING:
            this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
      }

      super.tick();
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      InteractionResult var4 = super.mobInteract(var1, var2);
      if (var4.consumesAction() && this.isFood(var3)) {
         this.level.playSound(null, this, this.getEatingSound(var3), SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level.random, 0.8F, 1.2F));
      }

      return var4;
   }

   private void playSearchingSound() {
      if (this.level.isClientSide() && this.tickCount % 20 == 0) {
         this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_SEARCHING, this.getSoundSource(), 1.0F, 1.0F, false);
      }
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 1.0F);
   }

   @Override
   public SoundEvent getEatingSound(ItemStack var1) {
      return SoundEvents.SNIFFER_EAT;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return Set.of(Sniffer.State.DIGGING, Sniffer.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.SNIFFER_IDLE;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SNIFFER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.SNIFFER_DEATH;
   }

   @Override
   public int getMaxHeadYRot() {
      return 50;
   }

   @Override
   public void setBaby(boolean var1) {
      this.setAge(var1 ? -48000 : 0);
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.SNIFFER.create(var1);
   }

   @Override
   public boolean canMate(Animal var1) {
      if (!(var1 instanceof Sniffer)) {
         return false;
      } else {
         Sniffer var2 = (Sniffer)var1;
         Set var3 = Set.of(Sniffer.State.IDLING, Sniffer.State.SCENTING, Sniffer.State.FEELING_HAPPY);
         return var3.contains(this.getState()) && var3.contains(var2.getState()) && super.canMate(var1);
      }
   }

   @Override
   public AABB getBoundingBoxForCulling() {
      return super.getBoundingBoxForCulling().inflate(0.6000000238418579);
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.SNIFFER_FOOD);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return SnifferAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Sniffer> getBrain() {
      return super.getBrain();
   }

   @Override
   protected Brain.Provider<Sniffer> brainProvider() {
      return Brain.provider(SnifferAi.MEMORY_TYPES, SnifferAi.SENSOR_TYPES);
   }

   @Override
   protected void customServerAiStep() {
      this.level.getProfiler().push("snifferBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().popPush("snifferActivityUpdate");
      SnifferAi.updateActivity(this);
      this.level.getProfiler().pop();
      super.customServerAiStep();
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public static enum State {
      IDLING,
      FEELING_HAPPY,
      SCENTING,
      SNIFFING,
      SEARCHING,
      DIGGING,
      RISING;

      private State() {
      }
   }
}
