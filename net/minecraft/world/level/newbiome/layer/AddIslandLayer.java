package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddIslandLayer implements BishopTransformer {
   INSTANCE;

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      if (!Layers.isShallowOcean(var6) || Layers.isShallowOcean(var5) && Layers.isShallowOcean(var4) && Layers.isShallowOcean(var2) && Layers.isShallowOcean(var3)) {
         if (!Layers.isShallowOcean(var6) && (Layers.isShallowOcean(var5) || Layers.isShallowOcean(var2) || Layers.isShallowOcean(var4) || Layers.isShallowOcean(var3)) && var1.nextRandom(5) == 0) {
            if (Layers.isShallowOcean(var5)) {
               return var6 == 4 ? 4 : var5;
            }

            if (Layers.isShallowOcean(var2)) {
               return var6 == 4 ? 4 : var2;
            }

            if (Layers.isShallowOcean(var4)) {
               return var6 == 4 ? 4 : var4;
            }

            if (Layers.isShallowOcean(var3)) {
               return var6 == 4 ? 4 : var3;
            }
         }

         return var6;
      } else {
         int var7 = 1;
         int var8 = 1;
         if (!Layers.isShallowOcean(var5) && var1.nextRandom(var7++) == 0) {
            var8 = var5;
         }

         if (!Layers.isShallowOcean(var4) && var1.nextRandom(var7++) == 0) {
            var8 = var4;
         }

         if (!Layers.isShallowOcean(var2) && var1.nextRandom(var7++) == 0) {
            var8 = var2;
         }

         if (!Layers.isShallowOcean(var3) && var1.nextRandom(var7++) == 0) {
            var8 = var3;
         }

         if (var1.nextRandom(3) == 0) {
            return var8;
         } else {
            return var8 == 4 ? 4 : var6;
         }
      }
   }
}
