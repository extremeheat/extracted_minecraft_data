package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeCollection {
   private final RegistryAccess registryAccess;
   private final List<RecipeHolder<?>> recipes;
   private final boolean singleResultItem;
   private final Set<RecipeHolder<?>> craftable = Sets.newHashSet();
   private final Set<RecipeHolder<?>> fitsDimensions = Sets.newHashSet();
   private final Set<RecipeHolder<?>> known = Sets.newHashSet();

   public RecipeCollection(RegistryAccess var1, List<RecipeHolder<?>> var2) {
      super();
      this.registryAccess = var1;
      this.recipes = ImmutableList.copyOf(var2);
      if (var2.size() <= 1) {
         this.singleResultItem = true;
      } else {
         this.singleResultItem = allRecipesHaveSameResult(var1, var2);
      }

   }

   private static boolean allRecipesHaveSameResult(RegistryAccess var0, List<RecipeHolder<?>> var1) {
      int var2 = var1.size();
      ItemStack var3 = ((RecipeHolder)var1.get(0)).value().getResultItem(var0);

      for(int var4 = 1; var4 < var2; ++var4) {
         ItemStack var5 = ((RecipeHolder)var1.get(var4)).value().getResultItem(var0);
         if (!ItemStack.isSameItemSameComponents(var3, var5)) {
            return false;
         }
      }

      return true;
   }

   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }

   public boolean hasKnownRecipes() {
      return !this.known.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook var1) {
      Iterator var2 = this.recipes.iterator();

      while(var2.hasNext()) {
         RecipeHolder var3 = (RecipeHolder)var2.next();
         if (var1.contains(var3)) {
            this.known.add(var3);
         }
      }

   }

   public void canCraft(StackedContents var1, int var2, int var3, RecipeBook var4) {
      Iterator var5 = this.recipes.iterator();

      while(true) {
         while(var5.hasNext()) {
            RecipeHolder var6 = (RecipeHolder)var5.next();
            boolean var7 = var6.value().canCraftInDimensions(var2, var3) && var4.contains(var6);
            if (var7) {
               this.fitsDimensions.add(var6);
            } else {
               this.fitsDimensions.remove(var6);
            }

            if (var7 && var1.canCraft(var6.value(), (IntList)null)) {
               this.craftable.add(var6);
            } else {
               this.craftable.remove(var6);
            }
         }

         return;
      }
   }

   public boolean isCraftable(RecipeHolder<?> var1) {
      return this.craftable.contains(var1);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasFitting() {
      return !this.fitsDimensions.isEmpty();
   }

   public List<RecipeHolder<?>> getRecipes() {
      return this.recipes;
   }

   public List<RecipeHolder<?>> getRecipes(boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      Set var3 = var1 ? this.craftable : this.fitsDimensions;
      Iterator var4 = this.recipes.iterator();

      while(var4.hasNext()) {
         RecipeHolder var5 = (RecipeHolder)var4.next();
         if (var3.contains(var5)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public List<RecipeHolder<?>> getDisplayRecipes(boolean var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.recipes.iterator();

      while(var3.hasNext()) {
         RecipeHolder var4 = (RecipeHolder)var3.next();
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
