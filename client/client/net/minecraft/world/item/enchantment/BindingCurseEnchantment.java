package net.minecraft.world.item.enchantment;

public class BindingCurseEnchantment extends Enchantment {
   public BindingCurseEnchantment(Enchantment.EnchantmentDefinition var1) {
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
