package net.minecraft.enchantment;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class EnchantmentProtection extends Enchantment {
   public final EnchantmentProtection.Type field_77356_a;

   public EnchantmentProtection(Enchantment.Rarity var1, EnchantmentProtection.Type var2, EntityEquipmentSlot... var3) {
      super(var1, EnumEnchantmentType.ARMOR, var3);
      this.field_77356_a = var2;
      if (var2 == EnchantmentProtection.Type.FALL) {
         this.field_77351_y = EnumEnchantmentType.ARMOR_FEET;
      }

   }

   public int func_77321_a(int var1) {
      return this.field_77356_a.func_185316_b() + (var1 - 1) * this.field_77356_a.func_185315_c();
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + this.field_77356_a.func_185315_c();
   }

   public int func_77325_b() {
      return 4;
   }

   public int func_77318_a(int var1, DamageSource var2) {
      if (var2.func_76357_e()) {
         return 0;
      } else if (this.field_77356_a == EnchantmentProtection.Type.ALL) {
         return var1;
      } else if (this.field_77356_a == EnchantmentProtection.Type.FIRE && var2.func_76347_k()) {
         return var1 * 2;
      } else if (this.field_77356_a == EnchantmentProtection.Type.FALL && var2 == DamageSource.field_76379_h) {
         return var1 * 3;
      } else if (this.field_77356_a == EnchantmentProtection.Type.EXPLOSION && var2.func_94541_c()) {
         return var1 * 2;
      } else {
         return this.field_77356_a == EnchantmentProtection.Type.PROJECTILE && var2.func_76352_a() ? var1 * 2 : 0;
      }
   }

   public boolean func_77326_a(Enchantment var1) {
      if (var1 instanceof EnchantmentProtection) {
         EnchantmentProtection var2 = (EnchantmentProtection)var1;
         if (this.field_77356_a == var2.field_77356_a) {
            return false;
         } else {
            return this.field_77356_a == EnchantmentProtection.Type.FALL || var2.field_77356_a == EnchantmentProtection.Type.FALL;
         }
      } else {
         return super.func_77326_a(var1);
      }
   }

   public static int func_92093_a(EntityLivingBase var0, int var1) {
      int var2 = EnchantmentHelper.func_185284_a(Enchantments.field_77329_d, var0);
      if (var2 > 0) {
         var1 -= MathHelper.func_76141_d((float)var1 * (float)var2 * 0.15F);
      }

      return var1;
   }

   public static double func_92092_a(EntityLivingBase var0, double var1) {
      int var3 = EnchantmentHelper.func_185284_a(Enchantments.field_185297_d, var0);
      if (var3 > 0) {
         var1 -= (double)MathHelper.func_76128_c(var1 * (double)((float)var3 * 0.15F));
      }

      return var1;
   }

   public static enum Type {
      ALL("all", 1, 11),
      FIRE("fire", 10, 8),
      FALL("fall", 5, 6),
      EXPLOSION("explosion", 5, 8),
      PROJECTILE("projectile", 3, 6);

      private final String field_185322_f;
      private final int field_185323_g;
      private final int field_185324_h;

      private Type(String var3, int var4, int var5) {
         this.field_185322_f = var3;
         this.field_185323_g = var4;
         this.field_185324_h = var5;
      }

      public int func_185316_b() {
         return this.field_185323_g;
      }

      public int func_185315_c() {
         return this.field_185324_h;
      }
   }
}
