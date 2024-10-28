package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
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

public class AnimalPanic<E extends PathfinderMob> extends Behavior<E> {
   private static final int PANIC_MIN_DURATION = 100;
   private static final int PANIC_MAX_DURATION = 120;
   private static final int PANIC_DISTANCE_HORIZONTAL = 5;
   private static final int PANIC_DISTANCE_VERTICAL = 4;
   private static final Predicate<PathfinderMob> DEFAULT_SHOULD_PANIC_PREDICATE = (var0) -> {
      return var0.getLastHurtByMob() != null || var0.isFreezing() || var0.isOnFire();
   };
   private final float speedMultiplier;
   private final Predicate<E> shouldPanic;

   public AnimalPanic(float var1) {
      Predicate var10002 = DEFAULT_SHOULD_PANIC_PREDICATE;
      Objects.requireNonNull(var10002);
      this(var1, var10002::test);
   }

   public AnimalPanic(float var1, Predicate<E> var2) {
      super(Map.of(MemoryModuleType.IS_PANICKING, MemoryStatus.REGISTERED, MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED), 100, 120);
      this.speedMultiplier = var1;
      this.shouldPanic = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.shouldPanic.test(var2) && (var2.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY) || var2.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING));
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return true;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.IS_PANICKING, (Object)true);
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      Brain var5 = var2.getBrain();
      var5.eraseMemory(MemoryModuleType.IS_PANICKING);
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      if (var2.getNavigation().isDone()) {
         Vec3 var5 = this.getPanicPos(var2, var1);
         if (var5 != null) {
            var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var5, this.speedMultiplier, 0)));
         }
      }

   }

   @Nullable
   private Vec3 getPanicPos(E var1, ServerLevel var2) {
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
      if (!var1.getBlockState(var3).getCollisionShape(var1, var3).isEmpty()) {
         return Optional.empty();
      } else {
         Predicate var4;
         if (Mth.ceil(var2.getBbWidth()) == 2) {
            var4 = (var1x) -> {
               return BlockPos.squareOutSouthEast(var1x).allMatch((var1xx) -> {
                  return var1.getFluidState(var1xx).is(FluidTags.WATER);
               });
            };
         } else {
            var4 = (var1x) -> {
               return var1.getFluidState(var1x).is(FluidTags.WATER);
            };
         }

         return BlockPos.findClosestMatch(var3, 5, 1, var4);
      }
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.tick(var1, (PathfinderMob)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (PathfinderMob)var2, var3);
   }
}
