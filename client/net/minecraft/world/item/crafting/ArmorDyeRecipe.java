package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
   public ArmorDyeRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      ItemStack var3 = ItemStack.EMPTY;
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.getItem() instanceof DyeableLeatherItem) {
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

   public ItemStack assemble(CraftingContainer var1) {
      ArrayList var2 = Lists.newArrayList();
      ItemStack var3 = ItemStack.EMPTY;

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            Item var6 = var5.getItem();
            if (var6 instanceof DyeableLeatherItem) {
               if (!var3.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var3 = var5.copy();
            } else {
               if (!(var6 instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               var2.add((DyeItem)var6);
            }
         }
      }

      if (!var3.isEmpty() && !var2.isEmpty()) {
         return DyeableLeatherItem.dyeArmor(var3, var2);
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
