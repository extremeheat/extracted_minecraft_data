package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum GenLayerBiomeEdge implements ICastleTransformer {
   INSTANCE;

   private static final int field_202752_b = IRegistry.field_212624_m.func_148757_b(Biomes.field_76769_d);
   private static final int field_202753_c = IRegistry.field_212624_m.func_148757_b(Biomes.field_76770_e);
   private static final int field_202754_d = IRegistry.field_212624_m.func_148757_b(Biomes.field_150580_W);
   private static final int field_202755_e = IRegistry.field_212624_m.func_148757_b(Biomes.field_76774_n);
   private static final int field_202756_f = IRegistry.field_212624_m.func_148757_b(Biomes.field_76782_w);
   private static final int field_202757_g = IRegistry.field_212624_m.func_148757_b(Biomes.field_150574_L);
   private static final int field_202758_h = IRegistry.field_212624_m.func_148757_b(Biomes.field_150589_Z);
   private static final int field_202759_i = IRegistry.field_212624_m.func_148757_b(Biomes.field_150608_ab);
   private static final int field_202760_j = IRegistry.field_212624_m.func_148757_b(Biomes.field_150607_aa);
   private static final int field_202761_k = IRegistry.field_212624_m.func_148757_b(Biomes.field_76772_c);
   private static final int field_202762_l = IRegistry.field_212624_m.func_148757_b(Biomes.field_150578_U);
   private static final int field_202763_m = IRegistry.field_212624_m.func_148757_b(Biomes.field_76783_v);
   private static final int field_202764_n = IRegistry.field_212624_m.func_148757_b(Biomes.field_76780_h);
   private static final int field_202765_o = IRegistry.field_212624_m.func_148757_b(Biomes.field_76768_g);
   private static final int field_202766_p = IRegistry.field_212624_m.func_148757_b(Biomes.field_150584_S);

   private GenLayerBiomeEdge() {
   }

   public int func_202748_a(IContext var1, int var2, int var3, int var4, int var5, int var6) {
      int[] var7 = new int[1];
      if (!this.func_202751_a(var7, var2, var3, var4, var5, var6, field_202753_c, field_202763_m) && !this.func_151635_b(var7, var2, var3, var4, var5, var6, field_202760_j, field_202758_h) && !this.func_151635_b(var7, var2, var3, var4, var5, var6, field_202759_i, field_202758_h) && !this.func_151635_b(var7, var2, var3, var4, var5, var6, field_202762_l, field_202765_o)) {
         if (var6 == field_202752_b && (var2 == field_202755_e || var3 == field_202755_e || var5 == field_202755_e || var4 == field_202755_e)) {
            return field_202754_d;
         } else {
            if (var6 == field_202764_n) {
               if (var2 == field_202752_b || var3 == field_202752_b || var5 == field_202752_b || var4 == field_202752_b || var2 == field_202766_p || var3 == field_202766_p || var5 == field_202766_p || var4 == field_202766_p || var2 == field_202755_e || var3 == field_202755_e || var5 == field_202755_e || var4 == field_202755_e) {
                  return field_202761_k;
               }

               if (var2 == field_202756_f || var4 == field_202756_f || var3 == field_202756_f || var5 == field_202756_f) {
                  return field_202757_g;
               }
            }

            return var6;
         }
      } else {
         return var7[0];
      }
   }

   private boolean func_202751_a(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (!LayerUtil.func_202826_a(var6, var7)) {
         return false;
      } else {
         if (this.func_151634_b(var2, var7) && this.func_151634_b(var3, var7) && this.func_151634_b(var5, var7) && this.func_151634_b(var4, var7)) {
            var1[0] = var6;
         } else {
            var1[0] = var8;
         }

         return true;
      }
   }

   private boolean func_151635_b(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var6 != var7) {
         return false;
      } else {
         if (LayerUtil.func_202826_a(var2, var7) && LayerUtil.func_202826_a(var3, var7) && LayerUtil.func_202826_a(var5, var7) && LayerUtil.func_202826_a(var4, var7)) {
            var1[0] = var6;
         } else {
            var1[0] = var8;
         }

         return true;
      }
   }

   private boolean func_151634_b(int var1, int var2) {
      if (LayerUtil.func_202826_a(var1, var2)) {
         return true;
      } else {
         Biome var3 = (Biome)IRegistry.field_212624_m.func_148754_a(var1);
         Biome var4 = (Biome)IRegistry.field_212624_m.func_148754_a(var2);
         if (var3 != null && var4 != null) {
            Biome.TempCategory var5 = var3.func_150561_m();
            Biome.TempCategory var6 = var4.func_150561_m();
            return var5 == var6 || var5 == Biome.TempCategory.MEDIUM || var6 == Biome.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
