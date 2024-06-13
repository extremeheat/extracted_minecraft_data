package net.minecraft.world.entity.monster.breeze;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BreezeUtil {
   private static final double MAX_LINE_OF_SIGHT_TEST_RANGE = 50.0;

   public BreezeUtil() {
      super();
   }

   public static Vec3 randomPointBehindTarget(LivingEntity var0, RandomSource var1) {
      byte var2 = 90;
      float var3 = var0.yHeadRot + 180.0F + (float)var1.nextGaussian() * 90.0F / 2.0F;
      float var4 = Mth.lerp(var1.nextFloat(), 4.0F, 8.0F);
      Vec3 var5 = Vec3.directionFromRotation(0.0F, var3).scale((double)var4);
      return var0.position().add(var5);
   }

   public static boolean hasLineOfSight(Breeze var0, Vec3 var1) {
      Vec3 var2 = new Vec3(var0.getX(), var0.getY(), var0.getZ());
      return var1.distanceTo(var2) > 50.0
         ? false
         : var0.level().clip(new ClipContext(var2, var1, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, var0)).getType() == HitResult.Type.MISS;
   }
}
