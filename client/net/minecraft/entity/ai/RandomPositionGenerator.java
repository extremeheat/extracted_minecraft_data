package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   private static Vec3 field_75465_a = new Vec3(0.0D, 0.0D, 0.0D);

   public static Vec3 func_75463_a(EntityCreature var0, int var1, int var2) {
      return func_75462_c(var0, var1, var2, (Vec3)null);
   }

   public static Vec3 func_75464_a(EntityCreature var0, int var1, int var2, Vec3 var3) {
      field_75465_a = var3.func_178786_a(var0.field_70165_t, var0.field_70163_u, var0.field_70161_v);
      return func_75462_c(var0, var1, var2, field_75465_a);
   }

   public static Vec3 func_75461_b(EntityCreature var0, int var1, int var2, Vec3 var3) {
      field_75465_a = (new Vec3(var0.field_70165_t, var0.field_70163_u, var0.field_70161_v)).func_178788_d(var3);
      return func_75462_c(var0, var1, var2, field_75465_a);
   }

   private static Vec3 func_75462_c(EntityCreature var0, int var1, int var2, Vec3 var3) {
      Random var4 = var0.func_70681_au();
      boolean var5 = false;
      int var6 = 0;
      int var7 = 0;
      int var8 = 0;
      float var9 = -99999.0F;
      boolean var10;
      if (var0.func_110175_bO()) {
         double var11 = var0.func_180486_cf().func_177954_c((double)MathHelper.func_76128_c(var0.field_70165_t), (double)MathHelper.func_76128_c(var0.field_70163_u), (double)MathHelper.func_76128_c(var0.field_70161_v)) + 4.0D;
         double var13 = (double)(var0.func_110174_bM() + (float)var1);
         var10 = var11 < var13 * var13;
      } else {
         var10 = false;
      }

      for(int var17 = 0; var17 < 10; ++var17) {
         int var12 = var4.nextInt(2 * var1 + 1) - var1;
         int var18 = var4.nextInt(2 * var2 + 1) - var2;
         int var14 = var4.nextInt(2 * var1 + 1) - var1;
         if (var3 == null || (double)var12 * var3.field_72450_a + (double)var14 * var3.field_72449_c >= 0.0D) {
            BlockPos var15;
            if (var0.func_110175_bO() && var1 > 1) {
               var15 = var0.func_180486_cf();
               if (var0.field_70165_t > (double)var15.func_177958_n()) {
                  var12 -= var4.nextInt(var1 / 2);
               } else {
                  var12 += var4.nextInt(var1 / 2);
               }

               if (var0.field_70161_v > (double)var15.func_177952_p()) {
                  var14 -= var4.nextInt(var1 / 2);
               } else {
                  var14 += var4.nextInt(var1 / 2);
               }
            }

            var12 += MathHelper.func_76128_c(var0.field_70165_t);
            var18 += MathHelper.func_76128_c(var0.field_70163_u);
            var14 += MathHelper.func_76128_c(var0.field_70161_v);
            var15 = new BlockPos(var12, var18, var14);
            if (!var10 || var0.func_180485_d(var15)) {
               float var16 = var0.func_180484_a(var15);
               if (var16 > var9) {
                  var9 = var16;
                  var6 = var12;
                  var7 = var18;
                  var8 = var14;
                  var5 = true;
               }
            }
         }
      }

      if (var5) {
         return new Vec3((double)var6, (double)var7, (double)var8);
      } else {
         return null;
      }
   }
}
