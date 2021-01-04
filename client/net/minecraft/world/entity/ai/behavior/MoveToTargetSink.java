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
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveToTargetSink extends Behavior<Mob> {
   @Nullable
   private Path path;
   @Nullable
   private BlockPos lastTargetPos;
   private float speed;
   private int remainingDelay;

   public MoveToTargetSink(int var1) {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), var1);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Mob var2) {
      Brain var3 = var2.getBrain();
      WalkTarget var4 = (WalkTarget)var3.getMemory(MemoryModuleType.WALK_TARGET).get();
      if (!this.reachedTarget(var2, var4) && this.tryComputePath(var2, var4, var1.getGameTime())) {
         this.lastTargetPos = var4.getTarget().getPos();
         return true;
      } else {
         var3.eraseMemory(MemoryModuleType.WALK_TARGET);
         return false;
      }
   }

   protected boolean canStillUse(ServerLevel var1, Mob var2, long var3) {
      if (this.path != null && this.lastTargetPos != null) {
         Optional var5 = var2.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         PathNavigation var6 = var2.getNavigation();
         return !var6.isDone() && var5.isPresent() && !this.reachedTarget(var2, (WalkTarget)var5.get());
      } else {
         return false;
      }
   }

   protected void stop(ServerLevel var1, Mob var2, long var3) {
      var2.getNavigation().stop();
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.PATH);
      this.path = null;
   }

   protected void start(ServerLevel var1, Mob var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.PATH, (Object)this.path);
      var2.getNavigation().moveTo(this.path, (double)this.speed);
      this.remainingDelay = var1.getRandom().nextInt(10);
   }

   protected void tick(ServerLevel var1, Mob var2, long var3) {
      --this.remainingDelay;
      if (this.remainingDelay <= 0) {
         Path var5 = var2.getNavigation().getPath();
         Brain var6 = var2.getBrain();
         if (this.path != var5) {
            this.path = var5;
            var6.setMemory(MemoryModuleType.PATH, (Object)var5);
         }

         if (var5 != null && this.lastTargetPos != null) {
            WalkTarget var7 = (WalkTarget)var6.getMemory(MemoryModuleType.WALK_TARGET).get();
            if (var7.getTarget().getPos().distSqr(this.lastTargetPos) > 4.0D && this.tryComputePath(var2, var7, var1.getGameTime())) {
               this.lastTargetPos = var7.getTarget().getPos();
               this.start(var1, var2, var3);
            }

         }
      }
   }

   private boolean tryComputePath(Mob var1, WalkTarget var2, long var3) {
      BlockPos var5 = var2.getTarget().getPos();
      this.path = var1.getNavigation().createPath((BlockPos)var5, 0);
      this.speed = var2.getSpeed();
      if (!this.reachedTarget(var1, var2)) {
         Brain var6 = var1.getBrain();
         boolean var7 = this.path != null && this.path.canReach();
         if (var7) {
            var6.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, Optional.empty());
         } else if (!var6.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            var6.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, (Object)var3);
         }

         if (this.path != null) {
            return true;
         }

         Vec3 var8 = RandomPos.getPosTowards((PathfinderMob)var1, 10, 7, new Vec3(var5));
         if (var8 != null) {
            this.path = var1.getNavigation().createPath(var8.x, var8.y, var8.z, 0);
            return this.path != null;
         }
      }

      return false;
   }

   private boolean reachedTarget(Mob var1, WalkTarget var2) {
      return var2.getTarget().getPos().distManhattan(new BlockPos(var1)) <= var2.getCloseEnoughDist();
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Mob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Mob)var2, var3);
   }
}
