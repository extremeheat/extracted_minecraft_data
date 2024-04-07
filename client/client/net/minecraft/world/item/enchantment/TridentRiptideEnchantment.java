package net.minecraft.world.item.enchantment;

public class TridentRiptideEnchantment extends Enchantment {
   public TridentRiptideEnchantment(Enchantment.EnchantmentDefinition var1) {
      super(var1);
   }

   @Override
   public boolean checkCompatibility(Enchantment var1) {
      return super.checkCompatibility(var1) && var1 != Enchantments.LOYALTY && var1 != Enchantments.CHANNELING;
   }
}
