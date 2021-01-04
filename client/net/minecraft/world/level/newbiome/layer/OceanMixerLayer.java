package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;

public enum OceanMixerLayer implements AreaTransformer2, DimensionOffset0Transformer {
   INSTANCE;

   private OceanMixerLayer() {
   }

   public int applyPixel(Context var1, Area var2, Area var3, int var4, int var5) {
      int var6 = var2.get(this.getParentX(var4), this.getParentY(var5));
      int var7 = var3.get(this.getParentX(var4), this.getParentY(var5));
      if (!Layers.isOcean(var6)) {
         return var6;
      } else {
         boolean var8 = true;
         boolean var9 = true;

         for(int var10 = -8; var10 <= 8; var10 += 4) {
            for(int var11 = -8; var11 <= 8; var11 += 4) {
               int var12 = var2.get(this.getParentX(var4 + var10), this.getParentY(var5 + var11));
               if (!Layers.isOcean(var12)) {
                  if (var7 == Layers.WARM_OCEAN) {
                     return Layers.LUKEWARM_OCEAN;
                  }

                  if (var7 == Layers.FROZEN_OCEAN) {
                     return Layers.COLD_OCEAN;
                  }
               }
            }
         }

         if (var6 == Layers.DEEP_OCEAN) {
            if (var7 == Layers.LUKEWARM_OCEAN) {
               return Layers.DEEP_LUKEWARM_OCEAN;
            }

            if (var7 == Layers.OCEAN) {
               return Layers.DEEP_OCEAN;
            }

            if (var7 == Layers.COLD_OCEAN) {
               return Layers.DEEP_COLD_OCEAN;
            }

            if (var7 == Layers.FROZEN_OCEAN) {
               return Layers.DEEP_FROZEN_OCEAN;
            }
         }

         return var7;
      }
   }
}
