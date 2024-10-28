package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerDuplicateRecipe extends CustomRecipe {
   public BannerDuplicateRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      DyeColor var3 = null;
      ItemStack var4 = null;
      ItemStack var5 = null;

      for(int var6 = 0; var6 < var1.size(); ++var6) {
         ItemStack var7 = var1.getItem(var6);
         if (!var7.isEmpty()) {
            Item var8 = var7.getItem();
            if (!(var8 instanceof BannerItem)) {
               return false;
            }

            BannerItem var9 = (BannerItem)var8;
            if (var3 == null) {
               var3 = var9.getColor();
            } else if (var3 != var9.getColor()) {
               return false;
            }

            int var10 = ((BannerPatternLayers)var7.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)).layers().size();
            if (var10 > 6) {
               return false;
            }

            if (var10 > 0) {
               if (var4 != null) {
                  return false;
               }

               var4 = var7;
            } else {
               if (var5 != null) {
                  return false;
               }

               var5 = var7;
            }
         }
      }

      return var4 != null && var5 != null;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (!var4.isEmpty()) {
            int var5 = ((BannerPatternLayers)var4.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)).layers().size();
            if (var5 > 0 && var5 <= 6) {
               return var4.copyWithCount(1);
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput var1) {
      NonNullList var2 = NonNullList.withSize(var1.size(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (!var4.isEmpty()) {
            if (var4.getItem().hasCraftingRemainingItem()) {
               var2.set(var3, new ItemStack(var4.getItem().getCraftingRemainingItem()));
            } else if (!((BannerPatternLayers)var4.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)).layers().isEmpty()) {
               var2.set(var3, var4.copyWithCount(1));
            }
         }
      }

      return var2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.BANNER_DUPLICATE;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }
}
