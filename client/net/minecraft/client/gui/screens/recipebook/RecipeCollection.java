package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class RecipeCollection {
   private final List<RecipeDisplayEntry> entries;
   private final boolean singleResultItem;
   private final Set<RecipeDisplayId> craftable = new HashSet<>();
   private final Set<RecipeDisplayId> selected = new HashSet<>();

   public RecipeCollection(List<RecipeDisplayEntry> var1) {
      super();
      this.entries = var1;
      if (var1.size() <= 1) {
         this.singleResultItem = true;
      } else {
         this.singleResultItem = allRecipesHaveSameResult(this.entries);
      }
   }

   private static boolean allRecipesHaveSameResult(List<RecipeDisplayEntry> var0) {
      int var1 = var0.size();
      SlotDisplay var2 = ((RecipeDisplayEntry)var0.getFirst()).display().result();

      for (int var3 = 1; var3 < var1; var3++) {
         SlotDisplay var4 = ((RecipeDisplayEntry)var0.get(var3)).display().result();
         if (!var4.equals(var2)) {
            return false;
         }
      }

      return true;
   }

   public void selectRecipes(StackedItemContents var1, Predicate<RecipeDisplay> var2) {
      for (RecipeDisplayEntry var4 : this.entries) {
         boolean var5 = var2.test(var4.display());
         if (var5) {
            this.selected.add(var4.id());
         } else {
            this.selected.remove(var4.id());
         }

         if (var5 && var4.canCraft(var1)) {
            this.craftable.add(var4.id());
         } else {
            this.craftable.remove(var4.id());
         }
      }
   }

   public boolean isCraftable(RecipeDisplayId var1) {
      return this.craftable.contains(var1);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasAnySelected() {
      return !this.selected.isEmpty();
   }

   public List<RecipeDisplayEntry> getRecipes() {
      return this.entries;
   }

   public List<RecipeDisplayEntry> getSelectedRecipes(RecipeCollection.CraftableStatus var1) {
      Predicate var2 = switch (var1) {
         case ANY -> this.selected::contains;
         case CRAFTABLE -> this.craftable::contains;
         case NOT_CRAFTABLE -> var1x -> this.selected.contains(var1x) && !this.craftable.contains(var1x);
      };
      ArrayList var3 = new ArrayList();

      for (RecipeDisplayEntry var5 : this.entries) {
         if (var2.test(var5.id())) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }

   public static enum CraftableStatus {
      ANY,
      CRAFTABLE,
      NOT_CRAFTABLE;

      private CraftableStatus() {
      }
   }
}
