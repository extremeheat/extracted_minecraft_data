package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum AddDeepOceanLayer implements CastleTransformer {
   INSTANCE;

   private AddDeepOceanLayer() {
   }

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      if (Layers.isShallowOcean(var6)) {
         int var7 = 0;
         if (Layers.isShallowOcean(var2)) {
            ++var7;
         }

         if (Layers.isShallowOcean(var3)) {
            ++var7;
         }

         if (Layers.isShallowOcean(var5)) {
            ++var7;
         }

         if (Layers.isShallowOcean(var4)) {
            ++var7;
         }

         if (var7 > 3) {
            if (var6 == 44) {
               return 47;
            }

            if (var6 == 45) {
               return 48;
            }

            if (var6 == 0) {
               return 24;
            }

            if (var6 == 46) {
               return 49;
            }

            if (var6 == 10) {
               return 50;
            }

            return 24;
         }
      }

      return var6;
   }
}
