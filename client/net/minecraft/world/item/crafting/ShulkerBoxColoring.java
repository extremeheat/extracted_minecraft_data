package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxColoring extends CustomRecipe {
   public ShulkerBoxColoring(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      int var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (Block.byItem(var6.getItem()) instanceof ShulkerBoxBlock) {
               ++var3;
            } else {
               if (!(var6.getItem() instanceof DyeItem)) {
                  return false;
               }

               ++var4;
            }

            if (var4 > 1 || var3 > 1) {
               return false;
            }
         }
      }

      return var3 == 1 && var4 == 1;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = ItemStack.EMPTY;
      DyeItem var4 = (DyeItem)Items.WHITE_DYE;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            Item var7 = var6.getItem();
            if (Block.byItem(var7) instanceof ShulkerBoxBlock) {
               var3 = var6;
            } else if (var7 instanceof DyeItem) {
               var4 = (DyeItem)var7;
            }
         }
      }

      Block var8 = ShulkerBoxBlock.getBlockByColor(var4.getDyeColor());
      return var3.transmuteCopy(var8, 1);
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHULKER_BOX_COLORING;
   }
}
