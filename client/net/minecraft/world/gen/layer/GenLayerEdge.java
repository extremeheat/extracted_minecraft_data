package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class GenLayerEdge {
   public static enum Special implements IC0Transformer {
      INSTANCE;

      private Special() {
      }

      public int func_202726_a(IContext var1, int var2) {
         if (!LayerUtil.func_203631_b(var2) && var1.func_202696_a(13) == 0) {
            var2 |= 1 + var1.func_202696_a(15) << 8 & 3840;
         }

         return var2;
      }
   }

   public static enum HeatIce implements ICastleTransformer {
      INSTANCE;

      private HeatIce() {
      }

      public int func_202748_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
         return var6 != 4 || var2 != 1 && var3 != 1 && var5 != 1 && var4 != 1 && var2 != 2 && var3 != 2 && var5 != 2 && var4 != 2 ? var6 : 3;
      }
   }

   public static enum CoolWarm implements ICastleTransformer {
      INSTANCE;

      private CoolWarm() {
      }

      public int func_202748_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
         return var6 != 1 || var2 != 3 && var3 != 3 && var5 != 3 && var4 != 3 && var2 != 4 && var3 != 4 && var5 != 4 && var4 != 4 ? var6 : 2;
      }
   }
}
