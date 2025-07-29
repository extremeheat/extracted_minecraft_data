package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;

public class TransportItemsBetweenContainers extends Behavior<PathfinderMob> {
   public static final int TARGET_INTERACTION_TIME = 60;
   private static final int VISITED_POSITIONS_MEMORY_TIME = 6000;
   private static final int TRANSPORTED_ITEM_MAX_STACK_SIZE = 16;
   private static final int MAX_VISITED_POSITIONS = 10;
   private static final int IDLE_COOLDOWN = 140;
   private static final double CLOSE_ENOUGH_TO_START_QUEUING_DISTANCE = 3.0;
   private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE = 0.2;
   private static final double CLOSE_ENOUGH_TO_GIVE_UP_UNREACHABLE_TARGET_DISTANCE = 0.5;
   private final float speedModifier;
   private final int horizontalSearchDistance;
   private final int verticalSearchDistance;
   private final Predicate<BlockState> sourceBlockType;
   private final Predicate<BlockState> destinationBlockType;
   private final Predicate<TransportItemTarget> shouldQueueForTarget;
   private final Consumer<PathfinderMob> onStartTravelling;
   private final Map<ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions;
   @Nullable
   private TransportItemTarget target = null;
   private TransportItemState state;
   @Nullable
   private ContainerInteractionState interactionState;
   private int ticksSinceReachingTarget;

   public TransportItemsBetweenContainers(float var1, Predicate<BlockState> var2, Predicate<BlockState> var3, int var4, int var5, Map<ContainerInteractionState, OnTargetReachedInteraction> var6, Consumer<PathfinderMob> var7, Predicate<TransportItemTarget> var8) {
      super(ImmutableMap.of(MemoryModuleType.VISITED_BLOCK_POSITIONS, MemoryStatus.REGISTERED, MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
      this.sourceBlockType = var2;
      this.destinationBlockType = var3;
      this.horizontalSearchDistance = var4;
      this.verticalSearchDistance = var5;
      this.onStartTravelling = var7;
      this.shouldQueueForTarget = var8;
      this.onTargetInteractionActions = var6;
      this.state = TransportItemsBetweenContainers.TransportItemState.TRAVELLING;
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return var2.getBrain().getMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS).isEmpty() && !var2.isPanicking();
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      this.tryEnsureTargetPosExists(var1, var2);
      if (this.target == null) {
         this.stop(var1, var2, var3);
      } else {
         if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.QUEUING)) {
            this.onQueuingForTarget(this.target, var1, var2);
         }

         if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
            this.onTravelToTarget(this.target, var1, var2);
         }

