package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerDeepOcean implements ICastleTransformer {
   INSTANCE;

   private GenLayerDeepOcean() {
   }

   public int func_202748_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
      if (LayerUtil.func_203631_b(var6)) {
         int var7 = 0;
         if (LayerUtil.func_203631_b(var2)) {
            ++var7;
         }

         if (LayerUtil.func_203631_b(var3)) {
            ++var7;
         }

         if (LayerUtil.func_203631_b(var5)) {
            ++var7;
         }

         if (LayerUtil.func_203631_b(var4)) {
            ++var7;
         }

         if (var7 > 3) {
            if (var6 == LayerUtil.field_203632_a) {
               return LayerUtil.field_203635_f;
            }

            if (var6 == LayerUtil.field_203633_b) {
               return LayerUtil.field_203636_g;
            }

            if (var6 == LayerUtil.field_202832_c) {
               return LayerUtil.field_202830_a;
            }

            if (var6 == LayerUtil.field_203634_d) {
               return LayerUtil.field_203637_i;
            }

            if (var6 == LayerUtil.field_202831_b) {
               return LayerUtil.field_203638_j;
            }

            return LayerUtil.field_202830_a;
         }
      }

      return var6;
   }
}
