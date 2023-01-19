package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
   public RepairItemRecipe(ResourceLocation var1, CraftingBookCategory var2) {
      super(var1, var2);
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

   public ItemStack assemble(CraftingContainer var1) {
      ArrayList var2 = Lists.newArrayList();

      for(int var3 = 0; var3 < var1.getContainerSize(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (!var4.isEmpty()) {
            var2.add(var4);
            if (var2.size() > 1) {
               ItemStack var5 = (ItemStack)var2.get(0);
               if (!var4.is(var5.getItem()) || var5.getCount() != 1 || var4.getCount() != 1 || !var5.getItem().canBeDepleted()) {
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (var2.size() == 2) {
         ItemStack var14 = (ItemStack)var2.get(0);
         ItemStack var15 = (ItemStack)var2.get(1);
         if (var14.is(var15.getItem()) && var14.getCount() == 1 && var15.getCount() == 1 && var14.getItem().canBeDepleted()) {
            Item var16 = var14.getItem();
            int var6 = var16.getMaxDamage() - var14.getDamageValue();
            int var7 = var16.getMaxDamage() - var15.getDamageValue();
            int var8 = var6 + var7 + var16.getMaxDamage() * 5 / 100;
            int var9 = var16.getMaxDamage() - var8;
            if (var9 < 0) {
               var9 = 0;
            }

            ItemStack var10 = new ItemStack(var14.getItem());
            var10.setDamageValue(var9);
            HashMap var11 = Maps.newHashMap();
            Map var12 = EnchantmentHelper.getEnchantments(var14);
            Map var13 = EnchantmentHelper.getEnchantments(var15);
            BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach(var3x -> {
               int var4x = Math.max(var12.getOrDefault(var3x, 0), var13.getOrDefault(var3x, 0));
               if (var4x > 0) {
                  var11.put(var3x, var4x);
               }
            });
            if (!var11.isEmpty()) {
               EnchantmentHelper.setEnchantments(var11, var10);
            }

            return var10;
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
