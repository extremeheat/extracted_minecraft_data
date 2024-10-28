package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
   public ArmorDyeRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.is(ItemTags.DYEABLE)) {
               if (!var3.isEmpty()) {
                  return false;
               }

               var3 = var6;
            } else {
               if (!(var6.getItem() instanceof DyeItem)) {
                  return false;
               }

               var4.add(var6);
            }
         }
      }

      return !var3.isEmpty() && !var4.isEmpty();
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ArrayList var3 = Lists.newArrayList();
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.is(ItemTags.DYEABLE)) {
               if (!var4.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var4 = var6.copy();
            } else {
               Item var8 = var6.getItem();
               if (!(var8 instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               DyeItem var7 = (DyeItem)var8;
               var3.add(var7);
            }
         }
      }

      if (!var4.isEmpty() && !var3.isEmpty()) {
         return DyedItemColor.applyDyes(var4, var3);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.ARMOR_DYE;
   }
}
