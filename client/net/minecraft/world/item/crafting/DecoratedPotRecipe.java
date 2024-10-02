package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;

public class DecoratedPotRecipe extends CustomRecipe {
   public DecoratedPotRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      if (var1.width() == 3 && var1.height() == 3) {
         for (int var3 = 0; var3 < var1.size(); var3++) {
            ItemStack var4 = var1.getItem(var3);
            switch (var3) {
               case 1:
               case 3:
               case 5:
               case 7:
                  if (!var4.is(ItemTags.DECORATED_POT_INGREDIENTS)) {
                     return false;
                  }
                  break;
               case 2:
               case 4:
               case 6:
               default:
                  if (!var4.is(Items.AIR)) {
                     return false;
                  }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      PotDecorations var3 = new PotDecorations(var1.getItem(1).getItem(), var1.getItem(3).getItem(), var1.getItem(5).getItem(), var1.getItem(7).getItem());
      return DecoratedPotBlockEntity.createDecoratedPotItem(var3);
   }

   @Override
   public RecipeSerializer<DecoratedPotRecipe> getSerializer() {
      return RecipeSerializer.DECORATED_POT_RECIPE;
   }
}
