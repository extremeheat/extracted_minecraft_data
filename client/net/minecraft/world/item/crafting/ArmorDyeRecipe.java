package net.minecraft.world.item.crafting;

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
      if (var1.ingredientCount() < 2) {
         return false;
      } else {
         boolean var3 = false;
         boolean var4 = false;

         for(int var5 = 0; var5 < var1.size(); ++var5) {
            ItemStack var6 = var1.getItem(var5);
            if (!var6.isEmpty()) {
               if (var6.is(ItemTags.DYEABLE)) {
                  if (var3) {
                     return false;
                  }

                  var3 = true;
               } else {
                  if (!(var6.getItem() instanceof DyeItem)) {
                     return false;
                  }

                  var4 = true;
               }
            }
         }

         return var4 && var3;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ArrayList var3 = new ArrayList();
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

   public RecipeSerializer<ArmorDyeRecipe> getSerializer() {
      return RecipeSerializer.ARMOR_DYE;
   }
}
