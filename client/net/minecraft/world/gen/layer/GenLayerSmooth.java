package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerSmooth implements ICastleTransformer {
   INSTANCE;

   private GenLayerSmooth() {
   }

   public int func_202748_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
      boolean var7 = var3 == var5;
      boolean var8 = var2 == var4;
      if (var7 == var8) {
         if (var7) {
            return var1.func_202696_a(2) == 0 ? var5 : var2;
         } else {
            return var6;
         }
      } else {
         return var7 ? var5 : var2;
      }
   }
}
