package net.minecraft.world.item.crafting;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe extends CustomRecipe {
   private static final Ingredient STAR_INGREDIENT;

   public FireworkStarFadeRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.getItem() instanceof DyeItem) {
               var3 = true;
            } else {
               if (!STAR_INGREDIENT.test(var6)) {
                  return false;
               }

               if (var4) {
                  return false;
               }

               var4 = true;
            }
         }
      }

      return var4 && var3;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      IntArrayList var3 = new IntArrayList();
      ItemStack var4 = null;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         Item var7 = var6.getItem();
         if (var7 instanceof DyeItem) {
            var3.add(((DyeItem)var7).getDyeColor().getFireworkColor());
         } else if (STAR_INGREDIENT.test(var6)) {
            var4 = var6.copyWithCount(1);
         }
      }

      if (var4 != null && !var3.isEmpty()) {
         var4.update(DataComponents.FIREWORK_EXPLOSION, FireworkExplosion.DEFAULT, var3, FireworkExplosion::withFadeColors);
         return var4;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR_FADE;
   }

   static {
      STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);
   }
}
