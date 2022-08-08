package net.minecraft.world.entity.ai.behavior;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class RandomSwim extends RandomStroll {
   public static final int[][] XY_DISTANCE_TIERS = new int[][]{{1, 1}, {3, 3}, {5, 5}, {6, 5}, {7, 7}, {10, 7}};

   public RandomSwim(float var1) {
      super(var1);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, PathfinderMob var2) {
      return var2.isInWaterOrBubble();
   }

   @Nullable
   protected Vec3 getTargetPos(PathfinderMob var1) {
      Vec3 var2 = null;
      Vec3 var3 = null;
      int[][] var4 = XY_DISTANCE_TIERS;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int[] var7 = var4[var6];
         if (var2 == null) {
            var3 = BehaviorUtils.getRandomSwimmablePos(var1, var7[0], var7[1]);
         } else {
            var3 = var1.position().add(var1.position().vectorTo(var2).normalize().multiply((double)var7[0], (double)var7[1], (double)var7[0]));
         }

         if (var3 == null || var1.level.getFluidState(new BlockPos(var3)).isEmpty()) {
            return var2;
         }

         var2 = var3;
      }

      return var3;
   }
}
