package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class GenLayerBiome implements IC0Transformer {
   private static final int field_202727_a;
   private static final int field_202728_b;
   private static final int field_202729_c;
   private static final int field_202730_d;
   private static final int field_202731_e;
   private static final int field_202732_f;
   private static final int field_202733_g;
   private static final int field_202734_h;
   private static final int field_202735_i;
   private static final int field_202736_j;
   private static final int field_202737_k;
   private static final int field_202738_l;
   private static final int field_202739_m;
   private static final int field_202740_n;
   private static final int field_202741_o;
   private static final int field_202742_p;
   private static final int[] field_202743_q;
   private static final int[] field_202744_r;
   private static final int[] field_202745_s;
   private static final int[] field_202746_t;
   private static final int[] field_202747_u;
   private final OverworldGenSettings field_175973_g;
   private int[] field_151623_c;

   public GenLayerBiome(WorldType var1, OverworldGenSettings var2) {
      super();
      this.field_151623_c = field_202744_r;
      if (var1 == WorldType.field_77136_e) {
         this.field_151623_c = field_202743_q;
         this.field_175973_g = null;
      } else {
         this.field_175973_g = var2;
      }

   }

   public int func_202726_a(IContext var1, int var2) {
      if (this.field_175973_g != null && this.field_175973_g.func_202199_l() >= 0) {
         return this.field_175973_g.func_202199_l();
      } else {
         int var3 = (var2 & 3840) >> 8;
         var2 &= -3841;
         if (!LayerUtil.func_202827_a(var2) && var2 != field_202735_i) {
            switch(var2) {
            case 1:
               if (var3 > 0) {
                  return var1.func_202696_a(3) == 0 ? field_202733_g : field_202734_h;
               }

               return this.field_151623_c[var1.func_202696_a(this.field_151623_c.length)];
            case 2:
               if (var3 > 0) {
                  return field_202732_f;
               }

               return field_202745_s[var1.func_202696_a(field_202745_s.length)];
            case 3:
               if (var3 > 0) {
                  return field_202737_k;
               }

               return field_202746_t[var1.func_202696_a(field_202746_t.length)];
            case 4:
               return field_202747_u[var1.func_202696_a(field_202747_u.length)];
            default:
               return field_202735_i;
            }
         } else {
            return var2;
         }
      }
   }

   static {
      field_202727_a = IRegistry.field_212624_m.func_148757_b(Biomes.field_150583_P);
      field_202728_b = IRegistry.field_212624_m.func_148757_b(Biomes.field_76769_d);
      field_202729_c = IRegistry.field_212624_m.func_148757_b(Biomes.field_76770_e);
      field_202730_d = IRegistry.field_212624_m.func_148757_b(Biomes.field_76767_f);
      field_202731_e = IRegistry.field_212624_m.func_148757_b(Biomes.field_76774_n);
      field_202732_f = IRegistry.field_212624_m.func_148757_b(Biomes.field_76782_w);
      field_202733_g = IRegistry.field_212624_m.func_148757_b(Biomes.field_150608_ab);
      field_202734_h = IRegistry.field_212624_m.func_148757_b(Biomes.field_150607_aa);
      field_202735_i = IRegistry.field_212624_m.func_148757_b(Biomes.field_76789_p);
      field_202736_j = IRegistry.field_212624_m.func_148757_b(Biomes.field_76772_c);
      field_202737_k = IRegistry.field_212624_m.func_148757_b(Biomes.field_150578_U);
      field_202738_l = IRegistry.field_212624_m.func_148757_b(Biomes.field_150585_R);
      field_202739_m = IRegistry.field_212624_m.func_148757_b(Biomes.field_150588_X);
      field_202740_n = IRegistry.field_212624_m.func_148757_b(Biomes.field_76780_h);
      field_202741_o = IRegistry.field_212624_m.func_148757_b(Biomes.field_76768_g);
      field_202742_p = IRegistry.field_212624_m.func_148757_b(Biomes.field_150584_S);
      field_202743_q = new int[]{field_202728_b, field_202730_d, field_202729_c, field_202740_n, field_202736_j, field_202741_o};
      field_202744_r = new int[]{field_202728_b, field_202728_b, field_202728_b, field_202739_m, field_202739_m, field_202736_j};
      field_202745_s = new int[]{field_202730_d, field_202738_l, field_202729_c, field_202736_j, field_202727_a, field_202740_n};
      field_202746_t = new int[]{field_202730_d, field_202729_c, field_202741_o, field_202736_j};
      field_202747_u = new int[]{field_202731_e, field_202731_e, field_202731_e, field_202742_p};
   }
}
