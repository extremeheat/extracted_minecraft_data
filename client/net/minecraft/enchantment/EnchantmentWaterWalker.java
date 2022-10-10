package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentWaterWalker extends Enchantment {
   public EnchantmentWaterWalker(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.ARMOR_FEET, var2);
   }

   public int func_77321_a(int var1) {
      return var1 * 10;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   public int func_77325_b() {
      return 3;
   }

   public boolean func_77326_a(Enchantment var1) {
      return super.func_77326_a(var1) && var1 != Enchantments.field_185301_j;
   }
}
