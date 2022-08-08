package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeCollection {
   private final List<Recipe<?>> recipes;
   private final boolean singleResultItem;
   private final Set<Recipe<?>> craftable = Sets.newHashSet();
   private final Set<Recipe<?>> fitsDimensions = Sets.newHashSet();
   private final Set<Recipe<?>> known = Sets.newHashSet();

   public RecipeCollection(List<Recipe<?>> var1) {
      super();
      this.recipes = ImmutableList.copyOf(var1);
      if (var1.size() <= 1) {
         this.singleResultItem = true;
      } else {
         this.singleResultItem = allRecipesHaveSameResult(var1);
      }

   }

   private static boolean allRecipesHaveSameResult(List<Recipe<?>> var0) {
      int var1 = var0.size();
      ItemStack var2 = ((Recipe)var0.get(0)).getResultItem();

      for(int var3 = 1; var3 < var1; ++var3) {
         ItemStack var4 = ((Recipe)var0.get(var3)).getResultItem();
         if (!ItemStack.isSame(var2, var4) || !ItemStack.tagMatches(var2, var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean hasKnownRecipes() {
      return !this.known.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook var1) {
      Iterator var2 = this.recipes.iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         if (var1.contains(var3)) {
            this.known.add(var3);
         }
      }

   }

   public void canCraft(StackedContents var1, int var2, int var3, RecipeBook var4) {
      Iterator var5 = this.recipes.iterator();

      while(true) {
         while(var5.hasNext()) {
            Recipe var6 = (Recipe)var5.next();
            boolean var7 = var6.canCraftInDimensions(var2, var3) && var4.contains(var6);
            if (var7) {
               this.fitsDimensions.add(var6);
            } else {
               this.fitsDimensions.remove(var6);
            }

            if (var7 && var1.canCraft(var6, (IntList)null)) {
               this.craftable.add(var6);
            } else {
               this.craftable.remove(var6);
            }
         }

         return;
      }
   }

   public boolean isCraftable(Recipe<?> var1) {
      return this.craftable.contains(var1);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasFitting() {
      return !this.fitsDimensions.isEmpty();
   }

   public List<Recipe<?>> getRecipes() {
      return this.recipes;
   }

   public List<Recipe<?>> getRecipes(boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      Set var3 = var1 ? this.craftable : this.fitsDimensions;
      Iterator var4 = this.recipes.iterator();

      while(var4.hasNext()) {
         Recipe var5 = (Recipe)var4.next();
         if (var3.contains(var5)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public List<Recipe<?>> getDisplayRecipes(boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.recipes.iterator();

      while(var3.hasNext()) {
         Recipe var4 = (Recipe)var3.next();
         if (this.fitsDimensions.contains(var4) && this.craftable.contains(var4) == var1) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}
