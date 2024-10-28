package net.minecraft.world.entity.monster.breeze;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.phys.Vec3;

public class Shoot extends Behavior<Breeze> {
   private static final int ATTACK_RANGE_MIN_SQRT = 4;
   private static final int ATTACK_RANGE_MAX_SQRT = 256;
   private static final int UNCERTAINTY_BASE = 5;
   private static final int UNCERTAINTY_MULTIPLIER = 4;
   private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
   private static final int SHOOT_INITIAL_DELAY_TICKS = Math.round(15.0F);
   private static final int SHOOT_RECOVER_DELAY_TICKS = Math.round(4.0F);
   private static final int SHOOT_COOLDOWN_TICKS = Math.round(10.0F);

   @VisibleForTesting
   public Shoot() {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.BREEZE_SHOOT_COOLDOWN, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREEZE_SHOOT_CHARGING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREEZE_SHOOT_RECOVERING, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREEZE_SHOOT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.BREEZE_JUMP_TARGET, MemoryStatus.VALUE_ABSENT), SHOOT_INITIAL_DELAY_TICKS + 1 + SHOOT_RECOVER_DELAY_TICKS);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Breeze var2) {
      return var2.getPose() != Pose.STANDING ? false : (Boolean)var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).map((var1x) -> {
         return isTargetWithinRange(var2, var1x);
      }).map((var1x) -> {
         if (!var1x) {
            var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
         }

         return var1x;
      }).orElse(false);
   }

   protected boolean canStillUse(ServerLevel var1, Breeze var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_SHOOT);
   }

   protected void start(ServerLevel var1, Breeze var2, long var3) {
      var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent((var1x) -> {
         var2.setPose(Pose.SHOOTING);
      });
      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_CHARGING, Unit.INSTANCE, (long)SHOOT_INITIAL_DELAY_TICKS);
      var2.playSound(SoundEvents.BREEZE_INHALE, 1.0F, 1.0F);
   }

   protected void stop(ServerLevel var1, Breeze var2, long var3) {
      if (var2.getPose() == Pose.SHOOTING) {
         var2.setPose(Pose.STANDING);
      }

      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_COOLDOWN, Unit.INSTANCE, (long)SHOOT_COOLDOWN_TICKS);
      var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
   }

   protected void tick(ServerLevel var1, Breeze var2, long var3) {
      Brain var5 = var2.getBrain();
      LivingEntity var6 = (LivingEntity)var5.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
      if (var6 != null) {
         var2.lookAt(EntityAnchorArgument.Anchor.EYES, var6.position());
         if (!var5.getMemory(MemoryModuleType.BREEZE_SHOOT_CHARGING).isPresent() && !var5.getMemory(MemoryModuleType.BREEZE_SHOOT_RECOVERING).isPresent()) {
            var5.setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_RECOVERING, Unit.INSTANCE, (long)SHOOT_RECOVER_DELAY_TICKS);
            if (isFacingTarget(var2, var6)) {
               double var7 = var6.getX() - var2.getX();
               double var9 = var6.getY(0.3) - var2.getY(0.5);
               double var11 = var6.getZ() - var2.getZ();
               BreezeWindCharge var13 = new BreezeWindCharge(var2, var1);
               var2.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
               var13.shoot(var7, var9, var11, 0.7F, (float)(5 - var1.getDifficulty().getId() * 4));
               var1.addFreshEntity(var13);
            }

         }
      }
   }

   @VisibleForTesting
   public static boolean isFacingTarget(Breeze var0, LivingEntity var1) {
      Vec3 var2 = var0.getViewVector(1.0F);
      Vec3 var3 = var1.position().subtract(var0.position()).normalize();
      return var2.dot(var3) > 0.5;
   }

   private static boolean isTargetWithinRange(Breeze var0, LivingEntity var1) {
      double var2 = var0.position().distanceToSqr(var1.position());
      return var2 > 4.0 && var2 < 256.0;
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Breeze)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Breeze)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Breeze)var2, var3);
   }
}
