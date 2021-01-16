package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;

public enum RiverMixerLayer implements AreaTransformer2, DimensionOffset0Transformer {
   INSTANCE;

   private RiverMixerLayer() {
   }

   public int applyPixel(Context var1, Area var2, Area var3, int var4, int var5) {
      int var6 = var2.get(this.getParentX(var4), this.getParentY(var5));
      int var7 = var3.get(this.getParentX(var4), this.getParentY(var5));
      if (Layers.isOcean(var6)) {
         return var6;
      } else if (var7 == 7) {
         if (var6 == 12) {
            return 11;
         } else {
            return var6 != 14 && var6 != 15 ? var7 & 255 : 15;
         }
      } else {
         return var6;
      }
   }
}
