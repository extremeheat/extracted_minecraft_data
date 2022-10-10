package net.minecraft.enchantment;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentImpaling extends Enchantment {
   public EnchantmentImpaling(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.TRIDENT, var2);
   }

   public int func_77321_a(int var1) {
      return 1 + (var1 - 1) * 8;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 20;
   }

   public int func_77325_b() {
      return 5;
   }

   public float func_152376_a(int var1, CreatureAttribute var2) {
      return var2 == CreatureAttribute.field_203100_e ? (float)var1 * 2.5F : 0.0F;
   }
}
