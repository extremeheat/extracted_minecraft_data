package net.minecraft.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;

public class Teleporter {
   private final WorldServer field_85192_a;
   private final Random field_77187_a;
   private final LongHashMap field_85191_c = new LongHashMap();
   private final List field_85190_d = new ArrayList();

   public Teleporter(WorldServer var1) {
      super();
      this.field_85192_a = var1;
      this.field_77187_a = new Random(var1.func_72905_C());
   }

   public void func_77185_a(Entity var1, double var2, double var4, double var6, float var8) {
      if (this.field_85192_a.field_73011_w.field_76574_g != 1) {
         if (!this.func_77184_b(var1, var2, var4, var6, var8)) {
            this.func_85188_a(var1);
            this.func_77184_b(var1, var2, var4, var6, var8);
         }
      } else {
         int var9 = MathHelper.func_76128_c(var1.field_70165_t);
         int var10 = MathHelper.func_76128_c(var1.field_70163_u) - 1;
         int var11 = MathHelper.func_76128_c(var1.field_70161_v);
         byte var12 = 1;
         byte var13 = 0;

         for(int var14 = -2; var14 <= 2; ++var14) {
            for(int var15 = -2; var15 <= 2; ++var15) {
               for(int var16 = -1; var16 < 3; ++var16) {
                  int var17 = var9 + var15 * var12 + var14 * var13;
                  int var18 = var10 + var16;
                  int var19 = var11 + var15 * var13 - var14 * var12;
                  boolean var20 = var16 < 0;
                  this.field_85192_a.func_147449_b(var17, var18, var19, var20 ? Blocks.field_150343_Z : Blocks.field_150350_a);
               }
            }
         }

         var1.func_70012_b((double)var9, (double)var10, (double)var11, var1.field_70177_z, 0.0F);
         var1.field_70159_w = var1.field_70181_x = var1.field_70179_y = 0.0;
      }
   }

