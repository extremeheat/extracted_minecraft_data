package net.minecraft.world.item.crafting;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class RecipeCache {
   private final Entry[] entries;
   private WeakReference<RecipeManager> cachedRecipeManager = new WeakReference((Object)null);

   public RecipeCache(int var1) {
      super();
      this.entries = new Entry[var1];
   }

   public Optional<RecipeHolder<CraftingRecipe>> get(ServerLevel var1, CraftingInput var2) {
      if (var2.isEmpty()) {
         return Optional.empty();
      } else {
         this.validateRecipeManager(var1);

         for(int var3 = 0; var3 < this.entries.length; ++var3) {
            Entry var4 = this.entries[var3];
            if (var4 != null && var4.matches(var2)) {
               this.moveEntryToFront(var3);
               return Optional.ofNullable(var4.value());
            }
         }

         return this.compute(var2, var1);
      }
   }

   private void validateRecipeManager(ServerLevel var1) {
      RecipeManager var2 = var1.recipeAccess();
      if (var2 != this.cachedRecipeManager.get()) {
         this.cachedRecipeManager = new WeakReference(var2);
         Arrays.fill(this.entries, (Object)null);
      }

   }

   private Optional<RecipeHolder<CraftingRecipe>> compute(CraftingInput var1, ServerLevel var2) {
      Optional var3 = var2.recipeAccess().getRecipeFor(RecipeType.CRAFTING, var1, var2);
      this.insert(var1, (RecipeHolder)var3.orElse((Object)null));
      return var3;
   }

   private void moveEntryToFront(int var1) {
      if (var1 > 0) {
         Entry var2 = this.entries[var1];
         System.arraycopy(this.entries, 0, this.entries, 1, var1);
         this.entries[0] = var2;
      }

   }

   private void insert(CraftingInput var1, @Nullable RecipeHolder<CraftingRecipe> var2) {
      NonNullList var3 = NonNullList.withSize(var1.size(), ItemStack.EMPTY);

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         var3.set(var4, var1.getItem(var4).copyWithCount(1));
      }

      System.arraycopy(this.entries, 0, this.entries, 1, this.entries.length - 1);
      this.entries[0] = new Entry(var3, var1.width(), var1.height(), var2);
   }

   static record Entry(NonNullList<ItemStack> key, int width, int height, @Nullable RecipeHolder<CraftingRecipe> value) {
      Entry(NonNullList<ItemStack> var1, int var2, int var3, @Nullable RecipeHolder<CraftingRecipe> var4) {
         super();
         this.key = var1;
         this.width = var2;
         this.height = var3;
         this.value = var4;
      }

      public boolean matches(CraftingInput var1) {
         if (this.width == var1.width() && this.height == var1.height()) {
            for(int var2 = 0; var2 < this.key.size(); ++var2) {
               if (!ItemStack.isSameItemSameComponents(this.key.get(var2), var1.getItem(var2))) {
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
