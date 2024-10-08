package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe extends CustomRecipe {
   private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
   private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
   private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

   public FireworkRocketRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      if (var1.ingredientCount() < 2) {
         return false;
      } else {
         boolean var3 = false;
         int var4 = 0;

         for (int var5 = 0; var5 < var1.size(); var5++) {
            ItemStack var6 = var1.getItem(var5);
            if (!var6.isEmpty()) {
               if (PAPER_INGREDIENT.test(var6)) {
                  if (var3) {
                     return false;
                  }

                  var3 = true;
               } else if (GUNPOWDER_INGREDIENT.test(var6)) {
                  if (++var4 > 3) {
                     return false;
                  }
               } else if (!STAR_INGREDIENT.test(var6)) {
                  return false;
               }
            }
         }

         return var3 && var4 >= 1;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ArrayList var3 = new ArrayList();
      int var4 = 0;

      for (int var5 = 0; var5 < var1.size(); var5++) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (GUNPOWDER_INGREDIENT.test(var6)) {
               var4++;
            } else if (STAR_INGREDIENT.test(var6)) {
               FireworkExplosion var7 = var6.get(DataComponents.FIREWORK_EXPLOSION);
               if (var7 != null) {
                  var3.add(var7);
               }
            }
         }
      }

      ItemStack var8 = new ItemStack(Items.FIREWORK_ROCKET, 3);
      var8.set(DataComponents.FIREWORKS, new Fireworks(var4, var3));
      return var8;
   }

   @Override
   public RecipeSerializer<FireworkRocketRecipe> getSerializer() {
      return RecipeSerializer.FIREWORK_ROCKET;
   }
}
