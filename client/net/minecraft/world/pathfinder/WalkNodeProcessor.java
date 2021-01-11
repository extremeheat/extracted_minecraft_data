package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor extends NodeProcessor {
   private boolean field_176180_f;
   private boolean field_176181_g;
   private boolean field_176183_h;
   private boolean field_176184_i;
   private boolean field_176182_j;

   public WalkNodeProcessor() {
      super();
   }

   public void func_176162_a(IBlockAccess var1, Entity var2) {
      super.func_176162_a(var1, var2);
      this.field_176182_j = this.field_176183_h;
   }

   public void func_176163_a() {
      super.func_176163_a();
      this.field_176183_h = this.field_176182_j;
   }

   public PathPoint func_176161_a(Entity var1) {
      int var2;
      if (this.field_176184_i && var1.func_70090_H()) {
         var2 = (int)var1.func_174813_aQ().field_72338_b;
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(var1.field_70165_t), var2, MathHelper.func_76128_c(var1.field_70161_v));

         for(Block var4 = this.field_176169_a.func_180495_p(var3).func_177230_c(); var4 == Blocks.field_150358_i || var4 == Blocks.field_150355_j; var4 = this.field_176169_a.func_180495_p(var3).func_177230_c()) {
            ++var2;
            var3.func_181079_c(MathHelper.func_76128_c(var1.field_70165_t), var2, MathHelper.func_76128_c(var1.field_70161_v));
         }

         this.field_176183_h = false;
      } else {
         var2 = MathHelper.func_76128_c(var1.func_174813_aQ().field_72338_b + 0.5D);
      }

      return this.func_176159_a(MathHelper.func_76128_c(var1.func_174813_aQ().field_72340_a), var2, MathHelper.func_76128_c(var1.func_174813_aQ().field_72339_c));
   }

   public PathPoint func_176160_a(Entity var1, double var2, double var4, double var6) {
      return this.func_176159_a(MathHelper.func_76128_c(var2 - (double)(var1.field_70130_N / 2.0F)), MathHelper.func_76128_c(var4), MathHelper.func_76128_c(var6 - (double)(var1.field_70130_N / 2.0F)));
   }

   public int func_176164_a(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5) {
      int var6 = 0;
      byte var7 = 0;
      if (this.func_176177_a(var2, var3.field_75839_a, var3.field_75837_b + 1, var3.field_75838_c) == 1) {
         var7 = 1;
      }

      PathPoint var8 = this.func_176171_a(var2, var3.field_75839_a, var3.field_75837_b, var3.field_75838_c + 1, var7);
      PathPoint var9 = this.func_176171_a(var2, var3.field_75839_a - 1, var3.field_75837_b, var3.field_75838_c, var7);
      PathPoint var10 = this.func_176171_a(var2, var3.field_75839_a + 1, var3.field_75837_b, var3.field_75838_c, var7);
      PathPoint var11 = this.func_176171_a(var2, var3.field_75839_a, var3.field_75837_b, var3.field_75838_c - 1, var7);
      if (var8 != null && !var8.field_75842_i && var8.func_75829_a(var4) < var5) {
         var1[var6++] = var8;
      }

      if (var9 != null && !var9.field_75842_i && var9.func_75829_a(var4) < var5) {
         var1[var6++] = var9;
      }

      if (var10 != null && !var10.field_75842_i && var10.func_75829_a(var4) < var5) {
         var1[var6++] = var10;
      }

      if (var11 != null && !var11.field_75842_i && var11.func_75829_a(var4) < var5) {
         var1[var6++] = var11;
      }

      return var6;
   }

   private PathPoint func_176171_a(Entity var1, int var2, int var3, int var4, int var5) {
      PathPoint var6 = null;
      int var7 = this.func_176177_a(var1, var2, var3, var4);
      if (var7 == 2) {
         return this.func_176159_a(var2, var3, var4);
      } else {
         if (var7 == 1) {
            var6 = this.func_176159_a(var2, var3, var4);
         }

         if (var6 == null && var5 > 0 && var7 != -3 && var7 != -4 && this.func_176177_a(var1, var2, var3 + var5, var4) == 1) {
            var6 = this.func_176159_a(var2, var3 + var5, var4);
            var3 += var5;
         }

         if (var6 != null) {
            int var8 = 0;

            int var9;
            for(var9 = 0; var3 > 0; var6 = this.func_176159_a(var2, var3, var4)) {
               var9 = this.func_176177_a(var1, var2, var3 - 1, var4);
               if (this.field_176183_h && var9 == -1) {
                  return null;
               }

               if (var9 != 1) {
                  break;
               }

               if (var8++ >= var1.func_82143_as()) {
                  return null;
               }

               --var3;
               if (var3 <= 0) {
                  return null;
               }
            }

            if (var9 == -2) {
               return null;
            }
         }

         return var6;
      }
   }

   private int func_176177_a(Entity var1, int var2, int var3, int var4) {
      return func_176170_a(this.field_176169_a, var1, var2, var3, var4, this.field_176168_c, this.field_176165_d, this.field_176166_e, this.field_176183_h, this.field_176181_g, this.field_176180_f);
   }

   public static int func_176170_a(IBlockAccess var0, Entity var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10) {
      boolean var11 = false;
      BlockPos var12 = new BlockPos(var1);
      BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

      for(int var14 = var2; var14 < var2 + var5; ++var14) {
         for(int var15 = var3; var15 < var3 + var6; ++var15) {
            for(int var16 = var4; var16 < var4 + var7; ++var16) {
               var13.func_181079_c(var14, var15, var16);
               Block var17 = var0.func_180495_p(var13).func_177230_c();
               if (var17.func_149688_o() != Material.field_151579_a) {
                  if (var17 != Blocks.field_150415_aT && var17 != Blocks.field_180400_cw) {
                     if (var17 != Blocks.field_150358_i && var17 != Blocks.field_150355_j) {
                        if (!var10 && var17 instanceof BlockDoor && var17.func_149688_o() == Material.field_151575_d) {
                           return 0;
                        }
                     } else {
                        if (var8) {
                           return -1;
                        }

                        var11 = true;
                     }
                  } else {
                     var11 = true;
                  }

                  if (var1.field_70170_p.func_180495_p(var13).func_177230_c() instanceof BlockRailBase) {
                     if (!(var1.field_70170_p.func_180495_p(var12).func_177230_c() instanceof BlockRailBase) && !(var1.field_70170_p.func_180495_p(var12.func_177977_b()).func_177230_c() instanceof BlockRailBase)) {
                        return -3;
                     }
                  } else if (!var17.func_176205_b(var0, var13) && (!var9 || !(var17 instanceof BlockDoor) || var17.func_149688_o() != Material.field_151575_d)) {
                     if (var17 instanceof BlockFence || var17 instanceof BlockFenceGate || var17 instanceof BlockWall) {
                        return -3;
                     }

                     if (var17 == Blocks.field_150415_aT || var17 == Blocks.field_180400_cw) {
                        return -4;
                     }

                     Material var18 = var17.func_149688_o();
                     if (var18 != Material.field_151587_i) {
                        return 0;
                     }

                     if (!var1.func_180799_ab()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return var11 ? 2 : 1;
   }

   public void func_176175_a(boolean var1) {
      this.field_176180_f = var1;
   }

   public void func_176172_b(boolean var1) {
      this.field_176181_g = var1;
   }

   public void func_176176_c(boolean var1) {
      this.field_176183_h = var1;
   }

   public void func_176178_d(boolean var1) {
      this.field_176184_i = var1;
   }

   public boolean func_176179_b() {
      return this.field_176180_f;
   }

   public boolean func_176174_d() {
      return this.field_176184_i;
   }

   public boolean func_176173_e() {
      return this.field_176183_h;
   }
}
