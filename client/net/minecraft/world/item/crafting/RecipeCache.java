package net.minecraft.world.item.crafting;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RecipeCache {
   private final Entry[] entries;
   private WeakReference<RecipeManager> cachedRecipeManager = new WeakReference((Object)null);

   public RecipeCache(int var1) {
      super();
      this.entries = new Entry[var1];
   }

   public Optional<RecipeHolder<CraftingRecipe>> get(Level var1, CraftingContainer var2) {
      if (var2.isEmpty()) {
         return Optional.empty();
      } else {
         this.validateRecipeManager(var1);

         for(int var3 = 0; var3 < this.entries.length; ++var3) {
            Entry var4 = this.entries[var3];
            if (var4 != null && var4.matches(var2.getItems())) {
               this.moveEntryToFront(var3);
               return Optional.ofNullable(var4.value());
            }
         }

         return this.compute(var2, var1);
      }
   }

   private void validateRecipeManager(Level var1) {
      RecipeManager var2 = var1.getRecipeManager();
      if (var2 != this.cachedRecipeManager.get()) {
         this.cachedRecipeManager = new WeakReference(var2);
         Arrays.fill(this.entries, (Object)null);
      }

   }

   private Optional<RecipeHolder<CraftingRecipe>> compute(CraftingContainer var1, Level var2) {
      Optional var3 = var2.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var1, var2);
      this.insert(var1.getItems(), (RecipeHolder)var3.orElse((Object)null));
      return var3;
   }

   private void moveEntryToFront(int var1) {
      if (var1 > 0) {
         Entry var2 = this.entries[var1];
         System.arraycopy(this.entries, 0, this.entries, 1, var1);
         this.entries[0] = var2;
      }

   }

   private void insert(List<ItemStack> var1, @Nullable RecipeHolder<CraftingRecipe> var2) {
      NonNullList var3 = NonNullList.withSize(var1.size(), ItemStack.EMPTY);

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         var3.set(var4, ((ItemStack)var1.get(var4)).copyWithCount(1));
      }

      System.arraycopy(this.entries, 0, this.entries, 1, this.entries.length - 1);
      this.entries[0] = new Entry(var3, var2);
   }

   static record Entry(NonNullList<ItemStack> key, @Nullable RecipeHolder<CraftingRecipe> value) {
      Entry(NonNullList<ItemStack> var1, @Nullable RecipeHolder<CraftingRecipe> var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public boolean matches(List<ItemStack> var1) {
         if (this.key.size() != var1.size()) {
            return false;
         } else {
            for(int var2 = 0; var2 < this.key.size(); ++var2) {
               if (!ItemStack.isSameItemSameComponents((ItemStack)this.key.get(var2), (ItemStack)var1.get(var2))) {
                  return false;
               }
            }

            return true;
         }
      }

      public NonNullList<ItemStack> key() {
         return this.key;
      }

      @Nullable
      public RecipeHolder<CraftingRecipe> value() {
         return this.value;
      }
   }
}
