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
            if (var6 == Layers.WARM_OCEAN) {
               return Layers.DEEP_WARM_OCEAN;
            }

            if (var6 == Layers.LUKEWARM_OCEAN) {
               return Layers.DEEP_LUKEWARM_OCEAN;
            }

            if (var6 == Layers.OCEAN) {
               return Layers.DEEP_OCEAN;
            }

            if (var6 == Layers.COLD_OCEAN) {
               return Layers.DEEP_COLD_OCEAN;
            }

            if (var6 == Layers.FROZEN_OCEAN) {
               return Layers.DEEP_FROZEN_OCEAN;
            }

            return Layers.DEEP_OCEAN;
         }
      }

      return var6;
   }
}
