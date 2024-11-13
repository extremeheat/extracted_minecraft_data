package net.minecraft.world.entity.animal.sniffer;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class Sniffer extends Animal {
   private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
   private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
   private static final int DIGGING_PARTICLES_AMOUNT = 30;
   private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
   private static final int SNIFFER_BABY_AGE_TICKS = 48000;
   private static final float DIGGING_BB_HEIGHT_OFFSET = 0.4F;
   private static final EntityDimensions DIGGING_DIMENSIONS;
   private static final EntityDataAccessor<State> DATA_STATE;
   private static final EntityDataAccessor<Integer> DATA_DROP_SEED_AT_TICK;
   public final AnimationState feelingHappyAnimationState = new AnimationState();
   public final AnimationState scentingAnimationState = new AnimationState();
   public final AnimationState sniffingAnimationState = new AnimationState();
   public final AnimationState diggingAnimationState = new AnimationState();
   public final AnimationState risingAnimationState = new AnimationState();

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 0.10000000149011612).add(Attributes.MAX_HEALTH, 14.0);
   }

   public Sniffer(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.getNavigation().setCanFloat(true);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_CAUTIOUS, -1.0F);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_STATE, Sniffer.State.IDLING);
      var1.define(DATA_DROP_SEED_AT_TICK, 0);
   }

   public void onPathfindingStart() {
      super.onPathfindingStart();
      if (this.isOnFire() || this.isInWater()) {
         this.setPathfindingMalus(PathType.WATER, 0.0F);
      }

   }

   public void onPathfindingDone() {
      this.setPathfindingMalus(PathType.WATER, -1.0F);
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.getState() == Sniffer.State.DIGGING ? DIGGING_DIMENSIONS.scale(this.getAgeScale()) : super.getDefaultDimensions(var1);
   }

   public boolean isSearching() {
      return this.getState() == Sniffer.State.SEARCHING;
   }

   public boolean isTempted() {
      return (Boolean)this.brain.getMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
   }

   public boolean canSniff() {
      return !this.isTempted() && !this.isPanicking() && !this.isInWater() && !this.isInLove() && this.onGround() && !this.isPassenger() && !this.isLeashed();
   }

   public boolean canPlayDiggingSound() {
      return this.getState() == Sniffer.State.DIGGING || this.getState() == Sniffer.State.SEARCHING;
   }

   private BlockPos getHeadBlock() {
      Vec3 var1 = this.getHeadPosition();
      return BlockPos.containing(var1.x(), this.getY() + 0.20000000298023224, var1.z());
   }

   private Vec3 getHeadPosition() {
      return this.position().add(this.getForward().scale(2.25));
   }

   private State getState() {
      return (State)this.entityData.get(DATA_STATE);
   }

   private Sniffer setState(State var1) {
      this.entityData.set(DATA_STATE, var1);
      return this;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_STATE.equals(var1)) {
         State var2 = this.getState();
         this.resetAnimations();
         switch (var2.ordinal()) {
            case 1:
               this.feelingHappyAnimationState.startIfStopped(this.tickCount);
               break;
            case 2:
               this.scentingAnimationState.startIfStopped(this.tickCount);
               break;
            case 3:
               this.sniffingAnimationState.startIfStopped(this.tickCount);
            case 4:
            default:
               break;
            case 5:
               this.diggingAnimationState.startIfStopped(this.tickCount);
               break;
            case 6:
               this.risingAnimationState.startIfStopped(this.tickCount);
         }

         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   private void resetAnimations() {
      this.diggingAnimationState.stop();
      this.sniffingAnimationState.stop();
      this.risingAnimationState.stop();
      this.feelingHappyAnimationState.stop();
      this.scentingAnimationState.stop();
   }

   public Sniffer transitionTo(State var1) {
      switch (var1.ordinal()) {
         case 0:
            this.setState(Sniffer.State.IDLING);
            break;
         case 1:
            this.playSound(SoundEvents.SNIFFER_HAPPY, 1.0F, 1.0F);
            this.setState(Sniffer.State.FEELING_HAPPY);
            break;
         case 2:
            this.setState(Sniffer.State.SCENTING).onScentingStart();
            break;
         case 3:
            this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0F, 1.0F);
            this.setState(Sniffer.State.SNIFFING);
            break;
         case 4:
            this.setState(Sniffer.State.SEARCHING);
            break;
         case 5:
            this.setState(Sniffer.State.DIGGING).onDiggingStart();
            break;
         case 6:
            this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
            this.setState(Sniffer.State.RISING);
      }

      return this;
   }

   private Sniffer onScentingStart() {
      this.playSound(SoundEvents.SNIFFER_SCENTING, 1.0F, this.isBaby() ? 1.3F : 1.0F);
      return this;
   }

   private Sniffer onDiggingStart() {
      this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + 120);
      this.level().broadcastEntityEvent(this, (byte)63);
      return this;
   }

   public Sniffer onDiggingComplete(boolean var1) {
      if (var1) {
         this.storeExploredPosition(this.getOnPos());
      }

      return this;
   }

   Optional<BlockPos> calculateDigPosition() {
      return IntStream.range(0, 5).mapToObj((var1) -> LandRandomPos.getPos(this, 10 + 2 * var1, 3)).filter(Objects::nonNull).map(BlockPos::containing).filter((var1) -> this.level().getWorldBorder().isWithinBounds(var1)).map(BlockPos::below).filter(this::canDig).findFirst();
   }

   boolean canDig() {
      return !this.isPanicking() && !this.isTempted() && !this.isBaby() && !this.isInWater() && this.onGround() && !this.isPassenger() && this.canDig(this.getHeadBlock().below());
   }

   private boolean canDig(BlockPos var1) {
      return this.level().getBlockState(var1).is(BlockTags.SNIFFER_DIGGABLE_BLOCK) && this.getExploredPositions().noneMatch((var2) -> GlobalPos.of(this.level().dimension(), var1).equals(var2)) && (Boolean)Optional.ofNullable(this.getNavigation().createPath(var1, 1)).map(Path::canReach).orElse(false);
   }

   private void dropSeed() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         if ((Integer)this.entityData.get(DATA_DROP_SEED_AT_TICK) == this.tickCount) {
            BlockPos var3 = this.getHeadBlock();
            this.dropFromGiftLootTable(var1, BuiltInLootTables.SNIFFER_DIGGING, (var2x, var3x) -> {
               ItemEntity var4 = new ItemEntity(this.level(), (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), var3x);
               var4.setDefaultPickUpDelay();
               var2x.addFreshEntity(var4);
            });
            this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
            return;
         }
      }

   }

   private Sniffer emitDiggingParticles(AnimationState var1) {
      boolean var2 = var1.getTimeInMillis((float)this.tickCount) > 1700L && var1.getTimeInMillis((float)this.tickCount) < 6000L;
      if (var2) {
         BlockPos var3 = this.getHeadBlock();
         BlockState var4 = this.level().getBlockState(var3.below());
         if (var4.getRenderShape() != RenderShape.INVISIBLE) {
            for(int var5 = 0; var5 < 30; ++var5) {
               Vec3 var6 = Vec3.atCenterOf(var3).add(0.0, -0.6499999761581421, 0.0);
               this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var4), var6.x, var6.y, var6.z, 0.0, 0.0, 0.0);
            }

            if (this.tickCount % 10 == 0) {
               this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), var4.getSoundType().getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
            }
         }
      }

      if (this.tickCount % 10 == 0) {
         this.level().gameEvent(GameEvent.ENTITY_ACTION, this.getHeadBlock(), GameEvent.Context.of((Entity)this));
      }

      return this;
   }

   private Sniffer storeExploredPosition(BlockPos var1) {
      List var2 = (List)this.getExploredPositions().limit(20L).collect(Collectors.toList());
      var2.add(0, GlobalPos.of(this.level().dimension(), var1));
      this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, var2);
      return this;
   }

   private Stream<GlobalPos> getExploredPositions() {
      return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      double var1 = this.moveControl.getSpeedModifier();
      if (var1 > 0.0) {
         double var3 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var3 < 0.01) {
            this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
         }
      }

   }

   public void spawnChildFromBreeding(ServerLevel var1, Animal var2) {
      ItemStack var3 = new ItemStack(Items.SNIFFER_EGG);
      ItemEntity var4 = new ItemEntity(var1, this.position().x(), this.position().y(), this.position().z(), var3);
      var4.setDefaultPickUpDelay();
      this.finalizeSpawnChildFromBreeding(var1, var2, (AgeableMob)null);
      this.playSound(SoundEvents.SNIFFER_EGG_PLOP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
      var1.addFreshEntity(var4);
   }

   public void die(DamageSource var1) {
      this.transitionTo(Sniffer.State.IDLING);
      super.die(var1);
   }

   public void tick() {
      switch (this.getState().ordinal()) {
         case 4 -> this.playSearchingSound();
         case 5 -> this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
      }

      super.tick();
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      boolean var4 = this.isFood(var3);
      InteractionResult var5 = super.mobInteract(var1, var2);
      if (var5.consumesAction() && var4) {
         this.playEatingSound();
      }

      return var5;
   }

   protected void playEatingSound() {
      this.level().playSound((Player)null, (Entity)this, SoundEvents.SNIFFER_EAT, SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level().random, 0.8F, 1.2F));
   }

   private void playSearchingSound() {
      if (this.level().isClientSide() && this.tickCount % 20 == 0) {
         this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_SEARCHING, this.getSoundSource(), 1.0F, 1.0F, false);
      }

   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 1.0F);
   }

   protected SoundEvent getAmbientSound() {
      return Set.of(Sniffer.State.DIGGING, Sniffer.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.SNIFFER_IDLE;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SNIFFER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SNIFFER_DEATH;
   }

   public int getMaxHeadYRot() {
      return 50;
   }

   public void setBaby(boolean var1) {
      this.setAge(var1 ? -48000 : 0);
   }

   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.SNIFFER.create(var1, EntitySpawnReason.BREEDING);
   }

   public boolean canMate(Animal var1) {
      if (!(var1 instanceof Sniffer var2)) {
         return false;
      } else {
         Set var3 = Set.of(Sniffer.State.IDLING, Sniffer.State.SCENTING, Sniffer.State.FEELING_HAPPY);
         return var3.contains(this.getState()) && var3.contains(var2.getState()) && super.canMate(var1);
      }
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.SNIFFER_FOOD);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return SnifferAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Sniffer> getBrain() {
      return super.getBrain();
   }

   protected Brain.Provider<Sniffer> brainProvider() {
      return Brain.<Sniffer>provider(SnifferAi.MEMORY_TYPES, SnifferAi.SENSOR_TYPES);
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("snifferBrain");
      this.getBrain().tick(var1, this);
      var2.popPush("snifferActivityUpdate");
      SnifferAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   static {
      DIGGING_DIMENSIONS = EntityDimensions.scalable(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - 0.4F).withEyeHeight(0.81F);
      DATA_STATE = SynchedEntityData.<State>defineId(Sniffer.class, EntityDataSerializers.SNIFFER_STATE);
      DATA_DROP_SEED_AT_TICK = SynchedEntityData.<Integer>defineId(Sniffer.class, EntityDataSerializers.INT);
   }

   public static enum State {
      IDLING(0),
      FEELING_HAPPY(1),
      SCENTING(2),
      SNIFFING(3),
      SEARCHING(4),
      DIGGING(5),
      RISING(6);

      public static final IntFunction<State> BY_ID = ByIdMap.<State>continuous(State::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, State> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, State::id);
      private final int id;

      private State(final int var3) {
         this.id = var3;
      }

      public int id() {
         return this.id;
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{IDLING, FEELING_HAPPY, SCENTING, SNIFFING, SEARCHING, DIGGING, RISING};
      }
   }
}
