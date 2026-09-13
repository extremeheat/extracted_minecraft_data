package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererChestHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderBlocks {
   public IBlockAccess field_147845_a;
   private IIcon field_147840_d;
   private boolean field_147842_e;
   private boolean field_152631_f;
   private boolean field_147837_f;
   public static boolean field_147843_b = true;
   public boolean field_147844_c = true;
   private boolean field_147838_g = false;
   private double field_147859_h;
   private double field_147861_i;
   private double field_147855_j;
   private double field_147857_k;
   private double field_147851_l;
   private double field_147853_m;
   private boolean field_147847_n;
   private boolean field_147849_o;
   private final Minecraft field_147877_p;
   private int field_147875_q;
   private int field_147873_r;
   private int field_147871_s;
   private int field_147869_t;
   private int field_147867_u;
   private int field_147865_v;
   private boolean field_147863_w;
   private float field_147888_x;
   private float field_147886_y;
   private float field_147884_z;
   private float field_147814_A;
   private float field_147815_B;
   private float field_147816_C;
   private float field_147810_D;
   private float field_147811_E;
   private float field_147812_F;
   private float field_147813_G;
   private float field_147821_H;
   private float field_147822_I;
   private float field_147823_J;
   private float field_147824_K;
   private float field_147817_L;
   private float field_147818_M;
   private float field_147819_N;
   private float field_147820_O;
   private float field_147830_P;
   private float field_147829_Q;
   private int field_147832_R;
   private int field_147831_S;
   private int field_147826_T;
   private int field_147825_U;
   private int field_147828_V;
   private int field_147827_W;
   private int field_147835_X;
   private int field_147834_Y;
   private int field_147836_Z;
   private int field_147880_aa;
   private int field_147881_ab;
   private int field_147878_ac;
   private int field_147879_ad;
   private int field_147885_ae;
   private int field_147887_af;
   private int field_147882_ag;
   private int field_147883_ah;
   private int field_147866_ai;
   private int field_147868_aj;
   private int field_147862_ak;
   private int field_147864_al;
   private int field_147874_am;
   private int field_147876_an;
   private int field_147870_ao;
   private float field_147872_ap;
   private float field_147852_aq;
   private float field_147850_ar;
   private float field_147848_as;
   private float field_147846_at;
   private float field_147860_au;
   private float field_147858_av;
   private float field_147856_aw;
   private float field_147854_ax;
   private float field_147841_ay;
   private float field_147839_az;
   private float field_147833_aA;

   public RenderBlocks(IBlockAccess var1) {
      super();
      this.field_147845_a = var1;
      this.field_152631_f = false;
      this.field_147842_e = false;
      this.field_147877_p = Minecraft.func_71410_x();
   }

   public RenderBlocks() {
      super();
      this.field_147877_p = Minecraft.func_71410_x();
   }

   public void func_147757_a(IIcon var1) {
      this.field_147840_d = var1;
   }

   public void func_147771_a() {
      this.field_147840_d = null;
   }

   public boolean func_147744_b() {
      return this.field_147840_d != null;
   }

   public void func_147786_a(boolean var1) {
      this.field_147838_g = var1;
   }

   public void func_147753_b(boolean var1) {
      this.field_147837_f = var1;
   }

   public void func_147782_a(double var1, double var3, double var5, double var7, double var9, double var11) {
      if (!this.field_147847_n) {
         this.field_147859_h = var1;
         this.field_147861_i = var7;
         this.field_147855_j = var3;
         this.field_147857_k = var9;
         this.field_147851_l = var5;
         this.field_147853_m = var11;
         this.field_147849_o = this.field_147877_p.field_71474_y.field_74348_k >= 2
            && (
               this.field_147859_h > 0.0
                  || this.field_147861_i < 1.0
                  || this.field_147855_j > 0.0
                  || this.field_147857_k < 1.0
                  || this.field_147851_l > 0.0
                  || this.field_147853_m < 1.0
            );
      }
   }

   public void func_147775_a(Block var1) {
      if (!this.field_147847_n) {
         this.field_147859_h = var1.func_149704_x();
         this.field_147861_i = var1.func_149753_y();
         this.field_147855_j = var1.func_149665_z();
         this.field_147857_k = var1.func_149669_A();
         this.field_147851_l = var1.func_149706_B();
         this.field_147853_m = var1.func_149693_C();
         this.field_147849_o = this.field_147877_p.field_71474_y.field_74348_k >= 2
            && (
               this.field_147859_h > 0.0
                  || this.field_147861_i < 1.0
                  || this.field_147855_j > 0.0
                  || this.field_147857_k < 1.0
                  || this.field_147851_l > 0.0
                  || this.field_147853_m < 1.0
            );
      }
   }

   public void func_147770_b(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.field_147859_h = var1;
      this.field_147861_i = var7;
      this.field_147855_j = var3;
      this.field_147857_k = var9;
      this.field_147851_l = var5;
      this.field_147853_m = var11;
      this.field_147847_n = true;
      this.field_147849_o = this.field_147877_p.field_71474_y.field_74348_k >= 2
         && (
            this.field_147859_h > 0.0
               || this.field_147861_i < 1.0
               || this.field_147855_j > 0.0
               || this.field_147857_k < 1.0
               || this.field_147851_l > 0.0
               || this.field_147853_m < 1.0
         );
   }

   public void func_147762_c() {
      this.field_147847_n = false;
   }

   public void func_147792_a(Block var1, int var2, int var3, int var4, IIcon var5) {
      this.func_147757_a(var5);
      this.func_147805_b(var1, var2, var3, var4);
      this.func_147771_a();
   }

   public void func_147769_a(Block var1, int var2, int var3, int var4) {
      this.field_147837_f = true;
      this.func_147805_b(var1, var2, var3, var4);
      this.field_147837_f = false;
   }

   public boolean func_147805_b(Block var1, int var2, int var3, int var4) {
      int var5 = var1.func_149645_b();
      if (var5 == -1) {
         return false;
      } else {
         var1.func_149719_a(this.field_147845_a, var2, var3, var4);
         this.func_147775_a(var1);
         if (var5 == 0) {
            return this.func_147784_q(var1, var2, var3, var4);
         } else if (var5 == 4) {
            return this.func_147721_p(var1, var2, var3, var4);
         } else if (var5 == 31) {
            return this.func_147742_r(var1, var2, var3, var4);
         } else if (var5 == 1) {
            return this.func_147746_l(var1, var2, var3, var4);
         } else if (var5 == 40) {
            return this.func_147774_a((BlockDoublePlant)var1, var2, var3, var4);
         } else if (var5 == 2) {
            return this.func_147791_c(var1, var2, var3, var4);
         } else if (var5 == 20) {
            return this.func_147726_j(var1, var2, var3, var4);
         } else if (var5 == 11) {
            return this.func_147735_a((BlockFence)var1, var2, var3, var4);
         } else if (var5 == 39) {
            return this.func_147779_s(var1, var2, var3, var4);
         } else if (var5 == 5) {
            return this.func_147788_h(var1, var2, var3, var4);
         } else if (var5 == 13) {
            return this.func_147755_t(var1, var2, var3, var4);
         } else if (var5 == 9) {
            return this.func_147766_a((BlockRailBase)var1, var2, var3, var4);
         } else if (var5 == 19) {
            return this.func_147724_m(var1, var2, var3, var4);
         } else if (var5 == 23) {
            return this.func_147783_o(var1, var2, var3, var4);
         } else if (var5 == 6) {
            return this.func_147796_n(var1, var2, var3, var4);
         } else if (var5 == 3) {
            return this.func_147801_a((BlockFire)var1, var2, var3, var4);
         } else if (var5 == 8) {
            return this.func_147794_i(var1, var2, var3, var4);
         } else if (var5 == 7) {
            return this.func_147760_u(var1, var2, var3, var4);
         } else if (var5 == 10) {
            return this.func_147722_a((BlockStairs)var1, var2, var3, var4);
         } else if (var5 == 27) {
            return this.func_147802_a((BlockDragonEgg)var1, var2, var3, var4);
         } else if (var5 == 32) {
            return this.func_147807_a((BlockWall)var1, var2, var3, var4);
         } else if (var5 == 12) {
            return this.func_147790_e(var1, var2, var3, var4);
         } else if (var5 == 29) {
            return this.func_147723_f(var1, var2, var3, var4);
         } else if (var5 == 30) {
            return this.func_147756_g(var1, var2, var3, var4);
         } else if (var5 == 14) {
            return this.func_147773_v(var1, var2, var3, var4);
         } else if (var5 == 15) {
            return this.func_147759_a((BlockRedstoneRepeater)var1, var2, var3, var4);
         } else if (var5 == 36) {
            return this.func_147748_a((BlockRedstoneDiode)var1, var2, var3, var4);
         } else if (var5 == 37) {
            return this.func_147781_a((BlockRedstoneComparator)var1, var2, var3, var4);
         } else if (var5 == 16) {
            return this.func_147731_b(var1, var2, var3, var4, false);
         } else if (var5 == 17) {
            return this.func_147809_c(var1, var2, var3, var4, true);
         } else if (var5 == 18) {
            return this.func_147767_a((BlockPane)var1, var2, var3, var4);
         } else if (var5 == 41) {
            return this.func_147733_k(var1, var2, var3, var4);
         } else if (var5 == 21) {
            return this.func_147776_a((BlockFenceGate)var1, var2, var3, var4);
         } else if (var5 == 24) {
            return this.func_147785_a((BlockCauldron)var1, var2, var3, var4);
         } else if (var5 == 33) {
            return this.func_147752_a((BlockFlowerPot)var1, var2, var3, var4);
         } else if (var5 == 35) {
            return this.func_147725_a((BlockAnvil)var1, var2, var3, var4);
         } else if (var5 == 25) {
            return this.func_147741_a((BlockBrewingStand)var1, var2, var3, var4);
         } else if (var5 == 26) {
            return this.func_147743_a((BlockEndPortalFrame)var1, var2, var3, var4);
         } else if (var5 == 28) {
            return this.func_147772_a((BlockCocoa)var1, var2, var3, var4);
         } else if (var5 == 34) {
            return this.func_147797_a((BlockBeacon)var1, var2, var3, var4);
         } else {
            return var5 == 38 ? this.func_147803_a((BlockHopper)var1, var2, var3, var4) : false;
         }
      }
   }

   private boolean func_147743_a(BlockEndPortalFrame var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var6 = var5 & 3;
      if (var6 == 0) {
         this.field_147867_u = 3;
      } else if (var6 == 3) {
         this.field_147867_u = 1;
      } else if (var6 == 1) {
         this.field_147867_u = 2;
      }

      if (!BlockEndPortalFrame.func_150020_b(var5)) {
         this.func_147782_a(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
         this.func_147784_q(var1, var2, var3, var4);
         this.field_147867_u = 0;
         return true;
      } else {
         this.field_147837_f = true;
         this.func_147782_a(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
         this.func_147784_q(var1, var2, var3, var4);
         this.func_147757_a(var1.func_150021_e());
         this.func_147782_a(0.25, 0.8125, 0.25, 0.75, 1.0, 0.75);
         this.func_147784_q(var1, var2, var3, var4);
         this.field_147837_f = false;
         this.func_147771_a();
         this.field_147867_u = 0;
         return true;
      }
   }

   private boolean func_147773_v(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var7 = BlockBed.func_149895_l(var6);
      boolean var8 = BlockBed.func_149975_b(var6);
      float var9 = 0.5F;
      float var10 = 1.0F;
      float var11 = 0.8F;
      float var12 = 0.6F;
      int var25 = var1.func_149677_c(this.field_147845_a, var2, var3, var4);
      var5.func_78380_c(var25);
      var5.func_78386_a(var9, var9, var9);
      IIcon var26 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 0);
      double var27 = (double)var26.func_94209_e();
      double var29 = (double)var26.func_94212_f();
      double var31 = (double)var26.func_94206_g();
      double var33 = (double)var26.func_94210_h();
      double var35 = (double)var2 + this.field_147859_h;
      double var37 = (double)var2 + this.field_147861_i;
      double var39 = (double)var3 + this.field_147855_j + 0.1875;
      double var41 = (double)var4 + this.field_147851_l;
      double var43 = (double)var4 + this.field_147853_m;
      var5.func_78374_a(var35, var39, var43, var27, var33);
      var5.func_78374_a(var35, var39, var41, var27, var31);
      var5.func_78374_a(var37, var39, var41, var29, var31);
      var5.func_78374_a(var37, var39, var43, var29, var33);
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4));
      var5.func_78386_a(var10, var10, var10);
      var26 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 1);
      var27 = (double)var26.func_94209_e();
      var29 = (double)var26.func_94212_f();
      var31 = (double)var26.func_94206_g();
      var33 = (double)var26.func_94210_h();
      var35 = var27;
      var37 = var29;
      var39 = var31;
      var41 = var31;
      var43 = var27;
      double var45 = var29;
      double var47 = var33;
      double var49 = var33;
      if (var7 == 0) {
         var37 = var27;
         var39 = var33;
         var43 = var29;
         var49 = var31;
      } else if (var7 == 2) {
         var35 = var29;
         var41 = var33;
         var45 = var27;
         var47 = var31;
      } else if (var7 == 3) {
         var35 = var29;
         var41 = var33;
         var45 = var27;
         var47 = var31;
         var37 = var27;
         var39 = var33;
         var43 = var29;
         var49 = var31;
      }

      double var51 = (double)var2 + this.field_147859_h;
      double var53 = (double)var2 + this.field_147861_i;
      double var55 = (double)var3 + this.field_147857_k;
      double var57 = (double)var4 + this.field_147851_l;
      double var59 = (double)var4 + this.field_147853_m;
      var5.func_78374_a(var53, var55, var59, var43, var47);
      var5.func_78374_a(var53, var55, var57, var35, var39);
      var5.func_78374_a(var51, var55, var57, var37, var41);
      var5.func_78374_a(var51, var55, var59, var45, var49);
      int var61 = Direction.field_71582_c[var7];
      if (var8) {
         var61 = Direction.field_71582_c[Direction.field_71580_e[var7]];
      }

      byte var62 = 4;
      switch(var7) {
         case 0:
            var62 = 5;
            break;
         case 1:
            var62 = 3;
         case 2:
         default:
            break;
         case 3:
            var62 = 2;
      }

      if (var61 != 2 && (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 - 1, 2))) {
         var5.func_78380_c(this.field_147851_l > 0.0 ? var25 : var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1));
         var5.func_78386_a(var11, var11, var11);
         this.field_147842_e = var62 == 2;
         this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 2));
      }

      if (var61 != 3 && (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 + 1, 3))) {
         var5.func_78380_c(this.field_147853_m < 1.0 ? var25 : var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1));
         var5.func_78386_a(var11, var11, var11);
         this.field_147842_e = var62 == 3;
         this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3));
      }

      if (var61 != 4 && (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 - 1, var3, var4, 4))) {
         var5.func_78380_c(this.field_147851_l > 0.0 ? var25 : var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4));
         var5.func_78386_a(var12, var12, var12);
         this.field_147842_e = var62 == 4;
         this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 4));
      }

      if (var61 != 5 && (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 + 1, var3, var4, 5))) {
         var5.func_78380_c(this.field_147853_m < 1.0 ? var25 : var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4));
         var5.func_78386_a(var12, var12, var12);
         this.field_147842_e = var62 == 5;
         this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 5));
      }

      this.field_147842_e = false;
      return true;
   }

   private boolean func_147741_a(BlockBrewingStand var1, int var2, int var3, int var4) {
      this.func_147782_a(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625);
      this.func_147784_q(var1, var2, var3, var4);
      this.func_147757_a(var1.func_149959_e());
      this.field_147837_f = true;
      this.func_147782_a(0.5625, 0.0, 0.3125, 0.9375, 0.125, 0.6875);
      this.func_147784_q(var1, var2, var3, var4);
      this.func_147782_a(0.125, 0.0, 0.0625, 0.5, 0.125, 0.4375);
      this.func_147784_q(var1, var2, var3, var4);
      this.func_147782_a(0.125, 0.0, 0.5625, 0.5, 0.125, 0.9375);
      this.func_147784_q(var1, var2, var3, var4);
      this.field_147837_f = false;
      this.func_147771_a();
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var6 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
         float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
         float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
         var7 = var10;
         var8 = var11;
         var9 = var12;
      }

      var5.func_78386_a(var7, var8, var9);
      IIcon var31 = this.func_147787_a(var1, 0, 0);
      if (this.func_147744_b()) {
         var31 = this.field_147840_d;
      }

      double var32 = (double)var31.func_94206_g();
      double var13 = (double)var31.func_94210_h();
      int var15 = this.field_147845_a.func_72805_g(var2, var3, var4);

      for(int var16 = 0; var16 < 3; ++var16) {
         double var17 = (double)var16 * 3.141592653589793 * 2.0 / 3.0 + 1.5707963267948966;
         double var19 = (double)var31.func_94214_a(8.0);
         double var21 = (double)var31.func_94212_f();
         if ((var15 & 1 << var16) != 0) {
            var21 = (double)var31.func_94209_e();
         }

         double var23 = (double)var2 + 0.5;
         double var25 = (double)var2 + 0.5 + Math.sin(var17) * 8.0 / 16.0;
         double var27 = (double)var4 + 0.5;
         double var29 = (double)var4 + 0.5 + Math.cos(var17) * 8.0 / 16.0;
         var5.func_78374_a(var23, (double)(var3 + 1), var27, var19, var32);
         var5.func_78374_a(var23, (double)(var3 + 0), var27, var19, var13);
         var5.func_78374_a(var25, (double)(var3 + 0), var29, var21, var13);
         var5.func_78374_a(var25, (double)(var3 + 1), var29, var21, var32);
         var5.func_78374_a(var25, (double)(var3 + 1), var29, var21, var32);
         var5.func_78374_a(var25, (double)(var3 + 0), var29, var21, var13);
         var5.func_78374_a(var23, (double)(var3 + 0), var27, var19, var13);
         var5.func_78374_a(var23, (double)(var3 + 1), var27, var19, var32);
      }

      var1.func_149683_g();
      return true;
   }

   private boolean func_147785_a(BlockCauldron var1, int var2, int var3, int var4) {
      this.func_147784_q(var1, var2, var3, var4);
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var6 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
         float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
         float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
         var7 = var10;
         var8 = var11;
         var9 = var12;
      }

      var5.func_78386_a(var7, var8, var9);
      IIcon var15 = var1.func_149733_h(2);
      float var16 = 0.125F;
      this.func_147764_f(var1, (double)((float)var2 - 1.0F + var16), (double)var3, (double)var4, var15);
      this.func_147798_e(var1, (double)((float)var2 + 1.0F - var16), (double)var3, (double)var4, var15);
      this.func_147734_d(var1, (double)var2, (double)var3, (double)((float)var4 - 1.0F + var16), var15);
      this.func_147761_c(var1, (double)var2, (double)var3, (double)((float)var4 + 1.0F - var16), var15);
      IIcon var17 = BlockCauldron.func_150026_e("inner");
      this.func_147806_b(var1, (double)var2, (double)((float)var3 - 1.0F + 0.25F), (double)var4, var17);
      this.func_147768_a(var1, (double)var2, (double)((float)var3 + 1.0F - 0.75F), (double)var4, var17);
      int var13 = this.field_147845_a.func_72805_g(var2, var3, var4);
      if (var13 > 0) {
         IIcon var14 = BlockLiquid.func_149803_e("water_still");
         this.func_147806_b(var1, (double)var2, (double)((float)var3 - 1.0F + BlockCauldron.func_150025_c(var13)), (double)var4, var14);
      }

      return true;
   }

   private boolean func_147752_a(BlockFlowerPot var1, int var2, int var3, int var4) {
      this.func_147784_q(var1, var2, var3, var4);
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      IIcon var7 = this.func_147777_a(var1, 0);
      float var8 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var6 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var5.func_78386_a(var8, var9, var10);
      float var25 = 0.1865F;
      this.func_147764_f(var1, (double)((float)var2 - 0.5F + var25), (double)var3, (double)var4, var7);
      this.func_147798_e(var1, (double)((float)var2 + 0.5F - var25), (double)var3, (double)var4, var7);
      this.func_147734_d(var1, (double)var2, (double)var3, (double)((float)var4 - 0.5F + var25), var7);
      this.func_147761_c(var1, (double)var2, (double)var3, (double)((float)var4 + 0.5F - var25), var7);
      this.func_147806_b(var1, (double)var2, (double)((float)var3 - 0.5F + var25 + 0.1875F), (double)var4, this.func_147745_b(Blocks.field_150346_d));
      TileEntity var26 = this.field_147845_a.func_147438_o(var2, var3, var4);
      if (var26 != null && var26 instanceof TileEntityFlowerPot) {
         Item var27 = ((TileEntityFlowerPot)var26).func_145965_a();
         int var14 = ((TileEntityFlowerPot)var26).func_145966_b();
         if (var27 instanceof ItemBlock) {
            Block var15 = Block.func_149634_a(var27);
            int var16 = var15.func_149645_b();
            float var17 = 0.0F;
            float var18 = 4.0F;
            float var19 = 0.0F;
            var5.func_78372_c(var17 / 16.0F, var18 / 16.0F, var19 / 16.0F);
            var6 = var15.func_149720_d(this.field_147845_a, var2, var3, var4);
            if (var6 != 16777215) {
               var8 = (float)(var6 >> 16 & 0xFF) / 255.0F;
               var9 = (float)(var6 >> 8 & 0xFF) / 255.0F;
               var10 = (float)(var6 & 0xFF) / 255.0F;
               var5.func_78386_a(var8, var9, var10);
            }

            if (var16 == 1) {
               this.func_147765_a(this.func_147787_a(var15, 0, var14), (double)var2, (double)var3, (double)var4, 0.75F);
            } else if (var16 == 13) {
               this.field_147837_f = true;
               float var20 = 0.125F;
               this.func_147782_a((double)(0.5F - var20), 0.0, (double)(0.5F - var20), (double)(0.5F + var20), 0.25, (double)(0.5F + var20));
               this.func_147784_q(var15, var2, var3, var4);
               this.func_147782_a((double)(0.5F - var20), 0.25, (double)(0.5F - var20), (double)(0.5F + var20), 0.5, (double)(0.5F + var20));
               this.func_147784_q(var15, var2, var3, var4);
               this.func_147782_a((double)(0.5F - var20), 0.5, (double)(0.5F - var20), (double)(0.5F + var20), 0.75, (double)(0.5F + var20));
               this.func_147784_q(var15, var2, var3, var4);
               this.field_147837_f = false;
               this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            }

            var5.func_78372_c(-var17 / 16.0F, -var18 / 16.0F, -var19 / 16.0F);
         }
      }

      return true;
   }

   private boolean func_147725_a(BlockAnvil var1, int var2, int var3, int var4) {
      return this.func_147780_a(var1, var2, var3, var4, this.field_147845_a.func_72805_g(var2, var3, var4));
   }

   public boolean func_147780_a(BlockAnvil var1, int var2, int var3, int var4, int var5) {
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var7 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var6.func_78386_a(var8, var9, var10);
      return this.func_147728_a(var1, var2, var3, var4, var5, false);
   }

   private boolean func_147728_a(BlockAnvil var1, int var2, int var3, int var4, int var5, boolean var6) {
      int var7 = var6 ? 0 : var5 & 3;
      boolean var8 = false;
      float var9 = 0.0F;
      switch(var7) {
         case 0:
            this.field_147871_s = 2;
            this.field_147869_t = 1;
            this.field_147867_u = 3;
            this.field_147865_v = 3;
            break;
         case 1:
            this.field_147875_q = 1;
            this.field_147873_r = 2;
            this.field_147867_u = 2;
            this.field_147865_v = 1;
            var8 = true;
            break;
         case 2:
            this.field_147871_s = 1;
            this.field_147869_t = 2;
            break;
         case 3:
            this.field_147875_q = 2;
            this.field_147873_r = 1;
            this.field_147867_u = 1;
            this.field_147865_v = 2;
            var8 = true;
      }

      var9 = this.func_147737_a(var1, var2, var3, var4, 0, var9, 0.75F, 0.25F, 0.75F, var8, var6, var5);
      var9 = this.func_147737_a(var1, var2, var3, var4, 1, var9, 0.5F, 0.0625F, 0.625F, var8, var6, var5);
      var9 = this.func_147737_a(var1, var2, var3, var4, 2, var9, 0.25F, 0.3125F, 0.5F, var8, var6, var5);
      var9 = this.func_147737_a(var1, var2, var3, var4, 3, var9, 0.625F, 0.375F, 1.0F, var8, var6, var5);
      this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.field_147875_q = 0;
      this.field_147873_r = 0;
      this.field_147871_s = 0;
      this.field_147869_t = 0;
      this.field_147867_u = 0;
      this.field_147865_v = 0;
      return true;
   }

   private float func_147737_a(
      BlockAnvil var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, float var9, boolean var10, boolean var11, int var12
   ) {
      if (var10) {
         float var13 = var7;
         var7 = var9;
         var9 = var13;
      }

      var7 /= 2.0F;
      var9 /= 2.0F;
      var1.field_149833_b = var5;
      this.func_147782_a((double)(0.5F - var7), (double)var6, (double)(0.5F - var9), (double)(0.5F + var7), (double)(var6 + var8), (double)(0.5F + var9));
      if (var11) {
         Tessellator var16 = Tessellator.field_78398_a;
         var16.func_78382_b();
         var16.func_78375_b(0.0F, -1.0F, 0.0F);
         this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 0, var12));
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78375_b(0.0F, 1.0F, 0.0F);
         this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 1, var12));
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78375_b(0.0F, 0.0F, -1.0F);
         this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 2, var12));
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78375_b(0.0F, 0.0F, 1.0F);
         this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 3, var12));
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78375_b(-1.0F, 0.0F, 0.0F);
         this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 4, var12));
         var16.func_78381_a();
         var16.func_78382_b();
         var16.func_78375_b(1.0F, 0.0F, 0.0F);
         this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 5, var12));
         var16.func_78381_a();
      } else {
         this.func_147784_q(var1, var2, var3, var4);
      }

      return var6 + var8;
   }

   public boolean func_147791_c(Block var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72805_g(var2, var3, var4);
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var6.func_78386_a(1.0F, 1.0F, 1.0F);
      double var7 = 0.4000000059604645;
      double var9 = 0.5 - var7;
      double var11 = 0.20000000298023224;
      if (var5 == 1) {
         this.func_147747_a(var1, (double)var2 - var9, (double)var3 + var11, (double)var4, -var7, 0.0, 0);
      } else if (var5 == 2) {
         this.func_147747_a(var1, (double)var2 + var9, (double)var3 + var11, (double)var4, var7, 0.0, 0);
      } else if (var5 == 3) {
         this.func_147747_a(var1, (double)var2, (double)var3 + var11, (double)var4 - var9, 0.0, -var7, 0);
      } else if (var5 == 4) {
         this.func_147747_a(var1, (double)var2, (double)var3 + var11, (double)var4 + var9, 0.0, var7, 0);
      } else {
         this.func_147747_a(var1, (double)var2, (double)var3, (double)var4, 0.0, 0.0, 0);
      }

      return true;
   }

   private boolean func_147759_a(BlockRedstoneRepeater var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var6 = var5 & 3;
      int var7 = (var5 & 12) >> 2;
      Tessellator var8 = Tessellator.field_78398_a;
      var8.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var8.func_78386_a(1.0F, 1.0F, 1.0F);
      double var9 = -0.1875;
      boolean var11 = var1.func_149910_g(this.field_147845_a, var2, var3, var4, var5);
      double var12 = 0.0;
      double var14 = 0.0;
      double var16 = 0.0;
      double var18 = 0.0;
      switch(var6) {
         case 0:
            var18 = -0.3125;
            var14 = BlockRedstoneRepeater.field_149973_b[var7];
            break;
         case 1:
            var16 = 0.3125;
            var12 = -BlockRedstoneRepeater.field_149973_b[var7];
            break;
         case 2:
            var18 = 0.3125;
            var14 = -BlockRedstoneRepeater.field_149973_b[var7];
            break;
         case 3:
            var16 = -0.3125;
            var12 = BlockRedstoneRepeater.field_149973_b[var7];
      }

      if (!var11) {
         this.func_147747_a(var1, (double)var2 + var12, (double)var3 + var9, (double)var4 + var14, 0.0, 0.0, 0);
      } else {
         IIcon var20 = this.func_147745_b(Blocks.field_150357_h);
         this.func_147757_a(var20);
         float var21 = 2.0F;
         float var22 = 14.0F;
         float var23 = 7.0F;
         float var24 = 9.0F;
         switch(var6) {
            case 1:
            case 3:
               var21 = 7.0F;
               var22 = 9.0F;
               var23 = 2.0F;
               var24 = 14.0F;
            case 0:
            case 2:
            default:
               this.func_147782_a(
                  (double)(var21 / 16.0F + (float)var12),
                  0.125,
                  (double)(var23 / 16.0F + (float)var14),
                  (double)(var22 / 16.0F + (float)var12),
                  0.25,
                  (double)(var24 / 16.0F + (float)var14)
               );
               double var25 = (double)var20.func_94214_a((double)var21);
               double var27 = (double)var20.func_94207_b((double)var23);
               double var29 = (double)var20.func_94214_a((double)var22);
               double var31 = (double)var20.func_94207_b((double)var24);
               var8.func_78374_a(
                  (double)((float)var2 + var21 / 16.0F) + var12, (double)((float)var3 + 0.25F), (double)((float)var4 + var23 / 16.0F) + var14, var25, var27
               );
               var8.func_78374_a(
                  (double)((float)var2 + var21 / 16.0F) + var12, (double)((float)var3 + 0.25F), (double)((float)var4 + var24 / 16.0F) + var14, var25, var31
               );
               var8.func_78374_a(
                  (double)((float)var2 + var22 / 16.0F) + var12, (double)((float)var3 + 0.25F), (double)((float)var4 + var24 / 16.0F) + var14, var29, var31
               );
               var8.func_78374_a(
                  (double)((float)var2 + var22 / 16.0F) + var12, (double)((float)var3 + 0.25F), (double)((float)var4 + var23 / 16.0F) + var14, var29, var27
               );
               this.func_147784_q(var1, var2, var3, var4);
               this.func_147782_a(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
               this.func_147771_a();
         }
      }

      var8.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var8.func_78386_a(1.0F, 1.0F, 1.0F);
      this.func_147747_a(var1, (double)var2 + var16, (double)var3 + var9, (double)var4 + var18, 0.0, 0.0, 0);
      this.func_147748_a(var1, var2, var3, var4);
      return true;
   }

   private boolean func_147781_a(BlockRedstoneComparator var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var7 = var6 & 3;
      double var8 = 0.0;
      double var10 = -0.1875;
      double var12 = 0.0;
      double var14 = 0.0;
      double var16 = 0.0;
      IIcon var18;
      if (var1.func_149969_d(var6)) {
         var18 = Blocks.field_150429_aA.func_149733_h(0);
      } else {
         var10 -= 0.1875;
         var18 = Blocks.field_150437_az.func_149733_h(0);
      }

      switch(var7) {
         case 0:
            var12 = -0.3125;
            var16 = 1.0;
            break;
         case 1:
            var8 = 0.3125;
            var14 = -1.0;
            break;
         case 2:
            var12 = 0.3125;
            var16 = -1.0;
            break;
         case 3:
            var8 = -0.3125;
            var14 = 1.0;
      }

      this.func_147747_a(
         var1, (double)var2 + 0.25 * var14 + 0.1875 * var16, (double)((float)var3 - 0.1875F), (double)var4 + 0.25 * var16 + 0.1875 * var14, 0.0, 0.0, var6
      );
      this.func_147747_a(
         var1, (double)var2 + 0.25 * var14 + -0.1875 * var16, (double)((float)var3 - 0.1875F), (double)var4 + 0.25 * var16 + -0.1875 * var14, 0.0, 0.0, var6
      );
      this.func_147757_a(var18);
      this.func_147747_a(var1, (double)var2 + var8, (double)var3 + var10, (double)var4 + var12, 0.0, 0.0, var6);
      this.func_147771_a();
      this.func_147732_a(var1, var2, var3, var4, var7);
      return true;
   }

   private boolean func_147748_a(BlockRedstoneDiode var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      this.func_147732_a(var1, var2, var3, var4, this.field_147845_a.func_72805_g(var2, var3, var4) & 3);
      return true;
   }

   private void func_147732_a(BlockRedstoneDiode var1, int var2, int var3, int var4, int var5) {
      this.func_147784_q(var1, var2, var3, var4);
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var6.func_78386_a(1.0F, 1.0F, 1.0F);
      int var7 = this.field_147845_a.func_72805_g(var2, var3, var4);
      IIcon var8 = this.func_147787_a(var1, 1, var7);
      double var9 = (double)var8.func_94209_e();
      double var11 = (double)var8.func_94212_f();
      double var13 = (double)var8.func_94206_g();
      double var15 = (double)var8.func_94210_h();
      double var17 = 0.125;
      double var19 = (double)(var2 + 1);
      double var21 = (double)(var2 + 1);
      double var23 = (double)(var2 + 0);
      double var25 = (double)(var2 + 0);
      double var27 = (double)(var4 + 0);
      double var29 = (double)(var4 + 1);
      double var31 = (double)(var4 + 1);
      double var33 = (double)(var4 + 0);
      double var35 = (double)var3 + var17;
      if (var5 == 2) {
         var19 = var21 = (double)(var2 + 0);
         var23 = var25 = (double)(var2 + 1);
         var27 = var33 = (double)(var4 + 1);
         var29 = var31 = (double)(var4 + 0);
      } else if (var5 == 3) {
         var19 = var25 = (double)(var2 + 0);
         var21 = var23 = (double)(var2 + 1);
         var27 = var29 = (double)(var4 + 0);
         var31 = var33 = (double)(var4 + 1);
      } else if (var5 == 1) {
         var19 = var25 = (double)(var2 + 1);
         var21 = var23 = (double)(var2 + 0);
         var27 = var29 = (double)(var4 + 1);
         var31 = var33 = (double)(var4 + 0);
      }

      var6.func_78374_a(var25, var35, var33, var9, var13);
      var6.func_78374_a(var23, var35, var31, var9, var15);
      var6.func_78374_a(var21, var35, var29, var11, var15);
      var6.func_78374_a(var19, var35, var27, var11, var13);
   }

   public void func_147804_d(Block var1, int var2, int var3, int var4) {
      this.field_147837_f = true;
      this.func_147731_b(var1, var2, var3, var4, true);
      this.field_147837_f = false;
   }

   private boolean func_147731_b(Block var1, int var2, int var3, int var4, boolean var5) {
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      boolean var7 = var5 || (var6 & 8) != 0;
      int var8 = BlockPistonBase.func_150076_b(var6);
      float var9 = 0.25F;
      if (var7) {
         switch(var8) {
            case 0:
               this.field_147875_q = 3;
               this.field_147873_r = 3;
               this.field_147871_s = 3;
               this.field_147869_t = 3;
               this.func_147782_a(0.0, 0.25, 0.0, 1.0, 1.0, 1.0);
               break;
            case 1:
               this.func_147782_a(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
               break;
            case 2:
               this.field_147871_s = 1;
               this.field_147869_t = 2;
               this.func_147782_a(0.0, 0.0, 0.25, 1.0, 1.0, 1.0);
               break;
            case 3:
               this.field_147871_s = 2;
               this.field_147869_t = 1;
               this.field_147867_u = 3;
               this.field_147865_v = 3;
               this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 0.75);
               break;
            case 4:
               this.field_147875_q = 1;
               this.field_147873_r = 2;
               this.field_147867_u = 2;
               this.field_147865_v = 1;
               this.func_147782_a(0.25, 0.0, 0.0, 1.0, 1.0, 1.0);
               break;
            case 5:
               this.field_147875_q = 2;
               this.field_147873_r = 1;
               this.field_147867_u = 1;
               this.field_147865_v = 2;
               this.func_147782_a(0.0, 0.0, 0.0, 0.75, 1.0, 1.0);
         }

         ((BlockPistonBase)var1)
            .func_150070_b(
               (float)this.field_147859_h,
               (float)this.field_147855_j,
               (float)this.field_147851_l,
               (float)this.field_147861_i,
               (float)this.field_147857_k,
               (float)this.field_147853_m
            );
         this.func_147784_q(var1, var2, var3, var4);
         this.field_147875_q = 0;
         this.field_147873_r = 0;
         this.field_147871_s = 0;
         this.field_147869_t = 0;
         this.field_147867_u = 0;
         this.field_147865_v = 0;
         this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         ((BlockPistonBase)var1)
            .func_150070_b(
               (float)this.field_147859_h,
               (float)this.field_147855_j,
               (float)this.field_147851_l,
               (float)this.field_147861_i,
               (float)this.field_147857_k,
               (float)this.field_147853_m
            );
      } else {
         switch(var8) {
            case 0:
               this.field_147875_q = 3;
               this.field_147873_r = 3;
               this.field_147871_s = 3;
               this.field_147869_t = 3;
            case 1:
            default:
               break;
            case 2:
               this.field_147871_s = 1;
               this.field_147869_t = 2;
               break;
            case 3:
               this.field_147871_s = 2;
               this.field_147869_t = 1;
               this.field_147867_u = 3;
               this.field_147865_v = 3;
               break;
            case 4:
               this.field_147875_q = 1;
               this.field_147873_r = 2;
               this.field_147867_u = 2;
               this.field_147865_v = 1;
               break;
            case 5:
               this.field_147875_q = 2;
               this.field_147873_r = 1;
               this.field_147867_u = 1;
               this.field_147865_v = 2;
         }

         this.func_147784_q(var1, var2, var3, var4);
         this.field_147875_q = 0;
         this.field_147873_r = 0;
         this.field_147871_s = 0;
         this.field_147869_t = 0;
         this.field_147867_u = 0;
         this.field_147865_v = 0;
      }

      return true;
   }

   private void func_147763_a(double var1, double var3, double var5, double var7, double var9, double var11, float var13, double var14) {
      IIcon var16 = BlockPistonBase.func_150074_e("piston_side");
      if (this.func_147744_b()) {
         var16 = this.field_147840_d;
      }

      Tessellator var17 = Tessellator.field_78398_a;
      double var18 = (double)var16.func_94209_e();
      double var20 = (double)var16.func_94206_g();
      double var22 = (double)var16.func_94214_a(var14);
      double var24 = (double)var16.func_94207_b(4.0);
      var17.func_78386_a(var13, var13, var13);
      var17.func_78374_a(var1, var7, var9, var22, var20);
      var17.func_78374_a(var1, var5, var9, var18, var20);
      var17.func_78374_a(var3, var5, var11, var18, var24);
      var17.func_78374_a(var3, var7, var11, var22, var24);
   }

   private void func_147789_b(double var1, double var3, double var5, double var7, double var9, double var11, float var13, double var14) {
      IIcon var16 = BlockPistonBase.func_150074_e("piston_side");
      if (this.func_147744_b()) {
         var16 = this.field_147840_d;
      }

      Tessellator var17 = Tessellator.field_78398_a;
      double var18 = (double)var16.func_94209_e();
      double var20 = (double)var16.func_94206_g();
      double var22 = (double)var16.func_94214_a(var14);
      double var24 = (double)var16.func_94207_b(4.0);
      var17.func_78386_a(var13, var13, var13);
      var17.func_78374_a(var1, var5, var11, var22, var20);
      var17.func_78374_a(var1, var5, var9, var18, var20);
      var17.func_78374_a(var3, var7, var9, var18, var24);
      var17.func_78374_a(var3, var7, var11, var22, var24);
   }

   private void func_147738_c(double var1, double var3, double var5, double var7, double var9, double var11, float var13, double var14) {
      IIcon var16 = BlockPistonBase.func_150074_e("piston_side");
      if (this.func_147744_b()) {
         var16 = this.field_147840_d;
      }

      Tessellator var17 = Tessellator.field_78398_a;
      double var18 = (double)var16.func_94209_e();
      double var20 = (double)var16.func_94206_g();
      double var22 = (double)var16.func_94214_a(var14);
      double var24 = (double)var16.func_94207_b(4.0);
      var17.func_78386_a(var13, var13, var13);
      var17.func_78374_a(var3, var5, var9, var22, var20);
      var17.func_78374_a(var1, var5, var9, var18, var20);
      var17.func_78374_a(var1, var7, var11, var18, var24);
      var17.func_78374_a(var3, var7, var11, var22, var24);
   }

   public void func_147750_a(Block var1, int var2, int var3, int var4, boolean var5) {
      this.field_147837_f = true;
      this.func_147809_c(var1, var2, var3, var4, var5);
      this.field_147837_f = false;
   }

   private boolean func_147809_c(Block var1, int var2, int var3, int var4, boolean var5) {
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var7 = BlockPistonExtension.func_150085_b(var6);
      float var8 = 0.25F;
      float var9 = 0.375F;
      float var10 = 0.625F;
      float var11 = var5 ? 1.0F : 0.5F;
      double var12 = var5 ? 16.0 : 8.0;
      switch(var7) {
         case 0:
            this.field_147875_q = 3;
            this.field_147873_r = 3;
            this.field_147871_s = 3;
            this.field_147869_t = 3;
            this.func_147782_a(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147763_a(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 + 0.25F),
               (double)((float)var3 + 0.25F + var11),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.625F),
               0.8F,
               var12
            );
            this.func_147763_a(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 + 0.25F),
               (double)((float)var3 + 0.25F + var11),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.375F),
               0.8F,
               var12
            );
            this.func_147763_a(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 + 0.25F),
               (double)((float)var3 + 0.25F + var11),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.625F),
               0.6F,
               var12
            );
            this.func_147763_a(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 + 0.25F),
               (double)((float)var3 + 0.25F + var11),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.375F),
               0.6F,
               var12
            );
            break;
         case 1:
            this.func_147782_a(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147763_a(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 - 0.25F + 1.0F - var11),
               (double)((float)var3 - 0.25F + 1.0F),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.625F),
               0.8F,
               var12
            );
            this.func_147763_a(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 - 0.25F + 1.0F - var11),
               (double)((float)var3 - 0.25F + 1.0F),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.375F),
               0.8F,
               var12
            );
            this.func_147763_a(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 - 0.25F + 1.0F - var11),
               (double)((float)var3 - 0.25F + 1.0F),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.625F),
               0.6F,
               var12
            );
            this.func_147763_a(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 - 0.25F + 1.0F - var11),
               (double)((float)var3 - 0.25F + 1.0F),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.375F),
               0.6F,
               var12
            );
            break;
         case 2:
            this.field_147871_s = 1;
            this.field_147869_t = 2;
            this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147789_b(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 + 0.25F),
               (double)((float)var4 + 0.25F + var11),
               0.6F,
               var12
            );
            this.func_147789_b(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 + 0.25F),
               (double)((float)var4 + 0.25F + var11),
               0.6F,
               var12
            );
            this.func_147789_b(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 + 0.25F),
               (double)((float)var4 + 0.25F + var11),
               0.5F,
               var12
            );
            this.func_147789_b(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 + 0.25F),
               (double)((float)var4 + 0.25F + var11),
               1.0F,
               var12
            );
            break;
         case 3:
            this.field_147871_s = 2;
            this.field_147869_t = 1;
            this.field_147867_u = 3;
            this.field_147865_v = 3;
            this.func_147782_a(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147789_b(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 - 0.25F + 1.0F - var11),
               (double)((float)var4 - 0.25F + 1.0F),
               0.6F,
               var12
            );
            this.func_147789_b(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 - 0.25F + 1.0F - var11),
               (double)((float)var4 - 0.25F + 1.0F),
               0.6F,
               var12
            );
            this.func_147789_b(
               (double)((float)var2 + 0.375F),
               (double)((float)var2 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 - 0.25F + 1.0F - var11),
               (double)((float)var4 - 0.25F + 1.0F),
               0.5F,
               var12
            );
            this.func_147789_b(
               (double)((float)var2 + 0.625F),
               (double)((float)var2 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 - 0.25F + 1.0F - var11),
               (double)((float)var4 - 0.25F + 1.0F),
               1.0F,
               var12
            );
            break;
         case 4:
            this.field_147875_q = 1;
            this.field_147873_r = 2;
            this.field_147867_u = 2;
            this.field_147865_v = 1;
            this.func_147782_a(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147738_c(
               (double)((float)var2 + 0.25F),
               (double)((float)var2 + 0.25F + var11),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.375F),
               0.5F,
               var12
            );
            this.func_147738_c(
               (double)((float)var2 + 0.25F),
               (double)((float)var2 + 0.25F + var11),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.625F),
               1.0F,
               var12
            );
            this.func_147738_c(
               (double)((float)var2 + 0.25F),
               (double)((float)var2 + 0.25F + var11),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.375F),
               0.6F,
               var12
            );
            this.func_147738_c(
               (double)((float)var2 + 0.25F),
               (double)((float)var2 + 0.25F + var11),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.625F),
               0.6F,
               var12
            );
            break;
         case 5:
            this.field_147875_q = 2;
            this.field_147873_r = 1;
            this.field_147867_u = 1;
            this.field_147865_v = 2;
            this.func_147782_a(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147738_c(
               (double)((float)var2 - 0.25F + 1.0F - var11),
               (double)((float)var2 - 0.25F + 1.0F),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.375F),
               0.5F,
               var12
            );
            this.func_147738_c(
               (double)((float)var2 - 0.25F + 1.0F - var11),
               (double)((float)var2 - 0.25F + 1.0F),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.625F),
               1.0F,
               var12
            );
            this.func_147738_c(
               (double)((float)var2 - 0.25F + 1.0F - var11),
               (double)((float)var2 - 0.25F + 1.0F),
               (double)((float)var3 + 0.375F),
               (double)((float)var3 + 0.625F),
               (double)((float)var4 + 0.375F),
               (double)((float)var4 + 0.375F),
               0.6F,
               var12
            );
            this.func_147738_c(
               (double)((float)var2 - 0.25F + 1.0F - var11),
               (double)((float)var2 - 0.25F + 1.0F),
               (double)((float)var3 + 0.625F),
               (double)((float)var3 + 0.375F),
               (double)((float)var4 + 0.625F),
               (double)((float)var4 + 0.625F),
               0.6F,
               var12
            );
      }

      this.field_147875_q = 0;
      this.field_147873_r = 0;
      this.field_147871_s = 0;
      this.field_147869_t = 0;
      this.field_147867_u = 0;
      this.field_147865_v = 0;
      this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      return true;
   }

   public boolean func_147790_e(Block var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var6 = var5 & 7;
      boolean var7 = (var5 & 8) > 0;
      Tessellator var8 = Tessellator.field_78398_a;
      boolean var9 = this.func_147744_b();
      if (!var9) {
         this.func_147757_a(this.func_147745_b(Blocks.field_150347_e));
      }

      float var10 = 0.25F;
      float var11 = 0.1875F;
      float var12 = 0.1875F;
      if (var6 == 5) {
         this.func_147782_a((double)(0.5F - var11), 0.0, (double)(0.5F - var10), (double)(0.5F + var11), (double)var12, (double)(0.5F + var10));
      } else if (var6 == 6) {
         this.func_147782_a((double)(0.5F - var10), 0.0, (double)(0.5F - var11), (double)(0.5F + var10), (double)var12, (double)(0.5F + var11));
      } else if (var6 == 4) {
         this.func_147782_a((double)(0.5F - var11), (double)(0.5F - var10), (double)(1.0F - var12), (double)(0.5F + var11), (double)(0.5F + var10), 1.0);
      } else if (var6 == 3) {
         this.func_147782_a((double)(0.5F - var11), (double)(0.5F - var10), 0.0, (double)(0.5F + var11), (double)(0.5F + var10), (double)var12);
      } else if (var6 == 2) {
         this.func_147782_a((double)(1.0F - var12), (double)(0.5F - var10), (double)(0.5F - var11), 1.0, (double)(0.5F + var10), (double)(0.5F + var11));
      } else if (var6 == 1) {
         this.func_147782_a(0.0, (double)(0.5F - var10), (double)(0.5F - var11), (double)var12, (double)(0.5F + var10), (double)(0.5F + var11));
      } else if (var6 == 0) {
         this.func_147782_a((double)(0.5F - var10), (double)(1.0F - var12), (double)(0.5F - var11), (double)(0.5F + var10), 1.0, (double)(0.5F + var11));
      } else if (var6 == 7) {
         this.func_147782_a((double)(0.5F - var11), (double)(1.0F - var12), (double)(0.5F - var10), (double)(0.5F + var11), 1.0, (double)(0.5F + var10));
      }

      this.func_147784_q(var1, var2, var3, var4);
      if (!var9) {
         this.func_147771_a();
      }

      var8.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var8.func_78386_a(1.0F, 1.0F, 1.0F);
      IIcon var13 = this.func_147777_a(var1, 0);
      if (this.func_147744_b()) {
         var13 = this.field_147840_d;
      }

      double var14 = (double)var13.func_94209_e();
      double var16 = (double)var13.func_94206_g();
      double var18 = (double)var13.func_94212_f();
      double var20 = (double)var13.func_94210_h();
      Vec3[] var22 = new Vec3[8];
      float var23 = 0.0625F;
      float var24 = 0.0625F;
      float var25 = 0.625F;
      var22[0] = Vec3.func_72443_a((double)(-var23), 0.0, (double)(-var24));
      var22[1] = Vec3.func_72443_a((double)var23, 0.0, (double)(-var24));
      var22[2] = Vec3.func_72443_a((double)var23, 0.0, (double)var24);
      var22[3] = Vec3.func_72443_a((double)(-var23), 0.0, (double)var24);
      var22[4] = Vec3.func_72443_a((double)(-var23), (double)var25, (double)(-var24));
      var22[5] = Vec3.func_72443_a((double)var23, (double)var25, (double)(-var24));
      var22[6] = Vec3.func_72443_a((double)var23, (double)var25, (double)var24);
      var22[7] = Vec3.func_72443_a((double)(-var23), (double)var25, (double)var24);

      for(int var26 = 0; var26 < 8; ++var26) {
         if (var7) {
            var22[var26].field_72449_c -= 0.0625;
            var22[var26].func_72440_a(0.69813174F);
         } else {
            var22[var26].field_72449_c += 0.0625;
            var22[var26].func_72440_a(-0.69813174F);
         }

         if (var6 == 0 || var6 == 7) {
            var22[var26].func_72446_c(3.1415927F);
         }

         if (var6 == 6 || var6 == 0) {
            var22[var26].func_72442_b(1.5707964F);
         }

         if (var6 > 0 && var6 < 5) {
            var22[var26].field_72448_b -= 0.375;
            var22[var26].func_72440_a(1.5707964F);
            if (var6 == 4) {
               var22[var26].func_72442_b(0.0F);
            }

            if (var6 == 3) {
               var22[var26].func_72442_b(3.1415927F);
            }

            if (var6 == 2) {
               var22[var26].func_72442_b(1.5707964F);
            }

            if (var6 == 1) {
               var22[var26].func_72442_b(-1.5707964F);
            }

            var22[var26].field_72450_a += (double)var2 + 0.5;
            var22[var26].field_72448_b += (double)((float)var3 + 0.5F);
            var22[var26].field_72449_c += (double)var4 + 0.5;
         } else if (var6 != 0 && var6 != 7) {
            var22[var26].field_72450_a += (double)var2 + 0.5;
            var22[var26].field_72448_b += (double)((float)var3 + 0.125F);
            var22[var26].field_72449_c += (double)var4 + 0.5;
         } else {
            var22[var26].field_72450_a += (double)var2 + 0.5;
            var22[var26].field_72448_b += (double)((float)var3 + 0.875F);
            var22[var26].field_72449_c += (double)var4 + 0.5;
         }
      }

      Vec3 var31 = null;
      Vec3 var27 = null;
      Vec3 var28 = null;
      Vec3 var29 = null;

      for(int var30 = 0; var30 < 6; ++var30) {
         if (var30 == 0) {
            var14 = (double)var13.func_94214_a(7.0);
            var16 = (double)var13.func_94207_b(6.0);
            var18 = (double)var13.func_94214_a(9.0);
            var20 = (double)var13.func_94207_b(8.0);
         } else if (var30 == 2) {
            var14 = (double)var13.func_94214_a(7.0);
            var16 = (double)var13.func_94207_b(6.0);
            var18 = (double)var13.func_94214_a(9.0);
            var20 = (double)var13.func_94210_h();
         }

         if (var30 == 0) {
            var31 = var22[0];
            var27 = var22[1];
            var28 = var22[2];
            var29 = var22[3];
         } else if (var30 == 1) {
            var31 = var22[7];
            var27 = var22[6];
            var28 = var22[5];
            var29 = var22[4];
         } else if (var30 == 2) {
            var31 = var22[1];
            var27 = var22[0];
            var28 = var22[4];
            var29 = var22[5];
         } else if (var30 == 3) {
            var31 = var22[2];
            var27 = var22[1];
            var28 = var22[5];
            var29 = var22[6];
         } else if (var30 == 4) {
            var31 = var22[3];
            var27 = var22[2];
            var28 = var22[6];
            var29 = var22[7];
         } else if (var30 == 5) {
            var31 = var22[0];
            var27 = var22[3];
            var28 = var22[7];
            var29 = var22[4];
         }

         var8.func_78374_a(var31.field_72450_a, var31.field_72448_b, var31.field_72449_c, var14, var20);
         var8.func_78374_a(var27.field_72450_a, var27.field_72448_b, var27.field_72449_c, var18, var20);
         var8.func_78374_a(var28.field_72450_a, var28.field_72448_b, var28.field_72449_c, var18, var16);
         var8.func_78374_a(var29.field_72450_a, var29.field_72448_b, var29.field_72449_c, var14, var16);
      }

      return true;
   }

   public boolean func_147723_f(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var7 = var6 & 3;
      boolean var8 = (var6 & 4) == 4;
      boolean var9 = (var6 & 8) == 8;
      boolean var10 = !World.func_147466_a(this.field_147845_a, var2, var3 - 1, var4);
      boolean var11 = this.func_147744_b();
      if (!var11) {
         this.func_147757_a(this.func_147745_b(Blocks.field_150344_f));
      }

      float var12 = 0.25F;
      float var13 = 0.125F;
      float var14 = 0.125F;
      float var15 = 0.3F - var12;
      float var16 = 0.3F + var12;
      if (var7 == 2) {
         this.func_147782_a((double)(0.5F - var13), (double)var15, (double)(1.0F - var14), (double)(0.5F + var13), (double)var16, 1.0);
      } else if (var7 == 0) {
         this.func_147782_a((double)(0.5F - var13), (double)var15, 0.0, (double)(0.5F + var13), (double)var16, (double)var14);
      } else if (var7 == 1) {
         this.func_147782_a((double)(1.0F - var14), (double)var15, (double)(0.5F - var13), 1.0, (double)var16, (double)(0.5F + var13));
      } else if (var7 == 3) {
         this.func_147782_a(0.0, (double)var15, (double)(0.5F - var13), (double)var14, (double)var16, (double)(0.5F + var13));
      }

      this.func_147784_q(var1, var2, var3, var4);
      if (!var11) {
         this.func_147771_a();
      }

      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      IIcon var17 = this.func_147777_a(var1, 0);
      if (this.func_147744_b()) {
         var17 = this.field_147840_d;
      }

      double var18 = (double)var17.func_94209_e();
      double var20 = (double)var17.func_94206_g();
      double var22 = (double)var17.func_94212_f();
      double var24 = (double)var17.func_94210_h();
      Vec3[] var26 = new Vec3[8];
      float var27 = 0.046875F;
      float var28 = 0.046875F;
      float var29 = 0.3125F;
      var26[0] = Vec3.func_72443_a((double)(-var27), 0.0, (double)(-var28));
      var26[1] = Vec3.func_72443_a((double)var27, 0.0, (double)(-var28));
      var26[2] = Vec3.func_72443_a((double)var27, 0.0, (double)var28);
      var26[3] = Vec3.func_72443_a((double)(-var27), 0.0, (double)var28);
      var26[4] = Vec3.func_72443_a((double)(-var27), (double)var29, (double)(-var28));
      var26[5] = Vec3.func_72443_a((double)var27, (double)var29, (double)(-var28));
      var26[6] = Vec3.func_72443_a((double)var27, (double)var29, (double)var28);
      var26[7] = Vec3.func_72443_a((double)(-var27), (double)var29, (double)var28);

      for(int var30 = 0; var30 < 8; ++var30) {
         var26[var30].field_72449_c += 0.0625;
         if (var9) {
            var26[var30].func_72440_a(0.5235988F);
            var26[var30].field_72448_b -= 0.4375;
         } else if (var8) {
            var26[var30].func_72440_a(0.08726647F);
            var26[var30].field_72448_b -= 0.4375;
         } else {
            var26[var30].func_72440_a(-0.69813174F);
            var26[var30].field_72448_b -= 0.375;
         }

         var26[var30].func_72440_a(1.5707964F);
         if (var7 == 2) {
            var26[var30].func_72442_b(0.0F);
         }

         if (var7 == 0) {
            var26[var30].func_72442_b(3.1415927F);
         }

         if (var7 == 1) {
            var26[var30].func_72442_b(1.5707964F);
         }

         if (var7 == 3) {
            var26[var30].func_72442_b(-1.5707964F);
         }

         var26[var30].field_72450_a += (double)var2 + 0.5;
         var26[var30].field_72448_b += (double)((float)var3 + 0.3125F);
         var26[var30].field_72449_c += (double)var4 + 0.5;
      }

      Vec3 var60 = null;
      Vec3 var31 = null;
      Vec3 var32 = null;
      Vec3 var33 = null;
      byte var34 = 7;
      byte var35 = 9;
      byte var36 = 9;
      byte var37 = 16;

      for(int var38 = 0; var38 < 6; ++var38) {
         if (var38 == 0) {
            var60 = var26[0];
            var31 = var26[1];
            var32 = var26[2];
            var33 = var26[3];
            var18 = (double)var17.func_94214_a((double)var34);
            var20 = (double)var17.func_94207_b((double)var36);
            var22 = (double)var17.func_94214_a((double)var35);
            var24 = (double)var17.func_94207_b((double)(var36 + 2));
         } else if (var38 == 1) {
            var60 = var26[7];
            var31 = var26[6];
            var32 = var26[5];
            var33 = var26[4];
         } else if (var38 == 2) {
            var60 = var26[1];
            var31 = var26[0];
            var32 = var26[4];
            var33 = var26[5];
            var18 = (double)var17.func_94214_a((double)var34);
            var20 = (double)var17.func_94207_b((double)var36);
            var22 = (double)var17.func_94214_a((double)var35);
            var24 = (double)var17.func_94207_b((double)var37);
         } else if (var38 == 3) {
            var60 = var26[2];
            var31 = var26[1];
            var32 = var26[5];
            var33 = var26[6];
         } else if (var38 == 4) {
            var60 = var26[3];
            var31 = var26[2];
            var32 = var26[6];
            var33 = var26[7];
         } else if (var38 == 5) {
            var60 = var26[0];
            var31 = var26[3];
            var32 = var26[7];
            var33 = var26[4];
         }

         var5.func_78374_a(var60.field_72450_a, var60.field_72448_b, var60.field_72449_c, var18, var24);
         var5.func_78374_a(var31.field_72450_a, var31.field_72448_b, var31.field_72449_c, var22, var24);
         var5.func_78374_a(var32.field_72450_a, var32.field_72448_b, var32.field_72449_c, var22, var20);
         var5.func_78374_a(var33.field_72450_a, var33.field_72448_b, var33.field_72449_c, var18, var20);
      }

      float var61 = 0.09375F;
      float var39 = 0.09375F;
      float var40 = 0.03125F;
      var26[0] = Vec3.func_72443_a((double)(-var61), 0.0, (double)(-var39));
      var26[1] = Vec3.func_72443_a((double)var61, 0.0, (double)(-var39));
      var26[2] = Vec3.func_72443_a((double)var61, 0.0, (double)var39);
      var26[3] = Vec3.func_72443_a((double)(-var61), 0.0, (double)var39);
      var26[4] = Vec3.func_72443_a((double)(-var61), (double)var40, (double)(-var39));
      var26[5] = Vec3.func_72443_a((double)var61, (double)var40, (double)(-var39));
      var26[6] = Vec3.func_72443_a((double)var61, (double)var40, (double)var39);
      var26[7] = Vec3.func_72443_a((double)(-var61), (double)var40, (double)var39);

      for(int var41 = 0; var41 < 8; ++var41) {
         var26[var41].field_72449_c += 0.21875;
         if (var9) {
            var26[var41].field_72448_b -= 0.09375;
            var26[var41].field_72449_c -= 0.1625;
            var26[var41].func_72440_a(0.0F);
         } else if (var8) {
            var26[var41].field_72448_b += 0.015625;
            var26[var41].field_72449_c -= 0.171875;
            var26[var41].func_72440_a(0.17453294F);
         } else {
            var26[var41].func_72440_a(0.87266463F);
         }

         if (var7 == 2) {
            var26[var41].func_72442_b(0.0F);
         }

         if (var7 == 0) {
            var26[var41].func_72442_b(3.1415927F);
         }

         if (var7 == 1) {
            var26[var41].func_72442_b(1.5707964F);
         }

         if (var7 == 3) {
            var26[var41].func_72442_b(-1.5707964F);
         }

         var26[var41].field_72450_a += (double)var2 + 0.5;
         var26[var41].field_72448_b += (double)((float)var3 + 0.3125F);
         var26[var41].field_72449_c += (double)var4 + 0.5;
      }

      byte var62 = 5;
      byte var42 = 11;
      byte var43 = 3;
      byte var44 = 9;

      for(int var45 = 0; var45 < 6; ++var45) {
         if (var45 == 0) {
            var60 = var26[0];
            var31 = var26[1];
            var32 = var26[2];
            var33 = var26[3];
            var18 = (double)var17.func_94214_a((double)var62);
            var20 = (double)var17.func_94207_b((double)var43);
            var22 = (double)var17.func_94214_a((double)var42);
            var24 = (double)var17.func_94207_b((double)var44);
         } else if (var45 == 1) {
            var60 = var26[7];
            var31 = var26[6];
            var32 = var26[5];
            var33 = var26[4];
         } else if (var45 == 2) {
            var60 = var26[1];
            var31 = var26[0];
            var32 = var26[4];
            var33 = var26[5];
            var18 = (double)var17.func_94214_a((double)var62);
            var20 = (double)var17.func_94207_b((double)var43);
            var22 = (double)var17.func_94214_a((double)var42);
            var24 = (double)var17.func_94207_b((double)(var43 + 2));
         } else if (var45 == 3) {
            var60 = var26[2];
            var31 = var26[1];
            var32 = var26[5];
            var33 = var26[6];
         } else if (var45 == 4) {
            var60 = var26[3];
            var31 = var26[2];
            var32 = var26[6];
            var33 = var26[7];
         } else if (var45 == 5) {
            var60 = var26[0];
            var31 = var26[3];
            var32 = var26[7];
            var33 = var26[4];
         }

         var5.func_78374_a(var60.field_72450_a, var60.field_72448_b, var60.field_72449_c, var18, var24);
         var5.func_78374_a(var31.field_72450_a, var31.field_72448_b, var31.field_72449_c, var22, var24);
         var5.func_78374_a(var32.field_72450_a, var32.field_72448_b, var32.field_72449_c, var22, var20);
         var5.func_78374_a(var33.field_72450_a, var33.field_72448_b, var33.field_72449_c, var18, var20);
      }

      if (var8) {
         double var63 = var26[0].field_72448_b;
         float var47 = 0.03125F;
         float var48 = 0.5F - var47 / 2.0F;
         float var49 = var48 + var47;
         double var50 = (double)var17.func_94209_e();
         double var52 = (double)var17.func_94207_b(var8 ? 2.0 : 0.0);
         double var54 = (double)var17.func_94212_f();
         double var56 = (double)var17.func_94207_b(var8 ? 4.0 : 2.0);
         double var58 = (double)(var10 ? 3.5F : 1.5F) / 16.0;
         var5.func_78386_a(0.75F, 0.75F, 0.75F);
         if (var7 == 2) {
            var5.func_78374_a((double)((float)var2 + var48), (double)var3 + var58, (double)var4 + 0.25, var50, var52);
            var5.func_78374_a((double)((float)var2 + var49), (double)var3 + var58, (double)var4 + 0.25, var50, var56);
            var5.func_78374_a((double)((float)var2 + var49), (double)var3 + var58, (double)var4, var54, var56);
            var5.func_78374_a((double)((float)var2 + var48), (double)var3 + var58, (double)var4, var54, var52);
            var5.func_78374_a((double)((float)var2 + var48), var63, (double)var4 + 0.5, var50, var52);
            var5.func_78374_a((double)((float)var2 + var49), var63, (double)var4 + 0.5, var50, var56);
            var5.func_78374_a((double)((float)var2 + var49), (double)var3 + var58, (double)var4 + 0.25, var54, var56);
            var5.func_78374_a((double)((float)var2 + var48), (double)var3 + var58, (double)var4 + 0.25, var54, var52);
         } else if (var7 == 0) {
            var5.func_78374_a((double)((float)var2 + var48), (double)var3 + var58, (double)var4 + 0.75, var50, var52);
            var5.func_78374_a((double)((float)var2 + var49), (double)var3 + var58, (double)var4 + 0.75, var50, var56);
            var5.func_78374_a((double)((float)var2 + var49), var63, (double)var4 + 0.5, var54, var56);
            var5.func_78374_a((double)((float)var2 + var48), var63, (double)var4 + 0.5, var54, var52);
            var5.func_78374_a((double)((float)var2 + var48), (double)var3 + var58, (double)(var4 + 1), var50, var52);
            var5.func_78374_a((double)((float)var2 + var49), (double)var3 + var58, (double)(var4 + 1), var50, var56);
            var5.func_78374_a((double)((float)var2 + var49), (double)var3 + var58, (double)var4 + 0.75, var54, var56);
            var5.func_78374_a((double)((float)var2 + var48), (double)var3 + var58, (double)var4 + 0.75, var54, var52);
         } else if (var7 == 1) {
            var5.func_78374_a((double)var2, (double)var3 + var58, (double)((float)var4 + var49), var50, var56);
            var5.func_78374_a((double)var2 + 0.25, (double)var3 + var58, (double)((float)var4 + var49), var54, var56);
            var5.func_78374_a((double)var2 + 0.25, (double)var3 + var58, (double)((float)var4 + var48), var54, var52);
            var5.func_78374_a((double)var2, (double)var3 + var58, (double)((float)var4 + var48), var50, var52);
            var5.func_78374_a((double)var2 + 0.25, (double)var3 + var58, (double)((float)var4 + var49), var50, var56);
            var5.func_78374_a((double)var2 + 0.5, var63, (double)((float)var4 + var49), var54, var56);
            var5.func_78374_a((double)var2 + 0.5, var63, (double)((float)var4 + var48), var54, var52);
            var5.func_78374_a((double)var2 + 0.25, (double)var3 + var58, (double)((float)var4 + var48), var50, var52);
         } else {
            var5.func_78374_a((double)var2 + 0.5, var63, (double)((float)var4 + var49), var50, var56);
            var5.func_78374_a((double)var2 + 0.75, (double)var3 + var58, (double)((float)var4 + var49), var54, var56);
            var5.func_78374_a((double)var2 + 0.75, (double)var3 + var58, (double)((float)var4 + var48), var54, var52);
            var5.func_78374_a((double)var2 + 0.5, var63, (double)((float)var4 + var48), var50, var52);
            var5.func_78374_a((double)var2 + 0.75, (double)var3 + var58, (double)((float)var4 + var49), var50, var56);
            var5.func_78374_a((double)(var2 + 1), (double)var3 + var58, (double)((float)var4 + var49), var54, var56);
            var5.func_78374_a((double)(var2 + 1), (double)var3 + var58, (double)((float)var4 + var48), var54, var52);
            var5.func_78374_a((double)var2 + 0.75, (double)var3 + var58, (double)((float)var4 + var48), var50, var52);
         }
      }

      return true;
   }

   public boolean func_147756_g(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      IIcon var6 = this.func_147777_a(var1, 0);
      int var7 = this.field_147845_a.func_72805_g(var2, var3, var4);
      boolean var8 = (var7 & 4) == 4;
      boolean var9 = (var7 & 2) == 2;
      if (this.func_147744_b()) {
         var6 = this.field_147840_d;
      }

      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      double var10 = (double)var6.func_94209_e();
      double var12 = (double)var6.func_94207_b(var8 ? 2.0 : 0.0);
      double var14 = (double)var6.func_94212_f();
      double var16 = (double)var6.func_94207_b(var8 ? 4.0 : 2.0);
      double var18 = (double)(var9 ? 3.5F : 1.5F) / 16.0;
      boolean var20 = BlockTripWire.func_150139_a(this.field_147845_a, var2, var3, var4, var7, 1);
      boolean var21 = BlockTripWire.func_150139_a(this.field_147845_a, var2, var3, var4, var7, 3);
      boolean var22 = BlockTripWire.func_150139_a(this.field_147845_a, var2, var3, var4, var7, 2);
      boolean var23 = BlockTripWire.func_150139_a(this.field_147845_a, var2, var3, var4, var7, 0);
      float var24 = 0.03125F;
      float var25 = 0.5F - var24 / 2.0F;
      float var26 = var25 + var24;
      if (!var22 && !var21 && !var23 && !var20) {
         var22 = true;
         var23 = true;
      }

      if (var22) {
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.25, var10, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.25, var10, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4, var14, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4, var14, var12);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4, var14, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4, var14, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.25, var10, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.25, var10, var12);
      }

      if (var22 || var23 && !var21 && !var20) {
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.5, var10, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.5, var10, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.25, var14, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.25, var14, var12);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.25, var14, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.25, var14, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.5, var10, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.5, var10, var12);
      }

      if (var23 || var22 && !var21 && !var20) {
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.75, var10, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.75, var10, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.5, var14, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.5, var14, var12);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.5, var14, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.5, var14, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.75, var10, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.75, var10, var12);
      }

      if (var23) {
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)(var4 + 1), var10, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)(var4 + 1), var10, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.75, var14, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.75, var14, var12);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)var4 + 0.75, var14, var12);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)var4 + 0.75, var14, var16);
         var5.func_78374_a((double)((float)var2 + var26), (double)var3 + var18, (double)(var4 + 1), var10, var16);
         var5.func_78374_a((double)((float)var2 + var25), (double)var3 + var18, (double)(var4 + 1), var10, var12);
      }

      if (var20) {
         var5.func_78374_a((double)var2, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
      }

      if (var20 || var21 && !var22 && !var23) {
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2 + 0.25, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
      }

      if (var21 || var20 && !var22 && !var23) {
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2 + 0.5, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
      }

      if (var21) {
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
         var5.func_78374_a((double)(var2 + 1), (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)(var2 + 1), (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var25), var10, var12);
         var5.func_78374_a((double)(var2 + 1), (double)var3 + var18, (double)((float)var4 + var25), var14, var12);
         var5.func_78374_a((double)(var2 + 1), (double)var3 + var18, (double)((float)var4 + var26), var14, var16);
         var5.func_78374_a((double)var2 + 0.75, (double)var3 + var18, (double)((float)var4 + var26), var10, var16);
      }

      return true;
   }

   public boolean func_147801_a(BlockFire var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      IIcon var6 = var1.func_149840_c(0);
      IIcon var7 = var1.func_149840_c(1);
      IIcon var8 = var6;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      double var9 = (double)var8.func_94209_e();
      double var11 = (double)var8.func_94206_g();
      double var13 = (double)var8.func_94212_f();
      double var15 = (double)var8.func_94210_h();
      float var17 = 1.4F;
      if (!World.func_147466_a(this.field_147845_a, var2, var3 - 1, var4) && !Blocks.field_150480_ab.func_149844_e(this.field_147845_a, var2, var3 - 1, var4)) {
         float var59 = 0.2F;
         float var19 = 0.0625F;
         if ((var2 + var3 + var4 & 1) == 1) {
            var9 = (double)var7.func_94209_e();
            var11 = (double)var7.func_94206_g();
            var13 = (double)var7.func_94212_f();
            var15 = (double)var7.func_94210_h();
         }

         if ((var2 / 2 + var3 / 2 + var4 / 2 & 1) == 1) {
            double var61 = var13;
            var13 = var9;
            var9 = var61;
         }

         if (Blocks.field_150480_ab.func_149844_e(this.field_147845_a, var2 - 1, var3, var4)) {
            var5.func_78374_a((double)((float)var2 + var59), (double)((float)var3 + var17 + var19), (double)(var4 + 1), var13, var11);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 1), var13, var15);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var9, var15);
            var5.func_78374_a((double)((float)var2 + var59), (double)((float)var3 + var17 + var19), (double)(var4 + 0), var9, var11);
            var5.func_78374_a((double)((float)var2 + var59), (double)((float)var3 + var17 + var19), (double)(var4 + 0), var9, var11);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var9, var15);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 1), var13, var15);
            var5.func_78374_a((double)((float)var2 + var59), (double)((float)var3 + var17 + var19), (double)(var4 + 1), var13, var11);
         }

         if (Blocks.field_150480_ab.func_149844_e(this.field_147845_a, var2 + 1, var3, var4)) {
            var5.func_78374_a((double)((float)(var2 + 1) - var59), (double)((float)var3 + var17 + var19), (double)(var4 + 0), var9, var11);
            var5.func_78374_a((double)(var2 + 1 - 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var9, var15);
            var5.func_78374_a((double)(var2 + 1 - 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 1), var13, var15);
            var5.func_78374_a((double)((float)(var2 + 1) - var59), (double)((float)var3 + var17 + var19), (double)(var4 + 1), var13, var11);
            var5.func_78374_a((double)((float)(var2 + 1) - var59), (double)((float)var3 + var17 + var19), (double)(var4 + 1), var13, var11);
            var5.func_78374_a((double)(var2 + 1 - 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 1), var13, var15);
            var5.func_78374_a((double)(var2 + 1 - 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var9, var15);
            var5.func_78374_a((double)((float)(var2 + 1) - var59), (double)((float)var3 + var17 + var19), (double)(var4 + 0), var9, var11);
         }

         if (Blocks.field_150480_ab.func_149844_e(this.field_147845_a, var2, var3, var4 - 1)) {
            var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17 + var19), (double)((float)var4 + var59), var13, var11);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var13, var15);
            var5.func_78374_a((double)(var2 + 1), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var9, var15);
            var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17 + var19), (double)((float)var4 + var59), var9, var11);
            var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17 + var19), (double)((float)var4 + var59), var9, var11);
            var5.func_78374_a((double)(var2 + 1), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var9, var15);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 0), var13, var15);
            var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17 + var19), (double)((float)var4 + var59), var13, var11);
         }

         if (Blocks.field_150480_ab.func_149844_e(this.field_147845_a, var2, var3, var4 + 1)) {
            var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17 + var19), (double)((float)(var4 + 1) - var59), var9, var11);
            var5.func_78374_a((double)(var2 + 1), (double)((float)(var3 + 0) + var19), (double)(var4 + 1 - 0), var9, var15);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 1 - 0), var13, var15);
            var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17 + var19), (double)((float)(var4 + 1) - var59), var13, var11);
            var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17 + var19), (double)((float)(var4 + 1) - var59), var13, var11);
            var5.func_78374_a((double)(var2 + 0), (double)((float)(var3 + 0) + var19), (double)(var4 + 1 - 0), var13, var15);
            var5.func_78374_a((double)(var2 + 1), (double)((float)(var3 + 0) + var19), (double)(var4 + 1 - 0), var9, var15);
            var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17 + var19), (double)((float)(var4 + 1) - var59), var9, var11);
         }

         if (Blocks.field_150480_ab.func_149844_e(this.field_147845_a, var2, var3 + 1, var4)) {
            double var62 = (double)var2 + 0.5 + 0.5;
            double var64 = (double)var2 + 0.5 - 0.5;
            double var66 = (double)var4 + 0.5 + 0.5;
            double var68 = (double)var4 + 0.5 - 0.5;
            double var70 = (double)var2 + 0.5 - 0.5;
            double var72 = (double)var2 + 0.5 + 0.5;
            double var74 = (double)var4 + 0.5 - 0.5;
            double var34 = (double)var4 + 0.5 + 0.5;
            var9 = (double)var6.func_94209_e();
            var11 = (double)var6.func_94206_g();
            var13 = (double)var6.func_94212_f();
            var15 = (double)var6.func_94210_h();
            ++var3;
            var17 = -0.2F;
            if ((var2 + var3 + var4 & 1) == 0) {
               var5.func_78374_a(var70, (double)((float)var3 + var17), (double)(var4 + 0), var13, var11);
               var5.func_78374_a(var62, (double)(var3 + 0), (double)(var4 + 0), var13, var15);
               var5.func_78374_a(var62, (double)(var3 + 0), (double)(var4 + 1), var9, var15);
               var5.func_78374_a(var70, (double)((float)var3 + var17), (double)(var4 + 1), var9, var11);
               var9 = (double)var7.func_94209_e();
               var11 = (double)var7.func_94206_g();
               var13 = (double)var7.func_94212_f();
               var15 = (double)var7.func_94210_h();
               var5.func_78374_a(var72, (double)((float)var3 + var17), (double)(var4 + 1), var13, var11);
               var5.func_78374_a(var64, (double)(var3 + 0), (double)(var4 + 1), var13, var15);
               var5.func_78374_a(var64, (double)(var3 + 0), (double)(var4 + 0), var9, var15);
               var5.func_78374_a(var72, (double)((float)var3 + var17), (double)(var4 + 0), var9, var11);
            } else {
               var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17), var34, var13, var11);
               var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), var68, var13, var15);
               var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), var68, var9, var15);
               var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17), var34, var9, var11);
               var9 = (double)var7.func_94209_e();
               var11 = (double)var7.func_94206_g();
               var13 = (double)var7.func_94212_f();
               var15 = (double)var7.func_94210_h();
               var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17), var74, var13, var11);
               var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), var66, var13, var15);
               var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), var66, var9, var15);
               var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17), var74, var9, var11);
            }
         }
      } else {
         double var18 = (double)var2 + 0.5 + 0.2;
         double var20 = (double)var2 + 0.5 - 0.2;
         double var22 = (double)var4 + 0.5 + 0.2;
         double var24 = (double)var4 + 0.5 - 0.2;
         double var26 = (double)var2 + 0.5 - 0.3;
         double var28 = (double)var2 + 0.5 + 0.3;
         double var30 = (double)var4 + 0.5 - 0.3;
         double var32 = (double)var4 + 0.5 + 0.3;
         var5.func_78374_a(var26, (double)((float)var3 + var17), (double)(var4 + 1), var13, var11);
         var5.func_78374_a(var18, (double)(var3 + 0), (double)(var4 + 1), var13, var15);
         var5.func_78374_a(var18, (double)(var3 + 0), (double)(var4 + 0), var9, var15);
         var5.func_78374_a(var26, (double)((float)var3 + var17), (double)(var4 + 0), var9, var11);
         var5.func_78374_a(var28, (double)((float)var3 + var17), (double)(var4 + 0), var13, var11);
         var5.func_78374_a(var20, (double)(var3 + 0), (double)(var4 + 0), var13, var15);
         var5.func_78374_a(var20, (double)(var3 + 0), (double)(var4 + 1), var9, var15);
         var5.func_78374_a(var28, (double)((float)var3 + var17), (double)(var4 + 1), var9, var11);
         var9 = (double)var7.func_94209_e();
         var11 = (double)var7.func_94206_g();
         var13 = (double)var7.func_94212_f();
         var15 = (double)var7.func_94210_h();
         var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17), var32, var13, var11);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), var24, var13, var15);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), var24, var9, var15);
         var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17), var32, var9, var11);
         var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17), var30, var13, var11);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), var22, var13, var15);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), var22, var9, var15);
         var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17), var30, var9, var11);
         var18 = (double)var2 + 0.5 - 0.5;
         var20 = (double)var2 + 0.5 + 0.5;
         var22 = (double)var4 + 0.5 - 0.5;
         var24 = (double)var4 + 0.5 + 0.5;
         var26 = (double)var2 + 0.5 - 0.4;
         var28 = (double)var2 + 0.5 + 0.4;
         var30 = (double)var4 + 0.5 - 0.4;
         var32 = (double)var4 + 0.5 + 0.4;
         var5.func_78374_a(var26, (double)((float)var3 + var17), (double)(var4 + 0), var9, var11);
         var5.func_78374_a(var18, (double)(var3 + 0), (double)(var4 + 0), var9, var15);
         var5.func_78374_a(var18, (double)(var3 + 0), (double)(var4 + 1), var13, var15);
         var5.func_78374_a(var26, (double)((float)var3 + var17), (double)(var4 + 1), var13, var11);
         var5.func_78374_a(var28, (double)((float)var3 + var17), (double)(var4 + 1), var9, var11);
         var5.func_78374_a(var20, (double)(var3 + 0), (double)(var4 + 1), var9, var15);
         var5.func_78374_a(var20, (double)(var3 + 0), (double)(var4 + 0), var13, var15);
         var5.func_78374_a(var28, (double)((float)var3 + var17), (double)(var4 + 0), var13, var11);
         var9 = (double)var6.func_94209_e();
         var11 = (double)var6.func_94206_g();
         var13 = (double)var6.func_94212_f();
         var15 = (double)var6.func_94210_h();
         var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17), var32, var9, var11);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), var24, var9, var15);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), var24, var13, var15);
         var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17), var32, var13, var11);
         var5.func_78374_a((double)(var2 + 1), (double)((float)var3 + var17), var30, var9, var11);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), var22, var9, var15);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), var22, var13, var15);
         var5.func_78374_a((double)(var2 + 0), (double)((float)var3 + var17), var30, var13, var11);
      }

      return true;
   }

   public boolean func_147788_h(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      IIcon var7 = BlockRedstoneWire.func_150173_e("cross");
      IIcon var8 = BlockRedstoneWire.func_150173_e("line");
      IIcon var9 = BlockRedstoneWire.func_150173_e("cross_overlay");
      IIcon var10 = BlockRedstoneWire.func_150173_e("line_overlay");
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      float var11 = (float)var6 / 15.0F;
      float var12 = var11 * 0.6F + 0.4F;
      if (var6 == 0) {
         var12 = 0.3F;
      }

      float var13 = var11 * var11 * 0.7F - 0.5F;
      float var14 = var11 * var11 * 0.6F - 0.7F;
      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      var5.func_78386_a(var12, var13, var14);
      double var15 = 0.015625;
      double var17 = 0.015625;
      boolean var19 = BlockRedstoneWire.func_150174_f(this.field_147845_a, var2 - 1, var3, var4, 1)
         || !this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2 - 1, var3 - 1, var4, -1);
      boolean var20 = BlockRedstoneWire.func_150174_f(this.field_147845_a, var2 + 1, var3, var4, 3)
         || !this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2 + 1, var3 - 1, var4, -1);
      boolean var21 = BlockRedstoneWire.func_150174_f(this.field_147845_a, var2, var3, var4 - 1, 2)
         || !this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2, var3 - 1, var4 - 1, -1);
      boolean var22 = BlockRedstoneWire.func_150174_f(this.field_147845_a, var2, var3, var4 + 1, 0)
         || !this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2, var3 - 1, var4 + 1, -1);
      if (!this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149637_q()) {
         if (this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2 - 1, var3 + 1, var4, -1)) {
            var19 = true;
         }

         if (this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2 + 1, var3 + 1, var4, -1)) {
            var20 = true;
         }

         if (this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2, var3 + 1, var4 - 1, -1)) {
            var21 = true;
         }

         if (this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149637_q()
            && BlockRedstoneWire.func_150174_f(this.field_147845_a, var2, var3 + 1, var4 + 1, -1)) {
            var22 = true;
         }
      }

      float var23 = (float)(var2 + 0);
      float var24 = (float)(var2 + 1);
      float var25 = (float)(var4 + 0);
      float var26 = (float)(var4 + 1);
      byte var27 = 0;
      if ((var19 || var20) && !var21 && !var22) {
         var27 = 1;
      }

      if ((var21 || var22) && !var20 && !var19) {
         var27 = 2;
      }

      if (var27 == 0) {
         int var28 = 0;
         int var29 = 0;
         int var30 = 16;
         int var31 = 16;
         boolean var32 = true;
         if (!var19) {
            var23 += 0.3125F;
         }

         if (!var19) {
            var28 += 5;
         }

         if (!var20) {
            var24 -= 0.3125F;
         }

         if (!var20) {
            var30 -= 5;
         }

         if (!var21) {
            var25 += 0.3125F;
         }

         if (!var21) {
            var29 += 5;
         }

         if (!var22) {
            var26 -= 0.3125F;
         }

         if (!var22) {
            var31 -= 5;
         }

         var5.func_78374_a(
            (double)var24, (double)var3 + 0.015625, (double)var26, (double)var7.func_94214_a((double)var30), (double)var7.func_94207_b((double)var31)
         );
         var5.func_78374_a(
            (double)var24, (double)var3 + 0.015625, (double)var25, (double)var7.func_94214_a((double)var30), (double)var7.func_94207_b((double)var29)
         );
         var5.func_78374_a(
            (double)var23, (double)var3 + 0.015625, (double)var25, (double)var7.func_94214_a((double)var28), (double)var7.func_94207_b((double)var29)
         );
         var5.func_78374_a(
            (double)var23, (double)var3 + 0.015625, (double)var26, (double)var7.func_94214_a((double)var28), (double)var7.func_94207_b((double)var31)
         );
         var5.func_78386_a(1.0F, 1.0F, 1.0F);
         var5.func_78374_a(
            (double)var24, (double)var3 + 0.015625, (double)var26, (double)var9.func_94214_a((double)var30), (double)var9.func_94207_b((double)var31)
         );
         var5.func_78374_a(
            (double)var24, (double)var3 + 0.015625, (double)var25, (double)var9.func_94214_a((double)var30), (double)var9.func_94207_b((double)var29)
         );
         var5.func_78374_a(
            (double)var23, (double)var3 + 0.015625, (double)var25, (double)var9.func_94214_a((double)var28), (double)var9.func_94207_b((double)var29)
         );
         var5.func_78374_a(
            (double)var23, (double)var3 + 0.015625, (double)var26, (double)var9.func_94214_a((double)var28), (double)var9.func_94207_b((double)var31)
         );
      } else if (var27 == 1) {
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var26, (double)var8.func_94212_f(), (double)var8.func_94210_h());
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var25, (double)var8.func_94212_f(), (double)var8.func_94206_g());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var25, (double)var8.func_94209_e(), (double)var8.func_94206_g());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var26, (double)var8.func_94209_e(), (double)var8.func_94210_h());
         var5.func_78386_a(1.0F, 1.0F, 1.0F);
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var26, (double)var10.func_94212_f(), (double)var10.func_94210_h());
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var25, (double)var10.func_94212_f(), (double)var10.func_94206_g());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var25, (double)var10.func_94209_e(), (double)var10.func_94206_g());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var26, (double)var10.func_94209_e(), (double)var10.func_94210_h());
      } else {
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var26, (double)var8.func_94212_f(), (double)var8.func_94210_h());
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var25, (double)var8.func_94209_e(), (double)var8.func_94210_h());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var25, (double)var8.func_94209_e(), (double)var8.func_94206_g());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var26, (double)var8.func_94212_f(), (double)var8.func_94206_g());
         var5.func_78386_a(1.0F, 1.0F, 1.0F);
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var26, (double)var10.func_94212_f(), (double)var10.func_94210_h());
         var5.func_78374_a((double)var24, (double)var3 + 0.015625, (double)var25, (double)var10.func_94209_e(), (double)var10.func_94210_h());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var25, (double)var10.func_94209_e(), (double)var10.func_94206_g());
         var5.func_78374_a((double)var23, (double)var3 + 0.015625, (double)var26, (double)var10.func_94212_f(), (double)var10.func_94206_g());
      }

      if (!this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149637_q()) {
         float var33 = 0.021875F;
         if (this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149637_q()
            && this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4) == Blocks.field_150488_af) {
            var5.func_78386_a(var12, var13, var14);
            var5.func_78374_a(
               (double)var2 + 0.015625, (double)((float)(var3 + 1) + 0.021875F), (double)(var4 + 1), (double)var8.func_94212_f(), (double)var8.func_94206_g()
            );
            var5.func_78374_a((double)var2 + 0.015625, (double)(var3 + 0), (double)(var4 + 1), (double)var8.func_94209_e(), (double)var8.func_94206_g());
            var5.func_78374_a((double)var2 + 0.015625, (double)(var3 + 0), (double)(var4 + 0), (double)var8.func_94209_e(), (double)var8.func_94210_h());
            var5.func_78374_a(
               (double)var2 + 0.015625, (double)((float)(var3 + 1) + 0.021875F), (double)(var4 + 0), (double)var8.func_94212_f(), (double)var8.func_94210_h()
            );
            var5.func_78386_a(1.0F, 1.0F, 1.0F);
            var5.func_78374_a(
               (double)var2 + 0.015625,
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1),
               (double)var10.func_94212_f(),
               (double)var10.func_94206_g()
            );
            var5.func_78374_a((double)var2 + 0.015625, (double)(var3 + 0), (double)(var4 + 1), (double)var10.func_94209_e(), (double)var10.func_94206_g());
            var5.func_78374_a((double)var2 + 0.015625, (double)(var3 + 0), (double)(var4 + 0), (double)var10.func_94209_e(), (double)var10.func_94210_h());
            var5.func_78374_a(
               (double)var2 + 0.015625,
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 0),
               (double)var10.func_94212_f(),
               (double)var10.func_94210_h()
            );
         }

         if (this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149637_q()
            && this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4) == Blocks.field_150488_af) {
            var5.func_78386_a(var12, var13, var14);
            var5.func_78374_a((double)(var2 + 1) - 0.015625, (double)(var3 + 0), (double)(var4 + 1), (double)var8.func_94209_e(), (double)var8.func_94210_h());
            var5.func_78374_a(
               (double)(var2 + 1) - 0.015625,
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1),
               (double)var8.func_94212_f(),
               (double)var8.func_94210_h()
            );
            var5.func_78374_a(
               (double)(var2 + 1) - 0.015625,
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 0),
               (double)var8.func_94212_f(),
               (double)var8.func_94206_g()
            );
            var5.func_78374_a((double)(var2 + 1) - 0.015625, (double)(var3 + 0), (double)(var4 + 0), (double)var8.func_94209_e(), (double)var8.func_94206_g());
            var5.func_78386_a(1.0F, 1.0F, 1.0F);
            var5.func_78374_a(
               (double)(var2 + 1) - 0.015625, (double)(var3 + 0), (double)(var4 + 1), (double)var10.func_94209_e(), (double)var10.func_94210_h()
            );
            var5.func_78374_a(
               (double)(var2 + 1) - 0.015625,
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1),
               (double)var10.func_94212_f(),
               (double)var10.func_94210_h()
            );
            var5.func_78374_a(
               (double)(var2 + 1) - 0.015625,
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 0),
               (double)var10.func_94212_f(),
               (double)var10.func_94206_g()
            );
            var5.func_78374_a(
               (double)(var2 + 1) - 0.015625, (double)(var3 + 0), (double)(var4 + 0), (double)var10.func_94209_e(), (double)var10.func_94206_g()
            );
         }

         if (this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149637_q()
            && this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1) == Blocks.field_150488_af) {
            var5.func_78386_a(var12, var13, var14);
            var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)var4 + 0.015625, (double)var8.func_94209_e(), (double)var8.func_94210_h());
            var5.func_78374_a(
               (double)(var2 + 1), (double)((float)(var3 + 1) + 0.021875F), (double)var4 + 0.015625, (double)var8.func_94212_f(), (double)var8.func_94210_h()
            );
            var5.func_78374_a(
               (double)(var2 + 0), (double)((float)(var3 + 1) + 0.021875F), (double)var4 + 0.015625, (double)var8.func_94212_f(), (double)var8.func_94206_g()
            );
            var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)var4 + 0.015625, (double)var8.func_94209_e(), (double)var8.func_94206_g());
            var5.func_78386_a(1.0F, 1.0F, 1.0F);
            var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)var4 + 0.015625, (double)var10.func_94209_e(), (double)var10.func_94210_h());
            var5.func_78374_a(
               (double)(var2 + 1),
               (double)((float)(var3 + 1) + 0.021875F),
               (double)var4 + 0.015625,
               (double)var10.func_94212_f(),
               (double)var10.func_94210_h()
            );
            var5.func_78374_a(
               (double)(var2 + 0),
               (double)((float)(var3 + 1) + 0.021875F),
               (double)var4 + 0.015625,
               (double)var10.func_94212_f(),
               (double)var10.func_94206_g()
            );
            var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)var4 + 0.015625, (double)var10.func_94209_e(), (double)var10.func_94206_g());
         }

         if (this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149637_q()
            && this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1) == Blocks.field_150488_af) {
            var5.func_78386_a(var12, var13, var14);
            var5.func_78374_a(
               (double)(var2 + 1),
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1) - 0.015625,
               (double)var8.func_94212_f(),
               (double)var8.func_94206_g()
            );
            var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)(var4 + 1) - 0.015625, (double)var8.func_94209_e(), (double)var8.func_94206_g());
            var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)(var4 + 1) - 0.015625, (double)var8.func_94209_e(), (double)var8.func_94210_h());
            var5.func_78374_a(
               (double)(var2 + 0),
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1) - 0.015625,
               (double)var8.func_94212_f(),
               (double)var8.func_94210_h()
            );
            var5.func_78386_a(1.0F, 1.0F, 1.0F);
            var5.func_78374_a(
               (double)(var2 + 1),
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1) - 0.015625,
               (double)var10.func_94212_f(),
               (double)var10.func_94206_g()
            );
            var5.func_78374_a(
               (double)(var2 + 1), (double)(var3 + 0), (double)(var4 + 1) - 0.015625, (double)var10.func_94209_e(), (double)var10.func_94206_g()
            );
            var5.func_78374_a(
               (double)(var2 + 0), (double)(var3 + 0), (double)(var4 + 1) - 0.015625, (double)var10.func_94209_e(), (double)var10.func_94210_h()
            );
            var5.func_78374_a(
               (double)(var2 + 0),
               (double)((float)(var3 + 1) + 0.021875F),
               (double)(var4 + 1) - 0.015625,
               (double)var10.func_94212_f(),
               (double)var10.func_94210_h()
            );
         }
      }

      return true;
   }

   public boolean func_147766_a(BlockRailBase var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      IIcon var7 = this.func_147787_a(var1, 0, var6);
      if (this.func_147744_b()) {
         var7 = this.field_147840_d;
      }

      if (var1.func_150050_e()) {
         var6 &= 7;
      }

      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      double var8 = (double)var7.func_94209_e();
      double var10 = (double)var7.func_94206_g();
      double var12 = (double)var7.func_94212_f();
      double var14 = (double)var7.func_94210_h();
      double var16 = 0.0625;
      double var18 = (double)(var2 + 1);
      double var20 = (double)(var2 + 1);
      double var22 = (double)(var2 + 0);
      double var24 = (double)(var2 + 0);
      double var26 = (double)(var4 + 0);
      double var28 = (double)(var4 + 1);
      double var30 = (double)(var4 + 1);
      double var32 = (double)(var4 + 0);
      double var34 = (double)var3 + var16;
      double var36 = (double)var3 + var16;
      double var38 = (double)var3 + var16;
      double var40 = (double)var3 + var16;
      if (var6 == 1 || var6 == 2 || var6 == 3 || var6 == 7) {
         var18 = var24 = (double)(var2 + 1);
         var20 = var22 = (double)(var2 + 0);
         var26 = var28 = (double)(var4 + 1);
         var30 = var32 = (double)(var4 + 0);
      } else if (var6 == 8) {
         var18 = var20 = (double)(var2 + 0);
         var22 = var24 = (double)(var2 + 1);
         var26 = var32 = (double)(var4 + 1);
         var28 = var30 = (double)(var4 + 0);
      } else if (var6 == 9) {
         var18 = var24 = (double)(var2 + 0);
         var20 = var22 = (double)(var2 + 1);
         var26 = var28 = (double)(var4 + 0);
         var30 = var32 = (double)(var4 + 1);
      }

      if (var6 == 2 || var6 == 4) {
         ++var34;
         ++var40;
      } else if (var6 == 3 || var6 == 5) {
         ++var36;
         ++var38;
      }

      var5.func_78374_a(var18, var34, var26, var12, var10);
      var5.func_78374_a(var20, var36, var28, var12, var14);
      var5.func_78374_a(var22, var38, var30, var8, var14);
      var5.func_78374_a(var24, var40, var32, var8, var10);
      var5.func_78374_a(var24, var40, var32, var8, var10);
      var5.func_78374_a(var22, var38, var30, var8, var14);
      var5.func_78374_a(var20, var36, var28, var12, var14);
      var5.func_78374_a(var18, var34, var26, var12, var10);
      return true;
   }

   public boolean func_147794_i(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      IIcon var6 = this.func_147777_a(var1, 0);
      if (this.func_147744_b()) {
         var6 = this.field_147840_d;
      }

      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      double var7 = (double)var6.func_94209_e();
      double var9 = (double)var6.func_94206_g();
      double var11 = (double)var6.func_94212_f();
      double var13 = (double)var6.func_94210_h();
      int var15 = this.field_147845_a.func_72805_g(var2, var3, var4);
      double var16 = 0.0;
      double var18 = 0.05000000074505806;
      if (var15 == 5) {
         var5.func_78374_a((double)var2 + var18, (double)(var3 + 1) + var16, (double)(var4 + 1) + var16, var7, var9);
         var5.func_78374_a((double)var2 + var18, (double)(var3 + 0) - var16, (double)(var4 + 1) + var16, var7, var13);
         var5.func_78374_a((double)var2 + var18, (double)(var3 + 0) - var16, (double)(var4 + 0) - var16, var11, var13);
         var5.func_78374_a((double)var2 + var18, (double)(var3 + 1) + var16, (double)(var4 + 0) - var16, var11, var9);
      }

      if (var15 == 4) {
         var5.func_78374_a((double)(var2 + 1) - var18, (double)(var3 + 0) - var16, (double)(var4 + 1) + var16, var11, var13);
         var5.func_78374_a((double)(var2 + 1) - var18, (double)(var3 + 1) + var16, (double)(var4 + 1) + var16, var11, var9);
         var5.func_78374_a((double)(var2 + 1) - var18, (double)(var3 + 1) + var16, (double)(var4 + 0) - var16, var7, var9);
         var5.func_78374_a((double)(var2 + 1) - var18, (double)(var3 + 0) - var16, (double)(var4 + 0) - var16, var7, var13);
      }

      if (var15 == 3) {
         var5.func_78374_a((double)(var2 + 1) + var16, (double)(var3 + 0) - var16, (double)var4 + var18, var11, var13);
         var5.func_78374_a((double)(var2 + 1) + var16, (double)(var3 + 1) + var16, (double)var4 + var18, var11, var9);
         var5.func_78374_a((double)(var2 + 0) - var16, (double)(var3 + 1) + var16, (double)var4 + var18, var7, var9);
         var5.func_78374_a((double)(var2 + 0) - var16, (double)(var3 + 0) - var16, (double)var4 + var18, var7, var13);
      }

      if (var15 == 2) {
         var5.func_78374_a((double)(var2 + 1) + var16, (double)(var3 + 1) + var16, (double)(var4 + 1) - var18, var7, var9);
         var5.func_78374_a((double)(var2 + 1) + var16, (double)(var3 + 0) - var16, (double)(var4 + 1) - var18, var7, var13);
         var5.func_78374_a((double)(var2 + 0) - var16, (double)(var3 + 0) - var16, (double)(var4 + 1) - var18, var11, var13);
         var5.func_78374_a((double)(var2 + 0) - var16, (double)(var3 + 1) + var16, (double)(var4 + 1) - var18, var11, var9);
      }

      return true;
   }

   public boolean func_147726_j(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      IIcon var6 = this.func_147777_a(var1, 0);
      if (this.func_147744_b()) {
         var6 = this.field_147840_d;
      }

      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var7 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      var5.func_78386_a(var8, var9, var10);
      double var18 = (double)var6.func_94209_e();
      double var19 = (double)var6.func_94206_g();
      double var11 = (double)var6.func_94212_f();
      double var13 = (double)var6.func_94210_h();
      double var15 = 0.05000000074505806;
      int var17 = this.field_147845_a.func_72805_g(var2, var3, var4);
      if ((var17 & 2) != 0) {
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 1), (double)(var4 + 1), var18, var19);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 0), (double)(var4 + 1), var18, var13);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 0), (double)(var4 + 0), var11, var13);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 1), (double)(var4 + 0), var11, var19);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 1), (double)(var4 + 0), var11, var19);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 0), (double)(var4 + 0), var11, var13);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 0), (double)(var4 + 1), var18, var13);
         var5.func_78374_a((double)var2 + var15, (double)(var3 + 1), (double)(var4 + 1), var18, var19);
      }

      if ((var17 & 8) != 0) {
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 0), (double)(var4 + 1), var11, var13);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 1), (double)(var4 + 1), var11, var19);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 1), (double)(var4 + 0), var18, var19);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 0), (double)(var4 + 0), var18, var13);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 0), (double)(var4 + 0), var18, var13);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 1), (double)(var4 + 0), var18, var19);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 1), (double)(var4 + 1), var11, var19);
         var5.func_78374_a((double)(var2 + 1) - var15, (double)(var3 + 0), (double)(var4 + 1), var11, var13);
      }

      if ((var17 & 4) != 0) {
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)var4 + var15, var11, var13);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 1), (double)var4 + var15, var11, var19);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 1), (double)var4 + var15, var18, var19);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)var4 + var15, var18, var13);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)var4 + var15, var18, var13);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 1), (double)var4 + var15, var18, var19);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 1), (double)var4 + var15, var11, var19);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)var4 + var15, var11, var13);
      }

      if ((var17 & 1) != 0) {
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 1), (double)(var4 + 1) - var15, var18, var19);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)(var4 + 1) - var15, var18, var13);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)(var4 + 1) - var15, var11, var13);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 1), (double)(var4 + 1) - var15, var11, var19);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 1), (double)(var4 + 1) - var15, var11, var19);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 0), (double)(var4 + 1) - var15, var11, var13);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 0), (double)(var4 + 1) - var15, var18, var13);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 1), (double)(var4 + 1) - var15, var18, var19);
      }

      if (this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149637_q()) {
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 1) - var15, (double)(var4 + 0), var18, var19);
         var5.func_78374_a((double)(var2 + 1), (double)(var3 + 1) - var15, (double)(var4 + 1), var18, var13);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 1) - var15, (double)(var4 + 1), var11, var13);
         var5.func_78374_a((double)(var2 + 0), (double)(var3 + 1) - var15, (double)(var4 + 0), var11, var19);
      }

      return true;
   }

   public boolean func_147733_k(Block var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72800_K();
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var7 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var6.func_78386_a(var8, var9, var10);
      boolean var67 = var1 instanceof BlockStainedGlassPane;
      IIcon var65;
      IIcon var66;
      if (this.func_147744_b()) {
         var65 = this.field_147840_d;
         var66 = this.field_147840_d;
      } else {
         int var14 = this.field_147845_a.func_72805_g(var2, var3, var4);
         var65 = this.func_147787_a(var1, 0, var14);
         var66 = var67 ? ((BlockStainedGlassPane)var1).func_150104_b(var14) : ((BlockPane)var1).func_150097_e();
      }

      double var68 = (double)var65.func_94209_e();
      double var16 = (double)var65.func_94214_a(7.0);
      double var18 = (double)var65.func_94214_a(9.0);
      double var20 = (double)var65.func_94212_f();
      double var22 = (double)var65.func_94206_g();
      double var24 = (double)var65.func_94210_h();
      double var26 = (double)var66.func_94214_a(7.0);
      double var28 = (double)var66.func_94214_a(9.0);
      double var30 = (double)var66.func_94206_g();
      double var32 = (double)var66.func_94210_h();
      double var34 = (double)var66.func_94207_b(7.0);
      double var36 = (double)var66.func_94207_b(9.0);
      double var38 = (double)var2;
      double var40 = (double)(var2 + 1);
      double var42 = (double)var4;
      double var44 = (double)(var4 + 1);
      double var46 = (double)var2 + 0.5 - 0.0625;
      double var48 = (double)var2 + 0.5 + 0.0625;
      double var50 = (double)var4 + 0.5 - 0.0625;
      double var52 = (double)var4 + 0.5 + 0.0625;
      boolean var54 = var67
         ? ((BlockStainedGlassPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2, var3, var4 - 1))
         : ((BlockPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2, var3, var4 - 1));
      boolean var55 = var67
         ? ((BlockStainedGlassPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2, var3, var4 + 1))
         : ((BlockPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2, var3, var4 + 1));
      boolean var56 = var67
         ? ((BlockStainedGlassPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2 - 1, var3, var4))
         : ((BlockPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2 - 1, var3, var4));
      boolean var57 = var67
         ? ((BlockStainedGlassPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2 + 1, var3, var4))
         : ((BlockPane)var1).func_150098_a(this.field_147845_a.func_147439_a(var2 + 1, var3, var4));
      double var58 = 0.001;
      double var60 = 0.999;
      double var62 = 0.001;
      boolean var64 = !var54 && !var55 && !var56 && !var57;
      if (!var56 && !var64) {
         if (!var54 && !var55) {
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var18, var22);
         }
      } else if (var56 && var57) {
         if (!var54) {
            var6.func_78374_a(var40, (double)var3 + 0.999, var50, var20, var22);
            var6.func_78374_a(var40, (double)var3 + 0.001, var50, var20, var24);
            var6.func_78374_a(var38, (double)var3 + 0.001, var50, var68, var24);
            var6.func_78374_a(var38, (double)var3 + 0.999, var50, var68, var22);
         } else {
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var38, (double)var3 + 0.001, var50, var68, var24);
            var6.func_78374_a(var38, (double)var3 + 0.999, var50, var68, var22);
            var6.func_78374_a(var40, (double)var3 + 0.999, var50, var20, var22);
            var6.func_78374_a(var40, (double)var3 + 0.001, var50, var20, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var18, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var18, var22);
         }

         if (!var55) {
            var6.func_78374_a(var38, (double)var3 + 0.999, var52, var68, var22);
            var6.func_78374_a(var38, (double)var3 + 0.001, var52, var68, var24);
            var6.func_78374_a(var40, (double)var3 + 0.001, var52, var20, var24);
            var6.func_78374_a(var40, (double)var3 + 0.999, var52, var20, var22);
         } else {
            var6.func_78374_a(var38, (double)var3 + 0.999, var52, var68, var22);
            var6.func_78374_a(var38, (double)var3 + 0.001, var52, var68, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var16, var22);
            var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var40, (double)var3 + 0.001, var52, var20, var24);
            var6.func_78374_a(var40, (double)var3 + 0.999, var52, var20, var22);
         }

         var6.func_78374_a(var38, (double)var3 + 0.999, var52, var28, var30);
         var6.func_78374_a(var40, (double)var3 + 0.999, var52, var28, var32);
         var6.func_78374_a(var40, (double)var3 + 0.999, var50, var26, var32);
         var6.func_78374_a(var38, (double)var3 + 0.999, var50, var26, var30);
         var6.func_78374_a(var40, (double)var3 + 0.001, var52, var26, var32);
         var6.func_78374_a(var38, (double)var3 + 0.001, var52, var26, var30);
         var6.func_78374_a(var38, (double)var3 + 0.001, var50, var28, var30);
         var6.func_78374_a(var40, (double)var3 + 0.001, var50, var28, var32);
      } else {
         if (!var54 && !var64) {
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var18, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var18, var24);
            var6.func_78374_a(var38, (double)var3 + 0.001, var50, var68, var24);
            var6.func_78374_a(var38, (double)var3 + 0.999, var50, var68, var22);
         } else {
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var38, (double)var3 + 0.001, var50, var68, var24);
            var6.func_78374_a(var38, (double)var3 + 0.999, var50, var68, var22);
         }

         if (!var55 && !var64) {
            var6.func_78374_a(var38, (double)var3 + 0.999, var52, var68, var22);
            var6.func_78374_a(var38, (double)var3 + 0.001, var52, var68, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
         } else {
            var6.func_78374_a(var38, (double)var3 + 0.999, var52, var68, var22);
            var6.func_78374_a(var38, (double)var3 + 0.001, var52, var68, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var16, var22);
         }

         var6.func_78374_a(var38, (double)var3 + 0.999, var52, var28, var30);
         var6.func_78374_a(var46, (double)var3 + 0.999, var52, var28, var34);
         var6.func_78374_a(var46, (double)var3 + 0.999, var50, var26, var34);
         var6.func_78374_a(var38, (double)var3 + 0.999, var50, var26, var30);
         var6.func_78374_a(var46, (double)var3 + 0.001, var52, var26, var34);
         var6.func_78374_a(var38, (double)var3 + 0.001, var52, var26, var30);
         var6.func_78374_a(var38, (double)var3 + 0.001, var50, var28, var30);
         var6.func_78374_a(var46, (double)var3 + 0.001, var50, var28, var34);
      }

      if ((var57 || var64) && !var56) {
         if (!var55 && !var64) {
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var16, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var16, var24);
            var6.func_78374_a(var40, (double)var3 + 0.001, var52, var20, var24);
            var6.func_78374_a(var40, (double)var3 + 0.999, var52, var20, var22);
         } else {
            var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var40, (double)var3 + 0.001, var52, var20, var24);
            var6.func_78374_a(var40, (double)var3 + 0.999, var52, var20, var22);
         }

         if (!var54 && !var64) {
            var6.func_78374_a(var40, (double)var3 + 0.999, var50, var20, var22);
            var6.func_78374_a(var40, (double)var3 + 0.001, var50, var20, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
         } else {
            var6.func_78374_a(var40, (double)var3 + 0.999, var50, var20, var22);
            var6.func_78374_a(var40, (double)var3 + 0.001, var50, var20, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var18, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var18, var22);
         }

         var6.func_78374_a(var48, (double)var3 + 0.999, var52, var28, var36);
         var6.func_78374_a(var40, (double)var3 + 0.999, var52, var28, var30);
         var6.func_78374_a(var40, (double)var3 + 0.999, var50, var26, var30);
         var6.func_78374_a(var48, (double)var3 + 0.999, var50, var26, var36);
         var6.func_78374_a(var40, (double)var3 + 0.001, var52, var26, var32);
         var6.func_78374_a(var48, (double)var3 + 0.001, var52, var26, var36);
         var6.func_78374_a(var48, (double)var3 + 0.001, var50, var28, var36);
         var6.func_78374_a(var40, (double)var3 + 0.001, var50, var28, var32);
      } else if (!var57 && !var54 && !var55) {
         var6.func_78374_a(var48, (double)var3 + 0.999, var52, var16, var22);
         var6.func_78374_a(var48, (double)var3 + 0.001, var52, var16, var24);
         var6.func_78374_a(var48, (double)var3 + 0.001, var50, var18, var24);
         var6.func_78374_a(var48, (double)var3 + 0.999, var50, var18, var22);
      }

      if (!var54 && !var64) {
         if (!var57 && !var56) {
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var18, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var18, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
         }
      } else if (var54 && var55) {
         if (!var56) {
            var6.func_78374_a(var46, (double)var3 + 0.999, var42, var68, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var44, var20, var22);
         } else {
            var6.func_78374_a(var46, (double)var3 + 0.999, var42, var68, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var18, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var44, var20, var22);
         }

         if (!var57) {
            var6.func_78374_a(var48, (double)var3 + 0.999, var44, var20, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var42, var68, var22);
         } else {
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var42, var68, var22);
            var6.func_78374_a(var48, (double)var3 + 0.999, var44, var20, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
         }

         var6.func_78374_a(var48, (double)var3 + 0.999, var42, var28, var30);
         var6.func_78374_a(var46, (double)var3 + 0.999, var42, var26, var30);
         var6.func_78374_a(var46, (double)var3 + 0.999, var44, var26, var32);
         var6.func_78374_a(var48, (double)var3 + 0.999, var44, var28, var32);
         var6.func_78374_a(var46, (double)var3 + 0.001, var42, var26, var30);
         var6.func_78374_a(var48, (double)var3 + 0.001, var42, var28, var30);
         var6.func_78374_a(var48, (double)var3 + 0.001, var44, var28, var32);
         var6.func_78374_a(var46, (double)var3 + 0.001, var44, var26, var32);
      } else {
         if (!var56 && !var64) {
            var6.func_78374_a(var46, (double)var3 + 0.999, var42, var68, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var18, var22);
         } else {
            var6.func_78374_a(var46, (double)var3 + 0.999, var42, var68, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
         }

         if (!var57 && !var64) {
            var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var42, var68, var22);
         } else {
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var42, var68, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var42, var68, var22);
         }

         var6.func_78374_a(var48, (double)var3 + 0.999, var42, var28, var30);
         var6.func_78374_a(var46, (double)var3 + 0.999, var42, var26, var30);
         var6.func_78374_a(var46, (double)var3 + 0.999, var50, var26, var34);
         var6.func_78374_a(var48, (double)var3 + 0.999, var50, var28, var34);
         var6.func_78374_a(var46, (double)var3 + 0.001, var42, var26, var30);
         var6.func_78374_a(var48, (double)var3 + 0.001, var42, var28, var30);
         var6.func_78374_a(var48, (double)var3 + 0.001, var50, var28, var34);
         var6.func_78374_a(var46, (double)var3 + 0.001, var50, var26, var34);
      }

      if ((var55 || var64) && !var54) {
         if (!var56 && !var64) {
            var6.func_78374_a(var46, (double)var3 + 0.999, var50, var16, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var44, var20, var22);
         } else {
            var6.func_78374_a(var46, (double)var3 + 0.999, var52, var18, var22);
            var6.func_78374_a(var46, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var46, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var46, (double)var3 + 0.999, var44, var20, var22);
         }

         if (!var57 && !var64) {
            var6.func_78374_a(var48, (double)var3 + 0.999, var44, var20, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var50, var16, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var50, var16, var22);
         } else {
            var6.func_78374_a(var48, (double)var3 + 0.999, var44, var20, var22);
            var6.func_78374_a(var48, (double)var3 + 0.001, var44, var20, var24);
            var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
            var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
         }

         var6.func_78374_a(var48, (double)var3 + 0.999, var52, var28, var36);
         var6.func_78374_a(var46, (double)var3 + 0.999, var52, var26, var36);
         var6.func_78374_a(var46, (double)var3 + 0.999, var44, var26, var32);
         var6.func_78374_a(var48, (double)var3 + 0.999, var44, var28, var32);
         var6.func_78374_a(var46, (double)var3 + 0.001, var52, var26, var36);
         var6.func_78374_a(var48, (double)var3 + 0.001, var52, var28, var36);
         var6.func_78374_a(var48, (double)var3 + 0.001, var44, var28, var32);
         var6.func_78374_a(var46, (double)var3 + 0.001, var44, var26, var32);
      } else if (!var55 && !var57 && !var56) {
         var6.func_78374_a(var46, (double)var3 + 0.999, var52, var16, var22);
         var6.func_78374_a(var46, (double)var3 + 0.001, var52, var16, var24);
         var6.func_78374_a(var48, (double)var3 + 0.001, var52, var18, var24);
         var6.func_78374_a(var48, (double)var3 + 0.999, var52, var18, var22);
      }

      var6.func_78374_a(var48, (double)var3 + 0.999, var50, var28, var34);
      var6.func_78374_a(var46, (double)var3 + 0.999, var50, var26, var34);
      var6.func_78374_a(var46, (double)var3 + 0.999, var52, var26, var36);
      var6.func_78374_a(var48, (double)var3 + 0.999, var52, var28, var36);
      var6.func_78374_a(var46, (double)var3 + 0.001, var50, var26, var34);
      var6.func_78374_a(var48, (double)var3 + 0.001, var50, var28, var34);
      var6.func_78374_a(var48, (double)var3 + 0.001, var52, var28, var36);
      var6.func_78374_a(var46, (double)var3 + 0.001, var52, var26, var36);
      if (var64) {
         var6.func_78374_a(var38, (double)var3 + 0.999, var50, var16, var22);
         var6.func_78374_a(var38, (double)var3 + 0.001, var50, var16, var24);
         var6.func_78374_a(var38, (double)var3 + 0.001, var52, var18, var24);
         var6.func_78374_a(var38, (double)var3 + 0.999, var52, var18, var22);
         var6.func_78374_a(var40, (double)var3 + 0.999, var52, var16, var22);
         var6.func_78374_a(var40, (double)var3 + 0.001, var52, var16, var24);
         var6.func_78374_a(var40, (double)var3 + 0.001, var50, var18, var24);
         var6.func_78374_a(var40, (double)var3 + 0.999, var50, var18, var22);
         var6.func_78374_a(var48, (double)var3 + 0.999, var42, var18, var22);
         var6.func_78374_a(var48, (double)var3 + 0.001, var42, var18, var24);
         var6.func_78374_a(var46, (double)var3 + 0.001, var42, var16, var24);
         var6.func_78374_a(var46, (double)var3 + 0.999, var42, var16, var22);
         var6.func_78374_a(var46, (double)var3 + 0.999, var44, var16, var22);
         var6.func_78374_a(var46, (double)var3 + 0.001, var44, var16, var24);
         var6.func_78374_a(var48, (double)var3 + 0.001, var44, var18, var24);
         var6.func_78374_a(var48, (double)var3 + 0.999, var44, var18, var22);
      }

      return true;
   }

   public boolean func_147767_a(BlockPane var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72800_K();
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var7 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var6.func_78386_a(var8, var9, var10);
      IIcon var63;
      IIcon var64;
      if (this.func_147744_b()) {
         var63 = this.field_147840_d;
         var64 = this.field_147840_d;
      } else {
         int var65 = this.field_147845_a.func_72805_g(var2, var3, var4);
         var63 = this.func_147787_a(var1, 0, var65);
         var64 = var1.func_150097_e();
      }

      double var66 = (double)var63.func_94209_e();
      double var15 = (double)var63.func_94214_a(8.0);
      double var17 = (double)var63.func_94212_f();
      double var19 = (double)var63.func_94206_g();
      double var21 = (double)var63.func_94210_h();
      double var23 = (double)var64.func_94214_a(7.0);
      double var25 = (double)var64.func_94214_a(9.0);
      double var27 = (double)var64.func_94206_g();
      double var29 = (double)var64.func_94207_b(8.0);
      double var31 = (double)var64.func_94210_h();
      double var33 = (double)var2;
      double var35 = (double)var2 + 0.5;
      double var37 = (double)(var2 + 1);
      double var39 = (double)var4;
      double var41 = (double)var4 + 0.5;
      double var43 = (double)(var4 + 1);
      double var45 = (double)var2 + 0.5 - 0.0625;
      double var47 = (double)var2 + 0.5 + 0.0625;
      double var49 = (double)var4 + 0.5 - 0.0625;
      double var51 = (double)var4 + 0.5 + 0.0625;
      boolean var53 = var1.func_150098_a(this.field_147845_a.func_147439_a(var2, var3, var4 - 1));
      boolean var54 = var1.func_150098_a(this.field_147845_a.func_147439_a(var2, var3, var4 + 1));
      boolean var55 = var1.func_150098_a(this.field_147845_a.func_147439_a(var2 - 1, var3, var4));
      boolean var56 = var1.func_150098_a(this.field_147845_a.func_147439_a(var2 + 1, var3, var4));
      boolean var57 = var1.func_149646_a(this.field_147845_a, var2, var3 + 1, var4, 1);
      boolean var58 = var1.func_149646_a(this.field_147845_a, var2, var3 - 1, var4, 0);
      double var59 = 0.01;
      double var61 = 0.005;
      if ((!var55 || !var56) && (var55 || var56 || var53 || var54)) {
         if (var55 && !var56) {
            var6.func_78374_a(var33, (double)(var3 + 1), var41, var66, var19);
            var6.func_78374_a(var33, (double)(var3 + 0), var41, var66, var21);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var15, var21);
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var15, var19);
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var66, var19);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var66, var21);
            var6.func_78374_a(var33, (double)(var3 + 0), var41, var15, var21);
            var6.func_78374_a(var33, (double)(var3 + 1), var41, var15, var19);
            if (!var54 && !var53) {
               var6.func_78374_a(var35, (double)(var3 + 1), var51, var23, var27);
               var6.func_78374_a(var35, (double)(var3 + 0), var51, var23, var31);
               var6.func_78374_a(var35, (double)(var3 + 0), var49, var25, var31);
               var6.func_78374_a(var35, (double)(var3 + 1), var49, var25, var27);
               var6.func_78374_a(var35, (double)(var3 + 1), var49, var23, var27);
               var6.func_78374_a(var35, (double)(var3 + 0), var49, var23, var31);
               var6.func_78374_a(var35, (double)(var3 + 0), var51, var25, var31);
               var6.func_78374_a(var35, (double)(var3 + 1), var51, var25, var27);
            }

            if (var57 || var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2 - 1, var3 + 1, var4)) {
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var31);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var31);
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var51, var25, var31);
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var49, var23, var31);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var29);
            }

            if (var58 || var3 > 1 && this.field_147845_a.func_147437_c(var2 - 1, var3 - 1, var4)) {
               var6.func_78374_a(var33, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var31);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var31);
               var6.func_78374_a(var33, (double)var3 - 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var33, (double)var3 - 0.01, var51, var25, var31);
               var6.func_78374_a(var33, (double)var3 - 0.01, var49, var23, var31);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var29);
            }
         } else if (!var55 && var56) {
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var15, var19);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var15, var21);
            var6.func_78374_a(var37, (double)(var3 + 0), var41, var17, var21);
            var6.func_78374_a(var37, (double)(var3 + 1), var41, var17, var19);
            var6.func_78374_a(var37, (double)(var3 + 1), var41, var15, var19);
            var6.func_78374_a(var37, (double)(var3 + 0), var41, var15, var21);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var17, var21);
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var17, var19);
            if (!var54 && !var53) {
               var6.func_78374_a(var35, (double)(var3 + 1), var49, var23, var27);
               var6.func_78374_a(var35, (double)(var3 + 0), var49, var23, var31);
               var6.func_78374_a(var35, (double)(var3 + 0), var51, var25, var31);
               var6.func_78374_a(var35, (double)(var3 + 1), var51, var25, var27);
               var6.func_78374_a(var35, (double)(var3 + 1), var51, var23, var27);
               var6.func_78374_a(var35, (double)(var3 + 0), var51, var23, var31);
               var6.func_78374_a(var35, (double)(var3 + 0), var49, var25, var31);
               var6.func_78374_a(var35, (double)(var3 + 1), var49, var25, var27);
            }

            if (var57 || var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2 + 1, var3 + 1, var4)) {
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var27);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var27);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var51, var25, var27);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var29);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var49, var23, var27);
            }

            if (var58 || var3 > 1 && this.field_147845_a.func_147437_c(var2 + 1, var3 - 1, var4)) {
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var27);
               var6.func_78374_a(var37, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var37, (double)var3 - 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var27);
               var6.func_78374_a(var37, (double)var3 - 0.01, var51, var25, var27);
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var29);
               var6.func_78374_a(var37, (double)var3 - 0.01, var49, var23, var27);
            }
         }
      } else {
         var6.func_78374_a(var33, (double)(var3 + 1), var41, var66, var19);
         var6.func_78374_a(var33, (double)(var3 + 0), var41, var66, var21);
         var6.func_78374_a(var37, (double)(var3 + 0), var41, var17, var21);
         var6.func_78374_a(var37, (double)(var3 + 1), var41, var17, var19);
         var6.func_78374_a(var37, (double)(var3 + 1), var41, var66, var19);
         var6.func_78374_a(var37, (double)(var3 + 0), var41, var66, var21);
         var6.func_78374_a(var33, (double)(var3 + 0), var41, var17, var21);
         var6.func_78374_a(var33, (double)(var3 + 1), var41, var17, var19);
         if (var57) {
            var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var51, var25, var31);
            var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var51, var25, var27);
            var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var49, var23, var27);
            var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var49, var23, var31);
            var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var51, var25, var31);
            var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var51, var25, var27);
            var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var49, var23, var27);
            var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var49, var23, var31);
         } else {
            if (var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2 - 1, var3 + 1, var4)) {
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var31);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var31);
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var51, var25, var31);
               var6.func_78374_a(var33, (double)(var3 + 1) + 0.01, var49, var23, var31);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var29);
            }

            if (var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2 + 1, var3 + 1, var4)) {
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var27);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var27);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var51, var25, var27);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)(var3 + 1) + 0.01, var49, var23, var29);
               var6.func_78374_a(var37, (double)(var3 + 1) + 0.01, var49, var23, var27);
            }
         }

         if (var58) {
            var6.func_78374_a(var33, (double)var3 - 0.01, var51, var25, var31);
            var6.func_78374_a(var37, (double)var3 - 0.01, var51, var25, var27);
            var6.func_78374_a(var37, (double)var3 - 0.01, var49, var23, var27);
            var6.func_78374_a(var33, (double)var3 - 0.01, var49, var23, var31);
            var6.func_78374_a(var37, (double)var3 - 0.01, var51, var25, var31);
            var6.func_78374_a(var33, (double)var3 - 0.01, var51, var25, var27);
            var6.func_78374_a(var33, (double)var3 - 0.01, var49, var23, var27);
            var6.func_78374_a(var37, (double)var3 - 0.01, var49, var23, var31);
         } else {
            if (var3 > 1 && this.field_147845_a.func_147437_c(var2 - 1, var3 - 1, var4)) {
               var6.func_78374_a(var33, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var31);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var31);
               var6.func_78374_a(var33, (double)var3 - 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var33, (double)var3 - 0.01, var51, var25, var31);
               var6.func_78374_a(var33, (double)var3 - 0.01, var49, var23, var31);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var29);
            }

            if (var3 > 1 && this.field_147845_a.func_147437_c(var2 + 1, var3 - 1, var4)) {
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var27);
               var6.func_78374_a(var37, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var37, (double)var3 - 0.01, var49, var23, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var27);
               var6.func_78374_a(var37, (double)var3 - 0.01, var51, var25, var27);
               var6.func_78374_a(var35, (double)var3 - 0.01, var51, var25, var29);
               var6.func_78374_a(var35, (double)var3 - 0.01, var49, var23, var29);
               var6.func_78374_a(var37, (double)var3 - 0.01, var49, var23, var27);
            }
         }
      }

      if ((!var53 || !var54) && (var55 || var56 || var53 || var54)) {
         if (var53 && !var54) {
            var6.func_78374_a(var35, (double)(var3 + 1), var39, var66, var19);
            var6.func_78374_a(var35, (double)(var3 + 0), var39, var66, var21);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var15, var21);
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var15, var19);
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var66, var19);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var66, var21);
            var6.func_78374_a(var35, (double)(var3 + 0), var39, var15, var21);
            var6.func_78374_a(var35, (double)(var3 + 1), var39, var15, var19);
            if (!var56 && !var55) {
               var6.func_78374_a(var45, (double)(var3 + 1), var41, var23, var27);
               var6.func_78374_a(var45, (double)(var3 + 0), var41, var23, var31);
               var6.func_78374_a(var47, (double)(var3 + 0), var41, var25, var31);
               var6.func_78374_a(var47, (double)(var3 + 1), var41, var25, var27);
               var6.func_78374_a(var47, (double)(var3 + 1), var41, var23, var27);
               var6.func_78374_a(var47, (double)(var3 + 0), var41, var23, var31);
               var6.func_78374_a(var45, (double)(var3 + 0), var41, var25, var31);
               var6.func_78374_a(var45, (double)(var3 + 1), var41, var25, var27);
            }

            if (var57 || var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2, var3 + 1, var4 - 1)) {
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var39, var25, var27);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var25, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var23, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var39, var23, var27);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var25, var27);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var39, var25, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var39, var23, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var23, var27);
            }

            if (var58 || var3 > 1 && this.field_147845_a.func_147437_c(var2, var3 - 1, var4 - 1)) {
               var6.func_78374_a(var45, (double)var3 - 0.005, var39, var25, var27);
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var25, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var23, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var39, var23, var27);
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var25, var27);
               var6.func_78374_a(var45, (double)var3 - 0.005, var39, var25, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var39, var23, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var23, var27);
            }
         } else if (!var53 && var54) {
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var15, var19);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var15, var21);
            var6.func_78374_a(var35, (double)(var3 + 0), var43, var17, var21);
            var6.func_78374_a(var35, (double)(var3 + 1), var43, var17, var19);
            var6.func_78374_a(var35, (double)(var3 + 1), var43, var15, var19);
            var6.func_78374_a(var35, (double)(var3 + 0), var43, var15, var21);
            var6.func_78374_a(var35, (double)(var3 + 0), var41, var17, var21);
            var6.func_78374_a(var35, (double)(var3 + 1), var41, var17, var19);
            if (!var56 && !var55) {
               var6.func_78374_a(var47, (double)(var3 + 1), var41, var23, var27);
               var6.func_78374_a(var47, (double)(var3 + 0), var41, var23, var31);
               var6.func_78374_a(var45, (double)(var3 + 0), var41, var25, var31);
               var6.func_78374_a(var45, (double)(var3 + 1), var41, var25, var27);
               var6.func_78374_a(var45, (double)(var3 + 1), var41, var23, var27);
               var6.func_78374_a(var45, (double)(var3 + 0), var41, var23, var31);
               var6.func_78374_a(var47, (double)(var3 + 0), var41, var25, var31);
               var6.func_78374_a(var47, (double)(var3 + 1), var41, var25, var27);
            }

            if (var57 || var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2, var3 + 1, var4 + 1)) {
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var23, var29);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var43, var23, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var43, var25, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var25, var29);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var43, var23, var29);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var23, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var25, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var43, var25, var29);
            }

            if (var58 || var3 > 1 && this.field_147845_a.func_147437_c(var2, var3 - 1, var4 + 1)) {
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var23, var29);
               var6.func_78374_a(var45, (double)var3 - 0.005, var43, var23, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var43, var25, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var25, var29);
               var6.func_78374_a(var45, (double)var3 - 0.005, var43, var23, var29);
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var23, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var25, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var43, var25, var29);
            }
         }
      } else {
         var6.func_78374_a(var35, (double)(var3 + 1), var43, var66, var19);
         var6.func_78374_a(var35, (double)(var3 + 0), var43, var66, var21);
         var6.func_78374_a(var35, (double)(var3 + 0), var39, var17, var21);
         var6.func_78374_a(var35, (double)(var3 + 1), var39, var17, var19);
         var6.func_78374_a(var35, (double)(var3 + 1), var39, var66, var19);
         var6.func_78374_a(var35, (double)(var3 + 0), var39, var66, var21);
         var6.func_78374_a(var35, (double)(var3 + 0), var43, var17, var21);
         var6.func_78374_a(var35, (double)(var3 + 1), var43, var17, var19);
         if (var57) {
            var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var43, var25, var31);
            var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var39, var25, var27);
            var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var39, var23, var27);
            var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var43, var23, var31);
            var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var39, var25, var31);
            var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var43, var25, var27);
            var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var43, var23, var27);
            var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var39, var23, var31);
         } else {
            if (var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2, var3 + 1, var4 - 1)) {
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var39, var25, var27);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var25, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var23, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var39, var23, var27);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var25, var27);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var39, var25, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var39, var23, var29);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var23, var27);
            }

            if (var3 < var5 - 1 && this.field_147845_a.func_147437_c(var2, var3 + 1, var4 + 1)) {
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var23, var29);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var43, var23, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var43, var25, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var25, var29);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var43, var23, var29);
               var6.func_78374_a(var45, (double)(var3 + 1) + 0.005, var41, var23, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var41, var25, var31);
               var6.func_78374_a(var47, (double)(var3 + 1) + 0.005, var43, var25, var29);
            }
         }

         if (var58) {
            var6.func_78374_a(var47, (double)var3 - 0.005, var43, var25, var31);
            var6.func_78374_a(var47, (double)var3 - 0.005, var39, var25, var27);
            var6.func_78374_a(var45, (double)var3 - 0.005, var39, var23, var27);
            var6.func_78374_a(var45, (double)var3 - 0.005, var43, var23, var31);
            var6.func_78374_a(var47, (double)var3 - 0.005, var39, var25, var31);
            var6.func_78374_a(var47, (double)var3 - 0.005, var43, var25, var27);
            var6.func_78374_a(var45, (double)var3 - 0.005, var43, var23, var27);
            var6.func_78374_a(var45, (double)var3 - 0.005, var39, var23, var31);
         } else {
            if (var3 > 1 && this.field_147845_a.func_147437_c(var2, var3 - 1, var4 - 1)) {
               var6.func_78374_a(var45, (double)var3 - 0.005, var39, var25, var27);
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var25, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var23, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var39, var23, var27);
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var25, var27);
               var6.func_78374_a(var45, (double)var3 - 0.005, var39, var25, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var39, var23, var29);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var23, var27);
            }

            if (var3 > 1 && this.field_147845_a.func_147437_c(var2, var3 - 1, var4 + 1)) {
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var23, var29);
               var6.func_78374_a(var45, (double)var3 - 0.005, var43, var23, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var43, var25, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var25, var29);
               var6.func_78374_a(var45, (double)var3 - 0.005, var43, var23, var29);
               var6.func_78374_a(var45, (double)var3 - 0.005, var41, var23, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var41, var25, var31);
               var6.func_78374_a(var47, (double)var3 - 0.005, var43, var25, var29);
            }
         }
      }

      return true;
   }

   public boolean func_147746_l(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var6 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
         float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
         float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
         var7 = var10;
         var8 = var11;
         var9 = var12;
      }

      var5.func_78386_a(var7, var8, var9);
      double var18 = (double)var2;
      double var19 = (double)var3;
      double var14 = (double)var4;
      if (var1 == Blocks.field_150329_H) {
         long var16 = (long)(var2 * 3129871) ^ (long)var4 * 116129781L ^ (long)var3;
         var16 = var16 * var16 * 42317861L + var16 * 11L;
         var18 += ((double)((float)(var16 >> 16 & 15L) / 15.0F) - 0.5) * 0.5;
         var19 += ((double)((float)(var16 >> 20 & 15L) / 15.0F) - 1.0) * 0.2;
         var14 += ((double)((float)(var16 >> 24 & 15L) / 15.0F) - 0.5) * 0.5;
      } else if (var1 == Blocks.field_150328_O || var1 == Blocks.field_150327_N) {
         long var21 = (long)(var2 * 3129871) ^ (long)var4 * 116129781L ^ (long)var3;
         var21 = var21 * var21 * 42317861L + var21 * 11L;
         var18 += ((double)((float)(var21 >> 16 & 15L) / 15.0F) - 0.5) * 0.3;
         var14 += ((double)((float)(var21 >> 24 & 15L) / 15.0F) - 0.5) * 0.3;
      }

      IIcon var23 = this.func_147787_a(var1, 0, this.field_147845_a.func_72805_g(var2, var3, var4));
      this.func_147765_a(var23, var18, var19, var14, 1.0F);
      return true;
   }

   public boolean func_147774_a(BlockDoublePlant var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var6 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
         float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
         float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
         var7 = var10;
         var8 = var11;
         var9 = var12;
      }

      var5.func_78386_a(var7, var8, var9);
      long var58 = (long)(var2 * 3129871) ^ (long)var4 * 116129781L;
      var58 = var58 * var58 * 42317861L + var58 * 11L;
      double var60 = (double)var2;
      double var14 = (double)var3;
      double var16 = (double)var4;
      var60 += ((double)((float)(var58 >> 16 & 15L) / 15.0F) - 0.5) * 0.3;
      var16 += ((double)((float)(var58 >> 24 & 15L) / 15.0F) - 0.5) * 0.3;
      int var18 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var19 = 0;
      boolean var20 = BlockDoublePlant.func_149887_c(var18);
      if (var20) {
         if (this.field_147845_a.func_147439_a(var2, var3 - 1, var4) != var1) {
            return false;
         }

         var19 = BlockDoublePlant.func_149890_d(this.field_147845_a.func_72805_g(var2, var3 - 1, var4));
      } else {
         var19 = BlockDoublePlant.func_149890_d(var18);
      }

      IIcon var21 = var1.func_149888_a(var20, var19);
      this.func_147765_a(var21, var60, var14, var16, 1.0F);
      if (var20 && var19 == 0) {
         IIcon var22 = var1.field_149891_b[0];
         double var23 = Math.cos((double)var58 * 0.8) * 3.141592653589793 * 0.1;
         double var25 = Math.cos(var23);
         double var27 = Math.sin(var23);
         double var29 = (double)var22.func_94209_e();
         double var31 = (double)var22.func_94206_g();
         double var33 = (double)var22.func_94212_f();
         double var35 = (double)var22.func_94210_h();
         double var37 = 0.3;
         double var39 = -0.05;
         double var41 = 0.5 + 0.3 * var25 - 0.5 * var27;
         double var43 = 0.5 + 0.5 * var25 + 0.3 * var27;
         double var45 = 0.5 + 0.3 * var25 + 0.5 * var27;
         double var47 = 0.5 + -0.5 * var25 + 0.3 * var27;
         double var49 = 0.5 + -0.05 * var25 + 0.5 * var27;
         double var51 = 0.5 + -0.5 * var25 + -0.05 * var27;
         double var53 = 0.5 + -0.05 * var25 - 0.5 * var27;
         double var55 = 0.5 + 0.5 * var25 + -0.05 * var27;
         var5.func_78374_a(var60 + var49, var14 + 1.0, var16 + var51, var29, var35);
         var5.func_78374_a(var60 + var53, var14 + 1.0, var16 + var55, var33, var35);
         var5.func_78374_a(var60 + var41, var14 + 0.0, var16 + var43, var33, var31);
         var5.func_78374_a(var60 + var45, var14 + 0.0, var16 + var47, var29, var31);
         IIcon var57 = var1.field_149891_b[1];
         var29 = (double)var57.func_94209_e();
         var31 = (double)var57.func_94206_g();
         var33 = (double)var57.func_94212_f();
         var35 = (double)var57.func_94210_h();
         var5.func_78374_a(var60 + var53, var14 + 1.0, var16 + var55, var29, var35);
         var5.func_78374_a(var60 + var49, var14 + 1.0, var16 + var51, var33, var35);
         var5.func_78374_a(var60 + var45, var14 + 0.0, var16 + var47, var33, var31);
         var5.func_78374_a(var60 + var41, var14 + 0.0, var16 + var43, var29, var31);
      }

      return true;
   }

   public boolean func_147724_m(Block var1, int var2, int var3, int var4) {
      BlockStem var5 = (BlockStem)var1;
      Tessellator var6 = Tessellator.field_78398_a;
      var6.func_78380_c(var5.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var7 = var5.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var8 = (float)(var7 >> 16 & 0xFF) / 255.0F;
      float var9 = (float)(var7 >> 8 & 0xFF) / 255.0F;
      float var10 = (float)(var7 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var11 = (var8 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
         float var12 = (var8 * 30.0F + var9 * 70.0F) / 100.0F;
         float var13 = (var8 * 30.0F + var10 * 70.0F) / 100.0F;
         var8 = var11;
         var9 = var12;
         var10 = var13;
      }

      var6.func_78386_a(var8, var9, var10);
      var5.func_149719_a(this.field_147845_a, var2, var3, var4);
      int var14 = var5.func_149873_e(this.field_147845_a, var2, var3, var4);
      if (var14 < 0) {
         this.func_147730_a(
            var5, this.field_147845_a.func_72805_g(var2, var3, var4), this.field_147857_k, (double)var2, (double)((float)var3 - 0.0625F), (double)var4
         );
      } else {
         this.func_147730_a(var5, this.field_147845_a.func_72805_g(var2, var3, var4), 0.5, (double)var2, (double)((float)var3 - 0.0625F), (double)var4);
         this.func_147740_a(
            var5, this.field_147845_a.func_72805_g(var2, var3, var4), var14, this.field_147857_k, (double)var2, (double)((float)var3 - 0.0625F), (double)var4
         );
      }

      return true;
   }

   public boolean func_147796_n(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      this.func_147795_a(var1, this.field_147845_a.func_72805_g(var2, var3, var4), (double)var2, (double)((float)var3 - 0.0625F), (double)var4);
      return true;
   }

   public void func_147747_a(Block var1, double var2, double var4, double var6, double var8, double var10, int var12) {
      Tessellator var13 = Tessellator.field_78398_a;
      IIcon var14 = this.func_147787_a(var1, 0, var12);
      if (this.func_147744_b()) {
         var14 = this.field_147840_d;
      }

      double var15 = (double)var14.func_94209_e();
      double var17 = (double)var14.func_94206_g();
      double var19 = (double)var14.func_94212_f();
      double var21 = (double)var14.func_94210_h();
      double var23 = (double)var14.func_94214_a(7.0);
      double var25 = (double)var14.func_94207_b(6.0);
      double var27 = (double)var14.func_94214_a(9.0);
      double var29 = (double)var14.func_94207_b(8.0);
      double var31 = (double)var14.func_94214_a(7.0);
      double var33 = (double)var14.func_94207_b(13.0);
      double var35 = (double)var14.func_94214_a(9.0);
      double var37 = (double)var14.func_94207_b(15.0);
      var2 += 0.5;
      var6 += 0.5;
      double var39 = var2 - 0.5;
      double var41 = var2 + 0.5;
      double var43 = var6 - 0.5;
      double var45 = var6 + 0.5;
      double var47 = 0.0625;
      double var49 = 0.625;
      var13.func_78374_a(var2 + var8 * (1.0 - var49) - var47, var4 + var49, var6 + var10 * (1.0 - var49) - var47, var23, var25);
      var13.func_78374_a(var2 + var8 * (1.0 - var49) - var47, var4 + var49, var6 + var10 * (1.0 - var49) + var47, var23, var29);
      var13.func_78374_a(var2 + var8 * (1.0 - var49) + var47, var4 + var49, var6 + var10 * (1.0 - var49) + var47, var27, var29);
      var13.func_78374_a(var2 + var8 * (1.0 - var49) + var47, var4 + var49, var6 + var10 * (1.0 - var49) - var47, var27, var25);
      var13.func_78374_a(var2 + var47 + var8, var4, var6 - var47 + var10, var35, var33);
      var13.func_78374_a(var2 + var47 + var8, var4, var6 + var47 + var10, var35, var37);
      var13.func_78374_a(var2 - var47 + var8, var4, var6 + var47 + var10, var31, var37);
      var13.func_78374_a(var2 - var47 + var8, var4, var6 - var47 + var10, var31, var33);
      var13.func_78374_a(var2 - var47, var4 + 1.0, var43, var15, var17);
      var13.func_78374_a(var2 - var47 + var8, var4 + 0.0, var43 + var10, var15, var21);
      var13.func_78374_a(var2 - var47 + var8, var4 + 0.0, var45 + var10, var19, var21);
      var13.func_78374_a(var2 - var47, var4 + 1.0, var45, var19, var17);
      var13.func_78374_a(var2 + var47, var4 + 1.0, var45, var15, var17);
      var13.func_78374_a(var2 + var8 + var47, var4 + 0.0, var45 + var10, var15, var21);
      var13.func_78374_a(var2 + var8 + var47, var4 + 0.0, var43 + var10, var19, var21);
      var13.func_78374_a(var2 + var47, var4 + 1.0, var43, var19, var17);
      var13.func_78374_a(var39, var4 + 1.0, var6 + var47, var15, var17);
      var13.func_78374_a(var39 + var8, var4 + 0.0, var6 + var47 + var10, var15, var21);
      var13.func_78374_a(var41 + var8, var4 + 0.0, var6 + var47 + var10, var19, var21);
      var13.func_78374_a(var41, var4 + 1.0, var6 + var47, var19, var17);
      var13.func_78374_a(var41, var4 + 1.0, var6 - var47, var15, var17);
      var13.func_78374_a(var41 + var8, var4 + 0.0, var6 - var47 + var10, var15, var21);
      var13.func_78374_a(var39 + var8, var4 + 0.0, var6 - var47 + var10, var19, var21);
      var13.func_78374_a(var39, var4 + 1.0, var6 - var47, var19, var17);
   }

   public void func_147765_a(IIcon var1, double var2, double var4, double var6, float var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var1 = this.field_147840_d;
      }

      double var10 = (double)var1.func_94209_e();
      double var12 = (double)var1.func_94206_g();
      double var14 = (double)var1.func_94212_f();
      double var16 = (double)var1.func_94210_h();
      double var18 = 0.45 * (double)var8;
      double var20 = var2 + 0.5 - var18;
      double var22 = var2 + 0.5 + var18;
      double var24 = var6 + 0.5 - var18;
      double var26 = var6 + 0.5 + var18;
      var9.func_78374_a(var20, var4 + (double)var8, var24, var10, var12);
      var9.func_78374_a(var20, var4 + 0.0, var24, var10, var16);
      var9.func_78374_a(var22, var4 + 0.0, var26, var14, var16);
      var9.func_78374_a(var22, var4 + (double)var8, var26, var14, var12);
      var9.func_78374_a(var22, var4 + (double)var8, var26, var10, var12);
      var9.func_78374_a(var22, var4 + 0.0, var26, var10, var16);
      var9.func_78374_a(var20, var4 + 0.0, var24, var14, var16);
      var9.func_78374_a(var20, var4 + (double)var8, var24, var14, var12);
      var9.func_78374_a(var20, var4 + (double)var8, var26, var10, var12);
      var9.func_78374_a(var20, var4 + 0.0, var26, var10, var16);
      var9.func_78374_a(var22, var4 + 0.0, var24, var14, var16);
      var9.func_78374_a(var22, var4 + (double)var8, var24, var14, var12);
      var9.func_78374_a(var22, var4 + (double)var8, var24, var10, var12);
      var9.func_78374_a(var22, var4 + 0.0, var24, var10, var16);
      var9.func_78374_a(var20, var4 + 0.0, var26, var14, var16);
      var9.func_78374_a(var20, var4 + (double)var8, var26, var14, var12);
   }

   public void func_147730_a(Block var1, int var2, double var3, double var5, double var7, double var9) {
      Tessellator var11 = Tessellator.field_78398_a;
      IIcon var12 = this.func_147787_a(var1, 0, var2);
      if (this.func_147744_b()) {
         var12 = this.field_147840_d;
      }

      double var13 = (double)var12.func_94209_e();
      double var15 = (double)var12.func_94206_g();
      double var17 = (double)var12.func_94212_f();
      double var19 = (double)var12.func_94207_b(var3 * 16.0);
      double var21 = var5 + 0.5 - 0.44999998807907104;
      double var23 = var5 + 0.5 + 0.44999998807907104;
      double var25 = var9 + 0.5 - 0.44999998807907104;
      double var27 = var9 + 0.5 + 0.44999998807907104;
      var11.func_78374_a(var21, var7 + var3, var25, var13, var15);
      var11.func_78374_a(var21, var7 + 0.0, var25, var13, var19);
      var11.func_78374_a(var23, var7 + 0.0, var27, var17, var19);
      var11.func_78374_a(var23, var7 + var3, var27, var17, var15);
      var11.func_78374_a(var23, var7 + var3, var27, var17, var15);
      var11.func_78374_a(var23, var7 + 0.0, var27, var17, var19);
      var11.func_78374_a(var21, var7 + 0.0, var25, var13, var19);
      var11.func_78374_a(var21, var7 + var3, var25, var13, var15);
      var11.func_78374_a(var21, var7 + var3, var27, var13, var15);
      var11.func_78374_a(var21, var7 + 0.0, var27, var13, var19);
      var11.func_78374_a(var23, var7 + 0.0, var25, var17, var19);
      var11.func_78374_a(var23, var7 + var3, var25, var17, var15);
      var11.func_78374_a(var23, var7 + var3, var25, var17, var15);
      var11.func_78374_a(var23, var7 + 0.0, var25, var17, var19);
      var11.func_78374_a(var21, var7 + 0.0, var27, var13, var19);
      var11.func_78374_a(var21, var7 + var3, var27, var13, var15);
   }

   public boolean func_147783_o(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      IIcon var6 = this.func_147777_a(var1, 1);
      if (this.func_147744_b()) {
         var6 = this.field_147840_d;
      }

      float var7 = 0.015625F;
      double var8 = (double)var6.func_94209_e();
      double var10 = (double)var6.func_94206_g();
      double var12 = (double)var6.func_94212_f();
      double var14 = (double)var6.func_94210_h();
      long var16 = (long)(var2 * 3129871) ^ (long)var4 * 116129781L ^ (long)var3;
      var16 = var16 * var16 * 42317861L + var16 * 11L;
      int var18 = (int)(var16 >> 16 & 3L);
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      float var19 = (float)var2 + 0.5F;
      float var20 = (float)var4 + 0.5F;
      float var21 = (float)(var18 & 1) * 0.5F * (float)(1 - var18 / 2 % 2 * 2);
      float var22 = (float)(var18 + 1 & 1) * 0.5F * (float)(1 - (var18 + 1) / 2 % 2 * 2);
      var5.func_78378_d(var1.func_149635_D());
      var5.func_78374_a((double)(var19 + var21 - var22), (double)((float)var3 + var7), (double)(var20 + var21 + var22), var8, var10);
      var5.func_78374_a((double)(var19 + var21 + var22), (double)((float)var3 + var7), (double)(var20 - var21 + var22), var12, var10);
      var5.func_78374_a((double)(var19 - var21 + var22), (double)((float)var3 + var7), (double)(var20 - var21 - var22), var12, var14);
      var5.func_78374_a((double)(var19 - var21 - var22), (double)((float)var3 + var7), (double)(var20 + var21 - var22), var8, var14);
      var5.func_78378_d((var1.func_149635_D() & 16711422) >> 1);
      var5.func_78374_a((double)(var19 - var21 - var22), (double)((float)var3 + var7), (double)(var20 + var21 - var22), var8, var14);
      var5.func_78374_a((double)(var19 - var21 + var22), (double)((float)var3 + var7), (double)(var20 - var21 - var22), var12, var14);
      var5.func_78374_a((double)(var19 + var21 + var22), (double)((float)var3 + var7), (double)(var20 - var21 + var22), var12, var10);
      var5.func_78374_a((double)(var19 + var21 - var22), (double)((float)var3 + var7), (double)(var20 + var21 + var22), var8, var10);
      return true;
   }

   public void func_147740_a(BlockStem var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      Tessellator var12 = Tessellator.field_78398_a;
      IIcon var13 = var1.func_149872_i();
      if (this.func_147744_b()) {
         var13 = this.field_147840_d;
      }

      double var14 = (double)var13.func_94209_e();
      double var16 = (double)var13.func_94206_g();
      double var18 = (double)var13.func_94212_f();
      double var20 = (double)var13.func_94210_h();
      double var22 = var6 + 0.5 - 0.5;
      double var24 = var6 + 0.5 + 0.5;
      double var26 = var10 + 0.5 - 0.5;
      double var28 = var10 + 0.5 + 0.5;
      double var30 = var6 + 0.5;
      double var32 = var10 + 0.5;
      if ((var3 + 1) / 2 % 2 == 1) {
         double var34 = var18;
         var18 = var14;
         var14 = var34;
      }

      if (var3 < 2) {
         var12.func_78374_a(var22, var8 + var4, var32, var14, var16);
         var12.func_78374_a(var22, var8 + 0.0, var32, var14, var20);
         var12.func_78374_a(var24, var8 + 0.0, var32, var18, var20);
         var12.func_78374_a(var24, var8 + var4, var32, var18, var16);
         var12.func_78374_a(var24, var8 + var4, var32, var18, var16);
         var12.func_78374_a(var24, var8 + 0.0, var32, var18, var20);
         var12.func_78374_a(var22, var8 + 0.0, var32, var14, var20);
         var12.func_78374_a(var22, var8 + var4, var32, var14, var16);
      } else {
         var12.func_78374_a(var30, var8 + var4, var28, var14, var16);
         var12.func_78374_a(var30, var8 + 0.0, var28, var14, var20);
         var12.func_78374_a(var30, var8 + 0.0, var26, var18, var20);
         var12.func_78374_a(var30, var8 + var4, var26, var18, var16);
         var12.func_78374_a(var30, var8 + var4, var26, var18, var16);
         var12.func_78374_a(var30, var8 + 0.0, var26, var18, var20);
         var12.func_78374_a(var30, var8 + 0.0, var28, var14, var20);
         var12.func_78374_a(var30, var8 + var4, var28, var14, var16);
      }
   }

   public void func_147795_a(Block var1, int var2, double var3, double var5, double var7) {
      Tessellator var9 = Tessellator.field_78398_a;
      IIcon var10 = this.func_147787_a(var1, 0, var2);
      if (this.func_147744_b()) {
         var10 = this.field_147840_d;
      }

      double var11 = (double)var10.func_94209_e();
      double var13 = (double)var10.func_94206_g();
      double var15 = (double)var10.func_94212_f();
      double var17 = (double)var10.func_94210_h();
      double var19 = var3 + 0.5 - 0.25;
      double var21 = var3 + 0.5 + 0.25;
      double var23 = var7 + 0.5 - 0.5;
      double var25 = var7 + 0.5 + 0.5;
      var9.func_78374_a(var19, var5 + 1.0, var23, var11, var13);
      var9.func_78374_a(var19, var5 + 0.0, var23, var11, var17);
      var9.func_78374_a(var19, var5 + 0.0, var25, var15, var17);
      var9.func_78374_a(var19, var5 + 1.0, var25, var15, var13);
      var9.func_78374_a(var19, var5 + 1.0, var25, var11, var13);
      var9.func_78374_a(var19, var5 + 0.0, var25, var11, var17);
      var9.func_78374_a(var19, var5 + 0.0, var23, var15, var17);
      var9.func_78374_a(var19, var5 + 1.0, var23, var15, var13);
      var9.func_78374_a(var21, var5 + 1.0, var25, var11, var13);
      var9.func_78374_a(var21, var5 + 0.0, var25, var11, var17);
      var9.func_78374_a(var21, var5 + 0.0, var23, var15, var17);
      var9.func_78374_a(var21, var5 + 1.0, var23, var15, var13);
      var9.func_78374_a(var21, var5 + 1.0, var23, var11, var13);
      var9.func_78374_a(var21, var5 + 0.0, var23, var11, var17);
      var9.func_78374_a(var21, var5 + 0.0, var25, var15, var17);
      var9.func_78374_a(var21, var5 + 1.0, var25, var15, var13);
      var19 = var3 + 0.5 - 0.5;
      var21 = var3 + 0.5 + 0.5;
      var23 = var7 + 0.5 - 0.25;
      var25 = var7 + 0.5 + 0.25;
      var9.func_78374_a(var19, var5 + 1.0, var23, var11, var13);
      var9.func_78374_a(var19, var5 + 0.0, var23, var11, var17);
      var9.func_78374_a(var21, var5 + 0.0, var23, var15, var17);
      var9.func_78374_a(var21, var5 + 1.0, var23, var15, var13);
      var9.func_78374_a(var21, var5 + 1.0, var23, var11, var13);
      var9.func_78374_a(var21, var5 + 0.0, var23, var11, var17);
      var9.func_78374_a(var19, var5 + 0.0, var23, var15, var17);
      var9.func_78374_a(var19, var5 + 1.0, var23, var15, var13);
      var9.func_78374_a(var21, var5 + 1.0, var25, var11, var13);
      var9.func_78374_a(var21, var5 + 0.0, var25, var11, var17);
      var9.func_78374_a(var19, var5 + 0.0, var25, var15, var17);
      var9.func_78374_a(var19, var5 + 1.0, var25, var15, var13);
      var9.func_78374_a(var19, var5 + 1.0, var25, var11, var13);
      var9.func_78374_a(var19, var5 + 0.0, var25, var11, var17);
      var9.func_78374_a(var21, var5 + 0.0, var25, var15, var17);
      var9.func_78374_a(var21, var5 + 1.0, var25, var15, var13);
   }

   public boolean func_147721_p(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var6 & 0xFF) / 255.0F;
      boolean var10 = var1.func_149646_a(this.field_147845_a, var2, var3 + 1, var4, 1);
      boolean var11 = var1.func_149646_a(this.field_147845_a, var2, var3 - 1, var4, 0);
      boolean[] var12 = new boolean[]{
         var1.func_149646_a(this.field_147845_a, var2, var3, var4 - 1, 2),
         var1.func_149646_a(this.field_147845_a, var2, var3, var4 + 1, 3),
         var1.func_149646_a(this.field_147845_a, var2 - 1, var3, var4, 4),
         var1.func_149646_a(this.field_147845_a, var2 + 1, var3, var4, 5)
      };
      if (!var10 && !var11 && !var12[0] && !var12[1] && !var12[2] && !var12[3]) {
         return false;
      } else {
         boolean var13 = false;
         float var14 = 0.5F;
         float var15 = 1.0F;
         float var16 = 0.8F;
         float var17 = 0.6F;
         double var18 = 0.0;
         double var20 = 1.0;
         Material var22 = var1.func_149688_o();
         int var23 = this.field_147845_a.func_72805_g(var2, var3, var4);
         double var24 = (double)this.func_147729_a(var2, var3, var4, var22);
         double var26 = (double)this.func_147729_a(var2, var3, var4 + 1, var22);
         double var28 = (double)this.func_147729_a(var2 + 1, var3, var4 + 1, var22);
         double var30 = (double)this.func_147729_a(var2 + 1, var3, var4, var22);
         double var32 = 0.0010000000474974513;
         if (this.field_147837_f || var10) {
            var13 = true;
            IIcon var34 = this.func_147787_a(var1, 1, var23);
            float var35 = (float)BlockLiquid.func_149802_a(this.field_147845_a, var2, var3, var4, var22);
            if (var35 > -999.0F) {
               var34 = this.func_147787_a(var1, 2, var23);
            }

            var24 -= var32;
            var26 -= var32;
            var28 -= var32;
            var30 -= var32;
            double var36;
            double var38;
            double var40;
            double var42;
            double var44;
            double var46;
            double var48;
            double var50;
            if (var35 < -999.0F) {
               var36 = (double)var34.func_94214_a(0.0);
               var44 = (double)var34.func_94207_b(0.0);
               var38 = var36;
               var46 = (double)var34.func_94207_b(16.0);
               var40 = (double)var34.func_94214_a(16.0);
               var48 = var46;
               var42 = var40;
               var50 = var44;
            } else {
               float var52 = MathHelper.func_76126_a(var35) * 0.25F;
               float var53 = MathHelper.func_76134_b(var35) * 0.25F;
               float var54 = 8.0F;
               var36 = (double)var34.func_94214_a((double)(8.0F + (-var53 - var52) * 16.0F));
               var44 = (double)var34.func_94207_b((double)(8.0F + (-var53 + var52) * 16.0F));
               var38 = (double)var34.func_94214_a((double)(8.0F + (-var53 + var52) * 16.0F));
               var46 = (double)var34.func_94207_b((double)(8.0F + (var53 + var52) * 16.0F));
               var40 = (double)var34.func_94214_a((double)(8.0F + (var53 + var52) * 16.0F));
               var48 = (double)var34.func_94207_b((double)(8.0F + (var53 - var52) * 16.0F));
               var42 = (double)var34.func_94214_a((double)(8.0F + (var53 - var52) * 16.0F));
               var50 = (double)var34.func_94207_b((double)(8.0F + (-var53 - var52) * 16.0F));
            }

            var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
            var5.func_78386_a(var15 * var7, var15 * var8, var15 * var9);
            var5.func_78374_a((double)(var2 + 0), (double)var3 + var24, (double)(var4 + 0), var36, var44);
            var5.func_78374_a((double)(var2 + 0), (double)var3 + var26, (double)(var4 + 1), var38, var46);
            var5.func_78374_a((double)(var2 + 1), (double)var3 + var28, (double)(var4 + 1), var40, var48);
            var5.func_78374_a((double)(var2 + 1), (double)var3 + var30, (double)(var4 + 0), var42, var50);
            var5.func_78374_a((double)(var2 + 0), (double)var3 + var24, (double)(var4 + 0), var36, var44);
            var5.func_78374_a((double)(var2 + 1), (double)var3 + var30, (double)(var4 + 0), var42, var50);
            var5.func_78374_a((double)(var2 + 1), (double)var3 + var28, (double)(var4 + 1), var40, var48);
            var5.func_78374_a((double)(var2 + 0), (double)var3 + var26, (double)(var4 + 1), var38, var46);
         }

         if (this.field_147837_f || var11) {
            var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4));
            var5.func_78386_a(var14, var14, var14);
            this.func_147768_a(var1, (double)var2, (double)var3 + var32, (double)var4, this.func_147777_a(var1, 0));
            var13 = true;
         }

         for(int var57 = 0; var57 < 4; ++var57) {
            int var58 = var2;
            int var37 = var4;
            if (var57 == 0) {
               var37 = var4 - 1;
            }

            if (var57 == 1) {
               ++var37;
            }

            if (var57 == 2) {
               var58 = var2 - 1;
            }

            if (var57 == 3) {
               ++var58;
            }

            IIcon var59 = this.func_147787_a(var1, var57 + 2, var23);
            if (this.field_147837_f || var12[var57]) {
               double var39;
               double var41;
               double var43;
               double var45;
               double var47;
               double var49;
               if (var57 == 0) {
                  var39 = var24;
                  var41 = var30;
                  var43 = (double)var2;
                  var47 = (double)(var2 + 1);
                  var45 = (double)var4 + var32;
                  var49 = (double)var4 + var32;
               } else if (var57 == 1) {
                  var39 = var28;
                  var41 = var26;
                  var43 = (double)(var2 + 1);
                  var47 = (double)var2;
                  var45 = (double)(var4 + 1) - var32;
                  var49 = (double)(var4 + 1) - var32;
               } else if (var57 == 2) {
                  var39 = var26;
                  var41 = var24;
                  var43 = (double)var2 + var32;
                  var47 = (double)var2 + var32;
                  var45 = (double)(var4 + 1);
                  var49 = (double)var4;
               } else {
                  var39 = var30;
                  var41 = var28;
                  var43 = (double)(var2 + 1) - var32;
                  var47 = (double)(var2 + 1) - var32;
                  var45 = (double)var4;
                  var49 = (double)(var4 + 1);
               }

               var13 = true;
               float var51 = var59.func_94214_a(0.0);
               float var60 = var59.func_94214_a(8.0);
               float var61 = var59.func_94207_b((1.0 - var39) * 16.0 * 0.5);
               float var62 = var59.func_94207_b((1.0 - var41) * 16.0 * 0.5);
               float var55 = var59.func_94207_b(8.0);
               var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var58, var3, var37));
               float var56 = 1.0F;
               var56 *= var57 < 2 ? var16 : var17;
               var5.func_78386_a(var15 * var56 * var7, var15 * var56 * var8, var15 * var56 * var9);
               var5.func_78374_a(var43, (double)var3 + var39, var45, (double)var51, (double)var61);
               var5.func_78374_a(var47, (double)var3 + var41, var49, (double)var60, (double)var62);
               var5.func_78374_a(var47, (double)(var3 + 0), var49, (double)var60, (double)var55);
               var5.func_78374_a(var43, (double)(var3 + 0), var45, (double)var51, (double)var55);
               var5.func_78374_a(var43, (double)(var3 + 0), var45, (double)var51, (double)var55);
               var5.func_78374_a(var47, (double)(var3 + 0), var49, (double)var60, (double)var55);
               var5.func_78374_a(var47, (double)var3 + var41, var49, (double)var60, (double)var62);
               var5.func_78374_a(var43, (double)var3 + var39, var45, (double)var51, (double)var61);
            }
         }

         this.field_147855_j = var18;
         this.field_147857_k = var20;
         return var13;
      }
   }

   private float func_147729_a(int var1, int var2, int var3, Material var4) {
      int var5 = 0;
      float var6 = 0.0F;

      for(int var7 = 0; var7 < 4; ++var7) {
         int var8 = var1 - (var7 & 1);
         int var10 = var3 - (var7 >> 1 & 1);
         if (this.field_147845_a.func_147439_a(var8, var2 + 1, var10).func_149688_o() == var4) {
            return 1.0F;
         }

         Material var11 = this.field_147845_a.func_147439_a(var8, var2, var10).func_149688_o();
         if (var11 == var4) {
            int var12 = this.field_147845_a.func_72805_g(var8, var2, var10);
            if (var12 >= 8 || var12 == 0) {
               var6 += BlockLiquid.func_149801_b(var12) * 10.0F;
               var5 += 10;
            }

            var6 += BlockLiquid.func_149801_b(var12);
            ++var5;
         } else if (!var11.func_76220_a()) {
            ++var6;
            ++var5;
         }
      }

      return 1.0F - var6 / (float)var5;
   }

   public void func_147749_a(Block var1, World var2, int var3, int var4, int var5, int var6) {
      float var7 = 0.5F;
      float var8 = 1.0F;
      float var9 = 0.8F;
      float var10 = 0.6F;
      Tessellator var11 = Tessellator.field_78398_a;
      var11.func_78382_b();
      var11.func_78380_c(var1.func_149677_c(var2, var3, var4, var5));
      var11.func_78386_a(var7, var7, var7);
      this.func_147768_a(var1, -0.5, -0.5, -0.5, this.func_147787_a(var1, 0, var6));
      var11.func_78386_a(var8, var8, var8);
      this.func_147806_b(var1, -0.5, -0.5, -0.5, this.func_147787_a(var1, 1, var6));
      var11.func_78386_a(var9, var9, var9);
      this.func_147761_c(var1, -0.5, -0.5, -0.5, this.func_147787_a(var1, 2, var6));
      var11.func_78386_a(var9, var9, var9);
      this.func_147734_d(var1, -0.5, -0.5, -0.5, this.func_147787_a(var1, 3, var6));
      var11.func_78386_a(var10, var10, var10);
      this.func_147798_e(var1, -0.5, -0.5, -0.5, this.func_147787_a(var1, 4, var6));
      var11.func_78386_a(var10, var10, var10);
      this.func_147764_f(var1, -0.5, -0.5, -0.5, this.func_147787_a(var1, 5, var6));
      var11.func_78381_a();
   }

   public boolean func_147784_q(Block var1, int var2, int var3, int var4) {
      int var5 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var6 = (float)(var5 >> 16 & 0xFF) / 255.0F;
      float var7 = (float)(var5 >> 8 & 0xFF) / 255.0F;
      float var8 = (float)(var5 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
         float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
         float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
         var6 = var9;
         var7 = var10;
         var8 = var11;
      }

      if (!Minecraft.func_71379_u() || var1.func_149750_m() != 0) {
         return this.func_147736_d(var1, var2, var3, var4, var6, var7, var8);
      } else {
         return this.field_147849_o
            ? this.func_147808_b(var1, var2, var3, var4, var6, var7, var8)
            : this.func_147751_a(var1, var2, var3, var4, var6, var7, var8);
      }
   }

   public boolean func_147742_r(Block var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var6 = var5 & 12;
      if (var6 == 4) {
         this.field_147875_q = 1;
         this.field_147873_r = 1;
         this.field_147867_u = 1;
         this.field_147865_v = 1;
      } else if (var6 == 8) {
         this.field_147871_s = 1;
         this.field_147869_t = 1;
      }

      boolean var7 = this.func_147784_q(var1, var2, var3, var4);
      this.field_147871_s = 0;
      this.field_147875_q = 0;
      this.field_147873_r = 0;
      this.field_147869_t = 0;
      this.field_147867_u = 0;
      this.field_147865_v = 0;
      return var7;
   }

   public boolean func_147779_s(Block var1, int var2, int var3, int var4) {
      int var5 = this.field_147845_a.func_72805_g(var2, var3, var4);
      if (var5 == 3) {
         this.field_147875_q = 1;
         this.field_147873_r = 1;
         this.field_147867_u = 1;
         this.field_147865_v = 1;
      } else if (var5 == 4) {
         this.field_147871_s = 1;
         this.field_147869_t = 1;
      }

      boolean var6 = this.func_147784_q(var1, var2, var3, var4);
      this.field_147871_s = 0;
      this.field_147875_q = 0;
      this.field_147873_r = 0;
      this.field_147869_t = 0;
      this.field_147867_u = 0;
      this.field_147865_v = 0;
      return var6;
   }

   public boolean func_147751_a(Block var1, int var2, int var3, int var4, float var5, float var6, float var7) {
      this.field_147863_w = true;
      boolean var8 = false;
      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      boolean var13 = true;
      int var14 = var1.func_149677_c(this.field_147845_a, var2, var3, var4);
      Tessellator var15 = Tessellator.field_78398_a;
      var15.func_78380_c(983055);
      if (this.func_147745_b(var1).func_94215_i().equals("grass_top")) {
         var13 = false;
      } else if (this.func_147744_b()) {
         var13 = false;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 - 1, var4, 0)) {
         if (this.field_147855_j <= 0.0) {
            --var3;
         }

         this.field_147831_S = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147825_U = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147828_V = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147835_X = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         this.field_147886_y = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147814_A = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147815_B = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         this.field_147810_D = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         boolean var16 = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149751_l();
         boolean var17 = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149751_l();
         boolean var18 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149751_l();
         boolean var19 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149751_l();
         if (!var19 && !var17) {
            this.field_147888_x = this.field_147886_y;
            this.field_147832_R = this.field_147831_S;
         } else {
            this.field_147888_x = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149685_I();
            this.field_147832_R = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 - 1);
         }

         if (!var18 && !var17) {
            this.field_147884_z = this.field_147886_y;
            this.field_147826_T = this.field_147831_S;
         } else {
            this.field_147884_z = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149685_I();
            this.field_147826_T = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 + 1);
         }

         if (!var19 && !var16) {
            this.field_147816_C = this.field_147810_D;
            this.field_147827_W = this.field_147835_X;
         } else {
            this.field_147816_C = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149685_I();
            this.field_147827_W = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 - 1);
         }

         if (!var18 && !var16) {
            this.field_147811_E = this.field_147810_D;
            this.field_147834_Y = this.field_147835_X;
         } else {
            this.field_147811_E = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149685_I();
            this.field_147834_Y = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 + 1);
         }

         if (this.field_147855_j <= 0.0) {
            ++var3;
         }

         int var20 = var14;
         if (this.field_147855_j <= 0.0 || !this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149662_c()) {
            var20 = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         }

         float var21 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         var9 = (this.field_147884_z + this.field_147886_y + this.field_147815_B + var21) / 4.0F;
         var12 = (this.field_147815_B + var21 + this.field_147811_E + this.field_147810_D) / 4.0F;
         var11 = (var21 + this.field_147814_A + this.field_147810_D + this.field_147816_C) / 4.0F;
         var10 = (this.field_147886_y + this.field_147888_x + var21 + this.field_147814_A) / 4.0F;
         this.field_147864_al = this.func_147778_a(this.field_147826_T, this.field_147831_S, this.field_147828_V, var20);
         this.field_147870_ao = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147835_X, var20);
         this.field_147876_an = this.func_147778_a(this.field_147825_U, this.field_147835_X, this.field_147827_W, var20);
         this.field_147874_am = this.func_147778_a(this.field_147831_S, this.field_147832_R, this.field_147825_U, var20);
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.5F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.5F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.5F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.5F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.5F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.5F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         this.func_147768_a(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 0));
         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 + 1, var4, 1)) {
         if (this.field_147857_k >= 1.0) {
            ++var3;
         }

         this.field_147880_aa = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147885_ae = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         this.field_147878_ac = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147887_af = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147813_G = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147824_K = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         this.field_147822_I = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147817_L = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         boolean var47 = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149751_l();
         boolean var52 = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149751_l();
         boolean var57 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149751_l();
         boolean var62 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149751_l();
         if (!var62 && !var52) {
            this.field_147812_F = this.field_147813_G;
            this.field_147836_Z = this.field_147880_aa;
         } else {
            this.field_147812_F = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149685_I();
            this.field_147836_Z = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 - 1);
         }

         if (!var62 && !var47) {
            this.field_147823_J = this.field_147824_K;
            this.field_147879_ad = this.field_147885_ae;
         } else {
            this.field_147823_J = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149685_I();
            this.field_147879_ad = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 - 1);
         }

         if (!var57 && !var52) {
            this.field_147821_H = this.field_147813_G;
            this.field_147881_ab = this.field_147880_aa;
         } else {
            this.field_147821_H = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149685_I();
            this.field_147881_ab = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 + 1);
         }

         if (!var57 && !var47) {
            this.field_147818_M = this.field_147824_K;
            this.field_147882_ag = this.field_147885_ae;
         } else {
            this.field_147818_M = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149685_I();
            this.field_147882_ag = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 + 1);
         }

         if (this.field_147857_k >= 1.0) {
            --var3;
         }

         int var67 = var14;
         if (this.field_147857_k >= 1.0 || !this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149662_c()) {
            var67 = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         }

         float var72 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         var12 = (this.field_147821_H + this.field_147813_G + this.field_147817_L + var72) / 4.0F;
         var9 = (this.field_147817_L + var72 + this.field_147818_M + this.field_147824_K) / 4.0F;
         var10 = (var72 + this.field_147822_I + this.field_147824_K + this.field_147823_J) / 4.0F;
         var11 = (this.field_147813_G + this.field_147812_F + var72 + this.field_147822_I) / 4.0F;
         this.field_147870_ao = this.func_147778_a(this.field_147881_ab, this.field_147880_aa, this.field_147887_af, var67);
         this.field_147864_al = this.func_147778_a(this.field_147887_af, this.field_147882_ag, this.field_147885_ae, var67);
         this.field_147874_am = this.func_147778_a(this.field_147878_ac, this.field_147885_ae, this.field_147879_ad, var67);
         this.field_147876_an = this.func_147778_a(this.field_147880_aa, this.field_147836_Z, this.field_147878_ac, var67);
         this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5;
         this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6;
         this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7;
         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         this.func_147806_b(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 1));
         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 - 1, 2)) {
         if (this.field_147851_l <= 0.0) {
            --var4;
         }

         this.field_147819_N = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147814_A = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147822_I = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147820_O = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         this.field_147883_ah = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147825_U = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147878_ac = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         this.field_147866_ai = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         boolean var48 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149751_l();
         boolean var53 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149751_l();
         boolean var58 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149751_l();
         boolean var63 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149751_l();
         if (!var53 && !var63) {
            this.field_147888_x = this.field_147819_N;
            this.field_147832_R = this.field_147883_ah;
         } else {
            this.field_147888_x = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149685_I();
            this.field_147832_R = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 - 1, var4);
         }

         if (!var53 && !var58) {
            this.field_147812_F = this.field_147819_N;
            this.field_147836_Z = this.field_147883_ah;
         } else {
            this.field_147812_F = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149685_I();
            this.field_147836_Z = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 + 1, var4);
         }

         if (!var48 && !var63) {
            this.field_147816_C = this.field_147820_O;
            this.field_147827_W = this.field_147866_ai;
         } else {
            this.field_147816_C = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149685_I();
            this.field_147827_W = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 - 1, var4);
         }

         if (!var48 && !var58) {
            this.field_147823_J = this.field_147820_O;
            this.field_147879_ad = this.field_147866_ai;
         } else {
            this.field_147823_J = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149685_I();
            this.field_147879_ad = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 + 1, var4);
         }

         if (this.field_147851_l <= 0.0) {
            ++var4;
         }

         int var68 = var14;
         if (this.field_147851_l <= 0.0 || !this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149662_c()) {
            var68 = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         }

         float var73 = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         var9 = (this.field_147819_N + this.field_147812_F + var73 + this.field_147822_I) / 4.0F;
         var10 = (var73 + this.field_147822_I + this.field_147820_O + this.field_147823_J) / 4.0F;
         var11 = (this.field_147814_A + var73 + this.field_147816_C + this.field_147820_O) / 4.0F;
         var12 = (this.field_147888_x + this.field_147819_N + this.field_147814_A + var73) / 4.0F;
         this.field_147864_al = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147878_ac, var68);
         this.field_147874_am = this.func_147778_a(this.field_147878_ac, this.field_147866_ai, this.field_147879_ad, var68);
         this.field_147876_an = this.func_147778_a(this.field_147825_U, this.field_147827_W, this.field_147866_ai, var68);
         this.field_147870_ao = this.func_147778_a(this.field_147832_R, this.field_147883_ah, this.field_147825_U, var68);
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.8F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var22 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 2);
         this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, var22);
         if (field_147843_b && var22.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 + 1, 3)) {
         if (this.field_147853_m >= 1.0) {
            ++var4;
         }

         this.field_147830_P = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147829_Q = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         this.field_147815_B = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147817_L = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147868_aj = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147862_ak = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         this.field_147828_V = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147887_af = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         boolean var49 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149751_l();
         boolean var54 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149751_l();
         boolean var59 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149751_l();
         boolean var64 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149751_l();
         if (!var54 && !var64) {
            this.field_147884_z = this.field_147830_P;
            this.field_147826_T = this.field_147868_aj;
         } else {
            this.field_147884_z = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149685_I();
            this.field_147826_T = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 - 1, var4);
         }

         if (!var54 && !var59) {
            this.field_147821_H = this.field_147830_P;
            this.field_147881_ab = this.field_147868_aj;
         } else {
            this.field_147821_H = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149685_I();
            this.field_147881_ab = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 + 1, var4);
         }

         if (!var49 && !var64) {
            this.field_147811_E = this.field_147829_Q;
            this.field_147834_Y = this.field_147862_ak;
         } else {
            this.field_147811_E = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149685_I();
            this.field_147834_Y = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 - 1, var4);
         }

         if (!var49 && !var59) {
            this.field_147818_M = this.field_147829_Q;
            this.field_147882_ag = this.field_147862_ak;
         } else {
            this.field_147818_M = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149685_I();
            this.field_147882_ag = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 + 1, var4);
         }

         if (this.field_147853_m >= 1.0) {
            --var4;
         }

         int var69 = var14;
         if (this.field_147853_m >= 1.0 || !this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149662_c()) {
            var69 = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         }

         float var74 = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         var9 = (this.field_147830_P + this.field_147821_H + var74 + this.field_147817_L) / 4.0F;
         var12 = (var74 + this.field_147817_L + this.field_147829_Q + this.field_147818_M) / 4.0F;
         var11 = (this.field_147815_B + var74 + this.field_147811_E + this.field_147829_Q) / 4.0F;
         var10 = (this.field_147884_z + this.field_147830_P + this.field_147815_B + var74) / 4.0F;
         this.field_147864_al = this.func_147778_a(this.field_147868_aj, this.field_147881_ab, this.field_147887_af, var69);
         this.field_147870_ao = this.func_147778_a(this.field_147887_af, this.field_147862_ak, this.field_147882_ag, var69);
         this.field_147876_an = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147862_ak, var69);
         this.field_147874_am = this.func_147778_a(this.field_147826_T, this.field_147868_aj, this.field_147828_V, var69);
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.8F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var77 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3);
         this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3));
         if (field_147843_b && var77.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 - 1, var3, var4, 4)) {
         if (this.field_147859_h <= 0.0) {
            --var2;
         }

         this.field_147886_y = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147819_N = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147830_P = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         this.field_147813_G = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147831_S = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147883_ah = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147868_aj = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147880_aa = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         boolean var50 = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149751_l();
         boolean var55 = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149751_l();
         boolean var60 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149751_l();
         boolean var65 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149751_l();
         if (!var60 && !var55) {
            this.field_147888_x = this.field_147819_N;
            this.field_147832_R = this.field_147883_ah;
         } else {
            this.field_147888_x = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149685_I();
            this.field_147832_R = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 - 1);
         }

         if (!var65 && !var55) {
            this.field_147884_z = this.field_147830_P;
            this.field_147826_T = this.field_147868_aj;
         } else {
            this.field_147884_z = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149685_I();
            this.field_147826_T = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 + 1);
         }

         if (!var60 && !var50) {
            this.field_147812_F = this.field_147819_N;
            this.field_147836_Z = this.field_147883_ah;
         } else {
            this.field_147812_F = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149685_I();
            this.field_147836_Z = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 - 1);
         }

         if (!var65 && !var50) {
            this.field_147821_H = this.field_147830_P;
            this.field_147881_ab = this.field_147868_aj;
         } else {
            this.field_147821_H = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149685_I();
            this.field_147881_ab = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 + 1);
         }

         if (this.field_147859_h <= 0.0) {
            ++var2;
         }

         int var70 = var14;
         if (this.field_147859_h <= 0.0 || !this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149662_c()) {
            var70 = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         }

         float var75 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         var12 = (this.field_147886_y + this.field_147884_z + var75 + this.field_147830_P) / 4.0F;
         var9 = (var75 + this.field_147830_P + this.field_147813_G + this.field_147821_H) / 4.0F;
         var10 = (this.field_147819_N + var75 + this.field_147812_F + this.field_147813_G) / 4.0F;
         var11 = (this.field_147888_x + this.field_147886_y + this.field_147819_N + var75) / 4.0F;
         this.field_147870_ao = this.func_147778_a(this.field_147831_S, this.field_147826_T, this.field_147868_aj, var70);
         this.field_147864_al = this.func_147778_a(this.field_147868_aj, this.field_147880_aa, this.field_147881_ab, var70);
         this.field_147874_am = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147880_aa, var70);
         this.field_147876_an = this.func_147778_a(this.field_147832_R, this.field_147831_S, this.field_147883_ah, var70);
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.6F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var78 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 4);
         this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, var78);
         if (field_147843_b && var78.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 + 1, var3, var4, 5)) {
         if (this.field_147861_i >= 1.0) {
            ++var2;
         }

         this.field_147810_D = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147820_O = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147829_Q = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         this.field_147824_K = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147835_X = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147866_ai = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147862_ak = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147885_ae = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         boolean var51 = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149751_l();
         boolean var56 = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149751_l();
         boolean var61 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149751_l();
         boolean var66 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149751_l();
         if (!var56 && !var66) {
            this.field_147816_C = this.field_147820_O;
            this.field_147827_W = this.field_147866_ai;
         } else {
            this.field_147816_C = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149685_I();
            this.field_147827_W = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 - 1);
         }

         if (!var56 && !var61) {
            this.field_147811_E = this.field_147829_Q;
            this.field_147834_Y = this.field_147862_ak;
         } else {
            this.field_147811_E = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149685_I();
            this.field_147834_Y = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 + 1);
         }

         if (!var51 && !var66) {
            this.field_147823_J = this.field_147820_O;
            this.field_147879_ad = this.field_147866_ai;
         } else {
            this.field_147823_J = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149685_I();
            this.field_147879_ad = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 - 1);
         }

         if (!var51 && !var61) {
            this.field_147818_M = this.field_147829_Q;
            this.field_147882_ag = this.field_147862_ak;
         } else {
            this.field_147818_M = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149685_I();
            this.field_147882_ag = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 + 1);
         }

         if (this.field_147861_i >= 1.0) {
            --var2;
         }

         int var71 = var14;
         if (this.field_147861_i >= 1.0 || !this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149662_c()) {
            var71 = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         }

         float var76 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         var9 = (this.field_147810_D + this.field_147811_E + var76 + this.field_147829_Q) / 4.0F;
         var10 = (this.field_147816_C + this.field_147810_D + this.field_147820_O + var76) / 4.0F;
         var11 = (this.field_147820_O + var76 + this.field_147823_J + this.field_147824_K) / 4.0F;
         var12 = (var76 + this.field_147829_Q + this.field_147824_K + this.field_147818_M) / 4.0F;
         this.field_147864_al = this.func_147778_a(this.field_147835_X, this.field_147834_Y, this.field_147862_ak, var71);
         this.field_147870_ao = this.func_147778_a(this.field_147862_ak, this.field_147885_ae, this.field_147882_ag, var71);
         this.field_147876_an = this.func_147778_a(this.field_147866_ai, this.field_147879_ad, this.field_147885_ae, var71);
         this.field_147874_am = this.func_147778_a(this.field_147827_W, this.field_147835_X, this.field_147866_ai, var71);
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.6F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var79 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 5);
         this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, var79);
         if (field_147843_b && var79.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      this.field_147863_w = false;
      return var8;
   }

   public boolean func_147808_b(Block var1, int var2, int var3, int var4, float var5, float var6, float var7) {
      this.field_147863_w = true;
      boolean var8 = false;
      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      boolean var13 = true;
      int var14 = var1.func_149677_c(this.field_147845_a, var2, var3, var4);
      Tessellator var15 = Tessellator.field_78398_a;
      var15.func_78380_c(983055);
      if (this.func_147745_b(var1).func_94215_i().equals("grass_top")) {
         var13 = false;
      } else if (this.func_147744_b()) {
         var13 = false;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 - 1, var4, 0)) {
         if (this.field_147855_j <= 0.0) {
            --var3;
         }

         this.field_147831_S = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147825_U = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147828_V = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147835_X = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         this.field_147886_y = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147814_A = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147815_B = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         this.field_147810_D = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         boolean var16 = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149751_l();
         boolean var17 = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149751_l();
         boolean var18 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149751_l();
         boolean var19 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149751_l();
         if (!var19 && !var17) {
            this.field_147888_x = this.field_147886_y;
            this.field_147832_R = this.field_147831_S;
         } else {
            this.field_147888_x = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149685_I();
            this.field_147832_R = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 - 1);
         }

         if (!var18 && !var17) {
            this.field_147884_z = this.field_147886_y;
            this.field_147826_T = this.field_147831_S;
         } else {
            this.field_147884_z = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149685_I();
            this.field_147826_T = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 + 1);
         }

         if (!var19 && !var16) {
            this.field_147816_C = this.field_147810_D;
            this.field_147827_W = this.field_147835_X;
         } else {
            this.field_147816_C = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149685_I();
            this.field_147827_W = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 - 1);
         }

         if (!var18 && !var16) {
            this.field_147811_E = this.field_147810_D;
            this.field_147834_Y = this.field_147835_X;
         } else {
            this.field_147811_E = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149685_I();
            this.field_147834_Y = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 + 1);
         }

         if (this.field_147855_j <= 0.0) {
            ++var3;
         }

         int var20 = var14;
         if (this.field_147855_j <= 0.0 || !this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149662_c()) {
            var20 = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         }

         float var21 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         var9 = (this.field_147884_z + this.field_147886_y + this.field_147815_B + var21) / 4.0F;
         var12 = (this.field_147815_B + var21 + this.field_147811_E + this.field_147810_D) / 4.0F;
         var11 = (var21 + this.field_147814_A + this.field_147810_D + this.field_147816_C) / 4.0F;
         var10 = (this.field_147886_y + this.field_147888_x + var21 + this.field_147814_A) / 4.0F;
         this.field_147864_al = this.func_147778_a(this.field_147826_T, this.field_147831_S, this.field_147828_V, var20);
         this.field_147870_ao = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147835_X, var20);
         this.field_147876_an = this.func_147778_a(this.field_147825_U, this.field_147835_X, this.field_147827_W, var20);
         this.field_147874_am = this.func_147778_a(this.field_147831_S, this.field_147832_R, this.field_147825_U, var20);
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.5F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.5F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.5F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.5F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.5F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.5F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         this.func_147768_a(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 0));
         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 + 1, var4, 1)) {
         if (this.field_147857_k >= 1.0) {
            ++var3;
         }

         this.field_147880_aa = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147885_ae = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         this.field_147878_ac = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147887_af = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147813_G = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147824_K = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         this.field_147822_I = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147817_L = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         boolean var54 = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149751_l();
         boolean var59 = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149751_l();
         boolean var64 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149751_l();
         boolean var69 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149751_l();
         if (!var69 && !var59) {
            this.field_147812_F = this.field_147813_G;
            this.field_147836_Z = this.field_147880_aa;
         } else {
            this.field_147812_F = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149685_I();
            this.field_147836_Z = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 - 1);
         }

         if (!var69 && !var54) {
            this.field_147823_J = this.field_147824_K;
            this.field_147879_ad = this.field_147885_ae;
         } else {
            this.field_147823_J = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149685_I();
            this.field_147879_ad = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 - 1);
         }

         if (!var64 && !var59) {
            this.field_147821_H = this.field_147813_G;
            this.field_147881_ab = this.field_147880_aa;
         } else {
            this.field_147821_H = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149685_I();
            this.field_147881_ab = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4 + 1);
         }

         if (!var64 && !var54) {
            this.field_147818_M = this.field_147824_K;
            this.field_147882_ag = this.field_147885_ae;
         } else {
            this.field_147818_M = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149685_I();
            this.field_147882_ag = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4 + 1);
         }

         if (this.field_147857_k >= 1.0) {
            --var3;
         }

         int var74 = var14;
         if (this.field_147857_k >= 1.0 || !this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149662_c()) {
            var74 = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         }

         float var79 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         var12 = (this.field_147821_H + this.field_147813_G + this.field_147817_L + var79) / 4.0F;
         var9 = (this.field_147817_L + var79 + this.field_147818_M + this.field_147824_K) / 4.0F;
         var10 = (var79 + this.field_147822_I + this.field_147824_K + this.field_147823_J) / 4.0F;
         var11 = (this.field_147813_G + this.field_147812_F + var79 + this.field_147822_I) / 4.0F;
         this.field_147870_ao = this.func_147778_a(this.field_147881_ab, this.field_147880_aa, this.field_147887_af, var74);
         this.field_147864_al = this.func_147778_a(this.field_147887_af, this.field_147882_ag, this.field_147885_ae, var74);
         this.field_147874_am = this.func_147778_a(this.field_147878_ac, this.field_147885_ae, this.field_147879_ad, var74);
         this.field_147876_an = this.func_147778_a(this.field_147880_aa, this.field_147836_Z, this.field_147878_ac, var74);
         this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5;
         this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6;
         this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7;
         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         this.func_147806_b(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 1));
         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 - 1, 2)) {
         if (this.field_147851_l <= 0.0) {
            --var4;
         }

         this.field_147819_N = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147814_A = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147822_I = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147820_O = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         this.field_147883_ah = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147825_U = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147878_ac = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         this.field_147866_ai = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         boolean var55 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149751_l();
         boolean var60 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149751_l();
         boolean var65 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149751_l();
         boolean var70 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149751_l();
         if (!var60 && !var70) {
            this.field_147888_x = this.field_147819_N;
            this.field_147832_R = this.field_147883_ah;
         } else {
            this.field_147888_x = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149685_I();
            this.field_147832_R = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 - 1, var4);
         }

         if (!var60 && !var65) {
            this.field_147812_F = this.field_147819_N;
            this.field_147836_Z = this.field_147883_ah;
         } else {
            this.field_147812_F = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149685_I();
            this.field_147836_Z = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 + 1, var4);
         }

         if (!var55 && !var70) {
            this.field_147816_C = this.field_147820_O;
            this.field_147827_W = this.field_147866_ai;
         } else {
            this.field_147816_C = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149685_I();
            this.field_147827_W = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 - 1, var4);
         }

         if (!var55 && !var65) {
            this.field_147823_J = this.field_147820_O;
            this.field_147879_ad = this.field_147866_ai;
         } else {
            this.field_147823_J = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149685_I();
            this.field_147879_ad = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 + 1, var4);
         }

         if (this.field_147851_l <= 0.0) {
            ++var4;
         }

         int var75 = var14;
         if (this.field_147851_l <= 0.0 || !this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149662_c()) {
            var75 = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         }

         float var80 = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         float var22 = (this.field_147819_N + this.field_147812_F + var80 + this.field_147822_I) / 4.0F;
         float var23 = (var80 + this.field_147822_I + this.field_147820_O + this.field_147823_J) / 4.0F;
         float var24 = (this.field_147814_A + var80 + this.field_147816_C + this.field_147820_O) / 4.0F;
         float var25 = (this.field_147888_x + this.field_147819_N + this.field_147814_A + var80) / 4.0F;
         var9 = (float)(
            (double)var22 * this.field_147857_k * (1.0 - this.field_147859_h)
               + (double)var23 * this.field_147857_k * this.field_147859_h
               + (double)var24 * (1.0 - this.field_147857_k) * this.field_147859_h
               + (double)var25 * (1.0 - this.field_147857_k) * (1.0 - this.field_147859_h)
         );
         var10 = (float)(
            (double)var22 * this.field_147857_k * (1.0 - this.field_147861_i)
               + (double)var23 * this.field_147857_k * this.field_147861_i
               + (double)var24 * (1.0 - this.field_147857_k) * this.field_147861_i
               + (double)var25 * (1.0 - this.field_147857_k) * (1.0 - this.field_147861_i)
         );
         var11 = (float)(
            (double)var22 * this.field_147855_j * (1.0 - this.field_147861_i)
               + (double)var23 * this.field_147855_j * this.field_147861_i
               + (double)var24 * (1.0 - this.field_147855_j) * this.field_147861_i
               + (double)var25 * (1.0 - this.field_147855_j) * (1.0 - this.field_147861_i)
         );
         var12 = (float)(
            (double)var22 * this.field_147855_j * (1.0 - this.field_147859_h)
               + (double)var23 * this.field_147855_j * this.field_147859_h
               + (double)var24 * (1.0 - this.field_147855_j) * this.field_147859_h
               + (double)var25 * (1.0 - this.field_147855_j) * (1.0 - this.field_147859_h)
         );
         int var26 = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147878_ac, var75);
         int var27 = this.func_147778_a(this.field_147878_ac, this.field_147866_ai, this.field_147879_ad, var75);
         int var28 = this.func_147778_a(this.field_147825_U, this.field_147827_W, this.field_147866_ai, var75);
         int var29 = this.func_147778_a(this.field_147832_R, this.field_147883_ah, this.field_147825_U, var75);
         this.field_147864_al = this.func_147727_a(
            var26,
            var27,
            var28,
            var29,
            this.field_147857_k * (1.0 - this.field_147859_h),
            this.field_147857_k * this.field_147859_h,
            (1.0 - this.field_147857_k) * this.field_147859_h,
            (1.0 - this.field_147857_k) * (1.0 - this.field_147859_h)
         );
         this.field_147874_am = this.func_147727_a(
            var26,
            var27,
            var28,
            var29,
            this.field_147857_k * (1.0 - this.field_147861_i),
            this.field_147857_k * this.field_147861_i,
            (1.0 - this.field_147857_k) * this.field_147861_i,
            (1.0 - this.field_147857_k) * (1.0 - this.field_147861_i)
         );
         this.field_147876_an = this.func_147727_a(
            var26,
            var27,
            var28,
            var29,
            this.field_147855_j * (1.0 - this.field_147861_i),
            this.field_147855_j * this.field_147861_i,
            (1.0 - this.field_147855_j) * this.field_147861_i,
            (1.0 - this.field_147855_j) * (1.0 - this.field_147861_i)
         );
         this.field_147870_ao = this.func_147727_a(
            var26,
            var27,
            var28,
            var29,
            this.field_147855_j * (1.0 - this.field_147859_h),
            this.field_147855_j * this.field_147859_h,
            (1.0 - this.field_147855_j) * this.field_147859_h,
            (1.0 - this.field_147855_j) * (1.0 - this.field_147859_h)
         );
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.8F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var84 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 2);
         this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, var84);
         if (field_147843_b && var84.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 + 1, 3)) {
         if (this.field_147853_m >= 1.0) {
            ++var4;
         }

         this.field_147830_P = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         this.field_147829_Q = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         this.field_147815_B = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147817_L = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147868_aj = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         this.field_147862_ak = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         this.field_147828_V = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147887_af = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         boolean var56 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149751_l();
         boolean var61 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149751_l();
         boolean var66 = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149751_l();
         boolean var71 = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149751_l();
         if (!var61 && !var71) {
            this.field_147884_z = this.field_147830_P;
            this.field_147826_T = this.field_147868_aj;
         } else {
            this.field_147884_z = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149685_I();
            this.field_147826_T = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 - 1, var4);
         }

         if (!var61 && !var66) {
            this.field_147821_H = this.field_147830_P;
            this.field_147881_ab = this.field_147868_aj;
         } else {
            this.field_147821_H = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149685_I();
            this.field_147881_ab = var1.func_149677_c(this.field_147845_a, var2 - 1, var3 + 1, var4);
         }

         if (!var56 && !var71) {
            this.field_147811_E = this.field_147829_Q;
            this.field_147834_Y = this.field_147862_ak;
         } else {
            this.field_147811_E = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149685_I();
            this.field_147834_Y = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 - 1, var4);
         }

         if (!var56 && !var66) {
            this.field_147818_M = this.field_147829_Q;
            this.field_147882_ag = this.field_147862_ak;
         } else {
            this.field_147818_M = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149685_I();
            this.field_147882_ag = var1.func_149677_c(this.field_147845_a, var2 + 1, var3 + 1, var4);
         }

         if (this.field_147853_m >= 1.0) {
            --var4;
         }

         int var76 = var14;
         if (this.field_147853_m >= 1.0 || !this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149662_c()) {
            var76 = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         }

         float var81 = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         float var85 = (this.field_147830_P + this.field_147821_H + var81 + this.field_147817_L) / 4.0F;
         float var91 = (var81 + this.field_147817_L + this.field_147829_Q + this.field_147818_M) / 4.0F;
         float var94 = (this.field_147815_B + var81 + this.field_147811_E + this.field_147829_Q) / 4.0F;
         float var97 = (this.field_147884_z + this.field_147830_P + this.field_147815_B + var81) / 4.0F;
         var9 = (float)(
            (double)var85 * this.field_147857_k * (1.0 - this.field_147859_h)
               + (double)var91 * this.field_147857_k * this.field_147859_h
               + (double)var94 * (1.0 - this.field_147857_k) * this.field_147859_h
               + (double)var97 * (1.0 - this.field_147857_k) * (1.0 - this.field_147859_h)
         );
         var10 = (float)(
            (double)var85 * this.field_147855_j * (1.0 - this.field_147859_h)
               + (double)var91 * this.field_147855_j * this.field_147859_h
               + (double)var94 * (1.0 - this.field_147855_j) * this.field_147859_h
               + (double)var97 * (1.0 - this.field_147855_j) * (1.0 - this.field_147859_h)
         );
         var11 = (float)(
            (double)var85 * this.field_147855_j * (1.0 - this.field_147861_i)
               + (double)var91 * this.field_147855_j * this.field_147861_i
               + (double)var94 * (1.0 - this.field_147855_j) * this.field_147861_i
               + (double)var97 * (1.0 - this.field_147855_j) * (1.0 - this.field_147861_i)
         );
         var12 = (float)(
            (double)var85 * this.field_147857_k * (1.0 - this.field_147861_i)
               + (double)var91 * this.field_147857_k * this.field_147861_i
               + (double)var94 * (1.0 - this.field_147857_k) * this.field_147861_i
               + (double)var97 * (1.0 - this.field_147857_k) * (1.0 - this.field_147861_i)
         );
         int var100 = this.func_147778_a(this.field_147868_aj, this.field_147881_ab, this.field_147887_af, var76);
         int var103 = this.func_147778_a(this.field_147887_af, this.field_147862_ak, this.field_147882_ag, var76);
         int var106 = this.func_147778_a(this.field_147828_V, this.field_147834_Y, this.field_147862_ak, var76);
         int var109 = this.func_147778_a(this.field_147826_T, this.field_147868_aj, this.field_147828_V, var76);
         this.field_147864_al = this.func_147727_a(
            var100,
            var109,
            var106,
            var103,
            this.field_147857_k * (1.0 - this.field_147859_h),
            (1.0 - this.field_147857_k) * (1.0 - this.field_147859_h),
            (1.0 - this.field_147857_k) * this.field_147859_h,
            this.field_147857_k * this.field_147859_h
         );
         this.field_147874_am = this.func_147727_a(
            var100,
            var109,
            var106,
            var103,
            this.field_147855_j * (1.0 - this.field_147859_h),
            (1.0 - this.field_147855_j) * (1.0 - this.field_147859_h),
            (1.0 - this.field_147855_j) * this.field_147859_h,
            this.field_147855_j * this.field_147859_h
         );
         this.field_147876_an = this.func_147727_a(
            var100,
            var109,
            var106,
            var103,
            this.field_147855_j * (1.0 - this.field_147861_i),
            (1.0 - this.field_147855_j) * (1.0 - this.field_147861_i),
            (1.0 - this.field_147855_j) * this.field_147861_i,
            this.field_147855_j * this.field_147861_i
         );
         this.field_147870_ao = this.func_147727_a(
            var100,
            var109,
            var106,
            var103,
            this.field_147857_k * (1.0 - this.field_147861_i),
            (1.0 - this.field_147857_k) * (1.0 - this.field_147861_i),
            (1.0 - this.field_147857_k) * this.field_147861_i,
            this.field_147857_k * this.field_147861_i
         );
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.8F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.8F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.8F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.8F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var86 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3);
         this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, var86);
         if (field_147843_b && var86.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 - 1, var3, var4, 4)) {
         if (this.field_147859_h <= 0.0) {
            --var2;
         }

         this.field_147886_y = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147819_N = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147830_P = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         this.field_147813_G = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147831_S = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147883_ah = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147868_aj = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147880_aa = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         boolean var57 = this.field_147845_a.func_147439_a(var2 - 1, var3 + 1, var4).func_149751_l();
         boolean var62 = this.field_147845_a.func_147439_a(var2 - 1, var3 - 1, var4).func_149751_l();
         boolean var67 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 - 1).func_149751_l();
         boolean var72 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4 + 1).func_149751_l();
         if (!var67 && !var62) {
            this.field_147888_x = this.field_147819_N;
            this.field_147832_R = this.field_147883_ah;
         } else {
            this.field_147888_x = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149685_I();
            this.field_147832_R = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 - 1);
         }

         if (!var72 && !var62) {
            this.field_147884_z = this.field_147830_P;
            this.field_147826_T = this.field_147868_aj;
         } else {
            this.field_147884_z = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149685_I();
            this.field_147826_T = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 + 1);
         }

         if (!var67 && !var57) {
            this.field_147812_F = this.field_147819_N;
            this.field_147836_Z = this.field_147883_ah;
         } else {
            this.field_147812_F = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149685_I();
            this.field_147836_Z = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 - 1);
         }

         if (!var72 && !var57) {
            this.field_147821_H = this.field_147830_P;
            this.field_147881_ab = this.field_147868_aj;
         } else {
            this.field_147821_H = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149685_I();
            this.field_147881_ab = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 + 1);
         }

         if (this.field_147859_h <= 0.0) {
            ++var2;
         }

         int var77 = var14;
         if (this.field_147859_h <= 0.0 || !this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149662_c()) {
            var77 = var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4);
         }

         float var82 = this.field_147845_a.func_147439_a(var2 - 1, var3, var4).func_149685_I();
         float var87 = (this.field_147886_y + this.field_147884_z + var82 + this.field_147830_P) / 4.0F;
         float var92 = (var82 + this.field_147830_P + this.field_147813_G + this.field_147821_H) / 4.0F;
         float var95 = (this.field_147819_N + var82 + this.field_147812_F + this.field_147813_G) / 4.0F;
         float var98 = (this.field_147888_x + this.field_147886_y + this.field_147819_N + var82) / 4.0F;
         var9 = (float)(
            (double)var92 * this.field_147857_k * this.field_147853_m
               + (double)var95 * this.field_147857_k * (1.0 - this.field_147853_m)
               + (double)var98 * (1.0 - this.field_147857_k) * (1.0 - this.field_147853_m)
               + (double)var87 * (1.0 - this.field_147857_k) * this.field_147853_m
         );
         var10 = (float)(
            (double)var92 * this.field_147857_k * this.field_147851_l
               + (double)var95 * this.field_147857_k * (1.0 - this.field_147851_l)
               + (double)var98 * (1.0 - this.field_147857_k) * (1.0 - this.field_147851_l)
               + (double)var87 * (1.0 - this.field_147857_k) * this.field_147851_l
         );
         var11 = (float)(
            (double)var92 * this.field_147855_j * this.field_147851_l
               + (double)var95 * this.field_147855_j * (1.0 - this.field_147851_l)
               + (double)var98 * (1.0 - this.field_147855_j) * (1.0 - this.field_147851_l)
               + (double)var87 * (1.0 - this.field_147855_j) * this.field_147851_l
         );
         var12 = (float)(
            (double)var92 * this.field_147855_j * this.field_147853_m
               + (double)var95 * this.field_147855_j * (1.0 - this.field_147853_m)
               + (double)var98 * (1.0 - this.field_147855_j) * (1.0 - this.field_147853_m)
               + (double)var87 * (1.0 - this.field_147855_j) * this.field_147853_m
         );
         int var101 = this.func_147778_a(this.field_147831_S, this.field_147826_T, this.field_147868_aj, var77);
         int var104 = this.func_147778_a(this.field_147868_aj, this.field_147880_aa, this.field_147881_ab, var77);
         int var107 = this.func_147778_a(this.field_147883_ah, this.field_147836_Z, this.field_147880_aa, var77);
         int var110 = this.func_147778_a(this.field_147832_R, this.field_147831_S, this.field_147883_ah, var77);
         this.field_147864_al = this.func_147727_a(
            var104,
            var107,
            var110,
            var101,
            this.field_147857_k * this.field_147853_m,
            this.field_147857_k * (1.0 - this.field_147853_m),
            (1.0 - this.field_147857_k) * (1.0 - this.field_147853_m),
            (1.0 - this.field_147857_k) * this.field_147853_m
         );
         this.field_147874_am = this.func_147727_a(
            var104,
            var107,
            var110,
            var101,
            this.field_147857_k * this.field_147851_l,
            this.field_147857_k * (1.0 - this.field_147851_l),
            (1.0 - this.field_147857_k) * (1.0 - this.field_147851_l),
            (1.0 - this.field_147857_k) * this.field_147851_l
         );
         this.field_147876_an = this.func_147727_a(
            var104,
            var107,
            var110,
            var101,
            this.field_147855_j * this.field_147851_l,
            this.field_147855_j * (1.0 - this.field_147851_l),
            (1.0 - this.field_147855_j) * (1.0 - this.field_147851_l),
            (1.0 - this.field_147855_j) * this.field_147851_l
         );
         this.field_147870_ao = this.func_147727_a(
            var104,
            var107,
            var110,
            var101,
            this.field_147855_j * this.field_147853_m,
            this.field_147855_j * (1.0 - this.field_147853_m),
            (1.0 - this.field_147855_j) * (1.0 - this.field_147853_m),
            (1.0 - this.field_147855_j) * this.field_147853_m
         );
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.6F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var88 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 4);
         this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, var88);
         if (field_147843_b && var88.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 + 1, var3, var4, 5)) {
         if (this.field_147861_i >= 1.0) {
            ++var2;
         }

         this.field_147810_D = this.field_147845_a.func_147439_a(var2, var3 - 1, var4).func_149685_I();
         this.field_147820_O = this.field_147845_a.func_147439_a(var2, var3, var4 - 1).func_149685_I();
         this.field_147829_Q = this.field_147845_a.func_147439_a(var2, var3, var4 + 1).func_149685_I();
         this.field_147824_K = this.field_147845_a.func_147439_a(var2, var3 + 1, var4).func_149685_I();
         this.field_147835_X = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4);
         this.field_147866_ai = var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1);
         this.field_147862_ak = var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1);
         this.field_147885_ae = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4);
         boolean var58 = this.field_147845_a.func_147439_a(var2 + 1, var3 + 1, var4).func_149751_l();
         boolean var63 = this.field_147845_a.func_147439_a(var2 + 1, var3 - 1, var4).func_149751_l();
         boolean var68 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 + 1).func_149751_l();
         boolean var73 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4 - 1).func_149751_l();
         if (!var63 && !var73) {
            this.field_147816_C = this.field_147820_O;
            this.field_147827_W = this.field_147866_ai;
         } else {
            this.field_147816_C = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 - 1).func_149685_I();
            this.field_147827_W = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 - 1);
         }

         if (!var63 && !var68) {
            this.field_147811_E = this.field_147829_Q;
            this.field_147834_Y = this.field_147862_ak;
         } else {
            this.field_147811_E = this.field_147845_a.func_147439_a(var2, var3 - 1, var4 + 1).func_149685_I();
            this.field_147834_Y = var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4 + 1);
         }

         if (!var58 && !var73) {
            this.field_147823_J = this.field_147820_O;
            this.field_147879_ad = this.field_147866_ai;
         } else {
            this.field_147823_J = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 - 1).func_149685_I();
            this.field_147879_ad = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 - 1);
         }

         if (!var58 && !var68) {
            this.field_147818_M = this.field_147829_Q;
            this.field_147882_ag = this.field_147862_ak;
         } else {
            this.field_147818_M = this.field_147845_a.func_147439_a(var2, var3 + 1, var4 + 1).func_149685_I();
            this.field_147882_ag = var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4 + 1);
         }

         if (this.field_147861_i >= 1.0) {
            --var2;
         }

         int var78 = var14;
         if (this.field_147861_i >= 1.0 || !this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149662_c()) {
            var78 = var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4);
         }

         float var83 = this.field_147845_a.func_147439_a(var2 + 1, var3, var4).func_149685_I();
         float var89 = (this.field_147810_D + this.field_147811_E + var83 + this.field_147829_Q) / 4.0F;
         float var93 = (this.field_147816_C + this.field_147810_D + this.field_147820_O + var83) / 4.0F;
         float var96 = (this.field_147820_O + var83 + this.field_147823_J + this.field_147824_K) / 4.0F;
         float var99 = (var83 + this.field_147829_Q + this.field_147824_K + this.field_147818_M) / 4.0F;
         var9 = (float)(
            (double)var89 * (1.0 - this.field_147855_j) * this.field_147853_m
               + (double)var93 * (1.0 - this.field_147855_j) * (1.0 - this.field_147853_m)
               + (double)var96 * this.field_147855_j * (1.0 - this.field_147853_m)
               + (double)var99 * this.field_147855_j * this.field_147853_m
         );
         var10 = (float)(
            (double)var89 * (1.0 - this.field_147855_j) * this.field_147851_l
               + (double)var93 * (1.0 - this.field_147855_j) * (1.0 - this.field_147851_l)
               + (double)var96 * this.field_147855_j * (1.0 - this.field_147851_l)
               + (double)var99 * this.field_147855_j * this.field_147851_l
         );
         var11 = (float)(
            (double)var89 * (1.0 - this.field_147857_k) * this.field_147851_l
               + (double)var93 * (1.0 - this.field_147857_k) * (1.0 - this.field_147851_l)
               + (double)var96 * this.field_147857_k * (1.0 - this.field_147851_l)
               + (double)var99 * this.field_147857_k * this.field_147851_l
         );
         var12 = (float)(
            (double)var89 * (1.0 - this.field_147857_k) * this.field_147853_m
               + (double)var93 * (1.0 - this.field_147857_k) * (1.0 - this.field_147853_m)
               + (double)var96 * this.field_147857_k * (1.0 - this.field_147853_m)
               + (double)var99 * this.field_147857_k * this.field_147853_m
         );
         int var102 = this.func_147778_a(this.field_147835_X, this.field_147834_Y, this.field_147862_ak, var78);
         int var105 = this.func_147778_a(this.field_147862_ak, this.field_147885_ae, this.field_147882_ag, var78);
         int var108 = this.func_147778_a(this.field_147866_ai, this.field_147879_ad, this.field_147885_ae, var78);
         int var111 = this.func_147778_a(this.field_147827_W, this.field_147835_X, this.field_147866_ai, var78);
         this.field_147864_al = this.func_147727_a(
            var102,
            var111,
            var108,
            var105,
            (1.0 - this.field_147855_j) * this.field_147853_m,
            (1.0 - this.field_147855_j) * (1.0 - this.field_147853_m),
            this.field_147855_j * (1.0 - this.field_147853_m),
            this.field_147855_j * this.field_147853_m
         );
         this.field_147874_am = this.func_147727_a(
            var102,
            var111,
            var108,
            var105,
            (1.0 - this.field_147855_j) * this.field_147851_l,
            (1.0 - this.field_147855_j) * (1.0 - this.field_147851_l),
            this.field_147855_j * (1.0 - this.field_147851_l),
            this.field_147855_j * this.field_147851_l
         );
         this.field_147876_an = this.func_147727_a(
            var102,
            var111,
            var108,
            var105,
            (1.0 - this.field_147857_k) * this.field_147851_l,
            (1.0 - this.field_147857_k) * (1.0 - this.field_147851_l),
            this.field_147857_k * (1.0 - this.field_147851_l),
            this.field_147857_k * this.field_147851_l
         );
         this.field_147870_ao = this.func_147727_a(
            var102,
            var111,
            var108,
            var105,
            (1.0 - this.field_147857_k) * this.field_147853_m,
            (1.0 - this.field_147857_k) * (1.0 - this.field_147853_m),
            this.field_147857_k * (1.0 - this.field_147853_m),
            this.field_147857_k * this.field_147853_m
         );
         if (var13) {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = var5 * 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = var6 * 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = var7 * 0.6F;
         } else {
            this.field_147872_ap = this.field_147852_aq = this.field_147850_ar = this.field_147848_as = 0.6F;
            this.field_147846_at = this.field_147860_au = this.field_147858_av = this.field_147856_aw = 0.6F;
            this.field_147854_ax = this.field_147841_ay = this.field_147839_az = this.field_147833_aA = 0.6F;
         }

         this.field_147872_ap *= var9;
         this.field_147846_at *= var9;
         this.field_147854_ax *= var9;
         this.field_147852_aq *= var10;
         this.field_147860_au *= var10;
         this.field_147841_ay *= var10;
         this.field_147850_ar *= var11;
         this.field_147858_av *= var11;
         this.field_147839_az *= var11;
         this.field_147848_as *= var12;
         this.field_147856_aw *= var12;
         this.field_147833_aA *= var12;
         IIcon var90 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 5);
         this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, var90);
         if (field_147843_b && var90.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            this.field_147872_ap *= var5;
            this.field_147852_aq *= var5;
            this.field_147850_ar *= var5;
            this.field_147848_as *= var5;
            this.field_147846_at *= var6;
            this.field_147860_au *= var6;
            this.field_147858_av *= var6;
            this.field_147856_aw *= var6;
            this.field_147854_ax *= var7;
            this.field_147841_ay *= var7;
            this.field_147839_az *= var7;
            this.field_147833_aA *= var7;
            this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var8 = true;
      }

      this.field_147863_w = false;
      return var8;
   }

   private int func_147778_a(int var1, int var2, int var3, int var4) {
      if (var1 == 0) {
         var1 = var4;
      }

      if (var2 == 0) {
         var2 = var4;
      }

      if (var3 == 0) {
         var3 = var4;
      }

      return var1 + var2 + var3 + var4 >> 2 & 16711935;
   }

   private int func_147727_a(int var1, int var2, int var3, int var4, double var5, double var7, double var9, double var11) {
      int var13 = (int)(
            (double)(var1 >> 16 & 0xFF) * var5 + (double)(var2 >> 16 & 0xFF) * var7 + (double)(var3 >> 16 & 0xFF) * var9 + (double)(var4 >> 16 & 0xFF) * var11
         )
         & 0xFF;
      int var14 = (int)((double)(var1 & 0xFF) * var5 + (double)(var2 & 0xFF) * var7 + (double)(var3 & 0xFF) * var9 + (double)(var4 & 0xFF) * var11) & 0xFF;
      return var13 << 16 | var14;
   }

   public boolean func_147736_d(Block var1, int var2, int var3, int var4, float var5, float var6, float var7) {
      this.field_147863_w = false;
      Tessellator var8 = Tessellator.field_78398_a;
      boolean var9 = false;
      float var10 = 0.5F;
      float var11 = 1.0F;
      float var12 = 0.8F;
      float var13 = 0.6F;
      float var14 = var11 * var5;
      float var15 = var11 * var6;
      float var16 = var11 * var7;
      float var17 = var10;
      float var18 = var12;
      float var19 = var13;
      float var20 = var10;
      float var21 = var12;
      float var22 = var13;
      float var23 = var10;
      float var24 = var12;
      float var25 = var13;
      if (var1 != Blocks.field_150349_c) {
         var17 = var10 * var5;
         var18 = var12 * var5;
         var19 = var13 * var5;
         var20 = var10 * var6;
         var21 = var12 * var6;
         var22 = var13 * var6;
         var23 = var10 * var7;
         var24 = var12 * var7;
         var25 = var13 * var7;
      }

      int var26 = var1.func_149677_c(this.field_147845_a, var2, var3, var4);
      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 - 1, var4, 0)) {
         var8.func_78380_c(this.field_147855_j > 0.0 ? var26 : var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4));
         var8.func_78386_a(var17, var20, var23);
         this.func_147768_a(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 0));
         var9 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 + 1, var4, 1)) {
         var8.func_78380_c(this.field_147857_k < 1.0 ? var26 : var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4));
         var8.func_78386_a(var14, var15, var16);
         this.func_147806_b(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 1));
         var9 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 - 1, 2)) {
         var8.func_78380_c(this.field_147851_l > 0.0 ? var26 : var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1));
         var8.func_78386_a(var18, var21, var24);
         IIcon var27 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 2);
         this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, var27);
         if (field_147843_b && var27.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            var8.func_78386_a(var18 * var5, var21 * var6, var24 * var7);
            this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var9 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3, var4 + 1, 3)) {
         var8.func_78380_c(this.field_147853_m < 1.0 ? var26 : var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1));
         var8.func_78386_a(var18, var21, var24);
         IIcon var28 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3);
         this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, var28);
         if (field_147843_b && var28.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            var8.func_78386_a(var18 * var5, var21 * var6, var24 * var7);
            this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var9 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 - 1, var3, var4, 4)) {
         var8.func_78380_c(this.field_147859_h > 0.0 ? var26 : var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4));
         var8.func_78386_a(var19, var22, var25);
         IIcon var29 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 4);
         this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, var29);
         if (field_147843_b && var29.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            var8.func_78386_a(var19 * var5, var22 * var6, var25 * var7);
            this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var9 = true;
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2 + 1, var3, var4, 5)) {
         var8.func_78380_c(this.field_147861_i < 1.0 ? var26 : var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4));
         var8.func_78386_a(var19, var22, var25);
         IIcon var30 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 5);
         this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, var30);
         if (field_147843_b && var30.func_94215_i().equals("grass_side") && !this.func_147744_b()) {
            var8.func_78386_a(var19 * var5, var22 * var6, var25 * var7);
            this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, BlockGrass.func_149990_e());
         }

         var9 = true;
      }

      return var9;
   }

   private boolean func_147772_a(BlockCocoa var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      var5.func_78386_a(1.0F, 1.0F, 1.0F);
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      int var7 = BlockDirectional.func_149895_l(var6);
      int var8 = BlockCocoa.func_149987_c(var6);
      IIcon var9 = var1.func_149988_b(var8);
      int var10 = 4 + var8 * 2;
      int var11 = 5 + var8 * 2;
      double var12 = 15.0 - (double)var10;
      double var14 = 15.0;
      double var16 = 4.0;
      double var18 = 4.0 + (double)var11;
      double var20 = (double)var9.func_94214_a(var12);
      double var22 = (double)var9.func_94214_a(var14);
      double var24 = (double)var9.func_94207_b(var16);
      double var26 = (double)var9.func_94207_b(var18);
      double var28 = 0.0;
      double var30 = 0.0;
      switch(var7) {
         case 0:
            var28 = 8.0 - (double)(var10 / 2);
            var30 = 15.0 - (double)var10;
            break;
         case 1:
            var28 = 1.0;
            var30 = 8.0 - (double)(var10 / 2);
            break;
         case 2:
            var28 = 8.0 - (double)(var10 / 2);
            var30 = 1.0;
            break;
         case 3:
            var28 = 15.0 - (double)var10;
            var30 = 8.0 - (double)(var10 / 2);
      }

      double var32 = (double)var2 + var28 / 16.0;
      double var34 = (double)var2 + (var28 + (double)var10) / 16.0;
      double var36 = (double)var3 + (12.0 - (double)var11) / 16.0;
      double var38 = (double)var3 + 0.75;
      double var40 = (double)var4 + var30 / 16.0;
      double var42 = (double)var4 + (var30 + (double)var10) / 16.0;
      var5.func_78374_a(var32, var36, var40, var20, var26);
      var5.func_78374_a(var32, var36, var42, var22, var26);
      var5.func_78374_a(var32, var38, var42, var22, var24);
      var5.func_78374_a(var32, var38, var40, var20, var24);
      var5.func_78374_a(var34, var36, var42, var20, var26);
      var5.func_78374_a(var34, var36, var40, var22, var26);
      var5.func_78374_a(var34, var38, var40, var22, var24);
      var5.func_78374_a(var34, var38, var42, var20, var24);
      var5.func_78374_a(var34, var36, var40, var20, var26);
      var5.func_78374_a(var32, var36, var40, var22, var26);
      var5.func_78374_a(var32, var38, var40, var22, var24);
      var5.func_78374_a(var34, var38, var40, var20, var24);
      var5.func_78374_a(var32, var36, var42, var20, var26);
      var5.func_78374_a(var34, var36, var42, var22, var26);
      var5.func_78374_a(var34, var38, var42, var22, var24);
      var5.func_78374_a(var32, var38, var42, var20, var24);
      int var44 = var10;
      if (var8 >= 2) {
         var44 = var10 - 1;
      }

      var20 = (double)var9.func_94209_e();
      var22 = (double)var9.func_94214_a((double)var44);
      var24 = (double)var9.func_94206_g();
      var26 = (double)var9.func_94207_b((double)var44);
      var5.func_78374_a(var32, var38, var42, var20, var26);
      var5.func_78374_a(var34, var38, var42, var22, var26);
      var5.func_78374_a(var34, var38, var40, var22, var24);
      var5.func_78374_a(var32, var38, var40, var20, var24);
      var5.func_78374_a(var32, var36, var40, var20, var24);
      var5.func_78374_a(var34, var36, var40, var22, var24);
      var5.func_78374_a(var34, var36, var42, var22, var26);
      var5.func_78374_a(var32, var36, var42, var20, var26);
      var20 = (double)var9.func_94214_a(12.0);
      var22 = (double)var9.func_94212_f();
      var24 = (double)var9.func_94206_g();
      var26 = (double)var9.func_94207_b(4.0);
      var28 = 8.0;
      var30 = 0.0;
      switch(var7) {
         case 0:
            var28 = 8.0;
            var30 = 12.0;
            double var63 = var20;
            var20 = var22;
            var22 = var63;
            break;
         case 1:
            var28 = 0.0;
            var30 = 8.0;
            break;
         case 2:
            var28 = 8.0;
            var30 = 0.0;
            break;
         case 3:
            var28 = 12.0;
            var30 = 8.0;
            double var45 = var20;
            var20 = var22;
            var22 = var45;
      }

      var32 = (double)var2 + var28 / 16.0;
      var34 = (double)var2 + (var28 + 4.0) / 16.0;
      var36 = (double)var3 + 0.75;
      var38 = (double)var3 + 1.0;
      var40 = (double)var4 + var30 / 16.0;
      var42 = (double)var4 + (var30 + 4.0) / 16.0;
      if (var7 == 2 || var7 == 0) {
         var5.func_78374_a(var32, var36, var40, var22, var26);
         var5.func_78374_a(var32, var36, var42, var20, var26);
         var5.func_78374_a(var32, var38, var42, var20, var24);
         var5.func_78374_a(var32, var38, var40, var22, var24);
         var5.func_78374_a(var32, var36, var42, var20, var26);
         var5.func_78374_a(var32, var36, var40, var22, var26);
         var5.func_78374_a(var32, var38, var40, var22, var24);
         var5.func_78374_a(var32, var38, var42, var20, var24);
      } else if (var7 == 1 || var7 == 3) {
         var5.func_78374_a(var34, var36, var40, var20, var26);
         var5.func_78374_a(var32, var36, var40, var22, var26);
         var5.func_78374_a(var32, var38, var40, var22, var24);
         var5.func_78374_a(var34, var38, var40, var20, var24);
         var5.func_78374_a(var32, var36, var40, var22, var26);
         var5.func_78374_a(var34, var36, var40, var20, var26);
         var5.func_78374_a(var34, var38, var40, var20, var24);
         var5.func_78374_a(var32, var38, var40, var22, var24);
      }

      return true;
   }

   private boolean func_147797_a(BlockBeacon var1, int var2, int var3, int var4) {
      float var5 = 0.1875F;
      this.func_147757_a(this.func_147745_b(Blocks.field_150359_w));
      this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      this.func_147784_q(var1, var2, var3, var4);
      this.field_147837_f = true;
      this.func_147757_a(this.func_147745_b(Blocks.field_150343_Z));
      this.func_147782_a(0.125, 0.0062500000931322575, 0.125, 0.875, (double)var5, 0.875);
      this.func_147784_q(var1, var2, var3, var4);
      this.func_147757_a(this.func_147745_b(Blocks.field_150461_bJ));
      this.func_147782_a(0.1875, (double)var5, 0.1875, 0.8125, 0.875, 0.8125);
      this.func_147784_q(var1, var2, var3, var4);
      this.field_147837_f = false;
      this.func_147771_a();
      return true;
   }

   public boolean func_147755_t(Block var1, int var2, int var3, int var4) {
      int var5 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var6 = (float)(var5 >> 16 & 0xFF) / 255.0F;
      float var7 = (float)(var5 >> 8 & 0xFF) / 255.0F;
      float var8 = (float)(var5 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
         float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
         float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
         var6 = var9;
         var7 = var10;
         var8 = var11;
      }

      return this.func_147754_e(var1, var2, var3, var4, var6, var7, var8);
   }

   public boolean func_147754_e(Block var1, int var2, int var3, int var4, float var5, float var6, float var7) {
      Tessellator var8 = Tessellator.field_78398_a;
      boolean var9 = false;
      float var10 = 0.5F;
      float var11 = 1.0F;
      float var12 = 0.8F;
      float var13 = 0.6F;
      float var14 = var10 * var5;
      float var15 = var11 * var5;
      float var16 = var12 * var5;
      float var17 = var13 * var5;
      float var18 = var10 * var6;
      float var19 = var11 * var6;
      float var20 = var12 * var6;
      float var21 = var13 * var6;
      float var22 = var10 * var7;
      float var23 = var11 * var7;
      float var24 = var12 * var7;
      float var25 = var13 * var7;
      float var26 = 0.0625F;
      int var27 = var1.func_149677_c(this.field_147845_a, var2, var3, var4);
      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 - 1, var4, 0)) {
         var8.func_78380_c(this.field_147855_j > 0.0 ? var27 : var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4));
         var8.func_78386_a(var14, var18, var22);
         this.func_147768_a(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 0));
      }

      if (this.field_147837_f || var1.func_149646_a(this.field_147845_a, var2, var3 + 1, var4, 1)) {
         var8.func_78380_c(this.field_147857_k < 1.0 ? var27 : var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4));
         var8.func_78386_a(var15, var19, var23);
         this.func_147806_b(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 1));
      }

      var8.func_78380_c(var27);
      var8.func_78386_a(var16, var20, var24);
      var8.func_78372_c(0.0F, 0.0F, var26);
      this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 2));
      var8.func_78372_c(0.0F, 0.0F, -var26);
      var8.func_78372_c(0.0F, 0.0F, -var26);
      this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3));
      var8.func_78372_c(0.0F, 0.0F, var26);
      var8.func_78386_a(var17, var21, var25);
      var8.func_78372_c(var26, 0.0F, 0.0F);
      this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 4));
      var8.func_78372_c(-var26, 0.0F, 0.0F);
      var8.func_78372_c(-var26, 0.0F, 0.0F);
      this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 5));
      var8.func_78372_c(var26, 0.0F, 0.0F);
      return true;
   }

   public boolean func_147735_a(BlockFence var1, int var2, int var3, int var4) {
      boolean var5 = false;
      float var6 = 0.375F;
      float var7 = 0.625F;
      this.func_147782_a((double)var6, 0.0, (double)var6, (double)var7, 1.0, (double)var7);
      this.func_147784_q(var1, var2, var3, var4);
      var5 = true;
      boolean var8 = false;
      boolean var9 = false;
      if (var1.func_149826_e(this.field_147845_a, var2 - 1, var3, var4) || var1.func_149826_e(this.field_147845_a, var2 + 1, var3, var4)) {
         var8 = true;
      }

      if (var1.func_149826_e(this.field_147845_a, var2, var3, var4 - 1) || var1.func_149826_e(this.field_147845_a, var2, var3, var4 + 1)) {
         var9 = true;
      }

      boolean var10 = var1.func_149826_e(this.field_147845_a, var2 - 1, var3, var4);
      boolean var11 = var1.func_149826_e(this.field_147845_a, var2 + 1, var3, var4);
      boolean var12 = var1.func_149826_e(this.field_147845_a, var2, var3, var4 - 1);
      boolean var13 = var1.func_149826_e(this.field_147845_a, var2, var3, var4 + 1);
      if (!var8 && !var9) {
         var8 = true;
      }

      var6 = 0.4375F;
      var7 = 0.5625F;
      float var14 = 0.75F;
      float var15 = 0.9375F;
      float var16 = var10 ? 0.0F : var6;
      float var17 = var11 ? 1.0F : var7;
      float var18 = var12 ? 0.0F : var6;
      float var19 = var13 ? 1.0F : var7;
      this.field_152631_f = true;
      if (var8) {
         this.func_147782_a((double)var16, (double)var14, (double)var6, (double)var17, (double)var15, (double)var7);
         this.func_147784_q(var1, var2, var3, var4);
         var5 = true;
      }

      if (var9) {
         this.func_147782_a((double)var6, (double)var14, (double)var18, (double)var7, (double)var15, (double)var19);
         this.func_147784_q(var1, var2, var3, var4);
         var5 = true;
      }

      var14 = 0.375F;
      var15 = 0.5625F;
      if (var8) {
         this.func_147782_a((double)var16, (double)var14, (double)var6, (double)var17, (double)var15, (double)var7);
         this.func_147784_q(var1, var2, var3, var4);
         var5 = true;
      }

      if (var9) {
         this.func_147782_a((double)var6, (double)var14, (double)var18, (double)var7, (double)var15, (double)var19);
         this.func_147784_q(var1, var2, var3, var4);
         var5 = true;
      }

      this.field_152631_f = false;
      var1.func_149719_a(this.field_147845_a, var2, var3, var4);
      return var5;
   }

   public boolean func_147807_a(BlockWall var1, int var2, int var3, int var4) {
      boolean var5 = var1.func_150091_e(this.field_147845_a, var2 - 1, var3, var4);
      boolean var6 = var1.func_150091_e(this.field_147845_a, var2 + 1, var3, var4);
      boolean var7 = var1.func_150091_e(this.field_147845_a, var2, var3, var4 - 1);
      boolean var8 = var1.func_150091_e(this.field_147845_a, var2, var3, var4 + 1);
      boolean var9 = var7 && var8 && !var5 && !var6;
      boolean var10 = !var7 && !var8 && var5 && var6;
      boolean var11 = this.field_147845_a.func_147437_c(var2, var3 + 1, var4);
      if ((var9 || var10) && var11) {
         if (var9) {
            this.func_147782_a(0.3125, 0.0, 0.0, 0.6875, 0.8125, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
         } else {
            this.func_147782_a(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
            this.func_147784_q(var1, var2, var3, var4);
         }
      } else {
         this.func_147782_a(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
         this.func_147784_q(var1, var2, var3, var4);
         if (var5) {
            this.func_147782_a(0.0, 0.0, 0.3125, 0.25, 0.8125, 0.6875);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var6) {
            this.func_147782_a(0.75, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var7) {
            this.func_147782_a(0.3125, 0.0, 0.0, 0.6875, 0.8125, 0.25);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var8) {
            this.func_147782_a(0.3125, 0.0, 0.75, 0.6875, 0.8125, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
         }
      }

      var1.func_149719_a(this.field_147845_a, var2, var3, var4);
      return true;
   }

   public boolean func_147802_a(BlockDragonEgg var1, int var2, int var3, int var4) {
      boolean var5 = false;
      int var6 = 0;

      for(int var7 = 0; var7 < 8; ++var7) {
         byte var8 = 0;
         byte var9 = 1;
         if (var7 == 0) {
            var8 = 2;
         }

         if (var7 == 1) {
            var8 = 3;
         }

         if (var7 == 2) {
            var8 = 4;
         }

         if (var7 == 3) {
            var8 = 5;
            var9 = 2;
         }

         if (var7 == 4) {
            var8 = 6;
            var9 = 3;
         }

         if (var7 == 5) {
            var8 = 7;
            var9 = 5;
         }

         if (var7 == 6) {
            var8 = 6;
            var9 = 2;
         }

         if (var7 == 7) {
            var8 = 3;
         }

         float var10 = (float)var8 / 16.0F;
         float var11 = 1.0F - (float)var6 / 16.0F;
         float var12 = 1.0F - (float)(var6 + var9) / 16.0F;
         var6 += var9;
         this.func_147782_a((double)(0.5F - var10), (double)var12, (double)(0.5F - var10), (double)(0.5F + var10), (double)var11, (double)(0.5F + var10));
         this.func_147784_q(var1, var2, var3, var4);
      }

      var5 = true;
      this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      return var5;
   }

   public boolean func_147776_a(BlockFenceGate var1, int var2, int var3, int var4) {
      boolean var5 = true;
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      boolean var7 = BlockFenceGate.func_149896_b(var6);
      int var8 = BlockDirectional.func_149895_l(var6);
      float var9 = 0.375F;
      float var10 = 0.5625F;
      float var11 = 0.75F;
      float var12 = 0.9375F;
      float var13 = 0.3125F;
      float var14 = 1.0F;
      if ((var8 == 2 || var8 == 0)
            && this.field_147845_a.func_147439_a(var2 - 1, var3, var4) == Blocks.field_150463_bK
            && this.field_147845_a.func_147439_a(var2 + 1, var3, var4) == Blocks.field_150463_bK
         || (var8 == 3 || var8 == 1)
            && this.field_147845_a.func_147439_a(var2, var3, var4 - 1) == Blocks.field_150463_bK
            && this.field_147845_a.func_147439_a(var2, var3, var4 + 1) == Blocks.field_150463_bK) {
         var9 -= 0.1875F;
         var10 -= 0.1875F;
         var11 -= 0.1875F;
         var12 -= 0.1875F;
         var13 -= 0.1875F;
         var14 -= 0.1875F;
      }

      this.field_147837_f = true;
      if (var8 != 3 && var8 != 1) {
         float var22 = 0.0F;
         float var33 = 0.125F;
         float var45 = 0.4375F;
         float var56 = 0.5625F;
         this.func_147782_a((double)var22, (double)var13, (double)var45, (double)var33, (double)var14, (double)var56);
         this.func_147784_q(var1, var2, var3, var4);
         var22 = 0.875F;
         var33 = 1.0F;
         this.func_147782_a((double)var22, (double)var13, (double)var45, (double)var33, (double)var14, (double)var56);
         this.func_147784_q(var1, var2, var3, var4);
      } else {
         this.field_147867_u = 1;
         float var15 = 0.4375F;
         float var16 = 0.5625F;
         float var17 = 0.0F;
         float var18 = 0.125F;
         this.func_147782_a((double)var15, (double)var13, (double)var17, (double)var16, (double)var14, (double)var18);
         this.func_147784_q(var1, var2, var3, var4);
         var17 = 0.875F;
         var18 = 1.0F;
         this.func_147782_a((double)var15, (double)var13, (double)var17, (double)var16, (double)var14, (double)var18);
         this.func_147784_q(var1, var2, var3, var4);
         this.field_147867_u = 0;
      }

      if (var7) {
         if (var8 == 2 || var8 == 0) {
            this.field_147867_u = 1;
         }

         if (var8 == 3) {
            float var24 = 0.0F;
            float var35 = 0.125F;
            float var46 = 0.875F;
            float var57 = 1.0F;
            float var19 = 0.5625F;
            float var20 = 0.8125F;
            float var21 = 0.9375F;
            this.func_147782_a(0.8125, (double)var9, 0.0, 0.9375, (double)var12, 0.125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.8125, (double)var9, 0.875, 0.9375, (double)var12, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.5625, (double)var9, 0.0, 0.8125, (double)var10, 0.125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.5625, (double)var9, 0.875, 0.8125, (double)var10, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.5625, (double)var11, 0.0, 0.8125, (double)var12, 0.125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.5625, (double)var11, 0.875, 0.8125, (double)var12, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
         } else if (var8 == 1) {
            float var25 = 0.0F;
            float var36 = 0.125F;
            float var47 = 0.875F;
            float var58 = 1.0F;
            float var66 = 0.0625F;
            float var69 = 0.1875F;
            float var72 = 0.4375F;
            this.func_147782_a(0.0625, (double)var9, 0.0, 0.1875, (double)var12, 0.125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.0625, (double)var9, 0.875, 0.1875, (double)var12, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.1875, (double)var9, 0.0, 0.4375, (double)var10, 0.125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.1875, (double)var9, 0.875, 0.4375, (double)var10, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.1875, (double)var11, 0.0, 0.4375, (double)var12, 0.125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.1875, (double)var11, 0.875, 0.4375, (double)var12, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
         } else if (var8 == 0) {
            float var26 = 0.0F;
            float var37 = 0.125F;
            float var48 = 0.875F;
            float var59 = 1.0F;
            float var67 = 0.5625F;
            float var70 = 0.8125F;
            float var73 = 0.9375F;
            this.func_147782_a(0.0, (double)var9, 0.8125, 0.125, (double)var12, 0.9375);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.875, (double)var9, 0.8125, 1.0, (double)var12, 0.9375);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.0, (double)var9, 0.5625, 0.125, (double)var10, 0.8125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.875, (double)var9, 0.5625, 1.0, (double)var10, 0.8125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.0, (double)var11, 0.5625, 0.125, (double)var12, 0.8125);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.875, (double)var11, 0.5625, 1.0, (double)var12, 0.8125);
            this.func_147784_q(var1, var2, var3, var4);
         } else if (var8 == 2) {
            float var27 = 0.0F;
            float var38 = 0.125F;
            float var49 = 0.875F;
            float var60 = 1.0F;
            float var68 = 0.0625F;
            float var71 = 0.1875F;
            float var74 = 0.4375F;
            this.func_147782_a(0.0, (double)var9, 0.0625, 0.125, (double)var12, 0.1875);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.875, (double)var9, 0.0625, 1.0, (double)var12, 0.1875);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.0, (double)var9, 0.1875, 0.125, (double)var10, 0.4375);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.875, (double)var9, 0.1875, 1.0, (double)var10, 0.4375);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.0, (double)var11, 0.1875, 0.125, (double)var12, 0.4375);
            this.func_147784_q(var1, var2, var3, var4);
            this.func_147782_a(0.875, (double)var11, 0.1875, 1.0, (double)var12, 0.4375);
            this.func_147784_q(var1, var2, var3, var4);
         }
      } else if (var8 != 3 && var8 != 1) {
         float var29 = 0.375F;
         float var40 = 0.5F;
         float var54 = 0.4375F;
         float var65 = 0.5625F;
         this.func_147782_a((double)var29, (double)var9, (double)var54, (double)var40, (double)var12, (double)var65);
         this.func_147784_q(var1, var2, var3, var4);
         var29 = 0.5F;
         var40 = 0.625F;
         this.func_147782_a((double)var29, (double)var9, (double)var54, (double)var40, (double)var12, (double)var65);
         this.func_147784_q(var1, var2, var3, var4);
         var29 = 0.625F;
         var40 = 0.875F;
         this.func_147782_a((double)var29, (double)var9, (double)var54, (double)var40, (double)var10, (double)var65);
         this.func_147784_q(var1, var2, var3, var4);
         this.func_147782_a((double)var29, (double)var11, (double)var54, (double)var40, (double)var12, (double)var65);
         this.func_147784_q(var1, var2, var3, var4);
         var29 = 0.125F;
         var40 = 0.375F;
         this.func_147782_a((double)var29, (double)var9, (double)var54, (double)var40, (double)var10, (double)var65);
         this.func_147784_q(var1, var2, var3, var4);
         this.func_147782_a((double)var29, (double)var11, (double)var54, (double)var40, (double)var12, (double)var65);
         this.func_147784_q(var1, var2, var3, var4);
      } else {
         this.field_147867_u = 1;
         float var28 = 0.4375F;
         float var39 = 0.5625F;
         float var50 = 0.375F;
         float var61 = 0.5F;
         this.func_147782_a((double)var28, (double)var9, (double)var50, (double)var39, (double)var12, (double)var61);
         this.func_147784_q(var1, var2, var3, var4);
         var50 = 0.5F;
         var61 = 0.625F;
         this.func_147782_a((double)var28, (double)var9, (double)var50, (double)var39, (double)var12, (double)var61);
         this.func_147784_q(var1, var2, var3, var4);
         var50 = 0.625F;
         var61 = 0.875F;
         this.func_147782_a((double)var28, (double)var9, (double)var50, (double)var39, (double)var10, (double)var61);
         this.func_147784_q(var1, var2, var3, var4);
         this.func_147782_a((double)var28, (double)var11, (double)var50, (double)var39, (double)var12, (double)var61);
         this.func_147784_q(var1, var2, var3, var4);
         var50 = 0.125F;
         var61 = 0.375F;
         this.func_147782_a((double)var28, (double)var9, (double)var50, (double)var39, (double)var10, (double)var61);
         this.func_147784_q(var1, var2, var3, var4);
         this.func_147782_a((double)var28, (double)var11, (double)var50, (double)var39, (double)var12, (double)var61);
         this.func_147784_q(var1, var2, var3, var4);
      }

      this.field_147837_f = false;
      this.field_147867_u = 0;
      this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      return var5;
   }

   private boolean func_147803_a(BlockHopper var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
      int var6 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
      float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
      float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
      float var9 = (float)(var6 & 0xFF) / 255.0F;
      if (EntityRenderer.field_78517_a) {
         float var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
         float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
         float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
         var7 = var10;
         var8 = var11;
         var9 = var12;
      }

      var5.func_78386_a(var7, var8, var9);
      return this.func_147799_a(var1, var2, var3, var4, this.field_147845_a.func_72805_g(var2, var3, var4), false);
   }

   private boolean func_147799_a(BlockHopper var1, int var2, int var3, int var4, int var5, boolean var6) {
      Tessellator var7 = Tessellator.field_78398_a;
      int var8 = BlockHopper.func_149918_b(var5);
      double var9 = 0.625;
      this.func_147782_a(0.0, var9, 0.0, 1.0, 1.0, 1.0);
      if (var6) {
         var7.func_78382_b();
         var7.func_78375_b(0.0F, -1.0F, 0.0F);
         this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 0, var5));
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 1.0F, 0.0F);
         this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 1, var5));
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 0.0F, -1.0F);
         this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 2, var5));
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 0.0F, 1.0F);
         this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 3, var5));
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(-1.0F, 0.0F, 0.0F);
         this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 4, var5));
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(1.0F, 0.0F, 0.0F);
         this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 5, var5));
         var7.func_78381_a();
      } else {
         this.func_147784_q(var1, var2, var3, var4);
      }

      if (!var6) {
         var7.func_78380_c(var1.func_149677_c(this.field_147845_a, var2, var3, var4));
         int var11 = var1.func_149720_d(this.field_147845_a, var2, var3, var4);
         float var12 = (float)(var11 >> 16 & 0xFF) / 255.0F;
         float var13 = (float)(var11 >> 8 & 0xFF) / 255.0F;
         float var14 = (float)(var11 & 0xFF) / 255.0F;
         if (EntityRenderer.field_78517_a) {
            float var15 = (var12 * 30.0F + var13 * 59.0F + var14 * 11.0F) / 100.0F;
            float var16 = (var12 * 30.0F + var13 * 70.0F) / 100.0F;
            float var17 = (var12 * 30.0F + var14 * 70.0F) / 100.0F;
            var12 = var15;
            var13 = var16;
            var14 = var17;
         }

         var7.func_78386_a(var12, var13, var14);
      }

      IIcon var24 = BlockHopper.func_149916_e("hopper_outside");
      IIcon var25 = BlockHopper.func_149916_e("hopper_inside");
      float var26 = 0.125F;
      if (var6) {
         var7.func_78382_b();
         var7.func_78375_b(1.0F, 0.0F, 0.0F);
         this.func_147764_f(var1, (double)(-1.0F + var26), 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(-1.0F, 0.0F, 0.0F);
         this.func_147798_e(var1, (double)(1.0F - var26), 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 0.0F, 1.0F);
         this.func_147734_d(var1, 0.0, 0.0, (double)(-1.0F + var26), var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 0.0F, -1.0F);
         this.func_147761_c(var1, 0.0, 0.0, (double)(1.0F - var26), var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 1.0F, 0.0F);
         this.func_147806_b(var1, 0.0, -1.0 + var9, 0.0, var25);
         var7.func_78381_a();
      } else {
         this.func_147764_f(var1, (double)((float)var2 - 1.0F + var26), (double)var3, (double)var4, var24);
         this.func_147798_e(var1, (double)((float)var2 + 1.0F - var26), (double)var3, (double)var4, var24);
         this.func_147734_d(var1, (double)var2, (double)var3, (double)((float)var4 - 1.0F + var26), var24);
         this.func_147761_c(var1, (double)var2, (double)var3, (double)((float)var4 + 1.0F - var26), var24);
         this.func_147806_b(var1, (double)var2, (double)((float)var3 - 1.0F) + var9, (double)var4, var25);
      }

      this.func_147757_a(var24);
      double var27 = 0.25;
      double var28 = 0.25;
      this.func_147782_a(var27, var28, var27, 1.0 - var27, var9 - 0.002, 1.0 - var27);
      if (var6) {
         var7.func_78382_b();
         var7.func_78375_b(1.0F, 0.0F, 0.0F);
         this.func_147764_f(var1, 0.0, 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(-1.0F, 0.0F, 0.0F);
         this.func_147798_e(var1, 0.0, 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 0.0F, 1.0F);
         this.func_147734_d(var1, 0.0, 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 0.0F, -1.0F);
         this.func_147761_c(var1, 0.0, 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, 1.0F, 0.0F);
         this.func_147806_b(var1, 0.0, 0.0, 0.0, var24);
         var7.func_78381_a();
         var7.func_78382_b();
         var7.func_78375_b(0.0F, -1.0F, 0.0F);
         this.func_147768_a(var1, 0.0, 0.0, 0.0, var24);
         var7.func_78381_a();
      } else {
         this.func_147784_q(var1, var2, var3, var4);
      }

      if (!var6) {
         double var20 = 0.375;
         double var22 = 0.25;
         this.func_147757_a(var24);
         if (var8 == 0) {
            this.func_147782_a(var20, 0.0, var20, 1.0 - var20, 0.25, 1.0 - var20);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var8 == 2) {
            this.func_147782_a(var20, var28, 0.0, 1.0 - var20, var28 + var22, var27);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var8 == 3) {
            this.func_147782_a(var20, var28, 1.0 - var27, 1.0 - var20, var28 + var22, 1.0);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var8 == 4) {
            this.func_147782_a(0.0, var28, var20, var27, var28 + var22, 1.0 - var20);
            this.func_147784_q(var1, var2, var3, var4);
         }

         if (var8 == 5) {
            this.func_147782_a(1.0 - var27, var28, var20, 1.0, var28 + var22, 1.0 - var20);
            this.func_147784_q(var1, var2, var3, var4);
         }
      }

      this.func_147771_a();
      return true;
   }

   public boolean func_147722_a(BlockStairs var1, int var2, int var3, int var4) {
      var1.func_150147_e(this.field_147845_a, var2, var3, var4);
      this.func_147775_a(var1);
      this.func_147784_q(var1, var2, var3, var4);
      this.field_152631_f = true;
      boolean var5 = var1.func_150145_f(this.field_147845_a, var2, var3, var4);
      this.func_147775_a(var1);
      this.func_147784_q(var1, var2, var3, var4);
      if (var5 && var1.func_150144_g(this.field_147845_a, var2, var3, var4)) {
         this.func_147775_a(var1);
         this.func_147784_q(var1, var2, var3, var4);
      }

      this.field_152631_f = false;
      return true;
   }

   public boolean func_147760_u(Block var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.field_78398_a;
      int var6 = this.field_147845_a.func_72805_g(var2, var3, var4);
      if ((var6 & 8) != 0) {
         if (this.field_147845_a.func_147439_a(var2, var3 - 1, var4) != var1) {
            return false;
         }
      } else if (this.field_147845_a.func_147439_a(var2, var3 + 1, var4) != var1) {
         return false;
      }

      boolean var7 = false;
      float var8 = 0.5F;
      float var9 = 1.0F;
      float var10 = 0.8F;
      float var11 = 0.6F;
      int var12 = var1.func_149677_c(this.field_147845_a, var2, var3, var4);
      var5.func_78380_c(this.field_147855_j > 0.0 ? var12 : var1.func_149677_c(this.field_147845_a, var2, var3 - 1, var4));
      var5.func_78386_a(var8, var8, var8);
      this.func_147768_a(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 0));
      var7 = true;
      var5.func_78380_c(this.field_147857_k < 1.0 ? var12 : var1.func_149677_c(this.field_147845_a, var2, var3 + 1, var4));
      var5.func_78386_a(var9, var9, var9);
      this.func_147806_b(var1, (double)var2, (double)var3, (double)var4, this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 1));
      var7 = true;
      var5.func_78380_c(this.field_147851_l > 0.0 ? var12 : var1.func_149677_c(this.field_147845_a, var2, var3, var4 - 1));
      var5.func_78386_a(var10, var10, var10);
      IIcon var13 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 2);
      this.func_147761_c(var1, (double)var2, (double)var3, (double)var4, var13);
      var7 = true;
      this.field_147842_e = false;
      var5.func_78380_c(this.field_147853_m < 1.0 ? var12 : var1.func_149677_c(this.field_147845_a, var2, var3, var4 + 1));
      var5.func_78386_a(var10, var10, var10);
      var13 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 3);
      this.func_147734_d(var1, (double)var2, (double)var3, (double)var4, var13);
      var7 = true;
      this.field_147842_e = false;
      var5.func_78380_c(this.field_147859_h > 0.0 ? var12 : var1.func_149677_c(this.field_147845_a, var2 - 1, var3, var4));
      var5.func_78386_a(var11, var11, var11);
      var13 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 4);
      this.func_147798_e(var1, (double)var2, (double)var3, (double)var4, var13);
      var7 = true;
      this.field_147842_e = false;
      var5.func_78380_c(this.field_147861_i < 1.0 ? var12 : var1.func_149677_c(this.field_147845_a, var2 + 1, var3, var4));
      var5.func_78386_a(var11, var11, var11);
      var13 = this.func_147793_a(var1, this.field_147845_a, var2, var3, var4, 5);
      this.func_147764_f(var1, (double)var2, (double)var3, (double)var4, var13);
      var7 = true;
      this.field_147842_e = false;
      return var7;
   }

   public void func_147768_a(Block var1, double var2, double var4, double var6, IIcon var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      double var10 = (double)var8.func_94214_a(this.field_147859_h * 16.0);
      double var12 = (double)var8.func_94214_a(this.field_147861_i * 16.0);
      double var14 = (double)var8.func_94207_b(this.field_147851_l * 16.0);
      double var16 = (double)var8.func_94207_b(this.field_147853_m * 16.0);
      if (this.field_147859_h < 0.0 || this.field_147861_i > 1.0) {
         var10 = (double)var8.func_94209_e();
         var12 = (double)var8.func_94212_f();
      }

      if (this.field_147851_l < 0.0 || this.field_147853_m > 1.0) {
         var14 = (double)var8.func_94206_g();
         var16 = (double)var8.func_94210_h();
      }

      double var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.field_147865_v == 2) {
         var10 = (double)var8.func_94214_a(this.field_147851_l * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147861_i * 16.0);
         var12 = (double)var8.func_94214_a(this.field_147853_m * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147859_h * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var14;
      } else if (this.field_147865_v == 1) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147853_m * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147851_l * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147861_i * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var10;
         var22 = var16;
         var24 = var14;
      } else if (this.field_147865_v == 3) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147861_i * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147851_l * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147853_m * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = var2 + this.field_147859_h;
      double var28 = var2 + this.field_147861_i;
      double var30 = var4 + this.field_147855_j;
      double var32 = var6 + this.field_147851_l;
      double var34 = var6 + this.field_147853_m;
      if (this.field_147838_g) {
         var26 = var2 + this.field_147861_i;
         var28 = var2 + this.field_147859_h;
      }

      if (this.field_147863_w) {
         var9.func_78386_a(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
         var9.func_78380_c(this.field_147864_al);
         var9.func_78374_a(var26, var30, var34, var20, var24);
         var9.func_78386_a(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
         var9.func_78380_c(this.field_147874_am);
         var9.func_78374_a(var26, var30, var32, var10, var14);
         var9.func_78386_a(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
         var9.func_78380_c(this.field_147876_an);
         var9.func_78374_a(var28, var30, var32, var18, var22);
         var9.func_78386_a(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
         var9.func_78380_c(this.field_147870_ao);
         var9.func_78374_a(var28, var30, var34, var12, var16);
      } else {
         var9.func_78374_a(var26, var30, var34, var20, var24);
         var9.func_78374_a(var26, var30, var32, var10, var14);
         var9.func_78374_a(var28, var30, var32, var18, var22);
         var9.func_78374_a(var28, var30, var34, var12, var16);
      }
   }

   public void func_147806_b(Block var1, double var2, double var4, double var6, IIcon var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      double var10 = (double)var8.func_94214_a(this.field_147859_h * 16.0);
      double var12 = (double)var8.func_94214_a(this.field_147861_i * 16.0);
      double var14 = (double)var8.func_94207_b(this.field_147851_l * 16.0);
      double var16 = (double)var8.func_94207_b(this.field_147853_m * 16.0);
      if (this.field_147859_h < 0.0 || this.field_147861_i > 1.0) {
         var10 = (double)var8.func_94209_e();
         var12 = (double)var8.func_94212_f();
      }

      if (this.field_147851_l < 0.0 || this.field_147853_m > 1.0) {
         var14 = (double)var8.func_94206_g();
         var16 = (double)var8.func_94210_h();
      }

      double var18 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.field_147867_u == 1) {
         var10 = (double)var8.func_94214_a(this.field_147851_l * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147861_i * 16.0);
         var12 = (double)var8.func_94214_a(this.field_147853_m * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147859_h * 16.0);
         var22 = var14;
         var24 = var16;
         var18 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var14;
      } else if (this.field_147867_u == 2) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147853_m * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147851_l * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147861_i * 16.0);
         var18 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var10;
         var22 = var16;
         var24 = var14;
      } else if (this.field_147867_u == 3) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147861_i * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147851_l * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147853_m * 16.0);
         var18 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = var2 + this.field_147859_h;
      double var28 = var2 + this.field_147861_i;
      double var30 = var4 + this.field_147857_k;
      double var32 = var6 + this.field_147851_l;
      double var34 = var6 + this.field_147853_m;
      if (this.field_147838_g) {
         var26 = var2 + this.field_147861_i;
         var28 = var2 + this.field_147859_h;
      }

      if (this.field_147863_w) {
         var9.func_78386_a(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
         var9.func_78380_c(this.field_147864_al);
         var9.func_78374_a(var28, var30, var34, var12, var16);
         var9.func_78386_a(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
         var9.func_78380_c(this.field_147874_am);
         var9.func_78374_a(var28, var30, var32, var18, var22);
         var9.func_78386_a(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
         var9.func_78380_c(this.field_147876_an);
         var9.func_78374_a(var26, var30, var32, var10, var14);
         var9.func_78386_a(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
         var9.func_78380_c(this.field_147870_ao);
         var9.func_78374_a(var26, var30, var34, var20, var24);
      } else {
         var9.func_78374_a(var28, var30, var34, var12, var16);
         var9.func_78374_a(var28, var30, var32, var18, var22);
         var9.func_78374_a(var26, var30, var32, var10, var14);
         var9.func_78374_a(var26, var30, var34, var20, var24);
      }
   }

   public void func_147761_c(Block var1, double var2, double var4, double var6, IIcon var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      double var10 = (double)var8.func_94214_a(this.field_147859_h * 16.0);
      double var12 = (double)var8.func_94214_a(this.field_147861_i * 16.0);
      if (this.field_152631_f) {
         var12 = (double)var8.func_94214_a((1.0 - this.field_147859_h) * 16.0);
         var10 = (double)var8.func_94214_a((1.0 - this.field_147861_i) * 16.0);
      }

      double var14 = (double)var8.func_94207_b(16.0 - this.field_147857_k * 16.0);
      double var16 = (double)var8.func_94207_b(16.0 - this.field_147855_j * 16.0);
      if (this.field_147842_e) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.field_147859_h < 0.0 || this.field_147861_i > 1.0) {
         var10 = (double)var8.func_94209_e();
         var12 = (double)var8.func_94212_f();
      }

      if (this.field_147855_j < 0.0 || this.field_147857_k > 1.0) {
         var14 = (double)var8.func_94206_g();
         var16 = (double)var8.func_94210_h();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.field_147875_q == 2) {
         var10 = (double)var8.func_94214_a(this.field_147855_j * 16.0);
         var12 = (double)var8.func_94214_a(this.field_147857_k * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147859_h * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147861_i * 16.0);
         var22 = var14;
         var24 = var16;
         var40 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var14;
      } else if (this.field_147875_q == 1) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147857_k * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147855_j * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147861_i * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147859_h * 16.0);
         var40 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var10;
         var22 = var16;
         var24 = var14;
      } else if (this.field_147875_q == 3) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147861_i * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147857_k * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147855_j * 16.0);
         var40 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = var2 + this.field_147859_h;
      double var28 = var2 + this.field_147861_i;
      double var30 = var4 + this.field_147855_j;
      double var32 = var4 + this.field_147857_k;
      double var34 = var6 + this.field_147851_l;
      if (this.field_147838_g) {
         var26 = var2 + this.field_147861_i;
         var28 = var2 + this.field_147859_h;
      }

      if (this.field_147863_w) {
         var9.func_78386_a(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
         var9.func_78380_c(this.field_147864_al);
         var9.func_78374_a(var26, var32, var34, var40, var22);
         var9.func_78386_a(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
         var9.func_78380_c(this.field_147874_am);
         var9.func_78374_a(var28, var32, var34, var10, var14);
         var9.func_78386_a(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
         var9.func_78380_c(this.field_147876_an);
         var9.func_78374_a(var28, var30, var34, var20, var24);
         var9.func_78386_a(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
         var9.func_78380_c(this.field_147870_ao);
         var9.func_78374_a(var26, var30, var34, var12, var16);
      } else {
         var9.func_78374_a(var26, var32, var34, var40, var22);
         var9.func_78374_a(var28, var32, var34, var10, var14);
         var9.func_78374_a(var28, var30, var34, var20, var24);
         var9.func_78374_a(var26, var30, var34, var12, var16);
      }
   }

   public void func_147734_d(Block var1, double var2, double var4, double var6, IIcon var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      double var10 = (double)var8.func_94214_a(this.field_147859_h * 16.0);
      double var12 = (double)var8.func_94214_a(this.field_147861_i * 16.0);
      double var14 = (double)var8.func_94207_b(16.0 - this.field_147857_k * 16.0);
      double var16 = (double)var8.func_94207_b(16.0 - this.field_147855_j * 16.0);
      if (this.field_147842_e) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.field_147859_h < 0.0 || this.field_147861_i > 1.0) {
         var10 = (double)var8.func_94209_e();
         var12 = (double)var8.func_94212_f();
      }

      if (this.field_147855_j < 0.0 || this.field_147857_k > 1.0) {
         var14 = (double)var8.func_94206_g();
         var16 = (double)var8.func_94210_h();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.field_147873_r == 1) {
         var10 = (double)var8.func_94214_a(this.field_147855_j * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(this.field_147857_k * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147861_i * 16.0);
         var22 = var14;
         var24 = var16;
         var40 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var14;
      } else if (this.field_147873_r == 2) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147857_k * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147855_j * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147861_i * 16.0);
         var40 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var10;
         var22 = var16;
         var24 = var14;
      } else if (this.field_147873_r == 3) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147859_h * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147861_i * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147857_k * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147855_j * 16.0);
         var40 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = var2 + this.field_147859_h;
      double var28 = var2 + this.field_147861_i;
      double var30 = var4 + this.field_147855_j;
      double var32 = var4 + this.field_147857_k;
      double var34 = var6 + this.field_147853_m;
      if (this.field_147838_g) {
         var26 = var2 + this.field_147861_i;
         var28 = var2 + this.field_147859_h;
      }

      if (this.field_147863_w) {
         var9.func_78386_a(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
         var9.func_78380_c(this.field_147864_al);
         var9.func_78374_a(var26, var32, var34, var10, var14);
         var9.func_78386_a(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
         var9.func_78380_c(this.field_147874_am);
         var9.func_78374_a(var26, var30, var34, var20, var24);
         var9.func_78386_a(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
         var9.func_78380_c(this.field_147876_an);
         var9.func_78374_a(var28, var30, var34, var12, var16);
         var9.func_78386_a(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
         var9.func_78380_c(this.field_147870_ao);
         var9.func_78374_a(var28, var32, var34, var40, var22);
      } else {
         var9.func_78374_a(var26, var32, var34, var10, var14);
         var9.func_78374_a(var26, var30, var34, var20, var24);
         var9.func_78374_a(var28, var30, var34, var12, var16);
         var9.func_78374_a(var28, var32, var34, var40, var22);
      }
   }

   public void func_147798_e(Block var1, double var2, double var4, double var6, IIcon var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      double var10 = (double)var8.func_94214_a(this.field_147851_l * 16.0);
      double var12 = (double)var8.func_94214_a(this.field_147853_m * 16.0);
      double var14 = (double)var8.func_94207_b(16.0 - this.field_147857_k * 16.0);
      double var16 = (double)var8.func_94207_b(16.0 - this.field_147855_j * 16.0);
      if (this.field_147842_e) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.field_147851_l < 0.0 || this.field_147853_m > 1.0) {
         var10 = (double)var8.func_94209_e();
         var12 = (double)var8.func_94212_f();
      }

      if (this.field_147855_j < 0.0 || this.field_147857_k > 1.0) {
         var14 = (double)var8.func_94206_g();
         var16 = (double)var8.func_94210_h();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.field_147869_t == 1) {
         var10 = (double)var8.func_94214_a(this.field_147855_j * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147853_m * 16.0);
         var12 = (double)var8.func_94214_a(this.field_147857_k * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147851_l * 16.0);
         var22 = var14;
         var24 = var16;
         var40 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var14;
      } else if (this.field_147869_t == 2) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147857_k * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147851_l * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147855_j * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147853_m * 16.0);
         var40 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var10;
         var22 = var16;
         var24 = var14;
      } else if (this.field_147869_t == 3) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147851_l * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147853_m * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147857_k * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147855_j * 16.0);
         var40 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = var2 + this.field_147859_h;
      double var28 = var4 + this.field_147855_j;
      double var30 = var4 + this.field_147857_k;
      double var32 = var6 + this.field_147851_l;
      double var34 = var6 + this.field_147853_m;
      if (this.field_147838_g) {
         var32 = var6 + this.field_147853_m;
         var34 = var6 + this.field_147851_l;
      }

      if (this.field_147863_w) {
         var9.func_78386_a(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
         var9.func_78380_c(this.field_147864_al);
         var9.func_78374_a(var26, var30, var34, var40, var22);
         var9.func_78386_a(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
         var9.func_78380_c(this.field_147874_am);
         var9.func_78374_a(var26, var30, var32, var10, var14);
         var9.func_78386_a(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
         var9.func_78380_c(this.field_147876_an);
         var9.func_78374_a(var26, var28, var32, var20, var24);
         var9.func_78386_a(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
         var9.func_78380_c(this.field_147870_ao);
         var9.func_78374_a(var26, var28, var34, var12, var16);
      } else {
         var9.func_78374_a(var26, var30, var34, var40, var22);
         var9.func_78374_a(var26, var30, var32, var10, var14);
         var9.func_78374_a(var26, var28, var32, var20, var24);
         var9.func_78374_a(var26, var28, var34, var12, var16);
      }
   }

   public void func_147764_f(Block var1, double var2, double var4, double var6, IIcon var8) {
      Tessellator var9 = Tessellator.field_78398_a;
      if (this.func_147744_b()) {
         var8 = this.field_147840_d;
      }

      double var10 = (double)var8.func_94214_a(this.field_147851_l * 16.0);
      double var12 = (double)var8.func_94214_a(this.field_147853_m * 16.0);
      if (this.field_152631_f) {
         var12 = (double)var8.func_94214_a((1.0 - this.field_147851_l) * 16.0);
         var10 = (double)var8.func_94214_a((1.0 - this.field_147853_m) * 16.0);
      }

      double var14 = (double)var8.func_94207_b(16.0 - this.field_147857_k * 16.0);
      double var16 = (double)var8.func_94207_b(16.0 - this.field_147855_j * 16.0);
      if (this.field_147842_e) {
         double var18 = var10;
         var10 = var12;
         var12 = var18;
      }

      if (this.field_147851_l < 0.0 || this.field_147853_m > 1.0) {
         var10 = (double)var8.func_94209_e();
         var12 = (double)var8.func_94212_f();
      }

      if (this.field_147855_j < 0.0 || this.field_147857_k > 1.0) {
         var14 = (double)var8.func_94206_g();
         var16 = (double)var8.func_94210_h();
      }

      double var40 = var12;
      double var20 = var10;
      double var22 = var14;
      double var24 = var16;
      if (this.field_147871_s == 2) {
         var10 = (double)var8.func_94214_a(this.field_147855_j * 16.0);
         var14 = (double)var8.func_94207_b(16.0 - this.field_147851_l * 16.0);
         var12 = (double)var8.func_94214_a(this.field_147857_k * 16.0);
         var16 = (double)var8.func_94207_b(16.0 - this.field_147853_m * 16.0);
         var22 = var14;
         var24 = var16;
         var40 = var10;
         var20 = var12;
         var14 = var16;
         var16 = var14;
      } else if (this.field_147871_s == 1) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147857_k * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147853_m * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147855_j * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147851_l * 16.0);
         var40 = var12;
         var20 = var10;
         var10 = var12;
         var12 = var10;
         var22 = var16;
         var24 = var14;
      } else if (this.field_147871_s == 3) {
         var10 = (double)var8.func_94214_a(16.0 - this.field_147851_l * 16.0);
         var12 = (double)var8.func_94214_a(16.0 - this.field_147853_m * 16.0);
         var14 = (double)var8.func_94207_b(this.field_147857_k * 16.0);
         var16 = (double)var8.func_94207_b(this.field_147855_j * 16.0);
         var40 = var12;
         var20 = var10;
         var22 = var14;
         var24 = var16;
      }

      double var26 = var2 + this.field_147861_i;
      double var28 = var4 + this.field_147855_j;
      double var30 = var4 + this.field_147857_k;
      double var32 = var6 + this.field_147851_l;
      double var34 = var6 + this.field_147853_m;
      if (this.field_147838_g) {
         var32 = var6 + this.field_147853_m;
         var34 = var6 + this.field_147851_l;
      }

      if (this.field_147863_w) {
         var9.func_78386_a(this.field_147872_ap, this.field_147846_at, this.field_147854_ax);
         var9.func_78380_c(this.field_147864_al);
         var9.func_78374_a(var26, var28, var34, var20, var24);
         var9.func_78386_a(this.field_147852_aq, this.field_147860_au, this.field_147841_ay);
         var9.func_78380_c(this.field_147874_am);
         var9.func_78374_a(var26, var28, var32, var12, var16);
         var9.func_78386_a(this.field_147850_ar, this.field_147858_av, this.field_147839_az);
         var9.func_78380_c(this.field_147876_an);
         var9.func_78374_a(var26, var30, var32, var40, var22);
         var9.func_78386_a(this.field_147848_as, this.field_147856_aw, this.field_147833_aA);
         var9.func_78380_c(this.field_147870_ao);
         var9.func_78374_a(var26, var30, var34, var10, var14);
      } else {
         var9.func_78374_a(var26, var28, var34, var20, var24);
         var9.func_78374_a(var26, var28, var32, var12, var16);
         var9.func_78374_a(var26, var30, var32, var40, var22);
         var9.func_78374_a(var26, var30, var34, var10, var14);
      }
   }

   public void func_147800_a(Block var1, int var2, float var3) {
      Tessellator var4 = Tessellator.field_78398_a;
      boolean var5 = var1 == Blocks.field_150349_c;
      if (var1 == Blocks.field_150367_z || var1 == Blocks.field_150409_cd || var1 == Blocks.field_150460_al) {
         var2 = 3;
      }

      if (this.field_147844_c) {
         int var6 = var1.func_149741_i(var2);
         if (var5) {
            var6 = 16777215;
         }

         float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
         float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
         float var9 = (float)(var6 & 0xFF) / 255.0F;
         GL11.glColor4f(var7 * var3, var8 * var3, var9 * var3, 1.0F);
      }

      int var14 = var1.func_149645_b();
      this.func_147775_a(var1);
      if (var14 != 0 && var14 != 31 && var14 != 39 && var14 != 16 && var14 != 26) {
         if (var14 == 1) {
            var4.func_78382_b();
            var4.func_78375_b(0.0F, -1.0F, 0.0F);
            IIcon var16 = this.func_147787_a(var1, 0, var2);
            this.func_147765_a(var16, -0.5, -0.5, -0.5, 1.0F);
            var4.func_78381_a();
         } else if (var14 == 19) {
            var4.func_78382_b();
            var4.func_78375_b(0.0F, -1.0F, 0.0F);
            var1.func_149683_g();
            this.func_147730_a(var1, var2, this.field_147857_k, -0.5, -0.5, -0.5);
            var4.func_78381_a();
         } else if (var14 == 23) {
            var4.func_78382_b();
            var4.func_78375_b(0.0F, -1.0F, 0.0F);
            var1.func_149683_g();
            var4.func_78381_a();
         } else if (var14 == 13) {
            var1.func_149683_g();
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float var17 = 0.0625F;
            var4.func_78382_b();
            var4.func_78375_b(0.0F, -1.0F, 0.0F);
            this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 0));
            var4.func_78381_a();
            var4.func_78382_b();
            var4.func_78375_b(0.0F, 1.0F, 0.0F);
            this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 1));
            var4.func_78381_a();
            var4.func_78382_b();
            var4.func_78375_b(0.0F, 0.0F, -1.0F);
            var4.func_78372_c(0.0F, 0.0F, var17);
            this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 2));
            var4.func_78372_c(0.0F, 0.0F, -var17);
            var4.func_78381_a();
            var4.func_78382_b();
            var4.func_78375_b(0.0F, 0.0F, 1.0F);
            var4.func_78372_c(0.0F, 0.0F, -var17);
            this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 3));
            var4.func_78372_c(0.0F, 0.0F, var17);
            var4.func_78381_a();
            var4.func_78382_b();
            var4.func_78375_b(-1.0F, 0.0F, 0.0F);
            var4.func_78372_c(var17, 0.0F, 0.0F);
            this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 4));
            var4.func_78372_c(-var17, 0.0F, 0.0F);
            var4.func_78381_a();
            var4.func_78382_b();
            var4.func_78375_b(1.0F, 0.0F, 0.0F);
            var4.func_78372_c(-var17, 0.0F, 0.0F);
            this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 5));
            var4.func_78372_c(var17, 0.0F, 0.0F);
            var4.func_78381_a();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
         } else if (var14 == 22) {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            TileEntityRendererChestHelper.field_147719_a.func_147715_a(var1, var2, var3);
            GL11.glEnable(32826);
         } else if (var14 == 6) {
            var4.func_78382_b();
            var4.func_78375_b(0.0F, -1.0F, 0.0F);
            this.func_147795_a(var1, var2, -0.5, -0.5, -0.5);
            var4.func_78381_a();
         } else if (var14 == 2) {
            var4.func_78382_b();
            var4.func_78375_b(0.0F, -1.0F, 0.0F);
            this.func_147747_a(var1, -0.5, -0.5, -0.5, 0.0, 0.0, 0);
            var4.func_78381_a();
         } else if (var14 == 10) {
            for(int var18 = 0; var18 < 2; ++var18) {
               if (var18 == 0) {
                  this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
               }

               if (var18 == 1) {
                  this.func_147782_a(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.func_78382_b();
               var4.func_78375_b(0.0F, -1.0F, 0.0F);
               this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 0));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 1.0F, 0.0F);
               this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 1));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, -1.0F);
               this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, 1.0F);
               this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 3));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(-1.0F, 0.0F, 0.0F);
               this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 4));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(1.0F, 0.0F, 0.0F);
               this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 5));
               var4.func_78381_a();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
         } else if (var14 == 27) {
            int var19 = 0;
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            var4.func_78382_b();

            for(int var25 = 0; var25 < 8; ++var25) {
               byte var31 = 0;
               byte var32 = 1;
               if (var25 == 0) {
                  var31 = 2;
               }

               if (var25 == 1) {
                  var31 = 3;
               }

               if (var25 == 2) {
                  var31 = 4;
               }

               if (var25 == 3) {
                  var31 = 5;
                  var32 = 2;
               }

               if (var25 == 4) {
                  var31 = 6;
                  var32 = 3;
               }

               if (var25 == 5) {
                  var31 = 7;
                  var32 = 5;
               }

               if (var25 == 6) {
                  var31 = 6;
                  var32 = 2;
               }

               if (var25 == 7) {
                  var31 = 3;
               }

               float var11 = (float)var31 / 16.0F;
               float var12 = 1.0F - (float)var19 / 16.0F;
               float var13 = 1.0F - (float)(var19 + var32) / 16.0F;
               var19 += var32;
               this.func_147782_a((double)(0.5F - var11), (double)var13, (double)(0.5F - var11), (double)(0.5F + var11), (double)var12, (double)(0.5F + var11));
               var4.func_78375_b(0.0F, -1.0F, 0.0F);
               this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 0));
               var4.func_78375_b(0.0F, 1.0F, 0.0F);
               this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 1));
               var4.func_78375_b(0.0F, 0.0F, -1.0F);
               this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 2));
               var4.func_78375_b(0.0F, 0.0F, 1.0F);
               this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 3));
               var4.func_78375_b(-1.0F, 0.0F, 0.0F);
               this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 4));
               var4.func_78375_b(1.0F, 0.0F, 0.0F);
               this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 5));
            }

            var4.func_78381_a();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var14 == 11) {
            for(int var20 = 0; var20 < 4; ++var20) {
               float var26 = 0.125F;
               if (var20 == 0) {
                  this.func_147782_a((double)(0.5F - var26), 0.0, 0.0, (double)(0.5F + var26), 1.0, (double)(var26 * 2.0F));
               }

               if (var20 == 1) {
                  this.func_147782_a((double)(0.5F - var26), 0.0, (double)(1.0F - var26 * 2.0F), (double)(0.5F + var26), 1.0, 1.0);
               }

               var26 = 0.0625F;
               if (var20 == 2) {
                  this.func_147782_a(
                     (double)(0.5F - var26),
                     (double)(1.0F - var26 * 3.0F),
                     (double)(-var26 * 2.0F),
                     (double)(0.5F + var26),
                     (double)(1.0F - var26),
                     (double)(1.0F + var26 * 2.0F)
                  );
               }

               if (var20 == 3) {
                  this.func_147782_a(
                     (double)(0.5F - var26),
                     (double)(0.5F - var26 * 3.0F),
                     (double)(-var26 * 2.0F),
                     (double)(0.5F + var26),
                     (double)(0.5F - var26),
                     (double)(1.0F + var26 * 2.0F)
                  );
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.func_78382_b();
               var4.func_78375_b(0.0F, -1.0F, 0.0F);
               this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 0));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 1.0F, 0.0F);
               this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 1));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, -1.0F);
               this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, 1.0F);
               this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 3));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(-1.0F, 0.0F, 0.0F);
               this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 4));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(1.0F, 0.0F, 0.0F);
               this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 5));
               var4.func_78381_a();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var14 == 21) {
            for(int var21 = 0; var21 < 3; ++var21) {
               float var28 = 0.0625F;
               if (var21 == 0) {
                  this.func_147782_a((double)(0.5F - var28), 0.30000001192092896, 0.0, (double)(0.5F + var28), 1.0, (double)(var28 * 2.0F));
               }

               if (var21 == 1) {
                  this.func_147782_a((double)(0.5F - var28), 0.30000001192092896, (double)(1.0F - var28 * 2.0F), (double)(0.5F + var28), 1.0, 1.0);
               }

               var28 = 0.0625F;
               if (var21 == 2) {
                  this.func_147782_a((double)(0.5F - var28), 0.5, 0.0, (double)(0.5F + var28), (double)(1.0F - var28), 1.0);
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.func_78382_b();
               var4.func_78375_b(0.0F, -1.0F, 0.0F);
               this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 0));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 1.0F, 0.0F);
               this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 1));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, -1.0F);
               this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, 1.0F);
               this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 3));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(-1.0F, 0.0F, 0.0F);
               this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 4));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(1.0F, 0.0F, 0.0F);
               this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147777_a(var1, 5));
               var4.func_78381_a();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }
         } else if (var14 == 32) {
            for(int var22 = 0; var22 < 2; ++var22) {
               if (var22 == 0) {
                  this.func_147782_a(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
               }

               if (var22 == 1) {
                  this.func_147782_a(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.func_78382_b();
               var4.func_78375_b(0.0F, -1.0F, 0.0F);
               this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 0, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 1.0F, 0.0F);
               this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 1, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, -1.0F);
               this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 2, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, 1.0F);
               this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 3, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(-1.0F, 0.0F, 0.0F);
               this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 4, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(1.0F, 0.0F, 0.0F);
               this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 5, var2));
               var4.func_78381_a();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
         } else if (var14 == 35) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            this.func_147728_a((BlockAnvil)var1, 0, 0, 0, var2 << 2, true);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
         } else if (var14 == 34) {
            for(int var23 = 0; var23 < 3; ++var23) {
               if (var23 == 0) {
                  this.func_147782_a(0.125, 0.0, 0.125, 0.875, 0.1875, 0.875);
                  this.func_147757_a(this.func_147745_b(Blocks.field_150343_Z));
               } else if (var23 == 1) {
                  this.func_147782_a(0.1875, 0.1875, 0.1875, 0.8125, 0.875, 0.8125);
                  this.func_147757_a(this.func_147745_b(Blocks.field_150461_bJ));
               } else if (var23 == 2) {
                  this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
                  this.func_147757_a(this.func_147745_b(Blocks.field_150359_w));
               }

               GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
               var4.func_78382_b();
               var4.func_78375_b(0.0F, -1.0F, 0.0F);
               this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 0, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 1.0F, 0.0F);
               this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 1, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, -1.0F);
               this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 2, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(0.0F, 0.0F, 1.0F);
               this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 3, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(-1.0F, 0.0F, 0.0F);
               this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 4, var2));
               var4.func_78381_a();
               var4.func_78382_b();
               var4.func_78375_b(1.0F, 0.0F, 0.0F);
               this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 5, var2));
               var4.func_78381_a();
               GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            }

            this.func_147782_a(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            this.func_147771_a();
         } else if (var14 == 38) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            this.func_147799_a((BlockHopper)var1, 0, 0, 0, 0, true);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
         }
      } else {
         if (var14 == 16) {
            var2 = 1;
         }

         var1.func_149683_g();
         this.func_147775_a(var1);
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
         var4.func_78382_b();
         var4.func_78375_b(0.0F, -1.0F, 0.0F);
         this.func_147768_a(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 0, var2));
         var4.func_78381_a();
         if (var5 && this.field_147844_c) {
            int var15 = var1.func_149741_i(var2);
            float var24 = (float)(var15 >> 16 & 0xFF) / 255.0F;
            float var30 = (float)(var15 >> 8 & 0xFF) / 255.0F;
            float var10 = (float)(var15 & 0xFF) / 255.0F;
            GL11.glColor4f(var24 * var3, var30 * var3, var10 * var3, 1.0F);
         }

         var4.func_78382_b();
         var4.func_78375_b(0.0F, 1.0F, 0.0F);
         this.func_147806_b(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 1, var2));
         var4.func_78381_a();
         if (var5 && this.field_147844_c) {
            GL11.glColor4f(var3, var3, var3, 1.0F);
         }

         var4.func_78382_b();
         var4.func_78375_b(0.0F, 0.0F, -1.0F);
         this.func_147761_c(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 2, var2));
         var4.func_78381_a();
         var4.func_78382_b();
         var4.func_78375_b(0.0F, 0.0F, 1.0F);
         this.func_147734_d(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 3, var2));
         var4.func_78381_a();
         var4.func_78382_b();
         var4.func_78375_b(-1.0F, 0.0F, 0.0F);
         this.func_147798_e(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 4, var2));
         var4.func_78381_a();
         var4.func_78382_b();
         var4.func_78375_b(1.0F, 0.0F, 0.0F);
         this.func_147764_f(var1, 0.0, 0.0, 0.0, this.func_147787_a(var1, 5, var2));
         var4.func_78381_a();
         GL11.glTranslatef(0.5F, 0.5F, 0.5F);
      }
   }

   public static boolean func_147739_a(int var0) {
      if (var0 == 0) {
         return true;
      } else if (var0 == 31) {
         return true;
      } else if (var0 == 39) {
         return true;
      } else if (var0 == 13) {
         return true;
      } else if (var0 == 10) {
         return true;
      } else if (var0 == 11) {
         return true;
      } else if (var0 == 27) {
         return true;
      } else if (var0 == 22) {
         return true;
      } else if (var0 == 21) {
         return true;
      } else if (var0 == 16) {
         return true;
      } else if (var0 == 26) {
         return true;
      } else if (var0 == 32) {
         return true;
      } else if (var0 == 34) {
         return true;
      } else if (var0 == 35) {
         return true;
      } else {
         return var0 == -1 ? false : false;
      }
   }

   public IIcon func_147793_a(Block var1, IBlockAccess var2, int var3, int var4, int var5, int var6) {
      return this.func_147758_b(var1.func_149673_e(var2, var3, var4, var5, var6));
   }

   public IIcon func_147787_a(Block var1, int var2, int var3) {
      return this.func_147758_b(var1.func_149691_a(var2, var3));
   }

   public IIcon func_147777_a(Block var1, int var2) {
      return this.func_147758_b(var1.func_149733_h(var2));
   }

   public IIcon func_147745_b(Block var1) {
      return this.func_147758_b(var1.func_149733_h(1));
   }

   public IIcon func_147758_b(IIcon var1) {
      if (var1 == null) {
         var1 = ((TextureMap)Minecraft.func_71410_x().func_110434_K().func_110581_b(TextureMap.field_110575_b)).func_110572_b("missingno");
      }

      return (IIcon)var1;
   }
}
