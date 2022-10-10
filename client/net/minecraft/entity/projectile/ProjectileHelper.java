package net.minecraft.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ProjectileHelper {
   public static RayTraceResult func_188802_a(Entity var0, boolean var1, boolean var2, @Nullable Entity var3) {
      double var4 = var0.field_70165_t;
      double var6 = var0.field_70163_u;
      double var8 = var0.field_70161_v;
      double var10 = var0.field_70159_w;
      double var12 = var0.field_70181_x;
      double var14 = var0.field_70179_y;
      World var16 = var0.field_70170_p;
      Vec3d var17 = new Vec3d(var4, var6, var8);
      if (!var16.func_211156_a(var0, var0.func_174813_aQ(), (Set)(!var2 && var3 != null ? func_211325_a(var3) : ImmutableSet.of()))) {
         return new RayTraceResult(RayTraceResult.Type.BLOCK, var17, EnumFacing.func_210769_a(var10, var12, var14), new BlockPos(var0));
      } else {
         Vec3d var18 = new Vec3d(var4 + var10, var6 + var12, var8 + var14);
         RayTraceResult var19 = var16.func_200259_a(var17, var18, RayTraceFluidMode.NEVER, true, false);
         if (var1) {
            if (var19 != null) {
               var18 = new Vec3d(var19.field_72307_f.field_72450_a, var19.field_72307_f.field_72448_b, var19.field_72307_f.field_72449_c);
            }

            Entity var20 = null;
            List var21 = var16.func_72839_b(var0, var0.func_174813_aQ().func_72321_a(var10, var12, var14).func_186662_g(1.0D));
            double var22 = 0.0D;

            for(int var24 = 0; var24 < var21.size(); ++var24) {
               Entity var25 = (Entity)var21.get(var24);
               if (var25.func_70067_L() && (var2 || !var25.func_70028_i(var3)) && !var25.field_70145_X) {
                  AxisAlignedBB var26 = var25.func_174813_aQ().func_186662_g(0.30000001192092896D);
                  RayTraceResult var27 = var26.func_72327_a(var17, var18);
                  if (var27 != null) {
                     double var28 = var17.func_72436_e(var27.field_72307_f);
                     if (var28 < var22 || var22 == 0.0D) {
                        var20 = var25;
                        var22 = var28;
                     }
                  }
               }
            }

            if (var20 != null) {
               var19 = new RayTraceResult(var20);
            }
         }

         return var19;
      }
   }

   private static Set<Entity> func_211325_a(Entity var0) {
      Entity var1 = var0.func_184187_bx();
      return var1 != null ? ImmutableSet.of(var0, var1) : ImmutableSet.of(var0);
   }

   public static final void func_188803_a(Entity var0, float var1) {
      double var2 = var0.field_70159_w;
      double var4 = var0.field_70181_x;
      double var6 = var0.field_70179_y;
      float var8 = MathHelper.func_76133_a(var2 * var2 + var6 * var6);
      var0.field_70177_z = (float)(MathHelper.func_181159_b(var6, var2) * 57.2957763671875D) + 90.0F;

      for(var0.field_70125_A = (float)(MathHelper.func_181159_b((double)var8, var4) * 57.2957763671875D) - 90.0F; var0.field_70125_A - var0.field_70127_C < -180.0F; var0.field_70127_C -= 360.0F) {
      }

      while(var0.field_70125_A - var0.field_70127_C >= 180.0F) {
         var0.field_70127_C += 360.0F;
      }

      while(var0.field_70177_z - var0.field_70126_B < -180.0F) {
         var0.field_70126_B -= 360.0F;
      }

      while(var0.field_70177_z - var0.field_70126_B >= 180.0F) {
         var0.field_70126_B += 360.0F;
      }

      var0.field_70125_A = var0.field_70127_C + (var0.field_70125_A - var0.field_70127_C) * var1;
      var0.field_70177_z = var0.field_70126_B + (var0.field_70177_z - var0.field_70126_B) * var1;
   }
}
