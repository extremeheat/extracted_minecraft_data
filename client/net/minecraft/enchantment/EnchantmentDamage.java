package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EnchantmentDamage extends Enchantment {
   private static final String[] field_77359_A = new String[]{"all", "undead", "arthropods"};
   private static final int[] field_77360_B = new int[]{1, 5, 5};
   private static final int[] field_77362_C = new int[]{11, 8, 8};
   private static final int[] field_77358_D = new int[]{20, 20, 20};
   public final int field_77361_a;

   public EnchantmentDamage(int var1, int var2, int var3) {
      super(var1, var2, EnumEnchantmentType.weapon);
      this.field_77361_a = var3;
   }

   @Override
   public int func_77321_a(int var1) {
      return field_77360_B[this.field_77361_a] + (var1 - 1) * field_77362_C[this.field_77361_a];
   }

   @Override
   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + field_77358_D[this.field_77361_a];
   }

   @Override
   public int func_77325_b() {
      return 5;
   }

   @Override
   public float func_152376_a(int var1, EnumCreatureAttribute var2) {
      if (this.field_77361_a == 0) {
         return (float)var1 * 1.25F;
      } else if (this.field_77361_a == 1 && var2 == EnumCreatureAttribute.UNDEAD) {
         return (float)var1 * 2.5F;
      } else {
         return this.field_77361_a == 2 && var2 == EnumCreatureAttribute.ARTHROPOD ? (float)var1 * 2.5F : 0.0F;
      }
   }

   @Override
   public String func_77320_a() {
      return "enchantment.damage." + field_77359_A[this.field_77361_a];
   }

   @Override
   public boolean func_77326_a(Enchantment var1) {
      return !(var1 instanceof EnchantmentDamage);
   }

   @Override
   public boolean func_92089_a(ItemStack var1) {
      return var1.func_77973_b() instanceof ItemAxe ? true : super.func_92089_a(var1);
   }

   @Override
   public void func_151368_a(EntityLivingBase var1, Entity var2, int var3) {
      if (var2 instanceof EntityLivingBase) {
         EntityLivingBase var4 = (EntityLivingBase)var2;
         if (this.field_77361_a == 2 && var4.func_70668_bt() == EnumCreatureAttribute.ARTHROPOD) {
            int var5 = 20 + var1.func_70681_au().nextInt(10 * var3);
            var4.func_70690_d(new PotionEffect(Potion.field_76421_d.field_76415_H, var5, 3));
         }
      }
   }
}
