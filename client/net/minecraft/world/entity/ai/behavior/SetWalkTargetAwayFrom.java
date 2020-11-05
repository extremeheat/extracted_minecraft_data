package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFrom<T> extends Behavior<PathfinderMob> {
   private final MemoryModuleType<T> walkAwayFromMemory;
   private final float speedModifier;
   private final int desiredDistance;
   private final Function<T, Vec3> toPosition;

   public SetWalkTargetAwayFrom(MemoryModuleType<T> var1, float var2, int var3, boolean var4, Function<T, Vec3> var5) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, var4 ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, var1, MemoryStatus.VALUE_PRESENT));
      this.walkAwayFromMemory = var1;
      this.speedModifier = var2;
      this.desiredDistance = var3;
      this.toPosition = var5;
   }

   public static SetWalkTargetAwayFrom<BlockPos> pos(MemoryModuleType<BlockPos> var0, float var1, int var2, boolean var3) {
      return new SetWalkTargetAwayFrom(var0, var1, var2, var3, Vec3::atBottomCenterOf);
   }

   public static SetWalkTargetAwayFrom<? extends Entity> entity(MemoryModuleType<? extends Entity> var0, float var1, int var2, boolean var3) {
      return new SetWalkTargetAwayFrom(var0, var1, var2, var3, Entity::position);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return this.alreadyWalkingAwayFromPosWithSameSpeed(var2) ? false : var2.position().closerThan(this.getPosToAvoid(var2), (double)this.desiredDistance);
   }

   private Vec3 getPosToAvoid(PathfinderMob var1) {
      return (Vec3)this.toPosition.apply(var1.getBrain().getMemory(this.walkAwayFromMemory).get());
   }

   private boolean alreadyWalkingAwayFromPosWithSameSpeed(PathfinderMob var1) {
      if (!var1.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
         return false;
      } else {
         WalkTarget var2 = (WalkTarget)var1.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get();
         if (var2.getSpeedModifier() != this.speedModifier) {
            return false;
         } else {
            Vec3 var3 = var2.getTarget().currentPosition().subtract(var1.position());
            Vec3 var4 = this.getPosToAvoid(var1).subtract(var1.position());
            return var3.dot(var4) < 0.0D;
         }
      }
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      moveAwayFrom(var2, this.getPosToAvoid(var2), this.speedModifier);
   }

   private static void moveAwayFrom(PathfinderMob var0, Vec3 var1, float var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         Vec3 var4 = LandRandomPos.getPosAway(var0, 16, 7, var1);
         if (var4 != null) {
            var0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var4, var2, 0)));
            return;
         }
      }

   }
}
