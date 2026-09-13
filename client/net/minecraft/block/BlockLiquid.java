package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLiquid extends Block {
   private IIcon[] field_149806_a;

   protected BlockLiquid(Material var1) {
      super(var1);
      float var2 = 0.0F;
      float var3 = 0.0F;
      this.func_149676_a(0.0F + var3, 0.0F + var2, 0.0F + var3, 1.0F + var3, 1.0F + var2, 1.0F + var3);
      this.func_149675_a(true);
   }

   @Override
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return this.field_149764_J != Material.field_151587_i;
   }

   @Override
   public int func_149635_D() {
      return 16777215;
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      if (this.field_149764_J != Material.field_151586_h) {
         return 16777215;
      } else {
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;

         for(int var8 = -1; var8 <= 1; ++var8) {
            for(int var9 = -1; var9 <= 1; ++var9) {
               int var10 = var1.func_72807_a(var2 + var9, var4 + var8).field_76759_H;
               var5 += (var10 & 0xFF0000) >> 16;
               var6 += (var10 & 0xFF00) >> 8;
               var7 += var10 & 0xFF;
            }
         }

         return (var5 / 9 & 0xFF) << 16 | (var6 / 9 & 0xFF) << 8 | var7 / 9 & 0xFF;
      }
   }

   public static float func_149801_b(int var0) {
      if (var0 >= 8) {
         var0 = 0;
      }

      return (float)(var0 + 1) / 9.0F;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var1 != 0 && var1 != 1 ? this.field_149806_a[1] : this.field_149806_a[0];
   }

   protected int func_149804_e(World var1, int var2, int var3, int var4) {
      return var1.func_147439_a(var2, var3, var4).func_149688_o() == this.field_149764_J ? var1.func_72805_g(var2, var3, var4) : -1;
   }

   protected int func_149798_e(IBlockAccess var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3, var4).func_149688_o() != this.field_149764_J) {
         return -1;
      } else {
         int var5 = var1.func_72805_g(var2, var3, var4);
         if (var5 >= 8) {
            var5 = 0;
         }

         return var5;
      }
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149678_a(int var1, boolean var2) {
      return var2 && var1 == 0;
   }

   @Override
   public boolean func_149747_d(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      Material var6 = var1.func_147439_a(var2, var3, var4).func_149688_o();
      if (var6 == this.field_149764_J) {
         return false;
      } else if (var5 == 1) {
         return true;
      } else {
         return var6 == Material.field_151588_w ? false : super.func_149747_d(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      Material var6 = var1.func_147439_a(var2, var3, var4).func_149688_o();
      if (var6 == this.field_149764_J) {
         return false;
      } else {
         return var5 == 1 ? true : super.func_149646_a(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public int func_149645_b() {
      return 4;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   private Vec3 func_149800_f(IBlockAccess var1, int var2, int var3, int var4) {
      Vec3 var5 = Vec3.func_72443_a(0.0, 0.0, 0.0);
      int var6 = this.func_149798_e(var1, var2, var3, var4);

      for(int var7 = 0; var7 < 4; ++var7) {
         int var8 = var2;
         int var10 = var4;
         if (var7 == 0) {
            var8 = var2 - 1;
         }

         if (var7 == 1) {
            var10 = var4 - 1;
         }

         if (var7 == 2) {
            ++var8;
         }

         if (var7 == 3) {
            ++var10;
         }

         int var11 = this.func_149798_e(var1, var8, var3, var10);
         if (var11 < 0) {
            if (!var1.func_147439_a(var8, var3, var10).func_149688_o().func_76230_c()) {
               var11 = this.func_149798_e(var1, var8, var3 - 1, var10);
               if (var11 >= 0) {
                  int var12 = var11 - (var6 - 8);
                  var5 = var5.func_72441_c((double)((var8 - var2) * var12), (double)((var3 - var3) * var12), (double)((var10 - var4) * var12));
               }
            }
         } else if (var11 >= 0) {
            int var15 = var11 - var6;
            var5 = var5.func_72441_c((double)((var8 - var2) * var15), (double)((var3 - var3) * var15), (double)((var10 - var4) * var15));
         }
      }

      if (var1.func_72805_g(var2, var3, var4) >= 8) {
         boolean var13 = false;
         if (var13 || this.func_149747_d(var1, var2, var3, var4 - 1, 2)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2, var3, var4 + 1, 3)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2 - 1, var3, var4, 4)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2 + 1, var3, var4, 5)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2, var3 + 1, var4 - 1, 2)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2, var3 + 1, var4 + 1, 3)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2 - 1, var3 + 1, var4, 4)) {
            var13 = true;
         }

         if (var13 || this.func_149747_d(var1, var2 + 1, var3 + 1, var4, 5)) {
            var13 = true;
         }

         if (var13) {
            var5 = var5.func_72432_b().func_72441_c(0.0, -6.0, 0.0);
         }
      }

      return var5.func_72432_b();
   }

   @Override
   public void func_149640_a(World var1, int var2, int var3, int var4, Entity var5, Vec3 var6) {
      Vec3 var7 = this.func_149800_f(var1, var2, var3, var4);
      var6.field_72450_a += var7.field_72450_a;
      var6.field_72448_b += var7.field_72448_b;
      var6.field_72449_c += var7.field_72449_c;
   }

   @Override
   public int func_149738_a(World var1) {
      if (this.field_149764_J == Material.field_151586_h) {
         return 5;
      } else if (this.field_149764_J == Material.field_151587_i) {
         return var1.field_73011_w.field_76576_e ? 10 : 30;
      } else {
         return 0;
      }
   }

   @Override
   public int func_149677_c(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72802_i(var2, var3, var4, 0);
      int var6 = var1.func_72802_i(var2, var3 + 1, var4, 0);
      int var7 = var5 & 0xFF;
      int var8 = var6 & 0xFF;
      int var9 = var5 >> 16 & 0xFF;
      int var10 = var6 >> 16 & 0xFF;
      return (var7 > var8 ? var7 : var8) | (var9 > var10 ? var9 : var10) << 16;
   }

   @Override
   public int func_149701_w() {
      return this.field_149764_J == Material.field_151586_h ? 1 : 0;
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_149764_J == Material.field_151586_h) {
         if (var5.nextInt(10) == 0) {
            int var6 = var1.func_72805_g(var2, var3, var4);
            if (var6 <= 0 || var6 >= 8) {
               var1.func_72869_a(
                  "suspended",
                  (double)((float)var2 + var5.nextFloat()),
                  (double)((float)var3 + var5.nextFloat()),
                  (double)((float)var4 + var5.nextFloat()),
                  0.0,
                  0.0,
                  0.0
               );
            }
         }

         for(int var21 = 0; var21 < 0; ++var21) {
            int var7 = var5.nextInt(4);
            int var8 = var2;
            int var9 = var4;
            if (var7 == 0) {
               var8 = var2 - 1;
            }

            if (var7 == 1) {
               ++var8;
            }

            if (var7 == 2) {
               var9 = var4 - 1;
            }

            if (var7 == 3) {
               ++var9;
            }

            if (var1.func_147439_a(var8, var3, var9).func_149688_o() == Material.field_151579_a
               && (
                  var1.func_147439_a(var8, var3 - 1, var9).func_149688_o().func_76230_c()
                     || var1.func_147439_a(var8, var3 - 1, var9).func_149688_o().func_76224_d()
               )) {
               float var10 = 0.0625F;
               double var11 = (double)((float)var2 + var5.nextFloat());
               double var13 = (double)((float)var3 + var5.nextFloat());
               double var15 = (double)((float)var4 + var5.nextFloat());
               if (var7 == 0) {
                  var11 = (double)((float)var2 - var10);
               }

               if (var7 == 1) {
                  var11 = (double)((float)(var2 + 1) + var10);
               }

               if (var7 == 2) {
                  var15 = (double)((float)var4 - var10);
               }

               if (var7 == 3) {
                  var15 = (double)((float)(var4 + 1) + var10);
               }

               double var17 = 0.0;
               double var19 = 0.0;
               if (var7 == 0) {
                  var17 = (double)(-var10);
               }

               if (var7 == 1) {
                  var17 = (double)var10;
               }

               if (var7 == 2) {
                  var19 = (double)(-var10);
               }

               if (var7 == 3) {
                  var19 = (double)var10;
               }

               var1.func_72869_a("splash", var11, var13, var15, var17, 0.0, var19);
            }
         }
      }

      if (this.field_149764_J == Material.field_151586_h && var5.nextInt(64) == 0) {
         int var22 = var1.func_72805_g(var2, var3, var4);
         if (var22 > 0 && var22 < 8) {
            var1.func_72980_b(
               (double)((float)var2 + 0.5F),
               (double)((float)var3 + 0.5F),
               (double)((float)var4 + 0.5F),
               "liquid.water",
               var5.nextFloat() * 0.25F + 0.75F,
               var5.nextFloat() * 1.0F + 0.5F,
               false
            );
         }
      }

      if (this.field_149764_J == Material.field_151587_i
         && var1.func_147439_a(var2, var3 + 1, var4).func_149688_o() == Material.field_151579_a
         && !var1.func_147439_a(var2, var3 + 1, var4).func_149662_c()) {
         if (var5.nextInt(100) == 0) {
            double var23 = (double)((float)var2 + var5.nextFloat());
            double var25 = (double)var3 + this.field_149756_F;
            double var27 = (double)((float)var4 + var5.nextFloat());
            var1.func_72869_a("lava", var23, var25, var27, 0.0, 0.0, 0.0);
            var1.func_72980_b(var23, var25, var27, "liquid.lavapop", 0.2F + var5.nextFloat() * 0.2F, 0.9F + var5.nextFloat() * 0.15F, false);
         }

         if (var5.nextInt(200) == 0) {
            var1.func_72980_b((double)var2, (double)var3, (double)var4, "liquid.lava", 0.2F + var5.nextFloat() * 0.2F, 0.9F + var5.nextFloat() * 0.15F, false);
         }
      }

      if (var5.nextInt(10) == 0 && World.func_147466_a(var1, var2, var3 - 1, var4) && !var1.func_147439_a(var2, var3 - 2, var4).func_149688_o().func_76230_c()
         )
       {
         double var24 = (double)((float)var2 + var5.nextFloat());
         double var26 = (double)var3 - 1.05;
         double var28 = (double)((float)var4 + var5.nextFloat());
         if (this.field_149764_J == Material.field_151586_h) {
            var1.func_72869_a("dripWater", var24, var26, var28, 0.0, 0.0, 0.0);
         } else {
            var1.func_72869_a("dripLava", var24, var26, var28, 0.0, 0.0, 0.0);
         }
      }
   }

   public static double func_149802_a(IBlockAccess var0, int var1, int var2, int var3, Material var4) {
      Vec3 var5 = null;
      if (var4 == Material.field_151586_h) {
         var5 = Blocks.field_150358_i.func_149800_f(var0, var1, var2, var3);
      }

      if (var4 == Material.field_151587_i) {
         var5 = Blocks.field_150356_k.func_149800_f(var0, var1, var2, var3);
      }

      return var5.field_72450_a == 0.0 && var5.field_72449_c == 0.0 ? -1000.0 : Math.atan2(var5.field_72449_c, var5.field_72450_a) - 1.5707963267948966;
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      this.func_149805_n(var1, var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_149805_n(var1, var2, var3, var4);
   }

   private void func_149805_n(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3, var4) == this) {
         if (this.field_149764_J == Material.field_151587_i) {
            boolean var5 = false;
            if (var5 || var1.func_147439_a(var2, var3, var4 - 1).func_149688_o() == Material.field_151586_h) {
               var5 = true;
            }

            if (var5 || var1.func_147439_a(var2, var3, var4 + 1).func_149688_o() == Material.field_151586_h) {
               var5 = true;
            }

            if (var5 || var1.func_147439_a(var2 - 1, var3, var4).func_149688_o() == Material.field_151586_h) {
               var5 = true;
            }

            if (var5 || var1.func_147439_a(var2 + 1, var3, var4).func_149688_o() == Material.field_151586_h) {
               var5 = true;
            }

            if (var5 || var1.func_147439_a(var2, var3 + 1, var4).func_149688_o() == Material.field_151586_h) {
               var5 = true;
            }

            if (var5) {
               int var6 = var1.func_72805_g(var2, var3, var4);
               if (var6 == 0) {
                  var1.func_147449_b(var2, var3, var4, Blocks.field_150343_Z);
               } else if (var6 <= 4) {
                  var1.func_147449_b(var2, var3, var4, Blocks.field_150347_e);
               }

               this.func_149799_m(var1, var2, var3, var4);
            }
         }
      }
   }

   protected void func_149799_m(World var1, int var2, int var3, int var4) {
      var1.func_72908_a(
         (double)((float)var2 + 0.5F),
         (double)((float)var3 + 0.5F),
         (double)((float)var4 + 0.5F),
         "random.fizz",
         0.5F,
         2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F
      );

      for(int var5 = 0; var5 < 8; ++var5) {
         var1.func_72869_a("largesmoke", (double)var2 + Math.random(), (double)var3 + 1.2, (double)var4 + Math.random(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      if (this.field_149764_J == Material.field_151587_i) {
         this.field_149806_a = new IIcon[]{var1.func_94245_a("lava_still"), var1.func_94245_a("lava_flow")};
      } else {
         this.field_149806_a = new IIcon[]{var1.func_94245_a("water_still"), var1.func_94245_a("water_flow")};
      }
   }

   public static IIcon func_149803_e(String var0) {
      if (var0 == "water_still") {
         return Blocks.field_150358_i.field_149806_a[0];
      } else if (var0 == "water_flow") {
         return Blocks.field_150358_i.field_149806_a[1];
      } else if (var0 == "lava_still") {
         return Blocks.field_150356_k.field_149806_a[0];
      } else {
         return var0 == "lava_flow" ? Blocks.field_150356_k.field_149806_a[1] : null;
      }
   }
}
