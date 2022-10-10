package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerRemoveTooMuchOcean implements ICastleTransformer {
   INSTANCE;

   private GenLayerRemoveTooMuchOcean() {
   }

   public int func_202748_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
      return LayerUtil.func_203631_b(var6) && LayerUtil.func_203631_b(var2) && LayerUtil.func_203631_b(var3) && LayerUtil.func_203631_b(var5) && LayerUtil.func_203631_b(var4) && var1.func_202696_a(2) == 0 ? 1 : var6;
   }
}
