package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabs {
   public static final CreativeTabs[] field_78032_a = new CreativeTabs[12];
   public static final CreativeTabs field_78030_b = new CreativeTabs$1(0, "buildingBlocks");
   public static final CreativeTabs field_78031_c = new CreativeTabs$2(1, "decorations");
   public static final CreativeTabs field_78028_d = new CreativeTabs$3(2, "redstone");
   public static final CreativeTabs field_78029_e = new CreativeTabs$4(3, "transportation");
   public static final CreativeTabs field_78026_f = new CreativeTabs$5(4, "misc").func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.all});
   public static final CreativeTabs field_78027_g = new CreativeTabs$6(5, "search").func_78025_a("item_search.png");
   public static final CreativeTabs field_78039_h = new CreativeTabs$7(6, "food");
   public static final CreativeTabs field_78040_i = new CreativeTabs$8(7, "tools")
      .func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.digger, EnumEnchantmentType.fishing_rod, EnumEnchantmentType.breakable});
   public static final CreativeTabs field_78037_j = new CreativeTabs$9(8, "combat")
      .func_111229_a(
         new EnumEnchantmentType[]{
            EnumEnchantmentType.armor,
            EnumEnchantmentType.armor_feet,
            EnumEnchantmentType.armor_head,
            EnumEnchantmentType.armor_legs,
            EnumEnchantmentType.armor_torso,
            EnumEnchantmentType.bow,
            EnumEnchantmentType.weapon
         }
      );
   public static final CreativeTabs field_78038_k = new CreativeTabs$10(9, "brewing");
   public static final CreativeTabs field_78035_l = new CreativeTabs$11(10, "materials");
   public static final CreativeTabs field_78036_m = new CreativeTabs$12(11, "inventory").func_78025_a("inventory.png").func_78022_j().func_78014_h();
   private final int field_78033_n;
   private final String field_78034_o;
   private String field_78043_p = "items.png";
   private boolean field_78042_q = true;
   private boolean field_78041_r = true;
   private EnumEnchantmentType[] field_111230_s;
   private ItemStack field_151245_t;

   public CreativeTabs(int var1, String var2) {
      super();
      this.field_78033_n = var1;
      this.field_78034_o = var2;
      field_78032_a[var1] = this;
   }

   public int func_78021_a() {
      return this.field_78033_n;
   }

   public String func_78013_b() {
      return this.field_78034_o;
   }

   public String func_78024_c() {
      return "itemGroup." + this.func_78013_b();
   }

   public ItemStack func_151244_d() {
      if (this.field_151245_t == null) {
         this.field_151245_t = new ItemStack(this.func_78016_d(), 1, this.func_151243_f());
      }

      return this.field_151245_t;
   }

   public abstract Item func_78016_d();

   public int func_151243_f() {
      return 0;
   }

   public String func_78015_f() {
      return this.field_78043_p;
   }

   public CreativeTabs func_78025_a(String var1) {
      this.field_78043_p = var1;
      return this;
   }

   public boolean func_78019_g() {
      return this.field_78041_r;
   }

   public CreativeTabs func_78014_h() {
      this.field_78041_r = false;
      return this;
   }

   public boolean func_78017_i() {
      return this.field_78042_q;
   }

   public CreativeTabs func_78022_j() {
      this.field_78042_q = false;
      return this;
   }

   public int func_78020_k() {
      return this.field_78033_n % 6;
   }

   public boolean func_78023_l() {
      return this.field_78033_n < 6;
   }

   public EnumEnchantmentType[] func_111225_m() {
      return this.field_111230_s;
   }

   public CreativeTabs func_111229_a(EnumEnchantmentType... var1) {
      this.field_111230_s = var1;
      return this;
   }

   public boolean func_111226_a(EnumEnchantmentType var1) {
      if (this.field_111230_s == null) {
         return false;
      } else {
         for(EnumEnchantmentType var5 : this.field_111230_s) {
            if (var5 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   public void func_78018_a(List var1) {
      for(Item var3 : Item.field_150901_e) {
         if (var3 != null && var3.func_77640_w() == this) {
            var3.func_150895_a(var3, this, var1);
         }
      }

      if (this.func_111225_m() != null) {
         this.func_92116_a(var1, this.func_111225_m());
      }
   }

   public void func_92116_a(List var1, EnumEnchantmentType... var2) {
      for(Enchantment var6 : Enchantment.field_77331_b) {
         if (var6 != null && var6.field_77351_y != null) {
            boolean var7 = false;

            for(int var8 = 0; var8 < var2.length && !var7; ++var8) {
               if (var6.field_77351_y == var2[var8]) {
                  var7 = true;
               }
            }

            if (var7) {
               var1.add(Items.field_151134_bR.func_92111_a(new EnchantmentData(var6, var6.func_77325_b())));
            }
         }
      }
   }
}
