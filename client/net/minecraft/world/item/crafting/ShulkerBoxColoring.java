package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxColoring extends CustomRecipe {
   public ShulkerBoxColoring(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      int var3 = 0;
      int var4 = 0;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
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

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = ItemStack.EMPTY;
      DyeItem var3 = (DyeItem)Items.WHITE_DYE;

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            Item var6 = var5.getItem();
            if (Block.byItem(var6) instanceof ShulkerBoxBlock) {
               var2 = var5;
            } else if (var6 instanceof DyeItem) {
               var3 = (DyeItem)var6;
            }
         }
      }

      ItemStack var7 = ShulkerBoxBlock.getColoredItemStack(var3.getDyeColor());
      if (var2.hasTag()) {
         var7.setTag(var2.getTag().copy());
      }

      return var7;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHULKER_BOX_COLORING;
   }
}