   public boolean func_77184_b(Entity var1, double var2, double var4, double var6, float var8) {
      short var9 = 128;
      double var10 = -1.0;
      int var12 = 0;
      int var13 = 0;
      int var14 = 0;
      int var15 = MathHelper.func_76128_c(var1.field_70165_t);
      int var16 = MathHelper.func_76128_c(var1.field_70161_v);
      long var17 = ChunkCoordIntPair.func_77272_a(var15, var16);
      boolean var19 = true;
      if (this.field_85191_c.func_76161_b(var17)) {
         Teleporter$PortalPosition var20 = (Teleporter$PortalPosition)this.field_85191_c.func_76164_a(var17);
         var10 = 0.0;
         var12 = var20.field_71574_a;
         var13 = var20.field_71572_b;
         var14 = var20.field_71573_c;
         var20.field_85087_d = this.field_85192_a.func_82737_E();
         var19 = false;
      } else {
         for(int var48 = var15 - var9; var48 <= var15 + var9; ++var48) {
            double var21 = (double)var48 + 0.5 - var1.field_70165_t;

            for(int var23 = var16 - var9; var23 <= var16 + var9; ++var23) {
               double var24 = (double)var23 + 0.5 - var1.field_70161_v;

               for(int var26 = this.field_85192_a.func_72940_L() - 1; var26 >= 0; --var26) {
                  if (this.field_85192_a.func_147439_a(var48, var26, var23) == Blocks.field_150427_aO) {
                     while(this.field_85192_a.func_147439_a(var48, var26 - 1, var23) == Blocks.field_150427_aO) {
                        --var26;
                     }

                     double var27 = (double)var26 + 0.5 - var1.field_70163_u;
                     double var29 = var21 * var21 + var27 * var27 + var24 * var24;
                     if (var10 < 0.0 || var29 < var10) {
                        var10 = var29;
                        var12 = var48;
                        var13 = var26;
                        var14 = var23;
                     }
                  }
               }
            }
         }
      }

      if (var10 >= 0.0) {
         if (var19) {
            this.field_85191_c.func_76163_a(var17, new Teleporter$PortalPosition(this, var12, var13, var14, this.field_85192_a.func_82737_E()));
            this.field_85190_d.add(var17);
         }

         double var50 = (double)var12 + 0.5;
         double var25 = (double)var13 + 0.5;
         double var51 = (double)var14 + 0.5;
         int var52 = -1;
         if (this.field_85192_a.func_147439_a(var12 - 1, var13, var14) == Blocks.field_150427_aO) {
            var52 = 2;
         }

         if (this.field_85192_a.func_147439_a(var12 + 1, var13, var14) == Blocks.field_150427_aO) {
            var52 = 0;
         }

         if (this.field_85192_a.func_147439_a(var12, var13, var14 - 1) == Blocks.field_150427_aO) {
            var52 = 3;
         }

         if (this.field_85192_a.func_147439_a(var12, var13, var14 + 1) == Blocks.field_150427_aO) {
            var52 = 1;
         }

         int var30 = var1.func_82148_at();
         if (var52 > -1) {
            int var31 = Direction.field_71578_g[var52];
            int var32 = Direction.field_71583_a[var52];
            int var33 = Direction.field_71581_b[var52];
            int var34 = Direction.field_71583_a[var31];
            int var35 = Direction.field_71581_b[var31];
            boolean var36 = !this.field_85192_a.func_147437_c(var12 + var32 + var34, var13, var14 + var33 + var35)
               || !this.field_85192_a.func_147437_c(var12 + var32 + var34, var13 + 1, var14 + var33 + var35);
            boolean var37 = !this.field_85192_a.func_147437_c(var12 + var32, var13, var14 + var33)
               || !this.field_85192_a.func_147437_c(var12 + var32, var13 + 1, var14 + var33);
            if (var36 && var37) {
               var52 = Direction.field_71580_e[var52];
               var31 = Direction.field_71580_e[var31];
               var32 = Direction.field_71583_a[var52];
               var33 = Direction.field_71581_b[var52];
               var34 = Direction.field_71583_a[var31];
               var35 = Direction.field_71581_b[var31];
               int var49 = var12 - var34;
               var50 -= (double)var34;
               int var22 = var14 - var35;
               var51 -= (double)var35;
               var36 = !this.field_85192_a.func_147437_c(var49 + var32 + var34, var13, var22 + var33 + var35)
                  || !this.field_85192_a.func_147437_c(var49 + var32 + var34, var13 + 1, var22 + var33 + var35);
               var37 = !this.field_85192_a.func_147437_c(var49 + var32, var13, var22 + var33)
                  || !this.field_85192_a.func_147437_c(var49 + var32, var13 + 1, var22 + var33);
            }

            float var38 = 0.5F;
            float var39 = 0.5F;
            if (!var36 && var37) {
               var38 = 1.0F;
            } else if (var36 && !var37) {
               var38 = 0.0F;
            } else if (var36 && var37) {
               var39 = 0.0F;
            }

            var50 += (double)((float)var34 * var38 + var39 * (float)var32);
            var51 += (double)((float)var35 * var38 + var39 * (float)var33);
            float var40 = 0.0F;
            float var41 = 0.0F;
            float var42 = 0.0F;
            float var43 = 0.0F;
            if (var52 == var30) {
               var40 = 1.0F;
               var41 = 1.0F;
            } else if (var52 == Direction.field_71580_e[var30]) {
               var40 = -1.0F;
               var41 = -1.0F;
            } else if (var52 == Direction.field_71577_f[var30]) {
               var42 = 1.0F;
               var43 = -1.0F;
            } else {
               var42 = -1.0F;
               var43 = 1.0F;
            }

            double var44 = var1.field_70159_w;
            double var46 = var1.field_70179_y;
            var1.field_70159_w = var44 * (double)var40 + var46 * (double)var43;
            var1.field_70179_y = var44 * (double)var42 + var46 * (double)var41;
            var1.field_70177_z = var8 - (float)(var30 * 90) + (float)(var52 * 90);
         } else {
            var1.field_70159_w = var1.field_70181_x = var1.field_70179_y = 0.0;
         }

         var1.func_70012_b(var50, var25, var51, var1.field_70177_z, var1.field_70125_A);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_85188_a(Entity var1) {
      byte var2 = 16;
      double var3 = -1.0;
      int var5 = MathHelper.func_76128_c(var1.field_70165_t);
      int var6 = MathHelper.func_76128_c(var1.field_70163_u);
      int var7 = MathHelper.func_76128_c(var1.field_70161_v);
      int var8 = var5;
      int var9 = var6;
      int var10 = var7;
      int var11 = 0;
      int var12 = this.field_77187_a.nextInt(4);

      for(int var13 = var5 - var2; var13 <= var5 + var2; ++var13) {
         double var14 = (double)var13 + 0.5 - var1.field_70165_t;

         for(int var16 = var7 - var2; var16 <= var7 + var2; ++var16) {
            double var17 = (double)var16 + 0.5 - var1.field_70161_v;

            label299:
            for(int var19 = this.field_85192_a.func_72940_L() - 1; var19 >= 0; --var19) {
               if (this.field_85192_a.func_147437_c(var13, var19, var16)) {
                  while(var19 > 0 && this.field_85192_a.func_147437_c(var13, var19 - 1, var16)) {
                     --var19;
                  }

                  for(int var20 = var12; var20 < var12 + 4; ++var20) {
                     int var21 = var20 % 2;
                     int var22 = 1 - var21;
                     if (var20 % 4 >= 2) {
                        var21 = -var21;
                        var22 = -var22;
                     }

                     for(int var23 = 0; var23 < 3; ++var23) {
                        for(int var24 = 0; var24 < 4; ++var24) {
                           for(int var25 = -1; var25 < 4; ++var25) {
                              int var26 = var13 + (var24 - 1) * var21 + var23 * var22;
                              int var27 = var19 + var25;
                              int var28 = var16 + (var24 - 1) * var22 - var23 * var21;
                              if (var25 < 0 && !this.field_85192_a.func_147439_a(var26, var27, var28).func_149688_o().func_76220_a()
                                 || var25 >= 0 && !this.field_85192_a.func_147437_c(var26, var27, var28)) {
                                 continue label299;
                              }
                           }
                        }
                     }

                     double var51 = (double)var19 + 0.5 - var1.field_70163_u;
                     double var61 = var14 * var14 + var51 * var51 + var17 * var17;
                     if (var3 < 0.0 || var61 < var3) {
                        var3 = var61;
                        var8 = var13;
                        var9 = var19;
                        var10 = var16;
                        var11 = var20 % 4;
                     }
                  }
               }
            }
         }
      }

      if (var3 < 0.0) {
         for(int var29 = var5 - var2; var29 <= var5 + var2; ++var29) {
            double var30 = (double)var29 + 0.5 - var1.field_70165_t;

            for(int var32 = var7 - var2; var32 <= var7 + var2; ++var32) {
               double var34 = (double)var32 + 0.5 - var1.field_70161_v;

               label236:
               for(int var36 = this.field_85192_a.func_72940_L() - 1; var36 >= 0; --var36) {
                  if (this.field_85192_a.func_147437_c(var29, var36, var32)) {
                     while(var36 > 0 && this.field_85192_a.func_147437_c(var29, var36 - 1, var32)) {
                        --var36;
                     }

                     for(int var39 = var12; var39 < var12 + 2; ++var39) {
                        int var43 = var39 % 2;
                        int var47 = 1 - var43;

                        for(int var52 = 0; var52 < 4; ++var52) {
                           for(int var57 = -1; var57 < 4; ++var57) {
                              int var62 = var29 + (var52 - 1) * var43;
                              int var66 = var36 + var57;
                              int var67 = var32 + (var52 - 1) * var47;
                              if (var57 < 0 && !this.field_85192_a.func_147439_a(var62, var66, var67).func_149688_o().func_76220_a()
                                 || var57 >= 0 && !this.field_85192_a.func_147437_c(var62, var66, var67)) {
                                 continue label236;
                              }
                           }
                        }

                        double var53 = (double)var36 + 0.5 - var1.field_70163_u;
                        double var63 = var30 * var30 + var53 * var53 + var34 * var34;
                        if (var3 < 0.0 || var63 < var3) {
                           var3 = var63;
                           var8 = var29;
                           var9 = var36;
                           var10 = var32;
                           var11 = var39 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int var31 = var8;
      int var15 = var9;
      int var33 = var10;
      int var35 = var11 % 2;
      int var18 = 1 - var35;
      if (var11 % 4 >= 2) {
         var35 = -var35;
         var18 = -var18;
      }

      if (var3 < 0.0) {
         if (var9 < 70) {
            var9 = 70;
         }

         if (var9 > this.field_85192_a.func_72940_L() - 10) {
            var9 = this.field_85192_a.func_72940_L() - 10;
         }

         var15 = var9;

         for(int var37 = -1; var37 <= 1; ++var37) {
            for(int var40 = 1; var40 < 3; ++var40) {
               for(int var44 = -1; var44 < 3; ++var44) {
                  int var48 = var31 + (var40 - 1) * var35 + var37 * var18;
                  int var54 = var15 + var44;
                  int var58 = var33 + (var40 - 1) * var18 - var37 * var35;
                  boolean var64 = var44 < 0;
                  this.field_85192_a.func_147449_b(var48, var54, var58, var64 ? Blocks.field_150343_Z : Blocks.field_150350_a);
               }
            }
         }
      }

      for(int var38 = 0; var38 < 4; ++var38) {
         for(int var41 = 0; var41 < 4; ++var41) {
            for(int var45 = -1; var45 < 4; ++var45) {
               int var49 = var31 + (var41 - 1) * var35;
               int var55 = var15 + var45;
               int var59 = var33 + (var41 - 1) * var18;
               boolean var65 = var41 == 0 || var41 == 3 || var45 == -1 || var45 == 3;
               this.field_85192_a.func_147465_d(var49, var55, var59, (Block)(var65 ? Blocks.field_150343_Z : Blocks.field_150427_aO), 0, 2);
            }
         }

         for(int var42 = 0; var42 < 4; ++var42) {
            for(int var46 = -1; var46 < 4; ++var46) {
               int var50 = var31 + (var42 - 1) * var35;
               int var56 = var15 + var46;
               int var60 = var33 + (var42 - 1) * var18;
               this.field_85192_a.func_147459_d(var50, var56, var60, this.field_85192_a.func_147439_a(var50, var56, var60));
            }
         }
      }

      return true;
   }

   public void func_85189_a(long var1) {
      if (var1 % 100L == 0L) {
         Iterator var3 = this.field_85190_d.iterator();
         long var4 = var1 - 600L;

         while(var3.hasNext()) {
            Long var6 = (Long)var3.next();
            Teleporter$PortalPosition var7 = (Teleporter$PortalPosition)this.field_85191_c.func_76164_a(var6);
            if (var7 == null || var7.field_85087_d < var4) {
               var3.remove();
               this.field_85191_c.func_76159_d(var6);
            }
         }
      }
   }
}
