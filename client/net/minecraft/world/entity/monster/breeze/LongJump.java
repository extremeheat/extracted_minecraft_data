package net.minecraft.world.entity.monster.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.LongJumpUtil;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LongJump extends Behavior<Breeze> {
   private static final int REQUIRED_AIR_BLOCKS_ABOVE = 4;
   private static final int JUMP_COOLDOWN_TICKS = 10;
   private static final int JUMP_COOLDOWN_WHEN_HURT_TICKS = 2;
   private static final int INHALING_DURATION_TICKS = Math.round(10.0F);
   private static final float DEFAULT_FOLLOW_RANGE = 24.0F;
   private static final float DEFAULT_MAX_JUMP_VELOCITY = 1.4F;
   private static final float MAX_JUMP_VELOCITY_MULTIPLIER = 0.058333334F;
   private static final ObjectArrayList<Integer> ALLOWED_ANGLES = new ObjectArrayList(Lists.newArrayList(new Integer[]{40, 55, 60, 75, 80}));

   @VisibleForTesting
   public LongJump() {
      super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.BREEZE_JUMP_COOLDOWN, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREEZE_JUMP_INHALING, MemoryStatus.REGISTERED, MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.BREEZE_SHOOT, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREEZE_LEAVING_WATER, MemoryStatus.REGISTERED), 200);
   }

   public static boolean canRun(ServerLevel var0, Breeze var1) {
      if (!var1.onGround() && !var1.isInWater()) {
         return false;
      } else if (Swim.shouldSwim(var1)) {
         return false;
      } else if (var1.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.VALUE_PRESENT)) {
         return true;
      } else {
         LivingEntity var2 = (LivingEntity)var1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
         if (var2 == null) {
            return false;
         } else if (outOfAggroRange(var1, var2)) {
            var1.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            return false;
         } else if (tooCloseForJump(var1, var2)) {
            return false;
         } else if (!canJumpFromCurrentPosition(var0, var1)) {
            return false;
         } else {
            BlockPos var3 = snapToSurface(var1, BreezeUtil.randomPointBehindTarget(var2, var1.getRandom()));
            if (var3 == null) {
               return false;
            } else {
               BlockState var4 = var0.getBlockState(var3.below());
               if (var1.getType().isBlockDangerous(var4)) {
                  return false;
               } else if (!BreezeUtil.hasLineOfSight(var1, var3.getCenter()) && !BreezeUtil.hasLineOfSight(var1, var3.above(4).getCenter())) {
                  return false;
               } else {
                  var1.getBrain().setMemory(MemoryModuleType.BREEZE_JUMP_TARGET, (Object)var3);
                  return true;
               }
            }
         }
      }
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Breeze var2) {
      return canRun(var1, var2);
   }

   protected boolean canStillUse(ServerLevel var1, Breeze var2, long var3) {
      return var2.getPose() != Pose.STANDING && !var2.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_JUMP_COOLDOWN);
   }

   protected void start(ServerLevel var1, Breeze var2, long var3) {
      if (var2.getBrain().checkMemory(MemoryModuleType.BREEZE_JUMP_INHALING, MemoryStatus.VALUE_ABSENT)) {
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_INHALING, Unit.INSTANCE, (long)INHALING_DURATION_TICKS);
      }

      var2.setPose(Pose.INHALING);
      var1.playSound((Player)null, var2, SoundEvents.BREEZE_CHARGE, SoundSource.HOSTILE, 1.0F, 1.0F);
      var2.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_TARGET).ifPresent((var1x) -> {
         var2.lookAt(EntityAnchorArgument.Anchor.EYES, var1x.getCenter());
      });
   }

   protected void tick(ServerLevel var1, Breeze var2, long var3) {
      boolean var5 = var2.isInWater();
      if (!var5 && var2.getBrain().checkMemory(MemoryModuleType.BREEZE_LEAVING_WATER, MemoryStatus.VALUE_PRESENT)) {
         var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_LEAVING_WATER);
      }

      if (isFinishedInhaling(var2)) {
         Vec3 var6 = (Vec3)var2.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_TARGET).flatMap((var1x) -> {
            return calculateOptimalJumpVector(var2, var2.getRandom(), Vec3.atBottomCenterOf(var1x));
         }).orElse((Object)null);
         if (var6 == null) {
            var2.setPose(Pose.STANDING);
            return;
         }

         if (var5) {
            var2.getBrain().setMemory(MemoryModuleType.BREEZE_LEAVING_WATER, (Object)Unit.INSTANCE);
         }

         var2.playSound(SoundEvents.BREEZE_JUMP, 1.0F, 1.0F);
         var2.setPose(Pose.LONG_JUMPING);
         var2.setYRot(var2.yBodyRot);
         var2.setDiscardFriction(true);
         var2.setDeltaMovement(var6);
      } else if (isFinishedJumping(var2)) {
         var2.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
         var2.setPose(Pose.STANDING);
         var2.setDiscardFriction(false);
         boolean var7 = var2.getBrain().hasMemoryValue(MemoryModuleType.HURT_BY);
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, var7 ? 2L : 10L);
         var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT, Unit.INSTANCE, 100L);
      }

   }

   protected void stop(ServerLevel var1, Breeze var2, long var3) {
      if (var2.getPose() == Pose.LONG_JUMPING || var2.getPose() == Pose.INHALING) {
         var2.setPose(Pose.STANDING);
      }

      var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_JUMP_INHALING);
      var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_LEAVING_WATER);
   }

   private static boolean isFinishedInhaling(Breeze var0) {
      return var0.getBrain().getMemory(MemoryModuleType.BREEZE_JUMP_INHALING).isEmpty() && var0.getPose() == Pose.INHALING;
   }

   private static boolean isFinishedJumping(Breeze var0) {
      boolean var1 = var0.getPose() == Pose.LONG_JUMPING;
      boolean var2 = var0.onGround();
      boolean var3 = var0.isInWater() && var0.getBrain().checkMemory(MemoryModuleType.BREEZE_LEAVING_WATER, MemoryStatus.VALUE_ABSENT);
      return var1 && (var2 || var3);
   }

   @Nullable
   private static BlockPos snapToSurface(LivingEntity var0, Vec3 var1) {
      ClipContext var2 = new ClipContext(var1, var1.relative(Direction.DOWN, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0);
      BlockHitResult var3 = var0.level().clip(var2);
      if (((HitResult)var3).getType() == HitResult.Type.BLOCK) {
         return BlockPos.containing(((HitResult)var3).getLocation()).above();
      } else {
         ClipContext var4 = new ClipContext(var1, var1.relative(Direction.UP, 10.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0);
         BlockHitResult var5 = var0.level().clip(var4);
         return ((HitResult)var5).getType() == HitResult.Type.BLOCK ? BlockPos.containing(((HitResult)var5).getLocation()).above() : null;
      }
   }

   private static boolean outOfAggroRange(Breeze var0, LivingEntity var1) {
      return !var1.closerThan(var0, var0.getAttributeValue(Attributes.FOLLOW_RANGE));
   }

   private static boolean tooCloseForJump(Breeze var0, LivingEntity var1) {
      return var1.distanceTo(var0) - 4.0F <= 0.0F;
   }

   private static boolean canJumpFromCurrentPosition(ServerLevel var0, Breeze var1) {
      BlockPos var2 = var1.blockPosition();
      if (var0.getBlockState(var2).is(Blocks.HONEY_BLOCK)) {
         return false;
      } else {
         for(int var3 = 1; var3 <= 4; ++var3) {
            BlockPos var4 = var2.relative(Direction.UP, var3);
            if (!var0.getBlockState(var4).isAir() && !var0.getFluidState(var4).is(FluidTags.WATER)) {
               return false;
            }
         }

         return true;
      }
   }

   private static Optional<Vec3> calculateOptimalJumpVector(Breeze var0, RandomSource var1, Vec3 var2) {
      List var3 = Util.shuffledCopy(ALLOWED_ANGLES, var1);
      Iterator var4 = var3.iterator();

      Optional var7;
      do {
         if (!var4.hasNext()) {
            return Optional.empty();
         }

         int var5 = (Integer)var4.next();
         float var6 = 0.058333334F * (float)var0.getAttributeValue(Attributes.FOLLOW_RANGE);
         var7 = LongJumpUtil.calculateJumpVectorForAngle(var0, var2, var6, var5, false);
      } while(!var7.isPresent());

      if (var0.hasEffect(MobEffects.JUMP)) {
         double var8 = ((Vec3)var7.get()).normalize().y * (double)var0.getJumpBoostPower();
         return var7.map((var2x) -> {
            return var2x.add(0.0, var8, 0.0);
         });
      } else {
         return var7;
      }
   }

   // $FF: synthetic method
   protected boolean checkExtraStartConditions(final ServerLevel var1, final LivingEntity var2) {
      return this.checkExtraStartConditions(var1, (Breeze)var2);
   }

   // $FF: synthetic method
   protected void stop(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.stop(var1, (Breeze)var2, var3);
   }

   // $FF: synthetic method
   protected void start(final ServerLevel var1, final LivingEntity var2, final long var3) {
      this.start(var1, (Breeze)var2, var3);
   }
}
