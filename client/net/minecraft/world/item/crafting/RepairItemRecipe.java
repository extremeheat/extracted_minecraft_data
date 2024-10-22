package net.minecraft.world.item.crafting;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class RepairItemRecipe extends CustomRecipe {
   public RepairItemRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   @Nullable
   private static Pair<ItemStack, ItemStack> getItemsToCombine(CraftingInput var0) {
      if (var0.ingredientCount() != 2) {
         return null;
      } else {
         ItemStack var1 = null;

         for (int var2 = 0; var2 < var0.size(); var2++) {
            ItemStack var3 = var0.getItem(var2);
            if (!var3.isEmpty()) {
               if (var1 != null) {
                  return canCombine(var1, var3) ? Pair.of(var1, var3) : null;
               }

               var1 = var3;
            }
         }

         return null;
      }
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

   public boolean matches(CraftingInput var1, Level var2) {
      return getItemsToCombine(var1) != null;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      Pair var3 = getItemsToCombine(var1);
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
            var10, var3x -> var2.lookupOrThrow(Registries.ENCHANTMENT).listElements().filter(var0x -> var0x.is(EnchantmentTags.CURSE)).forEach(var3xx -> {
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
   public RecipeSerializer<RepairItemRecipe> getSerializer() {
      return RecipeSerializer.REPAIR_ITEM;
   }
}
