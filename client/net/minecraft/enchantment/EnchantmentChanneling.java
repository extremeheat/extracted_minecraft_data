package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentChanneling extends Enchantment {
   public EnchantmentChanneling(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.TRIDENT, var2);
   }

   public int func_77321_a(int var1) {
      return 25;
   }

   public int func_77317_b(int var1) {
      return 50;
   }

   public int func_77325_b() {
      return 1;
   }

   public boolean func_77326_a(Enchantment var1) {
      return super.func_77326_a(var1);
   }
}
