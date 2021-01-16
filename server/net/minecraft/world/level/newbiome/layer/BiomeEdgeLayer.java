package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum BiomeEdgeLayer implements CastleTransformer {
   INSTANCE;

   private BiomeEdgeLayer() {
   }

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      int[] var7 = new int[1];
      if (!this.checkEdge(var7, var6) && !this.checkEdgeStrict(var7, var2, var3, var4, var5, var6, 38, 37) && !this.checkEdgeStrict(var7, var2, var3, var4, var5, var6, 39, 37) && !this.checkEdgeStrict(var7, var2, var3, var4, var5, var6, 32, 5)) {
         if (var6 == 2 && (var2 == 12 || var3 == 12 || var5 == 12 || var4 == 12)) {
            return 34;
         } else {
            if (var6 == 6) {
               if (var2 == 2 || var3 == 2 || var5 == 2 || var4 == 2 || var2 == 30 || var3 == 30 || var5 == 30 || var4 == 30 || var2 == 12 || var3 == 12 || var5 == 12 || var4 == 12) {
                  return 1;
               }

               if (var2 == 21 || var4 == 21 || var3 == 21 || var5 == 21 || var2 == 168 || var4 == 168 || var3 == 168 || var5 == 168) {
                  return 23;
               }
            }

            return var6;
         }
      } else {
         return var7[0];
      }
   }

   private boolean checkEdge(int[] var1, int var2) {
      if (!Layers.isSame(var2, 3)) {
         return false;
      } else {
         var1[0] = var2;
         return true;
      }
   }

   private boolean checkEdgeStrict(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var6 != var7) {
         return false;
      } else {
         if (Layers.isSame(var2, var7) && Layers.isSame(var3, var7) && Layers.isSame(var5, var7) && Layers.isSame(var4, var7)) {
            var1[0] = var6;
         } else {
            var1[0] = var8;
         }

         return true;
      }
   }
}
