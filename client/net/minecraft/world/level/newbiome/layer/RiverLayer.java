package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RiverLayer implements CastleTransformer {
   INSTANCE;

   private RiverLayer() {
   }

   public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = riverFilter(var6);
      return var7 == riverFilter(var5) && var7 == riverFilter(var2) && var7 == riverFilter(var3) && var7 == riverFilter(var4) ? -1 : 7;
   }

   private static int riverFilter(int var0) {
      return var0 >= 2 ? 2 + (var0 & 1) : var0;
   }
}
