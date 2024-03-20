package net.minecraft.world.item.enchantment;

public class SwiftSneakEnchantment extends Enchantment {
   public SwiftSneakEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }

   @Override
   public boolean isTradeable() {
      return false;
   }

   @Override
   public boolean isDiscoverable() {
      return false;
   }
}
