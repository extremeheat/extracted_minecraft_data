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
      if (var1.ingredientCount() != 2) {
         return false;
      } else {
         boolean var3 = false;
         boolean var4 = false;

         for (int var5 = 0; var5 < var1.size(); var5++) {
            ItemStack var6 = var1.getItem(var5);
            if (!var6.isEmpty()) {
               if (var6.getItem() instanceof BannerItem) {
                  if (var4) {
                     return false;
                  }

                  var4 = true;
               } else {
                  if (!var6.is(Items.SHIELD)) {
                     return false;
                  }

                  if (var3) {
                     return false;
                  }

                  BannerPatternLayers var7 = var6.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
                  if (!var7.layers().isEmpty()) {
                     return false;
                  }

                  var3 = true;
               }
            }
         }

         return var3 && var4;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ItemStack var4 = ItemStack.EMPTY;

      for (int var5 = 0; var5 < var1.size(); var5++) {
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
         var4.set(DataComponents.BANNER_PATTERNS, var3.get(DataComponents.BANNER_PATTERNS));
         var4.set(DataComponents.BASE_COLOR, ((BannerItem)var3.getItem()).getColor());
         return var4;
      }
   }

   @Override
   public RecipeSerializer<ShieldDecorationRecipe> getSerializer() {
      return RecipeSerializer.SHIELD_DECORATION;
   }
}
