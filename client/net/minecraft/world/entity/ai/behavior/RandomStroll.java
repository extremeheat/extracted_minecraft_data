package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStroll {
   private static final int MAX_XZ_DIST = 10;
   private static final int MAX_Y_DIST = 7;
   private static final int[][] SWIM_XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

   public RandomStroll() {
      super();
   }

   public static OneShot<PathfinderMob> stroll(float var0) {
      return stroll(var0, true);
   }

   public static OneShot<PathfinderMob> stroll(float var0, boolean var1) {
      return strollFlyOrSwim(var0, var0x -> LandRandomPos.getPos(var0x, 10, 7), var1 ? var0x -> true : var0x -> !var0x.isInWaterOrBubble());
   }

   public static BehaviorControl<PathfinderMob> stroll(float var0, int var1, int var2) {
      return strollFlyOrSwim(var0, var2x -> LandRandomPos.getPos(var2x, var1, var2), var0x -> true);
   }

   public static BehaviorControl<PathfinderMob> fly(float var0) {
      return strollFlyOrSwim(var0, var0x -> getTargetFlyPos(var0x, 10, 7), var0x -> true);
   }

   public static BehaviorControl<PathfinderMob> swim(float var0) {
      return strollFlyOrSwim(var0, RandomStroll::getTargetSwimPos, Entity::isInWaterOrBubble);
   }

   private static OneShot<PathfinderMob> strollFlyOrSwim(float var0, Function<PathfinderMob, Vec3> var1, Predicate<PathfinderMob> var2) {
      return BehaviorBuilder.create(var3 -> var3.group(var3.absent(MemoryModuleType.WALK_TARGET)).apply(var3, var3x -> (var4, var5, var6) -> {
               if (!var2.test(var5)) {
                  return false;
               } else {
                  Optional var8 = Optional.ofNullable((Vec3)var1.apply(var5));
                  var3x.setOrErase(var8.map(var1xxxx -> new WalkTarget(var1xxxx, var0, 0)));
                  return true;
               }
            }));
   }

   @Nullable
   private static Vec3 getTargetSwimPos(PathfinderMob var0) {
      Vec3 var1 = null;
      Vec3 var2 = null;

      for(int[] var6 : SWIM_XY_DISTANCE_TIERS) {
         if (var1 == null) {
            var2 = BehaviorUtils.getRandomSwimmablePos(var0, var6[0], var6[1]);
         } else {
            var2 = var0.position().add(var0.position().vectorTo(var1).normalize().multiply((double)var6[0], (double)var6[1], (double)var6[0]));
         }

         if (var2 == null || var0.level().getFluidState(BlockPos.containing(var2)).isEmpty()) {
            return var1;
         }

         var1 = var2;
      }

      return var2;
   }

   @Nullable
   private static Vec3 getTargetFlyPos(PathfinderMob var0, int var1, int var2) {
      Vec3 var3 = var0.getViewVector(0.0F);
      return AirAndWaterRandomPos.getPos(var0, var1, var2, -2, var3.x, var3.z, 1.5707963705062866);
   }
}
