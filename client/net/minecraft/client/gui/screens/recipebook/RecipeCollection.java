package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public class RecipeCollection {
   public static final RecipeCollection EMPTY = new RecipeCollection(List.of());
   private final List<RecipeDisplayEntry> entries;
   private final Set<RecipeDisplayId> craftable = new HashSet();
   private final Set<RecipeDisplayId> selected = new HashSet();

   public RecipeCollection(final List<RecipeDisplayEntry> recipes) {
      super();
      this.entries = recipes;
   }

   public void selectRecipes(final StackedItemContents stackedContents, final Predicate<RecipeDisplay> selector) {
      for(RecipeDisplayEntry entry : this.entries) {
         boolean isSelected = selector.test(entry.display());
         if (isSelected) {
            this.selected.add(entry.id());
         } else {
            this.selected.remove(entry.id());
         }

         if (isSelected && entry.canCraft(stackedContents)) {
            this.craftable.add(entry.id());
         } else {
            this.craftable.remove(entry.id());
         }
      }

   }

   public boolean isCraftable(final RecipeDisplayId recipe) {
      return this.craftable.contains(recipe);
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

   public List<RecipeDisplayEntry> getSelectedRecipes(final CraftableStatus selector) {
      Predicate var10000;
      switch (selector.ordinal()) {
         case 0:
            Set var7 = this.selected;
            Objects.requireNonNull(var7);
            var10000 = var7::contains;
            break;
         case 1:
            Set var6 = this.craftable;
            Objects.requireNonNull(var6);
            var10000 = var6::contains;
            break;
         case 2:
            var10000 = (recipe) -> this.selected.contains(recipe) && !this.craftable.contains(recipe);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Predicate<RecipeDisplayId> predicate = var10000;
      List<RecipeDisplayEntry> result = new ArrayList();

      for(RecipeDisplayEntry entries : this.entries) {
         if (predicate.test(entries.id())) {
            result.add(entries);
         }
      }

      return result;
   }

   public static enum CraftableStatus {
      ANY,
      CRAFTABLE,
      NOT_CRAFTABLE;

      private CraftableStatus() {
      }

      // $FF: synthetic method
      private static CraftableStatus[] $values() {
         return new CraftableStatus[]{ANY, CRAFTABLE, NOT_CRAFTABLE};
      }
   }
}
