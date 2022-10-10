package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentMending extends Enchantment {
   public EnchantmentMending(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.BREAKABLE, var2);
   }

   public int func_77321_a(int var1) {
      return var1 * 25;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 50;
   }

   public boolean func_185261_e() {
      return true;
   }

   public int func_77325_b() {
      return 1;
   }
}
