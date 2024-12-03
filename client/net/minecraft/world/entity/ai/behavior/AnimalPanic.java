package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
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
   private final float speedMultiplier;
   private final Function<PathfinderMob, TagKey<DamageType>> panicCausingDamageTypes;

   public AnimalPanic(float var1) {
      this(var1, (var0) -> DamageTypeTags.PANIC_CAUSES);
   }

   public AnimalPanic(float var1, Function<PathfinderMob, TagKey<DamageType>> var2) {
      super(Map.of(MemoryModuleType.IS_PANICKING, MemoryStatus.REGISTERED, MemoryModuleType.HURT_BY, MemoryStatus.REGISTERED), 100, 120);
      this.speedMultiplier = var1;
      this.panicCausingDamageTypes = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return (Boolean)var2.getBrain().getMemory(MemoryModuleType.HURT_BY).map((var2x) -> var2x.is((TagKey)this.panicCausingDamageTypes.apply(var2))).orElse(false) || var2.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return true;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      var2.getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);
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
            var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(var5, this.speedMultiplier, 0));
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
            var4 = (var1x) -> BlockPos.squareOutSouthEast(var1x).allMatch((var1xx) -> var1.getFluidState(var1xx).is(FluidTags.WATER));
         } else {
            var4 = (var1x) -> var1.getFluidState(var1x).is(FluidTags.WATER);
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
