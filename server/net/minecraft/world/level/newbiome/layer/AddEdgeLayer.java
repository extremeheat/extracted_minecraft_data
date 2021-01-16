package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public class AddEdgeLayer {
   public static enum IntroduceSpecial implements C0Transformer {
      INSTANCE;

      private IntroduceSpecial() {
      }

      public int apply(Context var1, int var2) {
         if (!Layers.isShallowOcean(var2) && var1.nextRandom(13) == 0) {
            var2 |= 1 + var1.nextRandom(15) << 8 & 3840;
         }

         return var2;
      }
   }

   public static enum HeatIce implements CastleTransformer {
      INSTANCE;

      private HeatIce() {
      }

      public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
         return var6 != 4 || var2 != 1 && var3 != 1 && var5 != 1 && var4 != 1 && var2 != 2 && var3 != 2 && var5 != 2 && var4 != 2 ? var6 : 3;
      }
   }

   public static enum CoolWarm implements CastleTransformer {
      INSTANCE;

      private CoolWarm() {
      }

      public int apply(Context var1, int var2, int var3, int var4, int var5, int var6) {
         return var6 != 1 || var2 != 3 && var3 != 3 && var5 != 3 && var4 != 3 && var2 != 4 && var3 != 4 && var5 != 4 && var4 != 4 ? var6 : 2;
      }
   }
}
