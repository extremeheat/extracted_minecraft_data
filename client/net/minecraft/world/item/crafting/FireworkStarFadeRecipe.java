package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe extends CustomRecipe {
   private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

   public FireworkStarFadeRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
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

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ArrayList var3 = Lists.newArrayList();
      ItemStack var4 = null;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         Item var7 = var6.getItem();
         if (var7 instanceof DyeItem) {
            var3.add(((DyeItem)var7).getDyeColor().getFireworkColor());
         } else if (STAR_INGREDIENT.test(var6)) {
            var4 = var6.copyWithCount(1);
         }
      }

      if (var4 != null && !var3.isEmpty()) {
         var4.getOrCreateTagElement("Explosion").putIntArray("FadeColors", var3);
         return var4;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.FIREWORK_STAR_FADE;
   }
}
