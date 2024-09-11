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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.item.ItemStack;

public class Shoot extends Behavior<Breeze> {
   private static final int ATTACK_RANGE_MAX_SQRT = 256;
   private static final int UNCERTAINTY_BASE = 5;
   private static final int UNCERTAINTY_MULTIPLIER = 4;
   private static final float PROJECTILE_MOVEMENT_SCALE = 0.7F;
   private static final int SHOOT_INITIAL_DELAY_TICKS = Math.round(15.0F);
   private static final int SHOOT_RECOVER_DELAY_TICKS = Math.round(4.0F);
   private static final int SHOOT_COOLDOWN_TICKS = Math.round(10.0F);

   @VisibleForTesting
   public Shoot() {
      super(
         ImmutableMap.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.BREEZE_SHOOT_COOLDOWN,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_SHOOT_CHARGING,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_SHOOT_RECOVERING,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_SHOOT,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_JUMP_TARGET,
            MemoryStatus.VALUE_ABSENT
         ),
         SHOOT_INITIAL_DELAY_TICKS + 1 + SHOOT_RECOVER_DELAY_TICKS
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Breeze var2) {
      return var2.getPose() != Pose.STANDING
         ? false
         : var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).map(var1x -> isTargetWithinRange(var2, var1x)).map(var1x -> {
            if (!var1x) {
               var2.getBrain().eraseMemory(MemoryModuleType.BREEZE_SHOOT);
            }

            return (Boolean)var1x;
         }).orElse(false);
   }

   protected boolean canStillUse(ServerLevel var1, Breeze var2, long var3) {
      return var2.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && var2.getBrain().hasMemoryValue(MemoryModuleType.BREEZE_SHOOT);
   }

   protected void start(ServerLevel var1, Breeze var2, long var3) {
      var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(var1x -> var2.setPose(Pose.SHOOTING));
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
      LivingEntity var6 = var5.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
      if (var6 != null) {
         var2.lookAt(EntityAnchorArgument.Anchor.EYES, var6.position());
         if (!var5.getMemory(MemoryModuleType.BREEZE_SHOOT_CHARGING).isPresent() && !var5.getMemory(MemoryModuleType.BREEZE_SHOOT_RECOVERING).isPresent()) {
            var5.setMemoryWithExpiry(MemoryModuleType.BREEZE_SHOOT_RECOVERING, Unit.INSTANCE, (long)SHOOT_RECOVER_DELAY_TICKS);
            double var7 = var6.getX() - var2.getX();
            double var9 = var6.getY(var6.isPassenger() ? 0.8 : 0.3) - var2.getFiringYPosition();
            double var11 = var6.getZ() - var2.getZ();
            Projectile.spawnProjectileUsingShoot(
               new BreezeWindCharge(var2, var1), var1, ItemStack.EMPTY, var7, var9, var11, 0.7F, (float)(5 - var1.getDifficulty().getId() * 4)
            );
            var2.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
         }
      }
   }

   private static boolean isTargetWithinRange(Breeze var0, LivingEntity var1) {
      double var2 = var0.position().distanceToSqr(var1.position());
      return var2 < 256.0;
   }
}
