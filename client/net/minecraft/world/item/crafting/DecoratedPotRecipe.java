package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;

public class DecoratedPotRecipe extends CustomRecipe {
   public DecoratedPotRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   private static ItemStack back(CraftingInput var0) {
      return var0.getItem(1, 0);
   }

   private static ItemStack left(CraftingInput var0) {
      return var0.getItem(0, 1);
   }

   private static ItemStack right(CraftingInput var0) {
      return var0.getItem(2, 1);
   }

   private static ItemStack front(CraftingInput var0) {
      return var0.getItem(1, 2);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      return var1.width() == 3 && var1.height() == 3 && var1.ingredientCount() == 4
         ? back(var1).is(ItemTags.DECORATED_POT_INGREDIENTS)
            && left(var1).is(ItemTags.DECORATED_POT_INGREDIENTS)
            && right(var1).is(ItemTags.DECORATED_POT_INGREDIENTS)
            && front(var1).is(ItemTags.DECORATED_POT_INGREDIENTS)
         : false;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      PotDecorations var3 = new PotDecorations(back(var1).getItem(), left(var1).getItem(), right(var1).getItem(), front(var1).getItem());
      return DecoratedPotBlockEntity.createDecoratedPotItem(var3);
   }

   @Override
   public RecipeSerializer<DecoratedPotRecipe> getSerializer() {
      return RecipeSerializer.DECORATED_POT_RECIPE;
   }
}
