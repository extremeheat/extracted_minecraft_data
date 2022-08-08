package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class PrepareRamNearestTarget<E extends PathfinderMob> extends Behavior<E> {
   public static final int TIME_OUT_DURATION = 160;
   private final ToIntFunction<E> getCooldownOnFail;
   private final int minRamDistance;
   private final int maxRamDistance;
   private final float walkSpeed;
   private final TargetingConditions ramTargeting;
   private final int ramPrepareTime;
   private final Function<E, SoundEvent> getPrepareRamSound;
   private Optional<Long> reachedRamPositionTimestamp = Optional.empty();
   private Optional<RamCandidate> ramCandidate = Optional.empty();

   public PrepareRamNearestTarget(ToIntFunction<E> var1, int var2, int var3, float var4, TargetingConditions var5, int var6, Function<E, SoundEvent> var7) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT), 160);
      this.getCooldownOnFail = var1;
      this.minRamDistance = var2;
      this.maxRamDistance = var3;
      this.walkSpeed = var4;
      this.ramTargeting = var5;
      this.ramPrepareTime = var6;
      this.getPrepareRamSound = var7;
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((var2x) -> {
         return var2x.findClosest((var2xx) -> {
            return this.ramTargeting.test(var2, var2xx);
         });
      }).ifPresent((var2x) -> {
         this.chooseRamPosition(var2, var2x);
      });
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      Brain var5 = var2.getBrain();
      if (!var5.hasMemoryValue(MemoryModuleType.RAM_TARGET)) {
         var1.broadcastEntityEvent(var2, (byte)59);
         var5.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object)this.getCooldownOnFail.applyAsInt(var2));
      }

   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return this.ramCandidate.isPresent() && ((RamCandidate)this.ramCandidate.get()).getTarget().isAlive();
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      if (this.ramCandidate.isPresent()) {
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(((RamCandidate)this.ramCandidate.get()).getStartPosition(), this.walkSpeed, 0)));
         var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(((RamCandidate)this.ramCandidate.get()).getTarget(), true)));
         boolean var5 = !((RamCandidate)this.ramCandidate.get()).getTarget().blockPosition().equals(((RamCandidate)this.ramCandidate.get()).getTargetPosition());
         if (var5) {
            var1.broadcastEntityEvent(var2, (byte)59);
            var2.getNavigation().stop();
            this.chooseRamPosition(var2, ((RamCandidate)this.ramCandidate.get()).target);
         } else {
            BlockPos var6 = var2.blockPosition();
            if (var6.equals(((RamCandidate)this.ramCandidate.get()).getStartPosition())) {
               var1.broadcastEntityEvent(var2, (byte)58);
               if (!this.reachedRamPositionTimestamp.isPresent()) {
                  this.reachedRamPositionTimestamp = Optional.of(var3);
               }

               if (var3 - (Long)this.reachedRamPositionTimestamp.get() >= (long)this.ramPrepareTime) {
                  var2.getBrain().setMemory(MemoryModuleType.RAM_TARGET, (Object)this.getEdgeOfBlock(var6, ((RamCandidate)this.ramCandidate.get()).getTargetPosition()));
                  var1.playSound((Player)null, var2, (SoundEvent)this.getPrepareRamSound.apply(var2), SoundSource.HOSTILE, 1.0F, var2.getVoicePitch());
                  this.ramCandidate = Optional.empty();
               }
            }
         }

      }
   }

   private Vec3 getEdgeOfBlock(BlockPos var1, BlockPos var2) {
      double var3 = 0.5;
      double var5 = 0.5 * (double)Mth.sign((double)(var2.getX() - var1.getX()));
      double var7 = 0.5 * (double)Mth.sign((double)(var2.getZ() - var1.getZ()));
      return Vec3.atBottomCenterOf(var2).add(var5, 0.0, var7);
   }

   private Optional<BlockPos> calculateRammingStartPosition(PathfinderMob var1, LivingEntity var2) {
      BlockPos var3 = var2.blockPosition();
      if (!this.isWalkableBlock(var1, var3)) {
         return Optional.empty();
      } else {
         ArrayList var4 = Lists.newArrayList();
         BlockPos.MutableBlockPos var5 = var3.mutable();
         Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

         while(var6.hasNext()) {
            Direction var7 = (Direction)var6.next();
            var5.set(var3);

            for(int var8 = 0; var8 < this.maxRamDistance; ++var8) {
               if (!this.isWalkableBlock(var1, var5.move(var7))) {
                  var5.move(var7.getOpposite());
                  break;
               }
            }

            if (var5.distManhattan(var3) >= this.minRamDistance) {
               var4.add(var5.immutable());
            }
         }

         PathNavigation var9 = var1.getNavigation();
         Stream var10000 = var4.stream();
         BlockPos var10001 = var1.blockPosition();
         Objects.requireNonNull(var10001);
         return var10000.sorted(Comparator.comparingDouble(var10001::distSqr)).filter((var1x) -> {
            Path var2 = var9.createPath((BlockPos)var1x, 0);
            return var2 != null && var2.canReach();
         }).findFirst();
      }
   }

   private boolean isWalkableBlock(PathfinderMob var1, BlockPos var2) {
      return var1.getNavigation().isStableDestination(var2) && var1.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(var1.level, var2.mutable())) == 0.0F;
   }

   private void chooseRamPosition(PathfinderMob var1, LivingEntity var2) {
      this.reachedRamPositionTimestamp = Optional.empty();
      this.ramCandidate = this.calculateRammingStartPosition(var1, var2).map((var1x) -> {
         return new RamCandidate(var1x, var2.blockPosition(), var2);
      });
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   public static class RamCandidate {
      private final BlockPos startPosition;
      private final BlockPos targetPosition;
      final LivingEntity target;

      public RamCandidate(BlockPos var1, BlockPos var2, LivingEntity var3) {
         super();
         this.startPosition = var1;
         this.targetPosition = var2;
         this.target = var3;
      }

      public BlockPos getStartPosition() {
         return this.startPosition;
      }

      public BlockPos getTargetPosition() {
         return this.targetPosition;
      }

      public LivingEntity getTarget() {
         return this.target;
      }
   }
}
