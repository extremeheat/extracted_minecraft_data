package net.minecraft.entity.item;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class PaintingType {
   public static final PaintingType field_200843_b = func_200836_a("kebab", 16, 16, 0, 0);
   public static final PaintingType field_200844_c = func_200836_a("aztec", 16, 16, 16, 0);
   public static final PaintingType field_200845_d = func_200836_a("alban", 16, 16, 32, 0);
   public static final PaintingType field_200846_e = func_200836_a("aztec2", 16, 16, 48, 0);
   public static final PaintingType field_200847_f = func_200836_a("bomb", 16, 16, 64, 0);
   public static final PaintingType field_200848_g = func_200836_a("plant", 16, 16, 80, 0);
   public static final PaintingType field_200849_h = func_200836_a("wasteland", 16, 16, 96, 0);
   public static final PaintingType field_200850_i = func_200836_a("pool", 32, 16, 0, 32);
   public static final PaintingType field_200851_j = func_200836_a("courbet", 32, 16, 32, 32);
   public static final PaintingType field_200852_k = func_200836_a("sea", 32, 16, 64, 32);
   public static final PaintingType field_200853_l = func_200836_a("sunset", 32, 16, 96, 32);
   public static final PaintingType field_200854_m = func_200836_a("creebet", 32, 16, 128, 32);
   public static final PaintingType field_200855_n = func_200836_a("wanderer", 16, 32, 0, 64);
   public static final PaintingType field_200856_o = func_200836_a("graham", 16, 32, 16, 64);
   public static final PaintingType field_200857_p = func_200836_a("match", 32, 32, 0, 128);
   public static final PaintingType field_200858_q = func_200836_a("bust", 32, 32, 32, 128);
   public static final PaintingType field_200859_r = func_200836_a("stage", 32, 32, 64, 128);
   public static final PaintingType field_200860_s = func_200836_a("void", 32, 32, 96, 128);
   public static final PaintingType field_200861_t = func_200836_a("skull_and_roses", 32, 32, 128, 128);
   public static final PaintingType field_200862_u = func_200836_a("wither", 32, 32, 160, 128);
   public static final PaintingType field_200863_v = func_200836_a("fighters", 64, 32, 0, 96);
   public static final PaintingType field_200864_w = func_200836_a("pointer", 64, 64, 0, 192);
   public static final PaintingType field_200865_x = func_200836_a("pigscene", 64, 64, 64, 192);
   public static final PaintingType field_200866_y = func_200836_a("burning_skull", 64, 64, 128, 192);
   public static final PaintingType field_200867_z = func_200836_a("skeleton", 64, 48, 192, 64);
   public static final PaintingType field_200837_A = func_200836_a("donkey_kong", 64, 48, 192, 112);
   private final int field_200838_B;
   private final int field_200839_C;
   private final int field_200840_D;
   private final int field_200841_E;

   public static void func_200831_a() {
   }

   public PaintingType(int var1, int var2, int var3, int var4) {
      super();
      this.field_200838_B = var1;
      this.field_200839_C = var2;
      this.field_200840_D = var3;
      this.field_200841_E = var4;
   }

   public int func_200834_b() {
      return this.field_200838_B;
   }

   public int func_200832_c() {
      return this.field_200839_C;
   }

   public int func_200833_d() {
      return this.field_200840_D;
   }

   public int func_200835_e() {
      return this.field_200841_E;
   }

   public static PaintingType func_200836_a(String var0, int var1, int var2, int var3, int var4) {
      PaintingType var5 = new PaintingType(var1, var2, var3, var4);
      IRegistry.field_212620_i.func_82595_a(new ResourceLocation(var0), var5);
      return var5;
   }
}
