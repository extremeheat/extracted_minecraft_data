package net.minecraft.world.item;

import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class EnchantedBookItem extends Item {
   public EnchantedBookItem(Item.Properties var1) {
      super(var1);
   }

   public boolean isEnchantable(ItemStack var1) {
      return false;
   }

   public static ItemStack createForEnchantment(EnchantmentInstance var0) {
      ItemStack var1 = new ItemStack(Items.ENCHANTED_BOOK);
      var1.enchant(var0.enchantment, var0.level);
      return var1;
   }
}
