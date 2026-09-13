package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesFood {
   public RecipesFood() {
      super();
   }

   public void func_77608_a(CraftingManager var1) {
      var1.func_77596_b(new ItemStack(Items.field_151009_A), Blocks.field_150338_P, Blocks.field_150337_Q, Items.field_151054_z);
      var1.func_92103_a(new ItemStack(Items.field_151106_aX, 8), "#X#", 'X', new ItemStack(Items.field_151100_aR, 1, 3), '#', Items.field_151015_O);
      var1.func_92103_a(new ItemStack(Blocks.field_150440_ba), "MMM", "MMM", "MMM", 'M', Items.field_151127_ba);
      var1.func_92103_a(new ItemStack(Items.field_151081_bc), "M", 'M', Items.field_151127_ba);
      var1.func_92103_a(new ItemStack(Items.field_151080_bb, 4), "M", 'M', Blocks.field_150423_aK);
      var1.func_77596_b(new ItemStack(Items.field_151158_bO), Blocks.field_150423_aK, Items.field_151102_aT, Items.field_151110_aK);
      var1.func_77596_b(new ItemStack(Items.field_151071_bq), Items.field_151070_bp, Blocks.field_150338_P, Items.field_151102_aT);
      var1.func_77596_b(new ItemStack(Items.field_151065_br, 2), Items.field_151072_bj);
      var1.func_77596_b(new ItemStack(Items.field_151064_bs), Items.field_151065_br, Items.field_151123_aH);
   }
}
