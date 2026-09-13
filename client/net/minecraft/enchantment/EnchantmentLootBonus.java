package net.minecraft.enchantment;

public class EnchantmentLootBonus extends Enchantment {
   protected EnchantmentLootBonus(int var1, int var2, EnumEnchantmentType var3) {
      super(var1, var2, var3);
      if (var3 == EnumEnchantmentType.digger) {
         this.func_77322_b("lootBonusDigger");
      } else if (var3 == EnumEnchantmentType.fishing_rod) {
         this.func_77322_b("lootBonusFishing");
      } else {
         this.func_77322_b("lootBonus");
      }
   }

   @Override
   public int func_77321_a(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   @Override
   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   @Override
   public int func_77325_b() {
      return 3;
   }

   @Override
   public boolean func_77326_a(Enchantment var1) {
      return super.func_77326_a(var1) && var1.field_77352_x != field_77348_q.field_77352_x;
   }
}
