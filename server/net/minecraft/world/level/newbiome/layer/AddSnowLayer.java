package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum AddSnowLayer implements C1Transformer {
   INSTANCE;

   private AddSnowLayer() {
   }

   public int apply(Context var1, int var2) {
      if (Layers.isShallowOcean(var2)) {
         return var2;
      } else {
         int var3 = var1.nextRandom(6);
         if (var3 == 0) {
            return 4;
         } else {
            return var3 == 1 ? 3 : 1;
         }
      }
   }
}
