package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC1Transformer;

public enum GenLayerAddSnow implements IC1Transformer {
   INSTANCE;

   private GenLayerAddSnow() {
   }

   public int func_202716_a(IContext var1, int var2) {
      if (LayerUtil.func_203631_b(var2)) {
         return var2;
      } else {
         int var3 = var1.func_202696_a(6);
         if (var3 == 0) {
            return 4;
         } else {
            return var3 == 1 ? 3 : 1;
         }
      }
   }
}
