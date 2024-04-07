package net.minecraft.world.item.enchantment;

public class MendingEnchantment extends Enchantment {
   public MendingEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }
}
