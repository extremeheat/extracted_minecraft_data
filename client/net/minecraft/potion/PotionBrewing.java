package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.IRegistry;

public class PotionBrewing {
   private static final List<PotionBrewing.MixPredicate<PotionType>> field_185213_a = Lists.newArrayList();
   private static final List<PotionBrewing.MixPredicate<Item>> field_185214_b = Lists.newArrayList();
   private static final List<Ingredient> field_185215_c = Lists.newArrayList();
   private static final Predicate<ItemStack> field_185216_d = (var0) -> {
      Iterator var1 = field_185215_c.iterator();

      Ingredient var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (Ingredient)var1.next();
      } while(!var2.test(var0));

      return true;
   };

   public static boolean func_185205_a(ItemStack var0) {
      return func_185203_b(var0) || func_185211_c(var0);
   }

   protected static boolean func_185203_b(ItemStack var0) {
      int var1 = 0;

      for(int var2 = field_185214_b.size(); var1 < var2; ++var1) {
         if (((PotionBrewing.MixPredicate)field_185214_b.get(var1)).field_185199_b.test(var0)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean func_185211_c(ItemStack var0) {
      int var1 = 0;

      for(int var2 = field_185213_a.size(); var1 < var2; ++var1) {
         if (((PotionBrewing.MixPredicate)field_185213_a.get(var1)).field_185199_b.test(var0)) {
            return true;
         }
      }

      return false;
   }

   public static boolean func_185208_a(ItemStack var0, ItemStack var1) {
      if (!field_185216_d.test(var0)) {
         return false;
      } else {
         return func_185206_b(var0, var1) || func_185209_c(var0, var1);
      }
   }

   protected static boolean func_185206_b(ItemStack var0, ItemStack var1) {
      Item var2 = var0.func_77973_b();
      int var3 = 0;

      for(int var4 = field_185214_b.size(); var3 < var4; ++var3) {
         PotionBrewing.MixPredicate var5 = (PotionBrewing.MixPredicate)field_185214_b.get(var3);
         if (var5.field_185198_a == var2 && var5.field_185199_b.test(var1)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean func_185209_c(ItemStack var0, ItemStack var1) {
      PotionType var2 = PotionUtils.func_185191_c(var0);
      int var3 = 0;

      for(int var4 = field_185213_a.size(); var3 < var4; ++var3) {
         PotionBrewing.MixPredicate var5 = (PotionBrewing.MixPredicate)field_185213_a.get(var3);
         if (var5.field_185198_a == var2 && var5.field_185199_b.test(var1)) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack func_185212_d(ItemStack var0, ItemStack var1) {
      if (!var1.func_190926_b()) {
         PotionType var2 = PotionUtils.func_185191_c(var1);
         Item var3 = var1.func_77973_b();
         int var4 = 0;

         int var5;
         PotionBrewing.MixPredicate var6;
         for(var5 = field_185214_b.size(); var4 < var5; ++var4) {
            var6 = (PotionBrewing.MixPredicate)field_185214_b.get(var4);
            if (var6.field_185198_a == var3 && var6.field_185199_b.test(var0)) {
               return PotionUtils.func_185188_a(new ItemStack((IItemProvider)var6.field_185200_c), var2);
            }
         }

         var4 = 0;

         for(var5 = field_185213_a.size(); var4 < var5; ++var4) {
            var6 = (PotionBrewing.MixPredicate)field_185213_a.get(var4);
            if (var6.field_185198_a == var2 && var6.field_185199_b.test(var0)) {
               return PotionUtils.func_185188_a(new ItemStack(var3), (PotionType)var6.field_185200_c);
            }
         }
      }

      return var1;
   }

   public static void func_185207_a() {
      func_196208_a(Items.field_151068_bn);
      func_196208_a(Items.field_185155_bH);
      func_196208_a(Items.field_185156_bI);
      func_196207_a(Items.field_151068_bn, Items.field_151016_H, Items.field_185155_bH);
      func_196207_a(Items.field_185155_bH, Items.field_185157_bK, Items.field_185156_bI);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151060_bw, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151073_bk, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_179556_br, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151065_br, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151070_bp, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151102_aT, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151064_bs, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151114_aO, PotionTypes.field_185232_d);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151137_ax, PotionTypes.field_185231_c);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151075_bm, PotionTypes.field_185233_e);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151150_bK, PotionTypes.field_185234_f);
      func_193357_a(PotionTypes.field_185234_f, Items.field_151137_ax, PotionTypes.field_185235_g);
      func_193357_a(PotionTypes.field_185234_f, Items.field_151071_bq, PotionTypes.field_185236_h);
      func_193357_a(PotionTypes.field_185235_g, Items.field_151071_bq, PotionTypes.field_185237_i);
      func_193357_a(PotionTypes.field_185236_h, Items.field_151137_ax, PotionTypes.field_185237_i);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151064_bs, PotionTypes.field_185241_m);
      func_193357_a(PotionTypes.field_185241_m, Items.field_151137_ax, PotionTypes.field_185242_n);
      func_193357_a(PotionTypes.field_185233_e, Items.field_179556_br, PotionTypes.field_185238_j);
      func_193357_a(PotionTypes.field_185238_j, Items.field_151137_ax, PotionTypes.field_185239_k);
      func_193357_a(PotionTypes.field_185238_j, Items.field_151114_aO, PotionTypes.field_185240_l);
      func_193357_a(PotionTypes.field_185238_j, Items.field_151071_bq, PotionTypes.field_185246_r);
      func_193357_a(PotionTypes.field_185239_k, Items.field_151071_bq, PotionTypes.field_185247_s);
      func_193357_a(PotionTypes.field_185246_r, Items.field_151137_ax, PotionTypes.field_185247_s);
      func_193357_a(PotionTypes.field_185246_r, Items.field_151114_aO, PotionTypes.field_203185_t);
      func_193357_a(PotionTypes.field_185233_e, Items.field_203179_ao, PotionTypes.field_203186_u);
      func_193357_a(PotionTypes.field_203186_u, Items.field_151137_ax, PotionTypes.field_203187_v);
      func_193357_a(PotionTypes.field_203186_u, Items.field_151114_aO, PotionTypes.field_203188_w);
      func_193357_a(PotionTypes.field_185243_o, Items.field_151071_bq, PotionTypes.field_185246_r);
      func_193357_a(PotionTypes.field_185244_p, Items.field_151071_bq, PotionTypes.field_185247_s);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151102_aT, PotionTypes.field_185243_o);
      func_193357_a(PotionTypes.field_185243_o, Items.field_151137_ax, PotionTypes.field_185244_p);
      func_193357_a(PotionTypes.field_185243_o, Items.field_151114_aO, PotionTypes.field_185245_q);
      func_193357_a(PotionTypes.field_185233_e, Items.field_196089_aZ, PotionTypes.field_185248_t);
      func_193357_a(PotionTypes.field_185248_t, Items.field_151137_ax, PotionTypes.field_185249_u);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151060_bw, PotionTypes.field_185250_v);
      func_193357_a(PotionTypes.field_185250_v, Items.field_151114_aO, PotionTypes.field_185251_w);
      func_193357_a(PotionTypes.field_185250_v, Items.field_151071_bq, PotionTypes.field_185252_x);
      func_193357_a(PotionTypes.field_185251_w, Items.field_151071_bq, PotionTypes.field_185253_y);
      func_193357_a(PotionTypes.field_185252_x, Items.field_151114_aO, PotionTypes.field_185253_y);
      func_193357_a(PotionTypes.field_185254_z, Items.field_151071_bq, PotionTypes.field_185252_x);
      func_193357_a(PotionTypes.field_185218_A, Items.field_151071_bq, PotionTypes.field_185252_x);
      func_193357_a(PotionTypes.field_185219_B, Items.field_151071_bq, PotionTypes.field_185253_y);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151070_bp, PotionTypes.field_185254_z);
      func_193357_a(PotionTypes.field_185254_z, Items.field_151137_ax, PotionTypes.field_185218_A);
      func_193357_a(PotionTypes.field_185254_z, Items.field_151114_aO, PotionTypes.field_185219_B);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151073_bk, PotionTypes.field_185220_C);
      func_193357_a(PotionTypes.field_185220_C, Items.field_151137_ax, PotionTypes.field_185221_D);
      func_193357_a(PotionTypes.field_185220_C, Items.field_151114_aO, PotionTypes.field_185222_E);
      func_193357_a(PotionTypes.field_185233_e, Items.field_151065_br, PotionTypes.field_185223_F);
      func_193357_a(PotionTypes.field_185223_F, Items.field_151137_ax, PotionTypes.field_185224_G);
      func_193357_a(PotionTypes.field_185223_F, Items.field_151114_aO, PotionTypes.field_185225_H);
      func_193357_a(PotionTypes.field_185230_b, Items.field_151071_bq, PotionTypes.field_185226_I);
      func_193357_a(PotionTypes.field_185226_I, Items.field_151137_ax, PotionTypes.field_185227_J);
      func_193357_a(PotionTypes.field_185233_e, Items.field_204840_eX, PotionTypes.field_204841_O);
      func_193357_a(PotionTypes.field_204841_O, Items.field_151137_ax, PotionTypes.field_204842_P);
   }

   private static void func_196207_a(Item var0, Item var1, Item var2) {
      if (!(var0 instanceof ItemPotion)) {
         throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.field_212630_s.func_177774_c(var0));
      } else if (!(var2 instanceof ItemPotion)) {
         throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.field_212630_s.func_177774_c(var2));
      } else {
         field_185214_b.add(new PotionBrewing.MixPredicate(var0, Ingredient.func_199804_a(var1), var2));
      }
   }

   private static void func_196208_a(Item var0) {
      if (!(var0 instanceof ItemPotion)) {
         throw new IllegalArgumentException("Expected a potion, got: " + IRegistry.field_212630_s.func_177774_c(var0));
      } else {
         field_185215_c.add(Ingredient.func_199804_a(var0));
      }
   }

   private static void func_193357_a(PotionType var0, Item var1, PotionType var2) {
      field_185213_a.add(new PotionBrewing.MixPredicate(var0, Ingredient.func_199804_a(var1), var2));
   }

   static class MixPredicate<T> {
      private final T field_185198_a;
      private final Ingredient field_185199_b;
      private final T field_185200_c;

      public MixPredicate(T var1, Ingredient var2, T var3) {
         super();
         this.field_185198_a = var1;
         this.field_185199_b = var2;
         this.field_185200_c = var3;
      }
   }
}
