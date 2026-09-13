package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class PathFinder {
   private IBlockAccess field_75868_a;
   private Path field_75866_b = new Path();
   private IntHashMap field_75867_c = new IntHashMap();
   private PathPoint[] field_75864_d = new PathPoint[32];
   private boolean field_75865_e;
   private boolean field_75862_f;
   private boolean field_75863_g;
   private boolean field_75869_h;

   public PathFinder(IBlockAccess var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      super();
      this.field_75868_a = var1;
      this.field_75865_e = var2;
      this.field_75862_f = var3;
      this.field_75863_g = var4;
      this.field_75869_h = var5;
   }

   public PathEntity func_75856_a(Entity var1, Entity var2, float var3) {
      return this.func_75857_a(var1, var2.field_70165_t, var2.field_70121_D.field_72338_b, var2.field_70161_v, var3);
   }

   public PathEntity func_75859_a(Entity var1, int var2, int var3, int var4, float var5) {
      return this.func_75857_a(var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), var5);
   }

   private PathEntity func_75857_a(Entity var1, double var2, double var4, double var6, float var8) {
      this.field_75866_b.func_75848_a();
      this.field_75867_c.func_76046_c();
      boolean var9 = this.field_75863_g;
      int var10 = MathHelper.func_76128_c(var1.field_70121_D.field_72338_b + 0.5);
      if (this.field_75869_h && var1.func_70090_H()) {
         var10 = (int)var1.field_70121_D.field_72338_b;
         Block var11 = this.field_75868_a.func_147439_a(MathHelper.func_76128_c(var1.field_70165_t), var10, MathHelper.func_76128_c(var1.field_70161_v));

         while(var11 == Blocks.field_150358_i || var11 == Blocks.field_150355_j) {
            var11 = this.field_75868_a.func_147439_a(MathHelper.func_76128_c(var1.field_70165_t), ++var10, MathHelper.func_76128_c(var1.field_70161_v));
         }

         var9 = this.field_75863_g;
         this.field_75863_g = false;
      } else {
         var10 = MathHelper.func_76128_c(var1.field_70121_D.field_72338_b + 0.5);
      }

      PathPoint var16 = this.func_75854_a(
         MathHelper.func_76128_c(var1.field_70121_D.field_72340_a), var10, MathHelper.func_76128_c(var1.field_70121_D.field_72339_c)
      );
      PathPoint var12 = this.func_75854_a(
         MathHelper.func_76128_c(var2 - (double)(var1.field_70130_N / 2.0F)),
         MathHelper.func_76128_c(var4),
         MathHelper.func_76128_c(var6 - (double)(var1.field_70130_N / 2.0F))
      );
      PathPoint var13 = new PathPoint(
         MathHelper.func_76141_d(var1.field_70130_N + 1.0F),
         MathHelper.func_76141_d(var1.field_70131_O + 1.0F),
         MathHelper.func_76141_d(var1.field_70130_N + 1.0F)
      );
      PathEntity var14 = this.func_75861_a(var1, var16, var12, var13, var8);
      this.field_75863_g = var9;
      return var14;
   }

   private PathEntity func_75861_a(Entity var1, PathPoint var2, PathPoint var3, PathPoint var4, float var5) {
      var2.field_75836_e = 0.0F;
      var2.field_75833_f = var2.func_75832_b(var3);
      var2.field_75834_g = var2.field_75833_f;
      this.field_75866_b.func_75848_a();
      this.field_75866_b.func_75849_a(var2);
      PathPoint var6 = var2;

      while(!this.field_75866_b.func_75845_e()) {
         PathPoint var7 = this.field_75866_b.func_75844_c();
         if (var7.equals(var3)) {
            return this.func_75853_a(var2, var3);
         }

         if (var7.func_75832_b(var3) < var6.func_75832_b(var3)) {
            var6 = var7;
         }

         var7.field_75842_i = true;
         int var8 = this.func_75860_b(var1, var7, var4, var3, var5);

         for(int var9 = 0; var9 < var8; ++var9) {
            PathPoint var10 = this.field_75864_d[var9];
            float var11 = var7.field_75836_e + var7.func_75832_b(var10);
            if (!var10.func_75831_a() || var11 < var10.field_75836_e) {
               var10.field_75841_h = var7;
               var10.field_75836_e = var11;
               var10.field_75833_f = var10.func_75832_b(var3);
               if (var10.func_75831_a()) {
                  this.field_75866_b.func_75850_a(var10, var10.field_75836_e + var10.field_75833_f);
               } else {
                  var10.field_75834_g = var10.field_75836_e + var10.field_75833_f;
                  this.field_75866_b.func_75849_a(var10);
               }
            }
         }
      }

      return var6 == var2 ? null : this.func_75853_a(var2, var6);
   }

   private int func_75860_b(Entity var1, PathPoint var2, PathPoint var3, PathPoint var4, float var5) {
      int var6 = 0;
      byte var7 = 0;
      if (this.func_75855_a(var1, var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c, var3) == 1) {
         var7 = 1;
      }

      PathPoint var8 = this.func_75858_a(var1, var2.field_75839_a, var2.field_75837_b, var2.field_75838_c + 1, var3, var7);
      PathPoint var9 = this.func_75858_a(var1, var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c, var3, var7);
      PathPoint var10 = this.func_75858_a(var1, var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c, var3, var7);
      PathPoint var11 = this.func_75858_a(var1, var2.field_75839_a, var2.field_75837_b, var2.field_75838_c - 1, var3, var7);
      if (var8 != null && !var8.field_75842_i && var8.func_75829_a(var4) < var5) {
         this.field_75864_d[var6++] = var8;
      }

      if (var9 != null && !var9.field_75842_i && var9.func_75829_a(var4) < var5) {
         this.field_75864_d[var6++] = var9;
      }

      if (var10 != null && !var10.field_75842_i && var10.func_75829_a(var4) < var5) {
         this.field_75864_d[var6++] = var10;
      }

      if (var11 != null && !var11.field_75842_i && var11.func_75829_a(var4) < var5) {
         this.field_75864_d[var6++] = var11;
      }

      return var6;
   }

   private PathPoint func_75858_a(Entity var1, int var2, int var3, int var4, PathPoint var5, int var6) {
      PathPoint var7 = null;
      int var8 = this.func_75855_a(var1, var2, var3, var4, var5);
      if (var8 == 2) {
         return this.func_75854_a(var2, var3, var4);
      } else {
         if (var8 == 1) {
            var7 = this.func_75854_a(var2, var3, var4);
         }

         if (var7 == null && var6 > 0 && var8 != -3 && var8 != -4 && this.func_75855_a(var1, var2, var3 + var6, var4, var5) == 1) {
            var7 = this.func_75854_a(var2, var3 + var6, var4);
            var3 += var6;
         }

         if (var7 != null) {
            int var9 = 0;
            int var10 = 0;

            while(var3 > 0) {
               var10 = this.func_75855_a(var1, var2, var3 - 1, var4, var5);
               if (this.field_75863_g && var10 == -1) {
                  return null;
               }

               if (var10 != 1) {
                  break;
               }

               if (var9++ >= var1.func_82143_as()) {
                  return null;
               }

               if (--var3 > 0) {
                  var7 = this.func_75854_a(var2, var3, var4);
               }
            }

            if (var10 == -2) {
               return null;
            }
         }

         return var7;
      }
   }

   private final PathPoint func_75854_a(int var1, int var2, int var3) {
      int var4 = PathPoint.func_75830_a(var1, var2, var3);
      PathPoint var5 = (PathPoint)this.field_75867_c.func_76041_a(var4);
      if (var5 == null) {
         var5 = new PathPoint(var1, var2, var3);
         this.field_75867_c.func_76038_a(var4, var5);
      }

      return var5;
   }

   public int func_75855_a(Entity var1, int var2, int var3, int var4, PathPoint var5) {
      return func_82565_a(var1, var2, var3, var4, var5, this.field_75863_g, this.field_75862_f, this.field_75865_e);
   }

   public static int func_82565_a(Entity var0, int var1, int var2, int var3, PathPoint var4, boolean var5, boolean var6, boolean var7) {
      boolean var8 = false;

      for(int var9 = var1; var9 < var1 + var4.field_75839_a; ++var9) {
         for(int var10 = var2; var10 < var2 + var4.field_75837_b; ++var10) {
            for(int var11 = var3; var11 < var3 + var4.field_75838_c; ++var11) {
               Block var12 = var0.field_70170_p.func_147439_a(var9, var10, var11);
               if (var12.func_149688_o() != Material.field_151579_a) {
                  if (var12 == Blocks.field_150415_aT) {
                     var8 = true;
                  } else if (var12 != Blocks.field_150358_i && var12 != Blocks.field_150355_j) {
                     if (!var7 && var12 == Blocks.field_150466_ao) {
                        return 0;
                     }
                  } else {
                     if (var5) {
                        return -1;
                     }

                     var8 = true;
                  }

                  int var13 = var12.func_149645_b();
                  if (var0.field_70170_p.func_147439_a(var9, var10, var11).func_149645_b() == 9) {
                     int var14 = MathHelper.func_76128_c(var0.field_70165_t);
                     int var15 = MathHelper.func_76128_c(var0.field_70163_u);
                     int var16 = MathHelper.func_76128_c(var0.field_70161_v);
                     if (var0.field_70170_p.func_147439_a(var14, var15, var16).func_149645_b() != 9
                        && var0.field_70170_p.func_147439_a(var14, var15 - 1, var16).func_149645_b() != 9) {
                        return -3;
                     }
                  } else if (!var12.func_149655_b(var0.field_70170_p, var9, var10, var11) && (!var6 || var12 != Blocks.field_150466_ao)) {
                     if (var13 == 11 || var12 == Blocks.field_150396_be || var13 == 32) {
                        return -3;
                     }

                     if (var12 == Blocks.field_150415_aT) {
                        return -4;
                     }

                     Material var17 = var12.func_149688_o();
                     if (var17 != Material.field_151587_i) {
                        return 0;
                     }

                     if (!var0.func_70058_J()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return var8 ? 2 : 1;
   }

   private PathEntity func_75853_a(PathPoint var1, PathPoint var2) {
      int var3 = 1;

      for(PathPoint var4 = var2; var4.field_75841_h != null; var4 = var4.field_75841_h) {
         ++var3;
      }

      PathPoint[] var5 = new PathPoint[var3];
      PathPoint var7 = var2;
      --var3;

      for(var5[var3] = var2; var7.field_75841_h != null; var5[var3] = var7) {
         var7 = var7.field_75841_h;
         --var3;
      }

      return new PathEntity(var5);
   }
}
