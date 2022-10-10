package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public abstract class Enchantment {
   private final EntityEquipmentSlot[] field_185263_a;
   private final Enchantment.Rarity field_77333_a;
   @Nullable
   public EnumEnchantmentType field_77351_y;
   @Nullable
   protected String field_77350_z;

   @Nullable
   public static Enchantment func_185262_c(int var0) {
      return (Enchantment)IRegistry.field_212628_q.func_148754_a(var0);
   }

   protected Enchantment(Enchantment.Rarity var1, EnumEnchantmentType var2, EntityEquipmentSlot[] var3) {
      super();
      this.field_77333_a = var1;
      this.field_77351_y = var2;
      this.field_185263_a = var3;
   }

   public List<ItemStack> func_185260_a(EntityLivingBase var1) {
      ArrayList var2 = Lists.newArrayList();
      EntityEquipmentSlot[] var3 = this.field_185263_a;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EntityEquipmentSlot var6 = var3[var5];
         ItemStack var7 = var1.func_184582_a(var6);
         if (!var7.func_190926_b()) {
            var2.add(var7);
         }
      }

      return var2;
   }

   public Enchantment.Rarity func_77324_c() {
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

   public float func_152376_a(int var1, CreatureAttribute var2) {
      return 0.0F;
   }

   public final boolean func_191560_c(Enchantment var1) {
      return this.func_77326_a(var1) && var1.func_77326_a(this);
   }

   protected boolean func_77326_a(Enchantment var1) {
      return this != var1;
   }

   protected String func_210771_f() {
      if (this.field_77350_z == null) {
         this.field_77350_z = Util.func_200697_a("enchantment", IRegistry.field_212628_q.func_177774_c(this));
      }

      return this.field_77350_z;
   }

   public String func_77320_a() {
      return this.func_210771_f();
   }

   public ITextComponent func_200305_d(int var1) {
      TextComponentTranslation var2 = new TextComponentTranslation(this.func_77320_a(), new Object[0]);
      if (this.func_190936_d()) {
         var2.func_211708_a(TextFormatting.RED);
      } else {
         var2.func_211708_a(TextFormatting.GRAY);
      }

      if (var1 != 1 || this.func_77325_b() != 1) {
         var2.func_150258_a(" ").func_150257_a(new TextComponentTranslation("enchantment.level." + var1, new Object[0]));
      }

      return var2;
   }

   public boolean func_92089_a(ItemStack var1) {
      return this.field_77351_y.func_77557_a(var1.func_77973_b());
   }

   public void func_151368_a(EntityLivingBase var1, Entity var2, int var3) {
   }

   public void func_151367_b(EntityLivingBase var1, Entity var2, int var3) {
   }

   public boolean func_185261_e() {
      return false;
   }

   public boolean func_190936_d() {
      return false;
   }

   public static void func_185257_f() {
      EntityEquipmentSlot[] var0 = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
      func_210770_a("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.ALL, var0));
      func_210770_a("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FIRE, var0));
      func_210770_a("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FALL, var0));
      func_210770_a("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.Type.EXPLOSION, var0));
      func_210770_a("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.PROJECTILE, var0));
      func_210770_a("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, var0));
      func_210770_a("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, var0));
      func_210770_a("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, var0));
      func_210770_a("depth_strider", new EnchantmentWaterWalker(Enchantment.Rarity.RARE, var0));
      func_210770_a("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET}));
      func_210770_a("binding_curse", new EnchantmentBindingCurse(Enchantment.Rarity.VERY_RARE, var0));
      func_210770_a("sharpness", new EnchantmentDamage(Enchantment.Rarity.COMMON, 0, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("smite", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 1, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("bane_of_arthropods", new EnchantmentDamage(Enchantment.Rarity.UNCOMMON, 2, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("fire_aspect", new EnchantmentFireAspect(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("sweeping", new EnchantmentSweepingEdge(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("silk_touch", new EnchantmentUntouching(Enchantment.Rarity.VERY_RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("flame", new EnchantmentArrowFire(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("infinity", new EnchantmentArrowInfinite(Enchantment.Rarity.VERY_RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("lure", new EnchantmentFishingSpeed(Enchantment.Rarity.RARE, EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("loyalty", new EnchantmentLoyalty(Enchantment.Rarity.UNCOMMON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("impaling", new EnchantmentImpaling(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("riptide", new EnchantmentRiptide(Enchantment.Rarity.RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("channeling", new EnchantmentChanneling(Enchantment.Rarity.VERY_RARE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}));
      func_210770_a("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EntityEquipmentSlot.values()));
      func_210770_a("vanishing_curse", new EnchantmentVanishingCurse(Enchantment.Rarity.VERY_RARE, EntityEquipmentSlot.values()));
   }

   private static void func_210770_a(String var0, Enchantment var1) {
      IRegistry.field_212628_q.func_82595_a(new ResourceLocation(var0), var1);
   }

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int field_185275_e;

      private Rarity(int var3) {
         this.field_185275_e = var3;
      }

      public int func_185270_a() {
         return this.field_185275_e;
      }
   }
}
