package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeLargeLayer implements C1Transformer {
   INSTANCE;

   private RareBiomeLargeLayer() {
   }

   public int apply(Context var1, int var2) {
      return var1.nextRandom(10) == 0 && var2 == 21 ? 168 : var2;
   }
}
