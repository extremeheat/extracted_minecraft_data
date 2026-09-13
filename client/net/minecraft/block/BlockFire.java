package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
   private int[] field_149849_a = new int[256];
   private int[] field_149848_b = new int[256];
   private IIcon[] field_149850_M;

   protected BlockFire() {
      super(Material.field_151581_o);
      this.func_149675_a(true);
   }

   public static void func_149843_e() {
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150344_f), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150373_bw), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150376_bx), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150422_aJ), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150476_ad), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150487_bG), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150485_bF), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150481_bH), 5, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150364_r), 5, 5);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150363_s), 5, 5);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150362_t), 30, 60);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150361_u), 30, 60);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150342_X), 30, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150335_W), 15, 100);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150329_H), 60, 100);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150398_cm), 60, 100);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150327_N), 60, 100);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150328_O), 60, 100);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150325_L), 30, 60);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150395_bd), 15, 100);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150402_ci), 5, 5);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150407_cf), 60, 20);
      Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150404_cg), 60, 20);
   }

   public void func_149842_a(int var1, int var2, int var3) {
      this.field_149849_a[var1] = var2;
      this.field_149848_b[var1] = var3;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 3;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public int func_149738_a(World var1) {
      return 30;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_82736_K().func_82766_b("doFireTick")) {
         boolean var6 = var1.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150424_aL;
         if (var1.field_73011_w instanceof WorldProviderEnd && var1.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150357_h) {
            var6 = true;
         }

         if (!this.func_149742_c(var1, var2, var3, var4)) {
            var1.func_147468_f(var2, var3, var4);
         }

         if (var6
            || !var1.func_72896_J()
            || !var1.func_72951_B(var2, var3, var4)
               && !var1.func_72951_B(var2 - 1, var3, var4)
               && !var1.func_72951_B(var2 + 1, var3, var4)
               && !var1.func_72951_B(var2, var3, var4 - 1)
               && !var1.func_72951_B(var2, var3, var4 + 1)) {
            int var7 = var1.func_72805_g(var2, var3, var4);
            if (var7 < 15) {
               var1.func_72921_c(var2, var3, var4, var7 + var5.nextInt(3) / 2, 4);
            }

            var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1) + var5.nextInt(10));
            if (!var6 && !this.func_149847_e(var1, var2, var3, var4)) {
               if (!World.func_147466_a(var1, var2, var3 - 1, var4) || var7 > 3) {
                  var1.func_147468_f(var2, var3, var4);
               }
            } else if (!var6 && !this.func_149844_e(var1, var2, var3 - 1, var4) && var7 == 15 && var5.nextInt(4) == 0) {
               var1.func_147468_f(var2, var3, var4);
            } else {
               boolean var8 = var1.func_72958_C(var2, var3, var4);
               byte var9 = 0;
               if (var8) {
                  var9 = -50;
               }

               this.func_149841_a(var1, var2 + 1, var3, var4, 300 + var9, var5, var7);
               this.func_149841_a(var1, var2 - 1, var3, var4, 300 + var9, var5, var7);
               this.func_149841_a(var1, var2, var3 - 1, var4, 250 + var9, var5, var7);
               this.func_149841_a(var1, var2, var3 + 1, var4, 250 + var9, var5, var7);
               this.func_149841_a(var1, var2, var3, var4 - 1, 300 + var9, var5, var7);
               this.func_149841_a(var1, var2, var3, var4 + 1, 300 + var9, var5, var7);

               for(int var10 = var2 - 1; var10 <= var2 + 1; ++var10) {
                  for(int var11 = var4 - 1; var11 <= var4 + 1; ++var11) {
                     for(int var12 = var3 - 1; var12 <= var3 + 4; ++var12) {
                        if (var10 != var2 || var12 != var3 || var11 != var4) {
                           int var13 = 100;
                           if (var12 > var3 + 1) {
                              var13 += (var12 - (var3 + 1)) * 100;
                           }

                           int var14 = this.func_149845_m(var1, var10, var12, var11);
                           if (var14 > 0) {
                              int var15 = (var14 + 40 + var1.field_73013_u.func_151525_a() * 7) / (var7 + 30);
                              if (var8) {
                                 var15 /= 2;
                              }

                              if (var15 > 0
                                 && var5.nextInt(var13) <= var15
                                 && (!var1.func_72896_J() || !var1.func_72951_B(var10, var12, var11))
                                 && !var1.func_72951_B(var10 - 1, var12, var4)
                                 && !var1.func_72951_B(var10 + 1, var12, var11)
                                 && !var1.func_72951_B(var10, var12, var11 - 1)
                                 && !var1.func_72951_B(var10, var12, var11 + 1)) {
                                 int var16 = var7 + var5.nextInt(5) / 4;
                                 if (var16 > 15) {
                                    var16 = 15;
                                 }

                                 var1.func_147465_d(var10, var12, var11, this, var16, 3);
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }

   @Override
   public boolean func_149698_L() {
      return false;
   }

   private void func_149841_a(World var1, int var2, int var3, int var4, int var5, Random var6, int var7) {
      int var8 = this.field_149848_b[Block.func_149682_b(var1.func_147439_a(var2, var3, var4))];
      if (var6.nextInt(var5) < var8) {
         boolean var9 = var1.func_147439_a(var2, var3, var4) == Blocks.field_150335_W;
         if (var6.nextInt(var7 + 10) < 5 && !var1.func_72951_B(var2, var3, var4)) {
            int var10 = var7 + var6.nextInt(5) / 4;
            if (var10 > 15) {
               var10 = 15;
            }

            var1.func_147465_d(var2, var3, var4, this, var10, 3);
         } else {
            var1.func_147468_f(var2, var3, var4);
         }

         if (var9) {
            Blocks.field_150335_W.func_149664_b(var1, var2, var3, var4, 1);
         }
      }
   }

   private boolean func_149847_e(World var1, int var2, int var3, int var4) {
      if (this.func_149844_e(var1, var2 + 1, var3, var4)) {
         return true;
      } else if (this.func_149844_e(var1, var2 - 1, var3, var4)) {
         return true;
      } else if (this.func_149844_e(var1, var2, var3 - 1, var4)) {
         return true;
      } else if (this.func_149844_e(var1, var2, var3 + 1, var4)) {
         return true;
      } else if (this.func_149844_e(var1, var2, var3, var4 - 1)) {
         return true;
      } else {
         return this.func_149844_e(var1, var2, var3, var4 + 1);
      }
   }

   private int func_149845_m(World var1, int var2, int var3, int var4) {
      int var5 = 0;
      if (!var1.func_147437_c(var2, var3, var4)) {
         return 0;
      } else {
         var5 = this.func_149846_a(var1, var2 + 1, var3, var4, var5);
         var5 = this.func_149846_a(var1, var2 - 1, var3, var4, var5);
         var5 = this.func_149846_a(var1, var2, var3 - 1, var4, var5);
         var5 = this.func_149846_a(var1, var2, var3 + 1, var4, var5);
         var5 = this.func_149846_a(var1, var2, var3, var4 - 1, var5);
         return this.func_149846_a(var1, var2, var3, var4 + 1, var5);
      }
   }

   @Override
   public boolean func_149703_v() {
      return false;
   }

   public boolean func_149844_e(IBlockAccess var1, int var2, int var3, int var4) {
      return this.field_149849_a[Block.func_149682_b(var1.func_147439_a(var2, var3, var4))] > 0;
   }

   public int func_149846_a(World var1, int var2, int var3, int var4, int var5) {
      int var6 = this.field_149849_a[Block.func_149682_b(var1.func_147439_a(var2, var3, var4))];
      return var6 > var5 ? var6 : var5;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return World.func_147466_a(var1, var2, var3 - 1, var4) || this.func_149847_e(var1, var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!World.func_147466_a(var1, var2, var3 - 1, var4) && !this.func_149847_e(var1, var2, var3, var4)) {
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (var1.field_73011_w.field_76574_g > 0 || !Blocks.field_150427_aO.func_150000_e(var1, var2, var3, var4)) {
         if (!World.func_147466_a(var1, var2, var3 - 1, var4) && !this.func_149847_e(var1, var2, var3, var4)) {
            var1.func_147468_f(var2, var3, var4);
         } else {
            var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1) + var1.field_73012_v.nextInt(10));
         }
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (var5.nextInt(24) == 0) {
         var1.func_72980_b(
            (double)((float)var2 + 0.5F),
            (double)((float)var3 + 0.5F),
            (double)((float)var4 + 0.5F),
            "fire.fire",
            1.0F + var5.nextFloat(),
            var5.nextFloat() * 0.7F + 0.3F,
            false
         );
      }

      if (!World.func_147466_a(var1, var2, var3 - 1, var4) && !Blocks.field_150480_ab.func_149844_e(var1, var2, var3 - 1, var4)) {
         if (Blocks.field_150480_ab.func_149844_e(var1, var2 - 1, var3, var4)) {
            for(int var10 = 0; var10 < 2; ++var10) {
               float var15 = (float)var2 + var5.nextFloat() * 0.1F;
               float var20 = (float)var3 + var5.nextFloat();
               float var25 = (float)var4 + var5.nextFloat();
               var1.func_72869_a("largesmoke", (double)var15, (double)var20, (double)var25, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.field_150480_ab.func_149844_e(var1, var2 + 1, var3, var4)) {
            for(int var11 = 0; var11 < 2; ++var11) {
               float var16 = (float)(var2 + 1) - var5.nextFloat() * 0.1F;
               float var21 = (float)var3 + var5.nextFloat();
               float var26 = (float)var4 + var5.nextFloat();
               var1.func_72869_a("largesmoke", (double)var16, (double)var21, (double)var26, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.field_150480_ab.func_149844_e(var1, var2, var3, var4 - 1)) {
            for(int var12 = 0; var12 < 2; ++var12) {
               float var17 = (float)var2 + var5.nextFloat();
               float var22 = (float)var3 + var5.nextFloat();
               float var27 = (float)var4 + var5.nextFloat() * 0.1F;
               var1.func_72869_a("largesmoke", (double)var17, (double)var22, (double)var27, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.field_150480_ab.func_149844_e(var1, var2, var3, var4 + 1)) {
            for(int var13 = 0; var13 < 2; ++var13) {
               float var18 = (float)var2 + var5.nextFloat();
               float var23 = (float)var3 + var5.nextFloat();
               float var28 = (float)(var4 + 1) - var5.nextFloat() * 0.1F;
               var1.func_72869_a("largesmoke", (double)var18, (double)var23, (double)var28, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.field_150480_ab.func_149844_e(var1, var2, var3 + 1, var4)) {
            for(int var14 = 0; var14 < 2; ++var14) {
               float var19 = (float)var2 + var5.nextFloat();
               float var24 = (float)(var3 + 1) - var5.nextFloat() * 0.1F;
               float var29 = (float)var4 + var5.nextFloat();
               var1.func_72869_a("largesmoke", (double)var19, (double)var24, (double)var29, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for(int var6 = 0; var6 < 3; ++var6) {
            float var7 = (float)var2 + var5.nextFloat();
            float var8 = (float)var3 + var5.nextFloat() * 0.5F + 0.5F;
            float var9 = (float)var4 + var5.nextFloat();
            var1.func_72869_a("largesmoke", (double)var7, (double)var8, (double)var9, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149850_M = new IIcon[]{var1.func_94245_a(this.func_149641_N() + "_layer_0"), var1.func_94245_a(this.func_149641_N() + "_layer_1")};
   }

   public IIcon func_149840_c(int var1) {
      return this.field_149850_M[var1];
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return this.field_149850_M[0];
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return MapColor.field_151656_f;
   }
}
