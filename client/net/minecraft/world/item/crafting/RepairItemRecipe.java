package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
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
         ItemStack var15 = (ItemStack)var3.get(0);
         ItemStack var16 = (ItemStack)var3.get(1);
         if (var15.is(var16.getItem()) && var15.getCount() == 1 && var16.getCount() == 1 && var15.getItem().canBeDepleted()) {
            Item var17 = var15.getItem();
            int var7 = var17.getMaxDamage() - var15.getDamageValue();
            int var8 = var17.getMaxDamage() - var16.getDamageValue();
            int var9 = var7 + var8 + var17.getMaxDamage() * 5 / 100;
            int var10 = var17.getMaxDamage() - var9;
            if (var10 < 0) {
               var10 = 0;
            }

            ItemStack var11 = new ItemStack(var15.getItem());
            var11.setDamageValue(var10);
            HashMap var12 = Maps.newHashMap();
            Map var13 = EnchantmentHelper.getEnchantments(var15);
            Map var14 = EnchantmentHelper.getEnchantments(var16);
            BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach(var3x -> {
               int var4x = Math.max(var13.getOrDefault(var3x, 0), var14.getOrDefault(var3x, 0));
               if (var4x > 0) {
                  var12.put(var3x, var4x);
               }
            });
            if (!var12.isEmpty()) {
               EnchantmentHelper.setEnchantments(var12, var11);
            }

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
