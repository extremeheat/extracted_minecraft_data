package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe extends CustomRecipe {
   public TippedArrowRecipe(ResourceLocation var1, CraftingBookCategory var2) {
      super(var1, var2);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      if (var1.getWidth() == 3 && var1.getHeight() == 3) {
         for(int var3 = 0; var3 < var1.getWidth(); ++var3) {
            for(int var4 = 0; var4 < var1.getHeight(); ++var4) {
               ItemStack var5 = var1.getItem(var3 + var4 * var1.getWidth());
               if (var5.isEmpty()) {
                  return false;
               }

               if (var3 == 1 && var4 == 1) {
                  if (!var5.is(Items.LINGERING_POTION)) {
                     return false;
                  }
               } else if (!var5.is(Items.ARROW)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ItemStack var3 = var1.getItem(1 + var1.getWidth());
      if (!var3.is(Items.LINGERING_POTION)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var4 = new ItemStack(Items.TIPPED_ARROW, 8);
         PotionUtils.setPotion(var4, PotionUtils.getPotion(var3));
         PotionUtils.setCustomEffects(var4, PotionUtils.getCustomEffects(var3));
         return var4;
      }
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.TIPPED_ARROW;
   }
}
