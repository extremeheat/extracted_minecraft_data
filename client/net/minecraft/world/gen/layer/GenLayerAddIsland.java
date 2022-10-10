package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum GenLayerAddIsland implements IBishopTransformer {
   INSTANCE;

   private GenLayerAddIsland() {
   }

   public int func_202792_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
      if (!LayerUtil.func_203631_b(var6) || LayerUtil.func_203631_b(var5) && LayerUtil.func_203631_b(var4) && LayerUtil.func_203631_b(var2) && LayerUtil.func_203631_b(var3)) {
         if (!LayerUtil.func_203631_b(var6) && (LayerUtil.func_203631_b(var5) || LayerUtil.func_203631_b(var2) || LayerUtil.func_203631_b(var4) || LayerUtil.func_203631_b(var3)) && var1.func_202696_a(5) == 0) {
            if (LayerUtil.func_203631_b(var5)) {
               return var6 == 4 ? 4 : var5;
            }

            if (LayerUtil.func_203631_b(var2)) {
               return var6 == 4 ? 4 : var2;
            }

            if (LayerUtil.func_203631_b(var4)) {
               return var6 == 4 ? 4 : var4;
            }

            if (LayerUtil.func_203631_b(var3)) {
               return var6 == 4 ? 4 : var3;
            }
         }

         return var6;
      } else {
         int var7 = 1;
         int var8 = 1;
         if (!LayerUtil.func_203631_b(var5) && var1.func_202696_a(var7++) == 0) {
            var8 = var5;
         }

         if (!LayerUtil.func_203631_b(var4) && var1.func_202696_a(var7++) == 0) {
            var8 = var4;
         }

         if (!LayerUtil.func_203631_b(var2) && var1.func_202696_a(var7++) == 0) {
            var8 = var2;
         }

         if (!LayerUtil.func_203631_b(var3) && var1.func_202696_a(var7++) == 0) {
            var8 = var3;
         }

         if (var1.func_202696_a(3) == 0) {
            return var8;
         } else {
            return var8 == 4 ? 4 : var6;
         }
      }
   }
}
