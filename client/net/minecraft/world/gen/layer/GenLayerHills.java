package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum GenLayerHills implements IAreaTransformer2, IDimOffset1Transformer {
   INSTANCE;

   private static final Logger field_151629_c = LogManager.getLogger();
   private static final int field_202796_c = IRegistry.field_212624_m.func_148757_b(Biomes.field_150583_P);
   private static final int field_202797_d = IRegistry.field_212624_m.func_148757_b(Biomes.field_150582_Q);
   private static final int field_202799_f = IRegistry.field_212624_m.func_148757_b(Biomes.field_76769_d);
   private static final int field_202800_g = IRegistry.field_212624_m.func_148757_b(Biomes.field_76786_s);
   private static final int field_202801_h = IRegistry.field_212624_m.func_148757_b(Biomes.field_76770_e);
   private static final int field_202802_i = IRegistry.field_212624_m.func_148757_b(Biomes.field_150580_W);
   private static final int field_202803_j = IRegistry.field_212624_m.func_148757_b(Biomes.field_76767_f);
   private static final int field_202804_k = IRegistry.field_212624_m.func_148757_b(Biomes.field_76785_t);
   private static final int field_202805_l = IRegistry.field_212624_m.func_148757_b(Biomes.field_76774_n);
   private static final int field_202806_m = IRegistry.field_212624_m.func_148757_b(Biomes.field_76775_o);
   private static final int field_202807_n = IRegistry.field_212624_m.func_148757_b(Biomes.field_76782_w);
   private static final int field_202808_o = IRegistry.field_212624_m.func_148757_b(Biomes.field_76792_x);
   private static final int field_202809_p = IRegistry.field_212624_m.func_148757_b(Biomes.field_150589_Z);
   private static final int field_202810_q = IRegistry.field_212624_m.func_148757_b(Biomes.field_150607_aa);
   private static final int field_202812_s = IRegistry.field_212624_m.func_148757_b(Biomes.field_76772_c);
   private static final int field_202813_t = IRegistry.field_212624_m.func_148757_b(Biomes.field_150578_U);
   private static final int field_202814_u = IRegistry.field_212624_m.func_148757_b(Biomes.field_150581_V);
   private static final int field_202815_v = IRegistry.field_212624_m.func_148757_b(Biomes.field_150585_R);
   private static final int field_202816_w = IRegistry.field_212624_m.func_148757_b(Biomes.field_150588_X);
   private static final int field_202817_x = IRegistry.field_212624_m.func_148757_b(Biomes.field_150587_Y);
   private static final int field_202818_y = IRegistry.field_212624_m.func_148757_b(Biomes.field_76768_g);
   private static final int field_202819_z = IRegistry.field_212624_m.func_148757_b(Biomes.field_150584_S);
   private static final int field_202794_A = IRegistry.field_212624_m.func_148757_b(Biomes.field_150579_T);
   private static final int field_202795_B = IRegistry.field_212624_m.func_148757_b(Biomes.field_76784_u);

   private GenLayerHills() {
   }

   public int func_202709_a(IContext var1, AreaDimension var2, IArea var3, IArea var4, int var5, int var6) {
      int var7 = var3.func_202678_a(var5 + 1, var6 + 1);
      int var8 = var4.func_202678_a(var5 + 1, var6 + 1);
      if (var7 > 255) {
         field_151629_c.debug("old! {}", var7);
      }

      int var9 = (var8 - 2) % 29;
      Biome var11;
      if (!LayerUtil.func_203631_b(var7) && var8 >= 2 && var9 == 1) {
         Biome var10 = (Biome)IRegistry.field_212624_m.func_148754_a(var7);
         if (var10 == null || !var10.func_185363_b()) {
            var11 = Biome.func_185356_b(var10);
            return var11 == null ? var7 : IRegistry.field_212624_m.func_148757_b(var11);
         }
      }

      if (var1.func_202696_a(3) == 0 || var9 == 0) {
         int var12 = var7;
         if (var7 == field_202799_f) {
            var12 = field_202800_g;
         } else if (var7 == field_202803_j) {
            var12 = field_202804_k;
         } else if (var7 == field_202796_c) {
            var12 = field_202797_d;
         } else if (var7 == field_202815_v) {
            var12 = field_202812_s;
         } else if (var7 == field_202818_y) {
            var12 = field_202795_B;
         } else if (var7 == field_202813_t) {
            var12 = field_202814_u;
         } else if (var7 == field_202819_z) {
            var12 = field_202794_A;
         } else if (var7 == field_202812_s) {
            var12 = var1.func_202696_a(3) == 0 ? field_202804_k : field_202803_j;
         } else if (var7 == field_202805_l) {
            var12 = field_202806_m;
         } else if (var7 == field_202807_n) {
            var12 = field_202808_o;
         } else if (var7 == LayerUtil.field_202832_c) {
            var12 = LayerUtil.field_202830_a;
         } else if (var7 == LayerUtil.field_203633_b) {
            var12 = LayerUtil.field_203636_g;
         } else if (var7 == LayerUtil.field_203634_d) {
            var12 = LayerUtil.field_203637_i;
         } else if (var7 == LayerUtil.field_202831_b) {
            var12 = LayerUtil.field_203638_j;
         } else if (var7 == field_202801_h) {
            var12 = field_202802_i;
         } else if (var7 == field_202816_w) {
            var12 = field_202817_x;
         } else if (LayerUtil.func_202826_a(var7, field_202810_q)) {
            var12 = field_202809_p;
         } else if ((var7 == LayerUtil.field_202830_a || var7 == LayerUtil.field_203636_g || var7 == LayerUtil.field_203637_i || var7 == LayerUtil.field_203638_j) && var1.func_202696_a(3) == 0) {
            var12 = var1.func_202696_a(2) == 0 ? field_202812_s : field_202803_j;
         }

         if (var9 == 0 && var12 != var7) {
            var11 = Biome.func_185356_b((Biome)IRegistry.field_212624_m.func_148754_a(var12));
            var12 = var11 == null ? var7 : IRegistry.field_212624_m.func_148757_b(var11);
         }

         if (var12 != var7) {
            int var13 = 0;
            if (LayerUtil.func_202826_a(var3.func_202678_a(var5 + 1, var6 + 0), var7)) {
               ++var13;
            }

            if (LayerUtil.func_202826_a(var3.func_202678_a(var5 + 2, var6 + 1), var7)) {
               ++var13;
            }

            if (LayerUtil.func_202826_a(var3.func_202678_a(var5 + 0, var6 + 1), var7)) {
               ++var13;
            }

            if (LayerUtil.func_202826_a(var3.func_202678_a(var5 + 1, var6 + 2), var7)) {
               ++var13;
            }

            if (var13 >= 3) {
               return var12;
            }
         }
      }

      return var7;
   }
}
