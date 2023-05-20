package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
   public ArmorDyeRecipe(ResourceLocation var1, CraftingBookCategory var2) {
      super(var1, var2);
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

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ArrayList var3 = Lists.newArrayList();
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            Item var7 = var6.getItem();
            if (var7 instanceof DyeableLeatherItem) {
               if (!var4.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var4 = var6.copy();
            } else {
               if (!(var7 instanceof DyeItem)) {
                  return ItemStack.EMPTY;
               }

               var3.add((DyeItem)var7);
            }
         }
      }

      return !var4.isEmpty() && !var3.isEmpty() ? DyeableLeatherItem.dyeArmor(var4, var3) : ItemStack.EMPTY;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.ARMOR_DYE;
   }
}
