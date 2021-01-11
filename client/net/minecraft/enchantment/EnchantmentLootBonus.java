package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentLootBonus extends Enchantment {
   protected EnchantmentLootBonus(int var1, ResourceLocation var2, int var3, EnumEnchantmentType var4) {
      super(var1, var2, var3, var4);
      if (var4 == EnumEnchantmentType.DIGGER) {
         this.func_77322_b("lootBonusDigger");
      } else if (var4 == EnumEnchantmentType.FISHING_ROD) {
         this.func_77322_b("lootBonusFishing");
      } else {
         this.func_77322_b("lootBonus");
      }

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
      return super.func_77326_a(var1) && var1.field_77352_x != field_77348_q.field_77352_x;
   }
}
