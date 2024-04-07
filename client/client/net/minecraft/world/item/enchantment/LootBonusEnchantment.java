package net.minecraft.world.item.enchantment;

public class LootBonusEnchantment extends Enchantment {
   protected LootBonusEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.SILK_TOUCH;
   }
}