         if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.INTERACTING)) {
            this.onReachedTarget(this.target, var2);
         }

      }
   }

   private void tryEnsureTargetPosExists(ServerLevel var1, PathfinderMob var2) {
      if (!this.hasValidTarget(var1, var2)) {
         this.stopTargetingCurrentTarget();
         Optional var3 = this.getTargetBlockPosition(var1, var2);
         if (var3.isPresent()) {
            this.target = (TransportItemTarget)var3.get();
            this.onStartTravelling(var2);
            this.setVisitedBlockPos(var2, var1, this.target.pos);
            this.walkTowardsTarget(var2);
         } else {
            this.enterCooldownAfterNoMatchingTargetFound(var2);
         }
      }

   }

   private void onQueuingForTarget(TransportItemTarget var1, Level var2, PathfinderMob var3) {
      if (!this.isAnotherMobInteractingWithTarget(var1, var2)) {
         this.resumeTravelling(var3);
      }

   }

   protected void onTravelToTarget(TransportItemTarget var1, Level var2, PathfinderMob var3) {
      if (this.isWithinTargetDistance(3.0, var1, var2, var3, false) && this.isAnotherMobInteractingWithTarget(var1, var2)) {
         this.startQueuing(var3);
      } else if (this.isWithinTargetDistance(0.2, var1, var2, var3, false)) {
         this.startOnReachedTargetInteraction(var1, var3);
      } else if (this.shouldGiveUpOnCurrentTarget(var1, var2, var3)) {
         this.stopTargetingCurrentTarget();
      } else {
         this.walkTowardsTarget(var3);
      }

   }

   protected void onReachedTarget(TransportItemTarget var1, PathfinderMob var2) {
      ++this.ticksSinceReachingTarget;
      this.onTargetInteraction(var1, var2);
      if (this.ticksSinceReachingTarget >= 60) {
         this.doReachedTargetInteraction(var2, var1.container, this::pickUpItems, (var1x, var2x) -> this.stopTargetingCurrentTarget(), this::putDownItem, (var1x, var2x) -> this.stopTargetingCurrentTarget());
         this.onStartTravelling(var2);
      }

   }

   private void startQueuing(PathfinderMob var1) {
      this.stopInPlace(var1);
      this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.QUEUING);
   }

   private void resumeTravelling(PathfinderMob var1) {
      this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
      this.walkTowardsTarget(var1);
   }

   private void walkTowardsTarget(PathfinderMob var1) {
      if (this.target != null) {
         BehaviorUtils.setWalkAndLookTargetMemories(var1, (BlockPos)this.target.pos, this.speedModifier, 0);
      }

   }

   private void startOnReachedTargetInteraction(TransportItemTarget var1, PathfinderMob var2) {
      this.doReachedTargetInteraction(var2, var1.container, this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM), this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM), this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM), this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM));
      this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.INTERACTING);
   }

   private void onStartTravelling(PathfinderMob var1) {
      this.onStartTravelling.accept(var1);
      this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
      this.interactionState = null;
   }

   private BiConsumer<PathfinderMob, Container> onReachedInteraction(ContainerInteractionState var1) {
      return (var2, var3) -> this.setInteractionState(var1);
   }

   private void setTransportingState(TransportItemState var1) {
      this.state = var1;
   }

   private void setInteractionState(ContainerInteractionState var1) {
      this.interactionState = var1;
   }

   private void onTargetInteraction(TransportItemTarget var1, PathfinderMob var2) {
      var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(var1.pos));
      this.stopInPlace(var2);
      if (this.interactionState != null) {
         Optional.ofNullable((OnTargetReachedInteraction)this.onTargetInteractionActions.get(this.interactionState)).ifPresent((var3) -> var3.accept(var2, var1, this.ticksSinceReachingTarget));
      }

   }

   private void doReachedTargetInteraction(PathfinderMob var1, Container var2, BiConsumer<PathfinderMob, Container> var3, BiConsumer<PathfinderMob, Container> var4, BiConsumer<PathfinderMob, Container> var5, BiConsumer<PathfinderMob, Container> var6) {
      if (isPickingUpItems(var1)) {
         if (matchesGettingItemsRequirement(var2)) {
            var3.accept(var1, var2);
         } else {
            var4.accept(var1, var2);
         }
      } else if (matchesLeavingItemsRequirement(var1, var2)) {
         var5.accept(var1, var2);
      } else {
         var6.accept(var1, var2);
      }

   }

   private Optional<TransportItemTarget> getTargetBlockPosition(ServerLevel var1, PathfinderMob var2) {
      AABB var3 = this.getTargetSearchArea(var2);
      Set var4 = getVisitedPositions(var2);
      BlockPos var5 = var2.blockPosition();
      return ChunkPos.rangeClosed(new ChunkPos(var5), Math.floorDiv(this.horizontalSearchDistance, 16) + 1).flatMap((var1x) -> var1.getChunk(var1x.x, var1x.z).getBlockEntities().entrySet().stream()).flatMap((var5x) -> this.isTargetValidToPick(var2, var1, (BlockPos)var5x.getKey(), (BlockEntity)var5x.getValue(), var4, var3).stream()).min(Comparator.comparing((var1x) -> var1x.pos.distToCenterSqr((double)var5.getX(), (double)var5.getY(), (double)var5.getZ())));
   }

   private Optional<TransportItemTarget> isTargetValidToPick(PathfinderMob var1, Level var2, BlockPos var3, BlockEntity var4, Set<GlobalPos> var5, AABB var6) {
      return TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(var3, var4, var2).filter((var5x) -> this.isWantedBlock(var1, var5x.state) && !this.isPositionAlreadyVisited(var5, var5x, var2) && var6.contains((double)var5x.pos.getX(), (double)var5x.pos.getY(), (double)var5x.pos.getZ()));
   }

   private boolean hasValidTarget(Level var1, PathfinderMob var2) {
      return this.target != null && this.isWantedBlock(var2, this.target.state) && this.targetHasNotChanged(var1, this.target);
   }

   private boolean targetHasNotChanged(Level var1, TransportItemTarget var2) {
      return var2.blockEntity.equals(var1.getBlockEntity(var2.pos)) && var2.state.getBlock().equals(var1.getBlockState(var2.pos).getBlock());
   }

   private List<TransportItemTarget> getConnectedTargets(TransportItemTarget var1, Level var2) {
      if (var1.state.hasProperty(ChestBlock.TYPE) && var1.state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
         Optional var3 = TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(ChestBlock.getConnectedBlockPos(var1.pos, var1.state), var2);
         return (List)var3.map((var1x) -> List.of(var1, var1x)).orElseGet(() -> List.of(var1));
      } else {
         return List.of(var1);
      }
   }

   private AABB getTargetSearchArea(PathfinderMob var1) {
      return (new AABB(var1.blockPosition())).inflate((double)this.horizontalSearchDistance, (double)this.verticalSearchDistance, (double)this.horizontalSearchDistance);
   }

   private static Set<GlobalPos> getVisitedPositions(PathfinderMob var0) {
      return (Set)var0.getBrain().getMemory(MemoryModuleType.VISITED_BLOCK_POSITIONS).map(HashSet::new).orElseGet(HashSet::new);
   }

   private boolean isPositionAlreadyVisited(Set<GlobalPos> var1, TransportItemTarget var2, Level var3) {
      Stream var10000 = this.getConnectedTargets(var2, var3).stream().map((var1x) -> new GlobalPos(var3.dimension(), var1x.pos));
      Objects.requireNonNull(var1);
      return var10000.anyMatch(var1::contains);
   }

   protected void setVisitedBlockPos(PathfinderMob var1, Level var2, BlockPos var3) {
      Set var4 = getVisitedPositions(var1);
      var4.add(new GlobalPos(var2.dimension(), var3));
      if (var4.size() > 10) {
         this.enterCooldownAfterNoMatchingTargetFound(var1);
      } else {
         var1.getBrain().setMemoryWithExpiry(MemoryModuleType.VISITED_BLOCK_POSITIONS, var4, 6000L);
      }

   }

   private boolean isWantedBlock(PathfinderMob var1, BlockState var2) {
      return isPickingUpItems(var1) ? this.sourceBlockType.test(var2) : this.destinationBlockType.test(var2);
   }

   private boolean isWithinTargetDistance(double var1, TransportItemTarget var3, Level var4, PathfinderMob var5, boolean var6) {
      return var3.state.getCollisionShape(var4, var3.pos).bounds().inflate(var1, 0.0, var1).move(new Vec3(0.0, var6 ? -1.0 : 0.5, 0.0)).move(var3.pos).intersects(var5.getBoundingBox());
   }

   private boolean shouldGiveUpOnCurrentTarget(TransportItemTarget var1, Level var2, PathfinderMob var3) {
      Path var4 = var3.getNavigation().getPath();
      boolean var5 = var4 != null && var4.isDone() && !var4.canReach();
      return var5 || this.isWithinTargetDistance(0.5, var1, var2, var3, true);
   }

   private boolean isAnotherMobInteractingWithTarget(TransportItemTarget var1, Level var2) {
      return this.getConnectedTargets(var1, var2).stream().anyMatch(this.shouldQueueForTarget);
   }

   private static boolean isPickingUpItems(PathfinderMob var0) {
      return var0.getMainHandItem().isEmpty();
   }

   private static boolean matchesGettingItemsRequirement(Container var0) {
      return !var0.isEmpty();
   }

   private static boolean matchesLeavingItemsRequirement(PathfinderMob var0, Container var1) {
      return var1.isEmpty() || hasItemMatchingHandItem(var0, var1);
   }

   private static boolean hasItemMatchingHandItem(PathfinderMob var0, Container var1) {
      ItemStack var2 = var0.getMainHandItem();

      for(ItemStack var4 : var1) {
         if (ItemStack.isSameItem(var4, var2)) {
            return true;
         }
      }

      return false;
   }

   private void pickUpItems(PathfinderMob var1, Container var2) {
      var1.setItemSlot(EquipmentSlot.MAINHAND, pickupItemFromContainer(var2));
      var1.setGuaranteedDrop(EquipmentSlot.MAINHAND);
      this.clearMemoriesAfterMatchingTargetFound(var1);
   }

   private void putDownItem(PathfinderMob var1, Container var2) {
      ItemStack var3 = addItemsToContainer(var1, var2);
      var1.setItemSlot(EquipmentSlot.MAINHAND, var3);
      if (var3.isEmpty()) {
         this.clearMemoriesAfterMatchingTargetFound(var1);
      } else {
         this.stopTargetingCurrentTarget();
      }

   }

   private static ItemStack pickupItemFromContainer(Container var0) {
      int var1 = 0;

      for(ItemStack var3 : var0) {
         if (!var3.isEmpty()) {
            int var4 = Math.min(var3.getCount(), 16);
            return var0.removeItem(var1, var4);
         }

         ++var1;
      }

      return ItemStack.EMPTY;
   }

   private static ItemStack addItemsToContainer(PathfinderMob var0, Container var1) {
      int var2 = 0;
      ItemStack var3 = var0.getMainHandItem();

      for(ItemStack var5 : var1) {
         if (var5.isEmpty()) {
            var1.setItem(var2, var3);
            return ItemStack.EMPTY;
         }

         if (ItemStack.isSameItemSameComponents(var5, var3) && var5.getCount() < var5.getMaxStackSize()) {
            int var6 = var5.getMaxStackSize() - var5.getCount();
            int var7 = Math.min(var6, var3.getCount());
            var5.setCount(var5.getCount() + var7);
            var3.setCount(var3.getCount() - var6);
            var1.setItem(var2, var5);
            if (var3.isEmpty()) {
               return ItemStack.EMPTY;
            }
         }

         ++var2;
      }

      return var3;
   }

   protected void stopTargetingCurrentTarget() {
      this.ticksSinceReachingTarget = 0;
      this.target = null;
   }

   protected void clearMemoriesAfterMatchingTargetFound(PathfinderMob var1) {
      var1.getBrain().eraseMemory(MemoryModuleType.VISITED_BLOCK_POSITIONS);
      this.stopTargetingCurrentTarget();
   }

   private void enterCooldownAfterNoMatchingTargetFound(PathfinderMob var1) {
      this.stopTargetingCurrentTarget();
      var1.getBrain().eraseMemory(MemoryModuleType.VISITED_BLOCK_POSITIONS);
      var1.getBrain().setMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, 140);
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      this.onStartTravelling(var2);
   }

   private void stopInPlace(PathfinderMob var1) {
      var1.getNavigation().stop();
      var1.setXxa(0.0F);
      var1.setYya(0.0F);
      var1.setSpeed(0.0F);
      var1.setDeltaMovement(0.0, 0.0, 0.0);
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.tick(var1, (PathfinderMob)var2, var3);
   }

   public static enum TransportItemState {
      TRAVELLING,
      QUEUING,
      INTERACTING;

      private TransportItemState() {
      }

      // $FF: synthetic method
      private static TransportItemState[] $values() {
         return new TransportItemState[]{TRAVELLING, QUEUING, INTERACTING};
      }
   }

   public static enum ContainerInteractionState {
      PICKUP_ITEM,
      PICKUP_NO_ITEM,
      PLACE_ITEM,
      PLACE_NO_ITEM;

      private ContainerInteractionState() {
      }

      // $FF: synthetic method
      private static ContainerInteractionState[] $values() {
         return new ContainerInteractionState[]{PICKUP_ITEM, PICKUP_NO_ITEM, PLACE_ITEM, PLACE_NO_ITEM};
      }
   }

   public static record TransportItemTarget(BlockPos pos, Container container, BlockEntity blockEntity, BlockState state) {
      final BlockPos pos;
      final Container container;
      final BlockEntity blockEntity;
      final BlockState state;

      public TransportItemTarget(BlockPos var1, Container var2, BlockEntity var3, BlockState var4) {
         super();
         this.pos = var1;
         this.container = var2;
         this.blockEntity = var3;
         this.state = var4;
      }

      public static Optional<TransportItemTarget> tryCreatePossibleTarget(BlockPos var0, BlockEntity var1, Level var2) {
         BlockState var3 = var2.getBlockState(var0);
         Container var4 = getBlockEntityContainer(var1, var3, var2, var0);
         return var4 != null ? Optional.of(new TransportItemTarget(var0, var4, var1, var3)) : Optional.empty();
      }

      public static Optional<TransportItemTarget> tryCreatePossibleTarget(BlockPos var0, Level var1) {
         BlockEntity var2 = var1.getBlockEntity(var0);
         return var2 == null ? Optional.empty() : tryCreatePossibleTarget(var0, var2, var1);
      }

      @Nullable
      private static Container getBlockEntityContainer(BlockEntity var0, BlockState var1, Level var2, BlockPos var3) {
         Block var6 = var1.getBlock();
         if (var6 instanceof ChestBlock var4) {
            return ChestBlock.getContainer(var4, var1, var2, var3, false);
         } else if (var0 instanceof Container var5) {
            return var5;
         } else {
            return null;
         }
      }
   }

   @FunctionalInterface
   public interface OnTargetReachedInteraction extends TriConsumer<PathfinderMob, TransportItemTarget, Integer> {
   }
}
