package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveToTargetSink extends Behavior<Mob> {
   private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
   private int remainingCooldown;
   @Nullable
   private Path path;
   @Nullable
   private BlockPos lastTargetPos;
   private float speedModifier;

   public MoveToTargetSink() {
      this(150, 250);
   }

   public MoveToTargetSink(int var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), var1, var2);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      if (this.remainingCooldown > 0) {
         --this.remainingCooldown;
         return false;
      } else {
         Brain var3 = var2.getBrain();
         WalkTarget var4 = (WalkTarget)var3.getMemory(MemoryModuleType.WALK_TARGET).get();
         boolean var5 = this.reachedTarget(var2, var4);
         if (!var5 && this.tryComputePath(var2, var4, var1.getGameTime())) {
            this.lastTargetPos = var4.getTarget().currentBlockPosition();
            return true;
         } else {
            var3.eraseMemory(MemoryModuleType.WALK_TARGET);
            if (var5) {
               var3.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }

            return false;
         }
      }
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      if (this.path != null && this.lastTargetPos != null) {
         Optional var5 = var2.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         boolean var6 = (Boolean)var5.map(MoveToTargetSink::isWalkTargetSpectator).orElse(false);
         PathNavigation var7 = var2.getNavigation();
         return !var7.isDone() && var5.isPresent() && !this.reachedTarget(var2, (WalkTarget)var5.get()) && !var6;
      } else {
         return false;
      }
   }

   protected void stop(ServerLevel var1, Mob var2, long var3) {
      if (var2.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && !this.reachedTarget(var2, (WalkTarget)var2.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && var2.getNavigation().isStuck()) {
         this.remainingCooldown = var1.getRandom().nextInt(40);
      }

      var2.getNavigation().stop();
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.PATH);
      this.path = null;
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.PATH, (Object)this.path);
      var2.getNavigation().moveTo(this.path, (double)this.speedModifier);
   }

   protected void tick(ServerLevel var1, Mob var2, long var3) {
      Path var5 = var2.getNavigation().getPath();
      Brain var6 = var2.getBrain();
      if (this.path != var5) {
         this.path = var5;
         var6.setMemory(MemoryModuleType.PATH, (Object)var5);
      }

      if (var5 != null && this.lastTargetPos != null) {
         WalkTarget var7 = (WalkTarget)var6.getMemory(MemoryModuleType.WALK_TARGET).get();
         if (var7.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0 && this.tryComputePath(var2, var7, var1.getGameTime())) {
            this.lastTargetPos = var7.getTarget().currentBlockPosition();
            this.start(var1, var2, var3);
         }

      }
   }

   private boolean tryComputePath(Mob var1, WalkTarget var2, long var3) {
      BlockPos var5 = var2.getTarget().currentBlockPosition();
      this.path = var1.getNavigation().createPath((BlockPos)var5, 0);
      this.speedModifier = var2.getSpeedModifier();
      Brain var6 = var1.getBrain();
      if (this.reachedTarget(var1, var2)) {
         var6.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      } else {
         boolean var7 = this.path != null && this.path.canReach();
         if (var7) {
            var6.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         } else if (!var6.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            var6.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)var3);
         }

         if (this.path != null) {
            return true;
         }

         Vec3 var8 = DefaultRandomPos.getPosTowards((PathfinderMob)var1, 10, 7, Vec3.atBottomCenterOf(var5), 1.5707963705062866);
         if (var8 != null) {
            this.path = var1.getNavigation().createPath(var8.x, var8.y, var8.z, 0);
            return this.path != null;
         }
      }

      return false;
   }

   private boolean reachedTarget(Mob var1, WalkTarget var2) {
      return var2.getTarget().currentBlockPosition().distManhattan(var1.blockPosition()) <= var2.getCloseEnoughDist();
   }

   private static boolean isWalkTargetSpectator(WalkTarget var0) {
      PositionTracker var1 = var0.getTarget();
      if (var1 instanceof EntityTracker var2) {
         return var2.getEntity().isSpectator();
      } else {
         return false;
      }
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Mob)var2, var3);
   }
}
