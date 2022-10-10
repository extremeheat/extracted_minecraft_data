package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.ARMOR_HEAD, var2);
   }

   public int func_77321_a(int var1) {
      return 1;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 40;
   }

   public int func_77325_b() {
      return 1;
   }
}
