package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public class BannerDuplicateRecipe extends CustomRecipe {
   public BannerDuplicateRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      DyeColor var3 = null;
      ItemStack var4 = null;
      ItemStack var5 = null;

      for(int var6 = 0; var6 < var1.getContainerSize(); ++var6) {
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

            int var10 = BannerBlockEntity.getPatternCount(var7);
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

   public ItemStack assemble(CraftingContainer var1) {
      for(int var2 = 0; var2 < var1.getContainerSize(); ++var2) {
         ItemStack var3 = var1.getItem(var2);
         if (!var3.isEmpty()) {
            int var4 = BannerBlockEntity.getPatternCount(var3);
            if (var4 > 0 && var4 <= 6) {
               ItemStack var5 = var3.copy();
               var5.setCount(1);
               return var5;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingContainer var1) {
      NonNullList var2 = NonNullList.withSize(var1.getContainerSize(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (!var4.isEmpty()) {
            if (var4.getItem().hasCraftingRemainingItem()) {
               var2.set(var3, new ItemStack(var4.getItem().getCraftingRemainingItem()));
            } else if (var4.hasTag() && BannerBlockEntity.getPatternCount(var4) > 0) {
               ItemStack var5 = var4.copy();
               var5.setCount(1);
               var2.set(var3, var5);
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
