package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadBase;

public enum ItemTier implements IItemTier {
   WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
      return Ingredient.func_199805_a(ItemTags.field_199905_b);
   }),
   STONE(1, 131, 4.0F, 1.0F, 5, () -> {
      return Ingredient.func_199804_a(Blocks.field_150347_e);
   }),
   IRON(2, 250, 6.0F, 2.0F, 14, () -> {
      return Ingredient.func_199804_a(Items.field_151042_j);
   }),
   DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
      return Ingredient.func_199804_a(Items.field_151045_i);
   }),
   GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
      return Ingredient.func_199804_a(Items.field_151043_k);
   });

   private final int field_78001_f;
   private final int field_78002_g;
   private final float field_78010_h;
   private final float field_78011_i;
   private final int field_78008_j;
   private final LazyLoadBase<Ingredient> field_200940_k;

   private ItemTier(int var3, int var4, float var5, float var6, int var7, Supplier<Ingredient> var8) {
      this.field_78001_f = var3;
      this.field_78002_g = var4;
      this.field_78010_h = var5;
      this.field_78011_i = var6;
      this.field_78008_j = var7;
      this.field_200940_k = new LazyLoadBase(var8);
   }

   public int func_200926_a() {
      return this.field_78002_g;
   }

   public float func_200928_b() {
      return this.field_78010_h;
   }

   public float func_200929_c() {
      return this.field_78011_i;
   }

   public int func_200925_d() {
      return this.field_78001_f;
   }

   public int func_200927_e() {
      return this.field_78008_j;
   }

   public Ingredient func_200924_f() {
      return (Ingredient)this.field_200940_k.func_179281_c();
   }
}
