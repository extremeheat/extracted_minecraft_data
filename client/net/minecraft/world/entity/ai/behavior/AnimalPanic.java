package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class AnimalPanic extends Behavior<PathfinderMob> {
   private static final int PANIC_MIN_DURATION = 100;
   private static final int PANIC_MAX_DURATION = 120;
   private static final int PANIC_DISTANCE_HORIZONTAL = 5;
   private static final int PANIC_DISTANCE_VERTICAL = 4;
   private final float speedMultiplier;

   public AnimalPanic(float var1) {
      super(ImmutableMap.of(MemoryModuleType.IS_PANICKING, MemoryStatus.REGISTERED, MemoryModuleType.HURT_BY, MemoryStatus.VALUE_PRESENT), 100, 120);
      this.speedMultiplier = var1;
   }

   protected boolean canStillUse(ServerLevel var1, PathfinderMob var2, long var3) {
      return true;
   }

   protected void start(ServerLevel var1, PathfinderMob var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.IS_PANICKING, (Object)true);
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }

   protected void stop(ServerLevel var1, PathfinderMob var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.eraseMemory(MemoryModuleType.IS_PANICKING);
   }

   protected void tick(ServerLevel var1, PathfinderMob var2, long var3) {
      if (var2.getNavigation().isDone()) {
         Vec3 var5 = this.getPanicPos(var2, var1);
         if (var5 != null) {
            var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var5, this.speedMultiplier, 0)));
         }
      }

   }

   @Nullable
   private Vec3 getPanicPos(PathfinderMob var1, ServerLevel var2) {
      if (var1.isOnFire()) {
         Optional var3 = this.lookForWater(var2, var1).map(Vec3::atBottomCenterOf);
         if (var3.isPresent()) {
            return (Vec3)var3.get();
         }
      }

      return LandRandomPos.getPos(var1, 5, 4);
   }

   private Optional<BlockPos> lookForWater(BlockGetter var1, Entity var2) {
      BlockPos var3 = var2.blockPosition();
      return !var1.getBlockState(var3).getCollisionShape(var1, var3).isEmpty() ? Optional.empty() : BlockPos.findClosestMatch(var3, 5, 1, (var1x) -> {
         return var1.getFluidState(var1x).is(FluidTags.WATER);
      });
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
   }
}
