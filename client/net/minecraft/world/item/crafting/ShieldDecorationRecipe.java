package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class ShieldDecorationRecipe extends CustomRecipe {
   public ShieldDecorationRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.getItem() instanceof BannerItem) {
               if (!var4.isEmpty()) {
                  return false;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.SHIELD)) {
                  return false;
               }

               if (!var3.isEmpty()) {
                  return false;
               }

               BannerPatternLayers var7 = (BannerPatternLayers)var6.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
               if (!var7.layers().isEmpty()) {
                  return false;
               }

               var3 = var6;
            }
         }
      }

      if (!var3.isEmpty() && !var4.isEmpty()) {
         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.getItem() instanceof BannerItem) {
               var3 = var6;
            } else if (var6.is(Items.SHIELD)) {
               var4 = var6.copy();
            }
         }
      }

      if (var4.isEmpty()) {
         return var4;
      } else {
         var4.set(DataComponents.BANNER_PATTERNS, (BannerPatternLayers)var3.get(DataComponents.BANNER_PATTERNS));
         var4.set(DataComponents.BASE_COLOR, ((BannerItem)var3.getItem()).getColor());
         return var4;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHIELD_DECORATION;
   }
}
