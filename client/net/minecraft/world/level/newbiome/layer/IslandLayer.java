package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum IslandLayer implements AreaTransformer0 {
   INSTANCE;

   private IslandLayer() {
   }

   public int applyPixel(Context var1, int var2, int var3) {
      if (var2 == 0 && var3 == 0) {
         return 1;
      } else {
         return var1.nextRandom(10) == 0 ? 1 : 0;
      }
   }
}
