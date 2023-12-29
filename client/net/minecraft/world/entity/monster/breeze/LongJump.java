package net.minecraft.world.entity.monster.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LongJump extends Behavior<Breeze> {
   private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
   private static final double MAX_LINE_OF_SIGHT_TEST_RANGE = 50.0;
   private static final int JUMP_COOLDOWN_TICKS = 10;
   private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
   private static final int INHALING_DURATION_TICKS = Math.round(10.0F);
   private static final float MAX_JUMP_VELOCITY = 1.4F;
   private static final ObjectArrayList<Integer> ALLOWED_ANGLES = new ObjectArrayList(Lists.newArrayList(new Integer[]{40, 55, 60, 75, 80}));

   @VisibleForTesting
   public LongJump() {
      super(
         Map.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.BREEZE_JUMP_COOLDOWN,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_JUMP_INHALING,
            MemoryStatus.REGISTERED,
            MemoryModuleType.BREEZE_JUMP_TARGET,
            MemoryStatus.REGISTERED,
            MemoryModuleType.BREEZE_SHOOT,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT
         ),
         200
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Breeze var2) {
      if (!var2.onGround() && !var2.isInWater()) {
         return false;
      } else if (var2.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.VALUE_PRESENT)) {
         return true;
      } else {
         LivingEntity var3 = var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
         if (var3 == null) {
            return false;
         } else if (outOfAggroRange(var2, var3)) {
            var2.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            return false;
         } else if (tooCloseForJump(var2, var3)) {
            return false;
         } else if (!canJumpFromCurrentPosition(var1, var2)) {
            return false;
         } else {
            BlockPos var4 = snapToSurface(var2, randomPointBehindTarget(var3, var2.getRandom()));
            if (var4 == null) {
               return false;
            } else if (!hasLineOfSight(var2, var4.getCenter()) && !hasLineOfSight(var2, var4.above(4).getCenter())) {
               return false;
            } else {
               var2.getBrain().setMemory(MemoryModuleType.BREEZE_JUMP_TARGET, var4);
               return true;
            }
         }
      }
   }

   protected boolean canStillUse(ServerLevel var1, Breeze var2, long var3) {
      return var2.getPose() != Pose.STANDING && !var2.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_JUMP_COOLDOWN);
   }

   protected void start(ServerLevel var1, Breeze var2, long var3) {
      if (var2.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_INHALING, MemoryStatus.VALUE_ABSENT)) {
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_INHALING, Unit.INSTANCE, (long)INHALING_DURATION_TICKS);
      }

      var2.setPose(Pose.INHALING);
      var2.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_TARGET).ifPresent(var1x -> var2.lookAt(EntityAnchorArgument.Anchor.EYES, var1x.getCenter()));
   }

   protected void tick(ServerLevel var1, Breeze var2, long var3) {
      if (finishedInhaling(var2)) {
         Vec3 var5 = var2.getBrain()
            .getMemory(MemoryModuleType.BREEZE_JUMP_TARGET)
            .flatMap(var1x -> calculateOptimalJumpVector(var2, var2.getRandom(), Vec3.atBottomCenterOf(var1x)))
            .orElse(null);
         if (var5 == null) {
            var2.setPose(Pose.STANDING);
            return;
         }

         var2.playSound(SoundEvents.BREEZE_JUMP, 1.0F, 1.0F);
         var2.setPose(Pose.LONG_JUMPING);
         var2.setYRot(var2.yBodyRot);
         var2.setDiscardFriction(true);
         var2.setDeltaMovement(var5);
      } else if (finishedJumping(var2)) {
         var2.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
         var2.setPose(Pose.STANDING);
         var2.setDiscardFriction(false);
         boolean var6 = var2.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, var6 ? 2L : 10L);
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 100L);
      }
   }

   protected void stop(ServerLevel var1, Breeze var2, long var3) {
      if (var2.getPose() == Pose.LONG_JUMPING || var2.getPose() == Pose.INHALING) {
         var2.setPose(Pose.STANDING);
      }

      var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_INHALING);
   }

   private static boolean finishedInhaling(Breeze var0) {
      return var0.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_INHALING).isEmpty() && var0.getPose() == Pose.INHALING;
   }

   private static boolean finishedJumping(Breeze var0) {
      return var0.getPose() == Pose.LONG_JUMPING && var0.onGround();
   }

   private static Vec3 randomPointBehindTarget(LivingEntity var0, RandomSource var1) {
      boolean var2 = true;
      float var3 = var0.yHeadRot + 180.0F + (float)var1.nextGaussian() * 90.0F / 2.0F;
      float var4 = Mth.lerp(var1.nextFloat(), 4.0F, 8.0F);
      Vec3 var5 = Vec3.directionFromRotation(0.0F, var3).scale((double)var4);
      return var0.position().add(var5);
   }

   @Nullable
   private static BlockPos snapToSurface(LivingEntity var0, Vec3 var1) {
      ClipContext var2 = new ClipContext(var1, var1.relative(Direction.DOWN, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0);
      BlockHitResult var3 = var0.level().clip(var2);
      if (var3.getType() == HitResult.Type.BLOCK) {
         return BlockPos.containing(var3.getLocation()).above();
      } else {
         ClipContext var4 = new ClipContext(var1, var1.relative(Direction.UP, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0);
         BlockHitResult var5 = var0.level().clip(var4);
         return var5.getType() == HitResult.Type.BLOCK ? BlockPos.containing(var3.getLocation()).above() : null;
      }
   }

   @VisibleForTesting
   public static boolean hasLineOfSight(Breeze var0, Vec3 var1) {
      Vec3 var2 = new Vec3(var0.getX(), var0.getY(), var0.getZ());
      if (var1.distanceTo(var2) > 50.0) {
         return false;
      } else {
         return var0.level().clip(new ClipContext(var2, var1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0)).getType() == HitResult.Type.MISS;
      }
   }

   private static boolean outOfAggroRange(Breeze var0, LivingEntity var1) {
      return !var1.closerThan(var0, 24.0);
   }

   private static boolean tooCloseForJump(Breeze var0, LivingEntity var1) {
      return var1.distanceTo(var0) - 4.0F <= 0.0F;
   }

   private static boolean canJumpFromCurrentPosition(ServerLevel var0, Breeze var1) {
      BlockPos var2 = var1.blockPosition();

      for(int var3 = 1; var3 <= 4; ++var3) {
         BlockPos var4 = var2.relative(Direction.UP, var3);
         if (!var0.getBlockState(var4).isAir() && !var0.getFluidState(var4).is(FluidTags.WATER)) {
            return false;
         }
      }

      return true;
   }

   private static Optional<Vec3> calculateOptimalJumpVector(Breeze var0, RandomSource var1, Vec3 var2) {
      for(int var5 : Util.shuffledCopy(ALLOWED_ANGLES, var1)) {
         Optional var6 = LongJumpUtil.calculateJumpVectorForAngle(var0, var2, 1.4F, var5, false);
         if (var6.isPresent()) {
            return var6;
         }
      }

      return Optional.empty();
   }
}
