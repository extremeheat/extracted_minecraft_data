package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockGrass extends Block implements IGrowable {
   private static final Logger field_149992_a = LogManager.getLogger();
   private IIcon field_149991_b;
   private IIcon field_149993_M;
   private IIcon field_149994_N;

   protected BlockGrass() {
      super(Material.field_151577_b);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_149991_b;
      } else {
         return var1 == 0 ? Blocks.field_150346_d.func_149733_h(var1) : this.field_149761_L;
      }
   }

   @Override
   public IIcon func_149673_e(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (var5 == 1) {
         return this.field_149991_b;
      } else if (var5 == 0) {
         return Blocks.field_150346_d.func_149733_h(var5);
      } else {
         Material var6 = var1.func_147439_a(var2, var3 + 1, var4).func_149688_o();
         return var6 != Material.field_151597_y && var6 != Material.field_151596_z ? this.field_149761_L : this.field_149993_M;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_149991_b = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_149993_M = var1.func_94245_a(this.func_149641_N() + "_side_snowed");
      this.field_149994_N = var1.func_94245_a(this.func_149641_N() + "_side_overlay");
   }

   @Override
   public int func_149635_D() {
      double var1 = 0.5;
      double var3 = 1.0;
      return ColorizerGrass.func_77480_a(var1, var3);
   }

   @Override
   public int func_149741_i(int var1) {
      return this.func_149635_D();
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      for(int var8 = -1; var8 <= 1; ++var8) {
         for(int var9 = -1; var9 <= 1; ++var9) {
            int var10 = var1.func_72807_a(var2 + var9, var4 + var8).func_150558_b(var2 + var9, var3, var4 + var8);
            var5 += (var10 & 0xFF0000) >> 16;
            var6 += (var10 & 0xFF00) >> 8;
            var7 += var10 & 0xFF;
         }
      }

      return (var5 / 9 & 0xFF) << 16 | (var6 / 9 & 0xFF) << 8 | var7 / 9 & 0xFF;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         if (var1.func_72957_l(var2, var3 + 1, var4) < 4 && var1.func_147439_a(var2, var3 + 1, var4).func_149717_k() > 2) {
            var1.func_147449_b(var2, var3, var4, Blocks.field_150346_d);
         } else if (var1.func_72957_l(var2, var3 + 1, var4) >= 9) {
            for(int var6 = 0; var6 < 4; ++var6) {
               int var7 = var2 + var5.nextInt(3) - 1;
               int var8 = var3 + var5.nextInt(5) - 3;
               int var9 = var4 + var5.nextInt(3) - 1;
               Block var10 = var1.func_147439_a(var7, var8 + 1, var9);
               if (var1.func_147439_a(var7, var8, var9) == Blocks.field_150346_d
                  && var1.func_72805_g(var7, var8, var9) == 0
                  && var1.func_72957_l(var7, var8 + 1, var9) >= 4
                  && var10.func_149717_k() <= 2) {
                  var1.func_147449_b(var7, var8, var9, Blocks.field_150349_c);
               }
            }
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Blocks.field_150346_d.func_149650_a(0, var2, var3);
   }

   public static IIcon func_149990_e() {
      return Blocks.field_150349_c.field_149994_N;
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      return true;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      label38:
      for(int var6 = 0; var6 < 128; ++var6) {
         int var7 = var3;
         int var8 = var4 + 1;
         int var9 = var5;

         for(int var10 = 0; var10 < var6 / 16; ++var10) {
            var7 += var2.nextInt(3) - 1;
            var8 += (var2.nextInt(3) - 1) * var2.nextInt(3) / 2;
            var9 += var2.nextInt(3) - 1;
            if (var1.func_147439_a(var7, var8 - 1, var9) != Blocks.field_150349_c || var1.func_147439_a(var7, var8, var9).func_149721_r()) {
               continue label38;
            }
         }

         if (var1.func_147439_a(var7, var8, var9).field_149764_J == Material.field_151579_a) {
            if (var2.nextInt(8) != 0) {
               if (Blocks.field_150329_H.func_149718_j(var1, var7, var8, var9)) {
                  var1.func_147465_d(var7, var8, var9, Blocks.field_150329_H, 1, 3);
               }
            } else {
               String var13 = var1.func_72807_a(var7, var9).func_150572_a(var2, var7, var8, var9);
               field_149992_a.debug("Flower in " + var1.func_72807_a(var7, var9).field_76791_y + ": " + var13);
               BlockFlower var11 = BlockFlower.func_149857_e(var13);
               if (var11 != null && var11.func_149718_j(var1, var7, var8, var9)) {
                  int var12 = BlockFlower.func_149856_f(var13);
                  var1.func_147465_d(var7, var8, var9, var11, var12, 3);
               }
            }
         }
      }
   }
}
