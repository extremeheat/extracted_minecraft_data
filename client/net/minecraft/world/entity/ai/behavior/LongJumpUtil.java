package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

public final class LongJumpUtil {
   public LongJumpUtil() {
      super();
   }

   public static Optional<Vec3> calculateJumpVectorForAngle(Mob var0, Vec3 var1, float var2, int var3, boolean var4) {
      Vec3 var5 = var0.position();
      Vec3 var6 = new Vec3(var1.x - var5.x, 0.0, var1.z - var5.z).normalize().scale(0.5);
      Vec3 var7 = var1.subtract(var6);
      Vec3 var8 = var7.subtract(var5);
      float var9 = (float)var3 * 3.1415927F / 180.0F;
      double var10 = Math.atan2(var8.z, var8.x);
      double var12 = var8.subtract(0.0, var8.y, 0.0).lengthSqr();
      double var14 = Math.sqrt(var12);
      double var16 = var8.y;
      double var18 = var0.getGravity();
      double var20 = Math.sin((double)(2.0F * var9));
      double var22 = Math.pow(Math.cos((double)var9), 2.0);
      double var24 = Math.sin((double)var9);
      double var26 = Math.cos((double)var9);
      double var28 = Math.sin(var10);
      double var30 = Math.cos(var10);
      double var32 = var12 * var18 / (var14 * var20 - 2.0 * var16 * var22);
      if (var32 < 0.0) {
         return Optional.empty();
      } else {
         double var34 = Math.sqrt(var32);
         if (var34 > (double)var2) {
            return Optional.empty();
         } else {
            double var36 = var34 * var26;
            double var38 = var34 * var24;
            if (var4) {
               int var40 = Mth.ceil(var14 / var36) * 2;
               double var41 = 0.0;
               Vec3 var43 = null;
               EntityDimensions var44 = var0.getDimensions(Pose.LONG_JUMPING);

               for(int var45 = 0; var45 < var40 - 1; ++var45) {
                  var41 += var14 / (double)var40;
                  double var46 = var24 / var26 * var41 - Math.pow(var41, 2.0) * var18 / (2.0 * var32 * Math.pow(var26, 2.0));
                  double var48 = var41 * var30;
                  double var50 = var41 * var28;
                  Vec3 var52 = new Vec3(var5.x + var48, var5.y + var46, var5.z + var50);
                  if (var43 != null && !isClearTransition(var0, var44, var43, var52)) {
                     return Optional.empty();
                  }

                  var43 = var52;
               }
            }

            return Optional.of(new Vec3(var36 * var30, var38, var36 * var28).scale(0.949999988079071));
         }
      }
   }

   private static boolean isClearTransition(Mob var0, EntityDimensions var1, Vec3 var2, Vec3 var3) {
      Vec3 var4 = var3.subtract(var2);
      double var5 = (double)Math.min(var1.width(), var1.height());
      int var7 = Mth.ceil(var4.length() / var5);
      Vec3 var8 = var4.normalize();
      Vec3 var9 = var2;

      for(int var10 = 0; var10 < var7; ++var10) {
         var9 = var10 == var7 - 1 ? var3 : var9.add(var8.scale(var5 * 0.8999999761581421));
         if (!var0.level().noCollision(var0, var1.makeBoundingBox(var9))) {
            return false;
         }
      }

      return true;
   }
}
