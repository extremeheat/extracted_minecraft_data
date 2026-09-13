package net.minecraft.enchantment;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public abstract class Enchantment {
   public static final Enchantment[] field_77331_b = new Enchantment[256];
   public static final Enchantment[] field_92090_c;
   public static final Enchantment field_77332_c = new EnchantmentProtection(0, 10, 0);
   public static final Enchantment field_77329_d = new EnchantmentProtection(1, 5, 1);
   public static final Enchantment field_77330_e = new EnchantmentProtection(2, 5, 2);
   public static final Enchantment field_77327_f = new EnchantmentProtection(3, 2, 3);
   public static final Enchantment field_77328_g = new EnchantmentProtection(4, 5, 4);
   public static final Enchantment field_77340_h = new EnchantmentOxygen(5, 2);
   public static final Enchantment field_77341_i = new EnchantmentWaterWorker(6, 2);
   public static final Enchantment field_92091_k = new EnchantmentThorns(7, 1);
   public static final Enchantment field_77338_j = new EnchantmentDamage(16, 10, 0);
   public static final Enchantment field_77339_k = new EnchantmentDamage(17, 5, 1);
   public static final Enchantment field_77336_l = new EnchantmentDamage(18, 5, 2);
   public static final Enchantment field_77337_m = new EnchantmentKnockback(19, 5);
   public static final Enchantment field_77334_n = new EnchantmentFireAspect(20, 2);
   public static final Enchantment field_77335_o = new EnchantmentLootBonus(21, 2, EnumEnchantmentType.weapon);
   public static final Enchantment field_77349_p = new EnchantmentDigging(32, 10);
   public static final Enchantment field_77348_q = new EnchantmentUntouching(33, 1);
   public static final Enchantment field_77347_r = new EnchantmentDurability(34, 5);
   public static final Enchantment field_77346_s = new EnchantmentLootBonus(35, 2, EnumEnchantmentType.digger);
   public static final Enchantment field_77345_t = new EnchantmentArrowDamage(48, 10);
   public static final Enchantment field_77344_u = new EnchantmentArrowKnockback(49, 2);
   public static final Enchantment field_77343_v = new EnchantmentArrowFire(50, 2);
   public static final Enchantment field_77342_w = new EnchantmentArrowInfinite(51, 1);
   public static final Enchantment field_151370_z = new EnchantmentLootBonus(61, 2, EnumEnchantmentType.fishing_rod);
   public static final Enchantment field_151369_A = new EnchantmentFishingSpeed(62, 2, EnumEnchantmentType.fishing_rod);
   public final int field_77352_x;
   private final int field_77333_a;
   public EnumEnchantmentType field_77351_y;
   protected String field_77350_z;

   protected Enchantment(int var1, int var2, EnumEnchantmentType var3) {
      super();
      this.field_77352_x = var1;
      this.field_77333_a = var2;
      this.field_77351_y = var3;
      if (field_77331_b[var1] != null) {
         throw new IllegalArgumentException("Duplicate enchantment id!");
      } else {
         field_77331_b[var1] = this;
      }
   }

   public int func_77324_c() {
      return this.field_77333_a;
   }

   public int func_77319_d() {
      return 1;
   }

   public int func_77325_b() {
      return 1;
   }

   public int func_77321_a(int var1) {
      return 1 + var1 * 10;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 5;
   }

   public int func_77318_a(int var1, DamageSource var2) {
      return 0;
   }

   public float func_152376_a(int var1, EnumCreatureAttribute var2) {
      return 0.0F;
   }

   public boolean func_77326_a(Enchantment var1) {
      return this != var1;
   }

   public Enchantment func_77322_b(String var1) {
      this.field_77350_z = var1;
      return this;
   }

   public String func_77320_a() {
      return "enchantment." + this.field_77350_z;
   }

   public String func_77316_c(int var1) {
      String var2 = StatCollector.func_74838_a(this.func_77320_a());
      return var2 + " " + StatCollector.func_74838_a("enchantment.level." + var1);
   }

   public boolean func_92089_a(ItemStack var1) {
      return this.field_77351_y.func_77557_a(var1.func_77973_b());
   }

   public void func_151368_a(EntityLivingBase var1, Entity var2, int var3) {
   }

   public void func_151367_b(EntityLivingBase var1, Entity var2, int var3) {
   }

   static {
      ArrayList var0 = new ArrayList();

      for(Enchantment var4 : field_77331_b) {
         if (var4 != null) {
            var0.add(var4);
         }
      }

      field_92090_c = var0.toArray(new Enchantment[0]);
   }
}
