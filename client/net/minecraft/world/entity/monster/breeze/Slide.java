package net.minecraft.world.entity.monster.breeze;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class Slide extends Behavior<Breeze> {
   public Slide() {
      super(
         Map.of(
            MemoryModuleType.ATTACK_TARGET,
            MemoryStatus.VALUE_PRESENT,
            MemoryModuleType.WALK_TARGET,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_JUMP_COOLDOWN,
            MemoryStatus.VALUE_ABSENT,
            MemoryModuleType.BREEZE_SHOOT,
            MemoryStatus.VALUE_ABSENT
         )
      );
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Breeze var2) {
      return var2.onGround() && !var2.isInWater() && var2.getPose() == Pose.STANDING;
   }

   protected void start(ServerLevel var1, Breeze var2, long var3) {
      LivingEntity var5 = var2.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
      if (var5 != null) {
         boolean var6 = var2.withinOuterCircleRange(var5.position());
         boolean var7 = var2.withinMiddleCircleRange(var5.position());
         boolean var8 = var2.withinInnerCircleRange(var5.position());
         Vec3 var9 = null;
         if (var6) {
            var9 = randomPointInMiddleCircle(var2, var5);
         } else if (var8) {
            Vec3 var10 = DefaultRandomPos.getPosAway(var2, 5, 5, var5.position());
            if (var10 != null && var5.distanceToSqr(var10.x, var10.y, var10.z) > var5.distanceToSqr(var2)) {
               var9 = var10;
            }
         } else if (var7) {
            var9 = LandRandomPos.getPos(var2, 5, 3);
         }

         if (var9 != null) {
            var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(BlockPos.containing(var9), 0.6F, 1));
         }
      }
   }

   protected void stop(ServerLevel var1, Breeze var2, long var3) {
      var2.getBrain().setMemoryWithExpiry(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, 20L);
   }

   private static Vec3 randomPointInMiddleCircle(Breeze var0, LivingEntity var1) {
      Vec3 var2 = var1.position().subtract(var0.position());
      double var3 = var2.length() - Mth.lerp(var0.getRandom().nextDouble(), 8.0, 4.0);
      Vec3 var5 = var2.normalize().multiply(var3, var3, var3);
      return var0.position().add(var5);
   }
}
