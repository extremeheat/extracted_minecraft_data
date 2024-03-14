package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
   public RepairItemRecipe(CraftingBookCategory var1) {
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
               if (!var5.is(var6.getItem()) || var6.getCount() != 1 || var5.getCount() != 1 || !var6.getItem().canBeDepleted()) {
                  return false;
               }
            }
         }
      }

      return var3.size() == 2;
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            var3.add(var5);
            if (var3.size() > 1) {
               ItemStack var6 = (ItemStack)var3.get(0);
               if (!var5.is(var6.getItem()) || var6.getCount() != 1 || var5.getCount() != 1 || !var6.getItem().canBeDepleted()) {
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (var3.size() == 2) {
         ItemStack var14 = (ItemStack)var3.get(0);
         ItemStack var15 = (ItemStack)var3.get(1);
         if (var14.is(var15.getItem()) && var14.getCount() == 1 && var15.getCount() == 1 && var14.getItem().canBeDepleted()) {
            Item var16 = var14.getItem();
            int var7 = var16.getMaxDamage() - var14.getDamageValue();
            int var8 = var16.getMaxDamage() - var15.getDamageValue();
            int var9 = var7 + var8 + var16.getMaxDamage() * 5 / 100;
            int var10 = var16.getMaxDamage() - var9;
            if (var10 < 0) {
               var10 = 0;
            }

            ItemStack var11 = new ItemStack(var14.getItem());
            var11.setDamageValue(var10);
            ItemEnchantments var12 = EnchantmentHelper.getEnchantmentsForCrafting(var14);
            ItemEnchantments var13 = EnchantmentHelper.getEnchantmentsForCrafting(var15);
            EnchantmentHelper.updateEnchantments(
               var11, var3x -> var2.registryOrThrow(Registries.ENCHANTMENT).stream().filter(Enchantment::isCurse).forEach(var3xx -> {
                     int var4xx = Math.max(var12.getLevel(var3xx), var13.getLevel(var3xx));
                     if (var4xx > 0) {
                        var3x.upgrade(var3xx, var4xx);
                     }
                  })
            );
            return var11;
         }
      }

      return ItemStack.EMPTY;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.REPAIR_ITEM;
   }
}
