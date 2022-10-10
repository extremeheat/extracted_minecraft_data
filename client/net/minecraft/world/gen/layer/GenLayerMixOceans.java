package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum GenLayerMixOceans implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   private GenLayerMixOceans() {
   }

   public int func_202709_a(IContext var1, AreaDimension var2, IArea var3, IArea var4, int var5, int var6) {
      int var7 = var3.func_202678_a(var5, var6);
      int var8 = var4.func_202678_a(var5, var6);
      if (!LayerUtil.func_202827_a(var7)) {
         return var7;
      } else {
         boolean var9 = true;
         boolean var10 = true;

         for(int var11 = -8; var11 <= 8; var11 += 4) {
            for(int var12 = -8; var12 <= 8; var12 += 4) {
               int var13 = var3.func_202678_a(var5 + var11, var6 + var12);
               if (!LayerUtil.func_202827_a(var13)) {
                  if (var8 == LayerUtil.field_203632_a) {
                     return LayerUtil.field_203633_b;
                  }

                  if (var8 == LayerUtil.field_202831_b) {
                     return LayerUtil.field_203634_d;
                  }
               }
            }
         }

         if (var7 == LayerUtil.field_202830_a) {
            if (var8 == LayerUtil.field_203633_b) {
               return LayerUtil.field_203636_g;
            }

            if (var8 == LayerUtil.field_202832_c) {
               return LayerUtil.field_202830_a;
            }

            if (var8 == LayerUtil.field_203634_d) {
               return LayerUtil.field_203637_i;
            }

            if (var8 == LayerUtil.field_202831_b) {
               return LayerUtil.field_203638_j;
            }
         }

         return var8;
      }
   }
}
