package net.minecraft.item;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;

public abstract class ItemGroup {
   public static final ItemGroup[] field_78032_a = new ItemGroup[12];
   public static final ItemGroup field_78030_b = (new ItemGroup(0, "buildingBlocks") {
      public ItemStack func_78016_d() {
         return new ItemStack(Blocks.field_196584_bK);
      }
   }).func_199783_b("building_blocks");
   public static final ItemGroup field_78031_c = new ItemGroup(1, "decorations") {
      public ItemStack func_78016_d() {
         return new ItemStack(Blocks.field_196803_gg);
      }
   };
   public static final ItemGroup field_78028_d = new ItemGroup(2, "redstone") {
      public ItemStack func_78016_d() {
         return new ItemStack(Items.field_151137_ax);
      }
   };
   public static final ItemGroup field_78029_e = new ItemGroup(3, "transportation") {
      public ItemStack func_78016_d() {
         return new ItemStack(Blocks.field_196552_aC);
      }
   };
   public static final ItemGroup field_78026_f = new ItemGroup(6, "misc") {
      public ItemStack func_78016_d() {
         return new ItemStack(Items.field_151129_at);
      }
   };
   public static final ItemGroup field_78027_g = (new ItemGroup(5, "search") {
      public ItemStack func_78016_d() {
         return new ItemStack(Items.field_151111_aL);
      }
   }).func_78025_a("item_search.png");
   public static final ItemGroup field_78039_h = new ItemGroup(7, "food") {
      public ItemStack func_78016_d() {
         return new ItemStack(Items.field_151034_e);
      }
   };
   public static final ItemGroup field_78040_i;
   public static final ItemGroup field_78037_j;
   public static final ItemGroup field_78038_k;
   public static final ItemGroup field_78035_l;
   public static final ItemGroup field_192395_m;
   public static final ItemGroup field_78036_m;
   private final int field_78033_n;
   private final String field_78034_o;
   private String field_199784_q;
   private String field_78043_p = "items.png";
   private boolean field_78042_q = true;
   private boolean field_78041_r = true;
   private EnumEnchantmentType[] field_111230_s = new EnumEnchantmentType[0];
   private ItemStack field_151245_t;

   public ItemGroup(int var1, String var2) {
      super();
      this.field_78033_n = var1;
      this.field_78034_o = var2;
      this.field_151245_t = ItemStack.field_190927_a;
      field_78032_a[var1] = this;
   }

   public int func_78021_a() {
      return this.field_78033_n;
   }

   public String func_78013_b() {
      return this.field_78034_o;
   }

   public String func_200300_c() {
      return this.field_199784_q == null ? this.field_78034_o : this.field_199784_q;
   }

   public String func_78024_c() {
      return "itemGroup." + this.func_78013_b();
   }

   public ItemStack func_151244_d() {
      if (this.field_151245_t.func_190926_b()) {
         this.field_151245_t = this.func_78016_d();
      }

      return this.field_151245_t;
   }

   public abstract ItemStack func_78016_d();

   public String func_78015_f() {
      return this.field_78043_p;
   }

   public ItemGroup func_78025_a(String var1) {
      this.field_78043_p = var1;
      return this;
   }

   public ItemGroup func_199783_b(String var1) {
      this.field_199784_q = var1;
      return this;
   }

   public boolean func_78019_g() {
      return this.field_78041_r;
   }

   public ItemGroup func_78014_h() {
      this.field_78041_r = false;
      return this;
   }

   public boolean func_78017_i() {
      return this.field_78042_q;
   }

   public ItemGroup func_78022_j() {
      this.field_78042_q = false;
      return this;
   }

   public int func_78020_k() {
      return this.field_78033_n % 6;
   }

   public boolean func_78023_l() {
      return this.field_78033_n < 6;
   }

   public boolean func_192394_m() {
      return this.func_78020_k() == 5;
   }

   public EnumEnchantmentType[] func_111225_m() {
      return this.field_111230_s;
   }

   public ItemGroup func_111229_a(EnumEnchantmentType... var1) {
      this.field_111230_s = var1;
      return this;
   }

   public boolean func_111226_a(@Nullable EnumEnchantmentType var1) {
      if (var1 != null) {
         EnumEnchantmentType[] var2 = this.field_111230_s;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumEnchantmentType var5 = var2[var4];
            if (var5 == var1) {
               return true;
            }
         }
      }

      return false;
   }

   public void func_78018_a(NonNullList<ItemStack> var1) {
      Iterator var2 = IRegistry.field_212630_s.iterator();

      while(var2.hasNext()) {
         Item var3 = (Item)var2.next();
         var3.func_150895_a(this, var1);
      }

   }

   static {
      field_78040_i = (new ItemGroup(8, "tools") {
         public ItemStack func_78016_d() {
            return new ItemStack(Items.field_151036_c);
         }
      }).func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.ALL, EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE});
      field_78037_j = (new ItemGroup(9, "combat") {
         public ItemStack func_78016_d() {
            return new ItemStack(Items.field_151010_B);
         }
      }).func_111229_a(new EnumEnchantmentType[]{EnumEnchantmentType.ALL, EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_CHEST, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON, EnumEnchantmentType.WEARABLE, EnumEnchantmentType.BREAKABLE, EnumEnchantmentType.TRIDENT});
      field_78038_k = new ItemGroup(10, "brewing") {
         public ItemStack func_78016_d() {
            return PotionUtils.func_185188_a(new ItemStack(Items.field_151068_bn), PotionTypes.field_185230_b);
         }
      };
      field_78035_l = field_78026_f;
      field_192395_m = new ItemGroup(4, "hotbar") {
         public ItemStack func_78016_d() {
            return new ItemStack(Blocks.field_150342_X);
         }

         public void func_78018_a(NonNullList<ItemStack> var1) {
            throw new RuntimeException("Implement exception client-side.");
         }

         public boolean func_192394_m() {
            return true;
         }
      };
      field_78036_m = (new ItemGroup(11, "inventory") {
         public ItemStack func_78016_d() {
            return new ItemStack(Blocks.field_150486_ae);
         }
      }).func_78025_a("inventory.png").func_78022_j().func_78014_h();
   }
}
