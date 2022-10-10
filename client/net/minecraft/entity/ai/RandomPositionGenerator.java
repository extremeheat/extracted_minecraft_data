package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGenerator {
   @Nullable
   public static Vec3d func_75463_a(EntityCreature var0, int var1, int var2) {
      return func_75462_c(var0, var1, var2, (Vec3d)null);
   }

   @Nullable
   public static Vec3d func_191377_b(EntityCreature var0, int var1, int var2) {
      return func_191379_a(var0, var1, var2, (Vec3d)null, false, 0.0D);
   }

   @Nullable
   public static Vec3d func_75464_a(EntityCreature var0, int var1, int var2, Vec3d var3) {
      Vec3d var4 = var3.func_178786_a(var0.field_70165_t, var0.field_70163_u, var0.field_70161_v);
      return func_75462_c(var0, var1, var2, var4);
   }

   @Nullable
   public static Vec3d func_203155_a(EntityCreature var0, int var1, int var2, Vec3d var3, double var4) {
      Vec3d var6 = var3.func_178786_a(var0.field_70165_t, var0.field_70163_u, var0.field_70161_v);
      return func_191379_a(var0, var1, var2, var6, true, var4);
   }

   @Nullable
   public static Vec3d func_75461_b(EntityCreature var0, int var1, int var2, Vec3d var3) {
      Vec3d var4 = (new Vec3d(var0.field_70165_t, var0.field_70163_u, var0.field_70161_v)).func_178788_d(var3);
      return func_75462_c(var0, var1, var2, var4);
   }

   @Nullable
   private static Vec3d func_75462_c(EntityCreature var0, int var1, int var2, @Nullable Vec3d var3) {
      return func_191379_a(var0, var1, var2, var3, true, 1.5707963705062866D);
   }

   @Nullable
   private static Vec3d func_191379_a(EntityCreature var0, int var1, int var2, @Nullable Vec3d var3, boolean var4, double var5) {
      PathNavigate var7 = var0.func_70661_as();
      Random var8 = var0.func_70681_au();
      boolean var9;
      if (var0.func_110175_bO()) {
         double var10 = var0.func_180486_cf().func_177954_c((double)MathHelper.func_76128_c(var0.field_70165_t), (double)MathHelper.func_76128_c(var0.field_70163_u), (double)MathHelper.func_76128_c(var0.field_70161_v)) + 4.0D;
         double var12 = (double)(var0.func_110174_bM() + (float)var1);
         var9 = var10 < var12 * var12;
      } else {
         var9 = false;
      }

      boolean var22 = false;
      float var11 = -99999.0F;
      int var23 = 0;
      int var13 = 0;
      int var14 = 0;

      for(int var15 = 0; var15 < 10; ++var15) {
         BlockPos var16 = func_203156_a(var8, var1, var2, var3, var5);
         if (var16 != null) {
            int var17 = var16.func_177958_n();
            int var18 = var16.func_177956_o();
            int var19 = var16.func_177952_p();
            BlockPos var20;
            if (var0.func_110175_bO() && var1 > 1) {
               var20 = var0.func_180486_cf();
               if (var0.field_70165_t > (double)var20.func_177958_n()) {
                  var17 -= var8.nextInt(var1 / 2);
               } else {
                  var17 += var8.nextInt(var1 / 2);
               }

               if (var0.field_70161_v > (double)var20.func_177952_p()) {
                  var19 -= var8.nextInt(var1 / 2);
               } else {
                  var19 += var8.nextInt(var1 / 2);
               }
            }

            var20 = new BlockPos((double)var17 + var0.field_70165_t, (double)var18 + var0.field_70163_u, (double)var19 + var0.field_70161_v);
            if ((!var9 || var0.func_180485_d(var20)) && var7.func_188555_b(var20)) {
               if (!var4) {
                  var20 = func_191378_a(var20, var0);
                  if (func_191380_b(var20, var0)) {
                     continue;
                  }
               }

               float var21 = var0.func_180484_a(var20);
               if (var21 > var11) {
                  var11 = var21;
                  var23 = var17;
                  var13 = var18;
                  var14 = var19;
                  var22 = true;
               }
            }
         }
      }

      if (var22) {
         return new Vec3d((double)var23 + var0.field_70165_t, (double)var13 + var0.field_70163_u, (double)var14 + var0.field_70161_v);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos func_203156_a(Random var0, int var1, int var2, @Nullable Vec3d var3, double var4) {
      if (var3 != null && var4 < 3.141592653589793D) {
         double var17 = MathHelper.func_181159_b(var3.field_72449_c, var3.field_72450_a) - 1.5707963705062866D;
         double var18 = var17 + (double)(2.0F * var0.nextFloat() - 1.0F) * var4;
         double var10 = Math.sqrt(var0.nextDouble()) * (double)MathHelper.field_180189_a * (double)var1;
         double var12 = -var10 * Math.sin(var18);
         double var14 = var10 * Math.cos(var18);
         if (Math.abs(var12) <= (double)var1 && Math.abs(var14) <= (double)var1) {
            int var16 = var0.nextInt(2 * var2 + 1) - var2;
            return new BlockPos(var12, (double)var16, var14);
         } else {
            return null;
         }
      } else {
         int var6 = var0.nextInt(2 * var1 + 1) - var1;
         int var7 = var0.nextInt(2 * var2 + 1) - var2;
         int var8 = var0.nextInt(2 * var1 + 1) - var1;
         return new BlockPos(var6, var7, var8);
      }
   }

   private static BlockPos func_191378_a(BlockPos var0, EntityCreature var1) {
      if (!var1.field_70170_p.func_180495_p(var0).func_185904_a().func_76220_a()) {
         return var0;
      } else {
         BlockPos var2;
         for(var2 = var0.func_177984_a(); var2.func_177956_o() < var1.field_70170_p.func_72800_K() && var1.field_70170_p.func_180495_p(var2).func_185904_a().func_76220_a(); var2 = var2.func_177984_a()) {
         }

         return var2;
      }
   }

   private static boolean func_191380_b(BlockPos var0, EntityCreature var1) {
      return var1.field_70170_p.func_204610_c(var0).func_206884_a(FluidTags.field_206959_a);
   }
}
