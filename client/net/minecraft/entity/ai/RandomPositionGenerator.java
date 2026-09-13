package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   private static Vec3 field_75465_a = Vec3.func_72443_a(0.0, 0.0, 0.0);

   public static Vec3 func_75463_a(EntityCreature var0, int var1, int var2) {
      return func_75462_c(var0, var1, var2, null);
   }

   public static Vec3 func_75464_a(EntityCreature var0, int var1, int var2, Vec3 var3) {
      field_75465_a.field_72450_a = var3.field_72450_a - var0.field_70165_t;
      field_75465_a.field_72448_b = var3.field_72448_b - var0.field_70163_u;
      field_75465_a.field_72449_c = var3.field_72449_c - var0.field_70161_v;
      return func_75462_c(var0, var1, var2, field_75465_a);
   }

   public static Vec3 func_75461_b(EntityCreature var0, int var1, int var2, Vec3 var3) {
      field_75465_a.field_72450_a = var0.field_70165_t - var3.field_72450_a;
      field_75465_a.field_72448_b = var0.field_70163_u - var3.field_72448_b;
      field_75465_a.field_72449_c = var0.field_70161_v - var3.field_72449_c;
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
         double var11 = (double)(
            var0.func_110172_bL()
                  .func_71569_e(
                     MathHelper.func_76128_c(var0.field_70165_t), MathHelper.func_76128_c(var0.field_70163_u), MathHelper.func_76128_c(var0.field_70161_v)
                  )
               + 4.0F
         );
         double var13 = (double)(var0.func_110174_bM() + (float)var1);
         var10 = var11 < var13 * var13;
      } else {
         var10 = false;
      }

      for(int var16 = 0; var16 < 10; ++var16) {
         int var12 = var4.nextInt(2 * var1) - var1;
         int var18 = var4.nextInt(2 * var2) - var2;
         int var14 = var4.nextInt(2 * var1) - var1;
         if (var3 == null || !((double)var12 * var3.field_72450_a + (double)var14 * var3.field_72449_c < 0.0)) {
            var12 += MathHelper.func_76128_c(var0.field_70165_t);
            var18 += MathHelper.func_76128_c(var0.field_70163_u);
            var14 += MathHelper.func_76128_c(var0.field_70161_v);
            if (!var10 || var0.func_110176_b(var12, var18, var14)) {
               float var15 = var0.func_70783_a(var12, var18, var14);
               if (var15 > var9) {
                  var9 = var15;
                  var6 = var12;
                  var7 = var18;
                  var8 = var14;
                  var5 = true;
               }
            }
         }
      }

      return var5 ? Vec3.func_72443_a((double)var6, (double)var7, (double)var8) : null;
   }
}
