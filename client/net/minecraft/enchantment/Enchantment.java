package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class Enchantment {
   private static final Enchantment[] field_180311_a = new Enchantment[256];
   public static final Enchantment[] field_77331_b;
   private static final Map<ResourceLocation, Enchantment> field_180307_E = Maps.newHashMap();
   public static final Enchantment field_180310_c = new EnchantmentProtection(0, new ResourceLocation("protection"), 10, 0);
   public static final Enchantment field_77329_d = new EnchantmentProtection(1, new ResourceLocation("fire_protection"), 5, 1);
   public static final Enchantment field_180309_e = new EnchantmentProtection(2, new ResourceLocation("feather_falling"), 5, 2);
   public static final Enchantment field_77327_f = new EnchantmentProtection(3, new ResourceLocation("blast_protection"), 2, 3);
   public static final Enchantment field_180308_g = new EnchantmentProtection(4, new ResourceLocation("projectile_protection"), 5, 4);
   public static final Enchantment field_180317_h = new EnchantmentOxygen(5, new ResourceLocation("respiration"), 2);
   public static final Enchantment field_77341_i = new EnchantmentWaterWorker(6, new ResourceLocation("aqua_affinity"), 2);
   public static final Enchantment field_92091_k = new EnchantmentThorns(7, new ResourceLocation("thorns"), 1);
   public static final Enchantment field_180316_k = new EnchantmentWaterWalker(8, new ResourceLocation("depth_strider"), 2);
   public static final Enchantment field_180314_l = new EnchantmentDamage(16, new ResourceLocation("sharpness"), 10, 0);
   public static final Enchantment field_180315_m = new EnchantmentDamage(17, new ResourceLocation("smite"), 5, 1);
   public static final Enchantment field_180312_n = new EnchantmentDamage(18, new ResourceLocation("bane_of_arthropods"), 5, 2);
   public static final Enchantment field_180313_o = new EnchantmentKnockback(19, new ResourceLocation("knockback"), 5);
   public static final Enchantment field_77334_n = new EnchantmentFireAspect(20, new ResourceLocation("fire_aspect"), 2);
   public static final Enchantment field_77335_o;
   public static final Enchantment field_77349_p;
   public static final Enchantment field_77348_q;
   public static final Enchantment field_77347_r;
   public static final Enchantment field_77346_s;
   public static final Enchantment field_77345_t;
   public static final Enchantment field_77344_u;
   public static final Enchantment field_77343_v;
   public static final Enchantment field_77342_w;
   public static final Enchantment field_151370_z;
   public static final Enchantment field_151369_A;
   public final int field_77352_x;
   private final int field_77333_a;
   public EnumEnchantmentType field_77351_y;
   protected String field_77350_z;

   public static Enchantment func_180306_c(int var0) {
      return var0 >= 0 && var0 < field_180311_a.length ? field_180311_a[var0] : null;
   }

   protected Enchantment(int var1, ResourceLocation var2, int var3, EnumEnchantmentType var4) {
      super();
      this.field_77352_x = var1;
      this.field_77333_a = var3;
      this.field_77351_y = var4;
      if (field_180311_a[var1] != null) {
         throw new IllegalArgumentException("Duplicate enchantment id!");
      } else {
         field_180311_a[var1] = this;
         field_180307_E.put(var2, this);
      }
   }

   public static Enchantment func_180305_b(String var0) {
      return (Enchantment)field_180307_E.get(new ResourceLocation(var0));
   }

   public static Set<ResourceLocation> func_181077_c() {
      return field_180307_E.keySet();
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
      field_77335_o = new EnchantmentLootBonus(21, new ResourceLocation("looting"), 2, EnumEnchantmentType.WEAPON);
      field_77349_p = new EnchantmentDigging(32, new ResourceLocation("efficiency"), 10);
      field_77348_q = new EnchantmentUntouching(33, new ResourceLocation("silk_touch"), 1);
      field_77347_r = new EnchantmentDurability(34, new ResourceLocation("unbreaking"), 5);
      field_77346_s = new EnchantmentLootBonus(35, new ResourceLocation("fortune"), 2, EnumEnchantmentType.DIGGER);
      field_77345_t = new EnchantmentArrowDamage(48, new ResourceLocation("power"), 10);
      field_77344_u = new EnchantmentArrowKnockback(49, new ResourceLocation("punch"), 2);
      field_77343_v = new EnchantmentArrowFire(50, new ResourceLocation("flame"), 2);
      field_77342_w = new EnchantmentArrowInfinite(51, new ResourceLocation("infinity"), 1);
      field_151370_z = new EnchantmentLootBonus(61, new ResourceLocation("luck_of_the_sea"), 2, EnumEnchantmentType.FISHING_ROD);
      field_151369_A = new EnchantmentFishingSpeed(62, new ResourceLocation("lure"), 2, EnumEnchantmentType.FISHING_ROD);
      ArrayList var0 = Lists.newArrayList();
      Enchantment[] var1 = field_180311_a;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Enchantment var4 = var1[var3];
         if (var4 != null) {
            var0.add(var4);
         }
      }

      field_77331_b = (Enchantment[])var0.toArray(new Enchantment[var0.size()]);
   }
}
