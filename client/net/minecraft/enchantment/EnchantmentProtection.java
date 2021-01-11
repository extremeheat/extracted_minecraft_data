package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class EnchantmentProtection extends Enchantment {
   private static final String[] field_77354_A = new String[]{"all", "fire", "fall", "explosion", "projectile"};
   private static final int[] field_77355_B = new int[]{1, 10, 5, 5, 3};
   private static final int[] field_77357_C = new int[]{11, 8, 6, 8, 6};
   private static final int[] field_77353_D = new int[]{20, 12, 10, 12, 15};
   public final int field_77356_a;

   public EnchantmentProtection(int var1, ResourceLocation var2, int var3, int var4) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR);
      this.field_77356_a = var4;
      if (var4 == 2) {
         this.field_77351_y = EnumEnchantmentType.ARMOR_FEET;
      }

   }

   public int func_77321_a(int var1) {
      return field_77355_B[this.field_77356_a] + (var1 - 1) * field_77357_C[this.field_77356_a];
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + field_77353_D[this.field_77356_a];
   }

   public int func_77325_b() {
      return 4;
   }

   public int func_77318_a(int var1, DamageSource var2) {
      if (var2.func_76357_e()) {
         return 0;
      } else {
         float var3 = (float)(6 + var1 * var1) / 3.0F;
         if (this.field_77356_a == 0) {
            return MathHelper.func_76141_d(var3 * 0.75F);
         } else if (this.field_77356_a == 1 && var2.func_76347_k()) {
            return MathHelper.func_76141_d(var3 * 1.25F);
         } else if (this.field_77356_a == 2 && var2 == DamageSource.field_76379_h) {
            return MathHelper.func_76141_d(var3 * 2.5F);
         } else if (this.field_77356_a == 3 && var2.func_94541_c()) {
            return MathHelper.func_76141_d(var3 * 1.5F);
         } else {
            return this.field_77356_a == 4 && var2.func_76352_a() ? MathHelper.func_76141_d(var3 * 1.5F) : 0;
         }
      }
   }

   public String func_77320_a() {
      return "enchantment.protect." + field_77354_A[this.field_77356_a];
   }

   public boolean func_77326_a(Enchantment var1) {
      if (var1 instanceof EnchantmentProtection) {
         EnchantmentProtection var2 = (EnchantmentProtection)var1;
         if (var2.field_77356_a == this.field_77356_a) {
            return false;
         } else {
            return this.field_77356_a == 2 || var2.field_77356_a == 2;
         }
      } else {
         return super.func_77326_a(var1);
      }
   }

   public static int func_92093_a(Entity var0, int var1) {
      int var2 = EnchantmentHelper.func_77511_a(Enchantment.field_77329_d.field_77352_x, var0.func_70035_c());
      if (var2 > 0) {
         var1 -= MathHelper.func_76141_d((float)var1 * (float)var2 * 0.15F);
      }

      return var1;
   }

   public static double func_92092_a(Entity var0, double var1) {
      int var3 = EnchantmentHelper.func_77511_a(Enchantment.field_77327_f.field_77352_x, var0.func_70035_c());
      if (var3 > 0) {
         var1 -= (double)MathHelper.func_76128_c(var1 * (double)((float)var3 * 0.15F));
      }

      return var1;
   }
}
