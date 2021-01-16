package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum OceanLayer implements AreaTransformer0 {
   INSTANCE;

   private OceanLayer() {
   }

   public int applyPixel(Context var1, int var2, int var3) {
      ImprovedNoise var4 = var1.getBiomeNoise();
      double var5 = var4.noise((double)var2 / 8.0D, (double)var3 / 8.0D, 0.0D, 0.0D, 0.0D);
      if (var5 > 0.4D) {
         return 44;
      } else if (var5 > 0.2D) {
         return 45;
      } else if (var5 < -0.4D) {
         return 10;
      } else {
         return var5 < -0.2D ? 46 : 0;
      }
   }
}
