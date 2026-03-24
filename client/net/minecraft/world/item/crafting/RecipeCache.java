package net.minecraft.world.item.crafting;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class RecipeCache {
   private final @Nullable RecipeCache.Entry[] entries;
   private WeakReference<@Nullable RecipeManager> cachedRecipeManager = new WeakReference((Object)null);

   public RecipeCache(final int capacity) {
      super();
      this.entries = new Entry[capacity];
   }

   public Optional<RecipeHolder<CraftingRecipe>> get(final ServerLevel level, final CraftingInput input) {
      if (input.isEmpty()) {
         return Optional.empty();
      } else {
         this.validateRecipeManager(level);

         for(int i = 0; i < this.entries.length; ++i) {
            Entry entry = this.entries[i];
            if (entry != null && entry.matches(input)) {
               this.moveEntryToFront(i);
               return Optional.ofNullable(entry.value());
            }
         }

         return this.compute(input, level);
      }
   }

   private void validateRecipeManager(final ServerLevel level) {
      RecipeManager recipeManager = level.recipeAccess();
      if (recipeManager != this.cachedRecipeManager.get()) {
         this.cachedRecipeManager = new WeakReference(recipeManager);
         Arrays.fill(this.entries, (Object)null);
      }

   }

   private Optional<RecipeHolder<CraftingRecipe>> compute(final CraftingInput input, final ServerLevel level) {
      Optional<RecipeHolder<CraftingRecipe>> recipe = level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level);
      this.insert(input, (RecipeHolder)recipe.orElse((Object)null));
      return recipe;
   }

   private void moveEntryToFront(final int index) {
      if (index > 0) {
         Entry entry = this.entries[index];
         System.arraycopy(this.entries, 0, this.entries, 1, index);
         this.entries[0] = entry;
      }

   }

   private void insert(final CraftingInput input, final @Nullable RecipeHolder<CraftingRecipe> recipe) {
      NonNullList<ItemStack> key = NonNullList.<ItemStack>withSize(input.size(), ItemStack.EMPTY);

      for(int i = 0; i < input.size(); ++i) {
         key.set(i, input.getItem(i).copyWithCount(1));
      }

      System.arraycopy(this.entries, 0, this.entries, 1, this.entries.length - 1);
      this.entries[0] = new Entry(key, input.width(), input.height(), recipe);
   }

   private static record Entry(NonNullList<ItemStack> key, int width, int height, @Nullable RecipeHolder<CraftingRecipe> value) {
      private Entry {
         super();
      }

      public boolean matches(final CraftingInput input) {
         if (this.width == input.width() && this.height == input.height()) {
            for(int i = 0; i < this.key.size(); ++i) {
               if (!ItemStack.isSameItemSameComponents(this.key.get(i), input.getItem(i))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
