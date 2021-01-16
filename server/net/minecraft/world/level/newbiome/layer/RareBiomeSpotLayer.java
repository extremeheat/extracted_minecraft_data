package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeSpotLayer implements C1Transformer {
   INSTANCE;

   private RareBiomeSpotLayer() {
   }

   public int apply(Context var1, int var2) {
      return var1.nextRandom(57) == 0 && var2 == 1 ? 129 : var2;
   }
}
