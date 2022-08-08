package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe extends CustomRecipe {
   private static final Ingredient STAR_INGREDIENT;

   public FireworkStarFadeRecipe(ResourceLocation var1) {
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

   public ItemStack assemble(CraftingContainer var1) {
      ArrayList var2 = Lists.newArrayList();
      ItemStack var3 = null;

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         Item var6 = var5.getItem();
         if (var6 instanceof DyeItem) {
            var2.add(((DyeItem)var6).getDyeColor().getFireworkColor());
         } else if (STAR_INGREDIENT.test(var5)) {
            var3 = var5.copy();
            var3.setCount(1);
         }
      }

      if (var3 != null && !var2.isEmpty()) {
         var3.getOrCreateTagElement("Explosion").putIntArray("FadeColors", (List)var2);
         return var3;
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
