package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.jspecify.annotations.Nullable;

public class TransportItemsBetweenContainers extends Behavior<PathfinderMob> {
   public static final int TARGET_INTERACTION_TIME = 60;
   private static final int VISITED_POSITIONS_MEMORY_TIME = 6000;
   private static final int TRANSPORTED_ITEM_MAX_STACK_SIZE = 16;
   private static final int MAX_VISITED_POSITIONS = 10;
   private static final int MAX_UNREACHABLE_POSITIONS = 50;
   private static final int PASSENGER_MOB_TARGET_SEARCH_DISTANCE = 1;
   private static final int IDLE_COOLDOWN = 140;
   private static final double CLOSE_ENOUGH_TO_START_QUEUING_DISTANCE = 3.0;
   private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE = 0.5;
   private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE = 1.0;
   private static final double CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET = 2.0;
   private final float speedModifier;
   private final int horizontalSearchDistance;
   private final int verticalSearchDistance;
   private final Predicate<BlockState> sourceBlockType;
   private final Predicate<BlockState> destinationBlockType;
   private final Predicate<TransportItemTarget> shouldQueueForTarget;
   private final Consumer<PathfinderMob> onStartTravelling;
   private final Map<ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions;
   private @Nullable TransportItemTarget target = null;
   private TransportItemState state;
   private @Nullable ContainerInteractionState interactionState;
   private int ticksSinceReachingTarget;

