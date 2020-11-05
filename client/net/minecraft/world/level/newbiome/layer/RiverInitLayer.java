package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public enum RiverInitLayer implements C0Transformer {
   INSTANCE;

   private RiverInitLayer() {
   }

   public int apply(Context var1, int var2) {
      return Layers.isShallowOcean(var2) ? var2 : var1.nextRandom(299999) + 2;
   }
}
