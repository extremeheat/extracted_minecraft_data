package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentLootBonus extends Enchantment {
   protected EnchantmentLootBonus(Enchantment.Rarity var1, EnumEnchantmentType var2, EntityEquipmentSlot... var3) {
      super(var1, var2, var3);
   }

   public int func_77321_a(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   public int func_77325_b() {
      return 3;
   }

   public boolean func_77326_a(Enchantment var1) {
      return super.func_77326_a(var1) && var1 != Enchantments.field_185306_r;
   }
}
