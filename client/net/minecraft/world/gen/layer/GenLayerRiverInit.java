package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum GenLayerRiverInit implements IC0Transformer {
   INSTANCE;

   private GenLayerRiverInit() {
   }

   public int func_202726_a(IContext var1, int var2) {
      return LayerUtil.func_203631_b(var2) ? var2 : var1.func_202696_a(299999) + 2;
   }
}
