package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe extends CustomRecipe {
   public TippedArrowRecipe(ResourceLocation var1) {
      super(var1);
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

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = var1.getItem(1 + var1.getWidth());
      if (!var2.is(Items.LINGERING_POTION)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var3 = new ItemStack(Items.TIPPED_ARROW, 8);
         PotionUtils.setPotion(var3, PotionUtils.getPotion(var2));
         PotionUtils.setCustomEffects(var3, PotionUtils.getCustomEffects(var2));
         return var3;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.TIPPED_ARROW;
   }
}
