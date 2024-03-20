package net.minecraft.world.item.enchantment;

public class VanishingCurseEnchantment extends Enchantment {
   public VanishingCurseEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean isTreasureOnly() {
      return true;
   }

   @Override
   public boolean isCurse() {
      return true;
   }
}
