package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
   public RepairItemRecipe(ResourceLocation var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            var3.add(var5);
            if (var3.size() > 1) {
               ItemStack var6 = (ItemStack)var3.get(0);
               if (var5.getItem() != var6.getItem() || var6.getCount() != 1 || var5.getCount() != 1 || !var6.getItem().canBeDepleted()) {
                  return false;
               }
            }
         }
      }

      return var3.size() == 2;
   }

   public ItemStack assemble(CraftingContainer var1) {
      ArrayList var2 = Lists.newArrayList();

      ItemStack var4;
      for(int var3 = 0; var3 < var1.getContainerSize(); ++var3) {
         var4 = var1.getItem(var3);
         if (!var4.isEmpty()) {
            var2.add(var4);
            if (var2.size() > 1) {
               ItemStack var5 = (ItemStack)var2.get(0);
               if (var4.getItem() != var5.getItem() || var5.getCount() != 1 || var4.getCount() != 1 || !var5.getItem().canBeDepleted()) {
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (var2.size() == 2) {
         ItemStack var11 = (ItemStack)var2.get(0);
         var4 = (ItemStack)var2.get(1);
         if (var11.getItem() == var4.getItem() && var11.getCount() == 1 && var4.getCount() == 1 && var11.getItem().canBeDepleted()) {
            Item var12 = var11.getItem();
            int var6 = var12.getMaxDamage() - var11.getDamageValue();
            int var7 = var12.getMaxDamage() - var4.getDamageValue();
            int var8 = var6 + var7 + var12.getMaxDamage() * 5 / 100;
            int var9 = var12.getMaxDamage() - var8;
            if (var9 < 0) {
               var9 = 0;
            }

            ItemStack var10 = new ItemStack(var11.getItem());
            var10.setDamageValue(var9);
            return var10;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.REPAIR_ITEM;
   }
}
