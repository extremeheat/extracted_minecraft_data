package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

public class BookCloningRecipe extends CustomRecipe {
   public BookCloningRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.is(Items.WRITTEN_BOOK)) {
               if (!var4.isEmpty()) {
                  return false;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.WRITABLE_BOOK)) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return !var4.isEmpty() && var3 > 0;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.is(Items.WRITTEN_BOOK)) {
               if (!var4.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.WRITABLE_BOOK)) {
                  return ItemStack.EMPTY;
               }

               ++var3;
            }
         }
      }

      WrittenBookContent var8 = (WrittenBookContent)var4.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (!var4.isEmpty() && var3 >= 1 && var8 != null) {
         WrittenBookContent var9 = var8.tryCraftCopy();
         if (var9 == null) {
            return ItemStack.EMPTY;
         } else {
            ItemStack var7 = var4.copyWithCount(var3);
            var7.set(DataComponents.WRITTEN_BOOK_CONTENT, var9);
            return var7;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput var1) {
      NonNullList var2 = NonNullList.withSize(var1.size(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (var4.getItem().hasCraftingRemainingItem()) {
            var2.set(var3, new ItemStack(var4.getItem().getCraftingRemainingItem()));
         } else if (var4.getItem() instanceof WrittenBookItem) {
            var2.set(var3, var4.copyWithCount(1));
            break;
         }
      }

      return var2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.BOOK_CLONING;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }
}
