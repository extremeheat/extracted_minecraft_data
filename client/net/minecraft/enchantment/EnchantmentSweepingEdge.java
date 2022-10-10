package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentSweepingEdge extends Enchantment {
   public EnchantmentSweepingEdge(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.WEAPON, var2);
   }

   public int func_77321_a(int var1) {
      return 5 + (var1 - 1) * 9;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   public int func_77325_b() {
      return 3;
   }

   public static float func_191526_e(int var0) {
      return 1.0F - 1.0F / (float)(var0 + 1);
   }
}
