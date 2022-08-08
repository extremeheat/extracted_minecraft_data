package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;

public class BookCloningRecipe extends CustomRecipe {
   public BookCloningRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
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

      return !var4.isEmpty() && var4.hasTag() && var3 > 0;
   }

   public ItemStack assemble(CraftingContainer var1) {
      int var2 = 0;
      ItemStack var3 = ItemStack.EMPTY;

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            if (var5.is(Items.WRITTEN_BOOK)) {
               if (!var3.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var3 = var5;
            } else {
               if (!var5.is(Items.WRITABLE_BOOK)) {
                  return ItemStack.EMPTY;
               }

               ++var2;
            }
         }
      }

      if (!var3.isEmpty() && var3.hasTag() && var2 >= 1 && WrittenBookItem.getGeneration(var3) < 2) {
         ItemStack var6 = new ItemStack(Items.WRITTEN_BOOK, var2);
         CompoundTag var7 = var3.getTag().copy();
         var7.putInt("generation", WrittenBookItem.getGeneration(var3) + 1);
         var6.setTag(var7);
         return var6;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingContainer var1) {
      NonNullList var2 = NonNullList.withSize(var1.getContainerSize(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (var4.getItem().hasCraftingRemainingItem()) {
            var2.set(var3, new ItemStack(var4.getItem().getCraftingRemainingItem()));
         } else if (var4.getItem() instanceof WrittenBookItem) {
            ItemStack var5 = var4.copy();
            var5.setCount(1);
            var2.set(var3, var5);
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
