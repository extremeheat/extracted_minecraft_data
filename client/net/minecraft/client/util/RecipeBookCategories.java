package net.minecraft.client.util;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public enum RecipeBookCategories {
   SEARCH(new ItemStack[]{new ItemStack(Items.field_151111_aL)}),
   BUILDING_BLOCKS(new ItemStack[]{new ItemStack(Blocks.field_196584_bK)}),
   REDSTONE(new ItemStack[]{new ItemStack(Items.field_151137_ax)}),
   EQUIPMENT(new ItemStack[]{new ItemStack(Items.field_151036_c), new ItemStack(Items.field_151010_B)}),
   MISC(new ItemStack[]{new ItemStack(Items.field_151129_at), new ItemStack(Items.field_151034_e)}),
   FURNACE_SEARCH(new ItemStack[]{new ItemStack(Items.field_151111_aL)}),
   FURNACE_FOOD(new ItemStack[]{new ItemStack(Items.field_151147_al)}),
   FURNACE_BLOCKS(new ItemStack[]{new ItemStack(Blocks.field_150348_b)}),
   FURNACE_MISC(new ItemStack[]{new ItemStack(Items.field_151129_at), new ItemStack(Items.field_151166_bC)});

   private final List<ItemStack> field_202904_j;

   private RecipeBookCategories(ItemStack... var3) {
      this.field_202904_j = ImmutableList.copyOf(var3);
   }

   public List<ItemStack> func_202903_a() {
      return this.field_202904_j;
   }
}
