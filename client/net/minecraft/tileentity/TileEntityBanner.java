package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityBanner extends TileEntity {
   private int field_175120_a;
   private NBTTagList field_175118_f;
   private boolean field_175119_g;
   private List<TileEntityBanner.EnumBannerPattern> field_175122_h;
   private List<EnumDyeColor> field_175123_i;
   private String field_175121_j;

   public TileEntityBanner() {
      super();
   }

   public void func_175112_a(ItemStack var1) {
      this.field_175118_f = null;
      if (var1.func_77942_o() && var1.func_77978_p().func_150297_b("BlockEntityTag", 10)) {
         NBTTagCompound var2 = var1.func_77978_p().func_74775_l("BlockEntityTag");
         if (var2.func_74764_b("Patterns")) {
            this.field_175118_f = (NBTTagList)var2.func_150295_c("Patterns", 10).func_74737_b();
         }

         if (var2.func_150297_b("Base", 99)) {
            this.field_175120_a = var2.func_74762_e("Base");
         } else {
            this.field_175120_a = var1.func_77960_j() & 15;
         }
      } else {
         this.field_175120_a = var1.func_77960_j() & 15;
      }

      this.field_175122_h = null;
      this.field_175123_i = null;
      this.field_175121_j = "";
      this.field_175119_g = true;
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      func_181020_a(var1, this.field_175120_a, this.field_175118_f);
   }

   public static void func_181020_a(NBTTagCompound var0, int var1, NBTTagList var2) {
      var0.func_74768_a("Base", var1);
      if (var2 != null) {
         var0.func_74782_a("Patterns", var2);
      }

   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_175120_a = var1.func_74762_e("Base");
      this.field_175118_f = var1.func_150295_c("Patterns", 10);
      this.field_175122_h = null;
      this.field_175123_i = null;
      this.field_175121_j = null;
      this.field_175119_g = true;
   }

   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      return new S35PacketUpdateTileEntity(this.field_174879_c, 6, var1);
   }

   public int func_175115_b() {
      return this.field_175120_a;
   }

   public static int func_175111_b(ItemStack var0) {
      NBTTagCompound var1 = var0.func_179543_a("BlockEntityTag", false);
      return var1 != null && var1.func_74764_b("Base") ? var1.func_74762_e("Base") : var0.func_77960_j();
   }

   public static int func_175113_c(ItemStack var0) {
      NBTTagCompound var1 = var0.func_179543_a("BlockEntityTag", false);
      return var1 != null && var1.func_74764_b("Patterns") ? var1.func_150295_c("Patterns", 10).func_74745_c() : 0;
   }

   public List<TileEntityBanner.EnumBannerPattern> func_175114_c() {
      this.func_175109_g();
      return this.field_175122_h;
   }

   public NBTTagList func_181021_d() {
      return this.field_175118_f;
   }

   public List<EnumDyeColor> func_175110_d() {
      this.func_175109_g();
      return this.field_175123_i;
   }

   public String func_175116_e() {
      this.func_175109_g();
      return this.field_175121_j;
   }

   private void func_175109_g() {
      if (this.field_175122_h == null || this.field_175123_i == null || this.field_175121_j == null) {
         if (!this.field_175119_g) {
            this.field_175121_j = "";
         } else {
            this.field_175122_h = Lists.newArrayList();
            this.field_175123_i = Lists.newArrayList();
            this.field_175122_h.add(TileEntityBanner.EnumBannerPattern.BASE);
            this.field_175123_i.add(EnumDyeColor.func_176766_a(this.field_175120_a));
            this.field_175121_j = "b" + this.field_175120_a;
            if (this.field_175118_f != null) {
               for(int var1 = 0; var1 < this.field_175118_f.func_74745_c(); ++var1) {
                  NBTTagCompound var2 = this.field_175118_f.func_150305_b(var1);
                  TileEntityBanner.EnumBannerPattern var3 = TileEntityBanner.EnumBannerPattern.func_177268_a(var2.func_74779_i("Pattern"));
                  if (var3 != null) {
                     this.field_175122_h.add(var3);
                     int var4 = var2.func_74762_e("Color");
                     this.field_175123_i.add(EnumDyeColor.func_176766_a(var4));
                     this.field_175121_j = this.field_175121_j + var3.func_177273_b() + var4;
                  }
               }
            }

         }
      }
   }

   public static void func_175117_e(ItemStack var0) {
      NBTTagCompound var1 = var0.func_179543_a("BlockEntityTag", false);
      if (var1 != null && var1.func_150297_b("Patterns", 9)) {
         NBTTagList var2 = var1.func_150295_c("Patterns", 10);
         if (var2.func_74745_c() > 0) {
            var2.func_74744_a(var2.func_74745_c() - 1);
            if (var2.func_82582_d()) {
               var0.func_77978_p().func_82580_o("BlockEntityTag");
               if (var0.func_77978_p().func_82582_d()) {
                  var0.func_77982_d((NBTTagCompound)null);
               }
            }

         }
      }
   }

   public static enum EnumBannerPattern {
      BASE("base", "b"),
      SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
      SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
      SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
      SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
      STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
      STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
      STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
      STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
      STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
      STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
      STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
      STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
      STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
      CROSS("cross", "cr", "# #", " # ", "# #"),
      STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
      TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
      TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
      TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
      TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
      DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
      DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
      DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
      DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
      CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
      RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
      HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
      HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
      HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
      HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
      BORDER("border", "bo", "###", "# #", "###"),
      CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.field_150395_bd)),
      CREEPER("creeper", "cre", new ItemStack(Items.field_151144_bL, 1, 4)),
      GRADIENT("gradient", "gra", "# #", " # ", " # "),
      GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
      BRICKS("bricks", "bri", new ItemStack(Blocks.field_150336_V)),
      SKULL("skull", "sku", new ItemStack(Items.field_151144_bL, 1, 1)),
      FLOWER("flower", "flo", new ItemStack(Blocks.field_150328_O, 1, BlockFlower.EnumFlowerType.OXEYE_DAISY.func_176968_b())),
      MOJANG("mojang", "moj", new ItemStack(Items.field_151153_ao, 1, 1));

      private String field_177284_N;
      private String field_177285_O;
      private String[] field_177291_P;
      private ItemStack field_177290_Q;

      private EnumBannerPattern(String var3, String var4) {
         this.field_177291_P = new String[3];
         this.field_177284_N = var3;
         this.field_177285_O = var4;
      }

      private EnumBannerPattern(String var3, String var4, ItemStack var5) {
         this(var3, var4);
         this.field_177290_Q = var5;
      }

      private EnumBannerPattern(String var3, String var4, String var5, String var6, String var7) {
         this(var3, var4);
         this.field_177291_P[0] = var5;
         this.field_177291_P[1] = var6;
         this.field_177291_P[2] = var7;
      }

      public String func_177271_a() {
         return this.field_177284_N;
      }

      public String func_177273_b() {
         return this.field_177285_O;
      }

      public String[] func_177267_c() {
         return this.field_177291_P;
      }

      public boolean func_177270_d() {
         return this.field_177290_Q != null || this.field_177291_P[0] != null;
      }

      public boolean func_177269_e() {
         return this.field_177290_Q != null;
      }

      public ItemStack func_177272_f() {
         return this.field_177290_Q;
      }

      public static TileEntityBanner.EnumBannerPattern func_177268_a(String var0) {
         TileEntityBanner.EnumBannerPattern[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            TileEntityBanner.EnumBannerPattern var4 = var1[var3];
            if (var4.field_177285_O.equals(var0)) {
               return var4;
            }
         }

         return null;
      }
   }
}