   public TransportItemsBetweenContainers(float var1, Predicate<BlockState> var2, Predicate<BlockState> var3, int var4, int var5, Map<ContainerInteractionState, OnTargetReachedInteraction> var6, Consumer<PathfinderMob> var7, Predicate<TransportItemTarget> var8) {
      super(ImmutableMap.of(MemoryModuleType.VISITED_BLOCK_POSITIONS, MemoryStatus.REGISTERED, MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS, MemoryStatus.REGISTERED, MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT));
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

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      PathNavigation var6 = var2.getNavigation();
      if (var6 instanceof GroundPathNavigation var5) {
         var5.setCanPathToTargetsBelowSurface(true);
      }

   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return !var2.isLeashed();
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return var2.getBrain().getMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS).isEmpty() && !var2.isPanicking() && !var2.isLeashed();
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      boolean var5 = this.updateInvalidTarget(var1, var2);
      if (this.target == null) {
         this.stop(var1, var2, var3);
      } else if (!var5) {
         if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.QUEUING)) {
            this.onQueuingForTarget(this.target, var1, var2);
         }

         if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
            this.onTravelToTarget(this.target, var1, var2);
         }

         if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.INTERACTING)) {
            this.onReachedTarget(this.target, var1, var2);
         }

      }
   }

   private boolean updateInvalidTarget(ServerLevel var1, PathfinderMob var2) {
      if (!this.hasValidTarget(var1, var2)) {
         this.stopTargetingCurrentTarget(var2);
         Optional var3 = this.getTransportTarget(var1, var2);
         if (var3.isPresent()) {
            this.target = (TransportItemTarget)var3.get();
            this.onStartTravelling(var2);
            this.setVisitedBlockPos(var2, var1, this.target.pos);
            return true;
         } else {
            this.enterCooldownAfterNoMatchingTargetFound(var2);
            return true;
         }
      } else {
         return false;
      }
   }

   private void onQueuingForTarget(TransportItemTarget var1, Level var2, PathfinderMob var3) {
      if (!this.isAnotherMobInteractingWithTarget(var1, var2)) {
         this.resumeTravelling(var3);
      }

   }

   protected void onTravelToTarget(TransportItemTarget var1, Level var2, PathfinderMob var3) {
      if (this.isWithinTargetDistance(3.0, var1, var2, var3, this.getCenterPos(var3)) && this.isAnotherMobInteractingWithTarget(var1, var2)) {
         this.startQueuing(var3);
      } else if (this.isWithinTargetDistance(getInteractionRange(var3), var1, var2, var3, this.getCenterPos(var3))) {
         this.startOnReachedTargetInteraction(var1, var3);
      } else {
         this.walkTowardsTarget(var3);
      }

   }

   private Vec3 getCenterPos(PathfinderMob var1) {
      return this.setMiddleYPosition(var1, var1.position());
   }

   protected void onReachedTarget(TransportItemTarget var1, Level var2, PathfinderMob var3) {
      if (!this.isWithinTargetDistance(2.0, var1, var2, var3, this.getCenterPos(var3))) {
         this.onStartTravelling(var3);
      } else {
         ++this.ticksSinceReachingTarget;
         this.onTargetInteraction(var1, var3);
         if (this.ticksSinceReachingTarget >= 60) {
            this.doReachedTargetInteraction(var3, var1.container, this::pickUpItems, (var2x, var3x) -> this.stopTargetingCurrentTarget(var3), this::putDownItem, (var2x, var3x) -> this.stopTargetingCurrentTarget(var3));
            this.onStartTravelling(var3);
         }
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
      this.ticksSinceReachingTarget = 0;
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

   private Optional<TransportItemTarget> getTransportTarget(ServerLevel var1, PathfinderMob var2) {
      AABB var3 = this.getTargetSearchArea(var2);
      Set var4 = getVisitedPositions(var2);
      Set var5 = getUnreachablePositions(var2);
      List var6 = ChunkPos.rangeClosed(new ChunkPos(var2.blockPosition()), Math.floorDiv(this.getHorizontalSearchDistance(var2), 16) + 1).toList();
      TransportItemTarget var7 = null;
      double var8 = 3.4028234663852886E38;

      for(ChunkPos var11 : var6) {
         LevelChunk var12 = var1.getChunkSource().getChunkNow(var11.x, var11.z);
         if (var12 != null) {
            for(BlockEntity var14 : var12.getBlockEntities().values()) {
               if (var14 instanceof ChestBlockEntity) {
                  ChestBlockEntity var15 = (ChestBlockEntity)var14;
                  double var16 = var15.getBlockPos().distToCenterSqr(var2.position());
                  if (var16 < var8) {
                     TransportItemTarget var18 = this.isTargetValidToPick(var2, var1, var15, var4, var5, var3);
                     if (var18 != null) {
                        var7 = var18;
                        var8 = var16;
                     }
                  }
               }
            }
         }
      }

      return var7 == null ? Optional.empty() : Optional.of(var7);
   }

   private @Nullable TransportItemTarget isTargetValidToPick(PathfinderMob var1, Level var2, BlockEntity var3, Set<GlobalPos> var4, Set<GlobalPos> var5, AABB var6) {
      BlockPos var7 = var3.getBlockPos();
      boolean var8 = var6.contains((double)var7.getX(), (double)var7.getY(), (double)var7.getZ());
      if (!var8) {
         return null;
      } else {
         TransportItemTarget var9 = TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(var3, var2);
         if (var9 == null) {
            return null;
         } else {
            boolean var10 = this.isWantedBlock(var1, var9.state) && !this.isPositionAlreadyVisited(var4, var5, var9, var2) && !this.isContainerLocked(var9);
            return var10 ? var9 : null;
         }
      }
   }

   private boolean isContainerLocked(TransportItemTarget var1) {
      BlockEntity var3 = var1.blockEntity;
      boolean var10000;
      if (var3 instanceof BaseContainerBlockEntity var2) {
         if (var2.isLocked()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private boolean hasValidTarget(Level var1, PathfinderMob var2) {
      boolean var3 = this.target != null && this.isWantedBlock(var2, this.target.state) && this.targetHasNotChanged(var1, this.target);
      if (var3 && !this.isTargetBlocked(var1, this.target)) {
         if (!this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
            return true;
         }

         if (this.hasValidTravellingPath(var1, this.target, var2)) {
            return true;
         }

         this.markVisitedBlockPosAsUnreachable(var2, var1, this.target.pos);
      }

      return false;
   }

   private boolean hasValidTravellingPath(Level var1, TransportItemTarget var2, PathfinderMob var3) {
      Path var4 = var3.getNavigation().getPath() == null ? var3.getNavigation().createPath(var2.pos, 0) : var3.getNavigation().getPath();
      Vec3 var5 = this.getPositionToReachTargetFrom(var4, var3);
      boolean var6 = this.isWithinTargetDistance(getInteractionRange(var3), var2, var1, var3, var5);
      boolean var7 = var4 == null && !var6;
      return var7 || this.targetIsReachableFromPosition(var1, var6, var5, var2, var3);
   }

   private Vec3 getPositionToReachTargetFrom(@Nullable Path var1, PathfinderMob var2) {
      boolean var3 = var1 == null || var1.getEndNode() == null;
      Vec3 var4 = var3 ? var2.position() : var1.getEndNode().asBlockPos().getBottomCenter();
      return this.setMiddleYPosition(var2, var4);
   }

   private Vec3 setMiddleYPosition(PathfinderMob var1, Vec3 var2) {
      return var2.add(0.0, var1.getBoundingBox().getYsize() / 2.0, 0.0);
   }

   private boolean isTargetBlocked(Level var1, TransportItemTarget var2) {
      return ChestBlock.isChestBlockedAt(var1, var2.pos);
   }

   private boolean targetHasNotChanged(Level var1, TransportItemTarget var2) {
      return var2.blockEntity.equals(var1.getBlockEntity(var2.pos));
   }

   private Stream<TransportItemTarget> getConnectedTargets(TransportItemTarget var1, Level var2) {
      if (var1.state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE) != ChestType.SINGLE) {
         TransportItemTarget var3 = TransportItemsBetweenContainers.TransportItemTarget.tryCreatePossibleTarget(ChestBlock.getConnectedBlockPos(var1.pos, var1.state), var2);
         return var3 != null ? Stream.of(var1, var3) : Stream.of(var1);
      } else {
         return Stream.of(var1);
      }
   }

   private AABB getTargetSearchArea(PathfinderMob var1) {
      int var2 = this.getHorizontalSearchDistance(var1);
      return (new AABB(var1.blockPosition())).inflate((double)var2, (double)this.getVerticalSearchDistance(var1), (double)var2);
   }

   private int getHorizontalSearchDistance(PathfinderMob var1) {
      return var1.isPassenger() ? 1 : this.horizontalSearchDistance;
   }

   private int getVerticalSearchDistance(PathfinderMob var1) {
      return var1.isPassenger() ? 1 : this.verticalSearchDistance;
   }

   private static Set<GlobalPos> getVisitedPositions(PathfinderMob var0) {
      return (Set)var0.getBrain().getMemory(MemoryModuleType.VISITED_BLOCK_POSITIONS).orElse(Set.of());
   }

   private static Set<GlobalPos> getUnreachablePositions(PathfinderMob var0) {
      return (Set)var0.getBrain().getMemory(MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS).orElse(Set.of());
   }

   private boolean isPositionAlreadyVisited(Set<GlobalPos> var1, Set<GlobalPos> var2, TransportItemTarget var3, Level var4) {
      return this.getConnectedTargets(var3, var4).map((var1x) -> new GlobalPos(var4.dimension(), var1x.pos)).anyMatch((var2x) -> var1.contains(var2x) || var2.contains(var2x));
   }

   private static boolean hasFinishedPath(PathfinderMob var0) {
      return var0.getNavigation().getPath() != null && var0.getNavigation().getPath().isDone();
   }

   protected void setVisitedBlockPos(PathfinderMob var1, Level var2, BlockPos var3) {
      HashSet var4 = new HashSet(getVisitedPositions(var1));
      var4.add(new GlobalPos(var2.dimension(), var3));
      if (var4.size() > 10) {
         this.enterCooldownAfterNoMatchingTargetFound(var1);
      } else {
         var1.getBrain().setMemoryWithExpiry(MemoryModuleType.VISITED_BLOCK_POSITIONS, var4, 6000L);
      }

   }

   protected void markVisitedBlockPosAsUnreachable(PathfinderMob var1, Level var2, BlockPos var3) {
      HashSet var4 = new HashSet(getVisitedPositions(var1));
      var4.remove(new GlobalPos(var2.dimension(), var3));
      HashSet var5 = new HashSet(getUnreachablePositions(var1));
      var5.add(new GlobalPos(var2.dimension(), var3));
      if (var5.size() > 50) {
         this.enterCooldownAfterNoMatchingTargetFound(var1);
      } else {
         var1.getBrain().setMemoryWithExpiry(MemoryModuleType.VISITED_BLOCK_POSITIONS, var4, 6000L);
         var1.getBrain().setMemoryWithExpiry(MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS, var5, 6000L);
      }

   }

   private boolean isWantedBlock(PathfinderMob var1, BlockState var2) {
      return isPickingUpItems(var1) ? this.sourceBlockType.test(var2) : this.destinationBlockType.test(var2);
   }

   private static double getInteractionRange(PathfinderMob var0) {
      return hasFinishedPath(var0) ? 1.0 : 0.5;
   }

   private boolean isWithinTargetDistance(double var1, TransportItemTarget var3, Level var4, PathfinderMob var5, Vec3 var6) {
      AABB var7 = var5.getBoundingBox();
      AABB var8 = AABB.ofSize(var6, var7.getXsize(), var7.getYsize(), var7.getZsize());
      return var3.state.getCollisionShape(var4, var3.pos).bounds().inflate(var1, 0.5, var1).move(var3.pos).intersects(var8);
   }

   private boolean targetIsReachableFromPosition(Level var1, boolean var2, Vec3 var3, TransportItemTarget var4, PathfinderMob var5) {
      return var2 && this.canSeeAnyTargetSide(var4, var1, var5, var3);
   }

   private boolean canSeeAnyTargetSide(TransportItemTarget var1, Level var2, PathfinderMob var3, Vec3 var4) {
      Vec3 var5 = var1.pos.getCenter();
      return Direction.stream().map((var1x) -> var5.add(0.5 * (double)var1x.getStepX(), 0.5 * (double)var1x.getStepY(), 0.5 * (double)var1x.getStepZ())).map((var3x) -> var2.clip(new ClipContext(var4, var3x, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var3))).anyMatch((var1x) -> var1x.getType() == HitResult.Type.BLOCK && var1x.getBlockPos().equals(var1.pos));
   }

   private boolean isAnotherMobInteractingWithTarget(TransportItemTarget var1, Level var2) {
      return this.getConnectedTargets(var1, var2).anyMatch(this.shouldQueueForTarget);
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
      var2.setChanged();
      this.clearMemoriesAfterMatchingTargetFound(var1);
   }

   private void putDownItem(PathfinderMob var1, Container var2) {
      ItemStack var3 = addItemsToContainer(var1, var2);
      var2.setChanged();
      var1.setItemSlot(EquipmentSlot.MAINHAND, var3);
      if (var3.isEmpty()) {
         this.clearMemoriesAfterMatchingTargetFound(var1);
      } else {
         this.stopTargetingCurrentTarget(var1);
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

   protected void stopTargetingCurrentTarget(PathfinderMob var1) {
      this.ticksSinceReachingTarget = 0;
      this.target = null;
      var1.getNavigation().stop();
      var1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }

   protected void clearMemoriesAfterMatchingTargetFound(PathfinderMob var1) {
      this.stopTargetingCurrentTarget(var1);
      var1.getBrain().eraseMemory(MemoryModuleType.VISITED_BLOCK_POSITIONS);
      var1.getBrain().eraseMemory(MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS);
   }

   private void enterCooldownAfterNoMatchingTargetFound(PathfinderMob var1) {
      this.stopTargetingCurrentTarget(var1);
      var1.getBrain().setMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, 140);
      var1.getBrain().eraseMemory(MemoryModuleType.VISITED_BLOCK_POSITIONS);
      var1.getBrain().eraseMemory(MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS);
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      this.onStartTravelling(var2);
      PathNavigation var6 = var2.getNavigation();
      if (var6 instanceof GroundPathNavigation var5) {
         var5.setCanPathToTargetsBelowSurface(false);
      }

   }

   private void stopInPlace(PathfinderMob var1) {
      var1.getNavigation().stop();
      var1.setXxa(0.0F);
      var1.setYya(0.0F);
      var1.setSpeed(0.0F);
      var1.setDeltaMovement(0.0, var1.getDeltaMovement().y, 0.0);
   }

   // $FF: synthetic method
   protected boolean canStillUse(final ServerLevel var1, final LivingEntity var2, final long var3) {
      return this.canStillUse(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
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

      public static @Nullable TransportItemTarget tryCreatePossibleTarget(BlockEntity var0, Level var1) {
         BlockPos var2 = var0.getBlockPos();
         BlockState var3 = var0.getBlockState();
         Container var4 = getBlockEntityContainer(var0, var3, var1, var2);
         return var4 != null ? new TransportItemTarget(var2, var4, var0, var3) : null;
      }

      public static @Nullable TransportItemTarget tryCreatePossibleTarget(BlockPos var0, Level var1) {
         BlockEntity var2 = var1.getBlockEntity(var0);
         return var2 == null ? null : tryCreatePossibleTarget(var2, var1);
      }

      private static @Nullable Container getBlockEntityContainer(BlockEntity var0, BlockState var1, Level var2, BlockPos var3) {
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
