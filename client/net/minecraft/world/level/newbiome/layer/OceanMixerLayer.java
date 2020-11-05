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
                  if (var7 == 44) {
                     return 45;
                  }

                  if (var7 == 10) {
                     return 46;
                  }
               }
            }
         }

         if (var6 == 24) {
            if (var7 == 45) {
               return 48;
            }

            if (var7 == 0) {
               return 24;
            }

            if (var7 == 46) {
               return 49;
            }

            if (var7 == 10) {
               return 50;
            }
         }

         return var7;
      }
   }
}
