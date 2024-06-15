package net.minecraft.world.item.crafting;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
   public RepairItemRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   @Nullable
   private Pair<ItemStack, ItemStack> getItemsToCombine(CraftingContainer var1) {
      ItemStack var2 = null;
      ItemStack var3 = null;

      for (int var4 = 0; var4 < var1.getContainerSize(); var4++) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty()) {
            if (var2 == null) {
               var2 = var5;
            } else {
               if (var3 != null) {
                  return null;
               }

               var3 = var5;
            }
         }
      }

      return var2 != null && var3 != null && canCombine(var2, var3) ? Pair.of(var2, var3) : null;
   }

   private static boolean canCombine(ItemStack var0, ItemStack var1) {
      return var1.is(var0.getItem())
         && var0.getCount() == 1
         && var1.getCount() == 1
         && var0.has(DataComponents.MAX_DAMAGE)
         && var1.has(DataComponents.MAX_DAMAGE)
         && var0.has(DataComponents.DAMAGE)
         && var1.has(DataComponents.DAMAGE);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      return this.getItemsToCombine(var1) != null;
   }

   public ItemStack assemble(CraftingContainer var1, HolderLookup.Provider var2) {
      Pair var3 = this.getItemsToCombine(var1);
      if (var3 == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var4 = (ItemStack)var3.getFirst();
         ItemStack var5 = (ItemStack)var3.getSecond();
         int var6 = Math.max(var4.getMaxDamage(), var5.getMaxDamage());
         int var7 = var4.getMaxDamage() - var4.getDamageValue();
         int var8 = var5.getMaxDamage() - var5.getDamageValue();
         int var9 = var7 + var8 + var6 * 5 / 100;
         ItemStack var10 = new ItemStack(var4.getItem());
         var10.set(DataComponents.MAX_DAMAGE, var6);
         var10.setDamageValue(Math.max(var6 - var9, 0));
         ItemEnchantments var11 = EnchantmentHelper.getEnchantmentsForCrafting(var4);
         ItemEnchantments var12 = EnchantmentHelper.getEnchantmentsForCrafting(var5);
         EnchantmentHelper.updateEnchantments(
            var10, var3x -> var2.lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Holder::value).filter(Enchantment::isCurse).forEach(var3xx -> {
                  int var4x = Math.max(var11.getLevel(var3xx), var12.getLevel(var3xx));
                  if (var4x > 0) {
                     var3x.upgrade(var3xx, var4x);
                  }
               })
         );
         return var10;
      }
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
