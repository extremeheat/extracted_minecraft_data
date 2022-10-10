package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentBindingCurse extends Enchantment {
   public EnchantmentBindingCurse(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.WEARABLE, var2);
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

   public boolean func_185261_e() {
      return true;
   }

   public boolean func_190936_d() {
      return true;
   }
}
