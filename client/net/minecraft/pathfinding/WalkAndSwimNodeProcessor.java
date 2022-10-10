package net.minecraft.pathfinding;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class WalkAndSwimNodeProcessor extends WalkNodeProcessor {
   private float field_203247_k;
   private float field_203248_l;

   public WalkAndSwimNodeProcessor() {
      super();
   }

   public void func_186315_a(IBlockReader var1, EntityLiving var2) {
      super.func_186315_a(var1, var2);
      var2.func_184644_a(PathNodeType.WATER, 0.0F);
      this.field_203247_k = var2.func_184643_a(PathNodeType.WALKABLE);
      var2.func_184644_a(PathNodeType.WALKABLE, 6.0F);
      this.field_203248_l = var2.func_184643_a(PathNodeType.WATER_BORDER);
      var2.func_184644_a(PathNodeType.WATER_BORDER, 4.0F);
   }

   public void func_176163_a() {
      this.field_186326_b.func_184644_a(PathNodeType.WALKABLE, this.field_203247_k);
      this.field_186326_b.func_184644_a(PathNodeType.WATER_BORDER, this.field_203248_l);
      super.func_176163_a();
   }

   public PathPoint func_186318_b() {
      return this.func_176159_a(MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72340_a), MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72338_b + 0.5D), MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72339_c));
   }

   public PathPoint func_186325_a(double var1, double var3, double var5) {
      return this.func_176159_a(MathHelper.func_76128_c(var1), MathHelper.func_76128_c(var3 + 0.5D), MathHelper.func_76128_c(var5));
   }

   public int func_186320_a(PathPoint[] var1, PathPoint var2, PathPoint var3, float var4) {
      int var5 = 0;
      boolean var6 = true;
      BlockPos var7 = new BlockPos(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c);
      double var8 = this.func_203246_a(var7);
      PathPoint var10 = this.func_203245_a(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c + 1, 1, var8);
      PathPoint var11 = this.func_203245_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c, 1, var8);
      PathPoint var12 = this.func_203245_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c, 1, var8);
      PathPoint var13 = this.func_203245_a(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c - 1, 1, var8);
      PathPoint var14 = this.func_203245_a(var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c, 0, var8);
      PathPoint var15 = this.func_203245_a(var2.field_75839_a, var2.field_75837_b - 1, var2.field_75838_c, 1, var8);
      if (var10 != null && !var10.field_75842_i && var10.func_75829_a(var3) < var4) {
         var1[var5++] = var10;
      }

      if (var11 != null && !var11.field_75842_i && var11.func_75829_a(var3) < var4) {
         var1[var5++] = var11;
      }

      if (var12 != null && !var12.field_75842_i && var12.func_75829_a(var3) < var4) {
         var1[var5++] = var12;
      }

      if (var13 != null && !var13.field_75842_i && var13.func_75829_a(var3) < var4) {
         var1[var5++] = var13;
      }

      if (var14 != null && !var14.field_75842_i && var14.func_75829_a(var3) < var4) {
         var1[var5++] = var14;
      }

      if (var15 != null && !var15.field_75842_i && var15.func_75829_a(var3) < var4) {
         var1[var5++] = var15;
      }

      boolean var16 = var13 == null || var13.field_186287_m == PathNodeType.OPEN || var13.field_186286_l != 0.0F;
      boolean var17 = var10 == null || var10.field_186287_m == PathNodeType.OPEN || var10.field_186286_l != 0.0F;
      boolean var18 = var12 == null || var12.field_186287_m == PathNodeType.OPEN || var12.field_186286_l != 0.0F;
      boolean var19 = var11 == null || var11.field_186287_m == PathNodeType.OPEN || var11.field_186286_l != 0.0F;
      PathPoint var20;
      if (var16 && var19) {
         var20 = this.func_203245_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c - 1, 1, var8);
         if (var20 != null && !var20.field_75842_i && var20.func_75829_a(var3) < var4) {
            var1[var5++] = var20;
         }
      }

      if (var16 && var18) {
         var20 = this.func_203245_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c - 1, 1, var8);
         if (var20 != null && !var20.field_75842_i && var20.func_75829_a(var3) < var4) {
            var1[var5++] = var20;
         }
      }

      if (var17 && var19) {
         var20 = this.func_203245_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c + 1, 1, var8);
         if (var20 != null && !var20.field_75842_i && var20.func_75829_a(var3) < var4) {
            var1[var5++] = var20;
         }
      }

      if (var17 && var18) {
         var20 = this.func_203245_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c + 1, 1, var8);
         if (var20 != null && !var20.field_75842_i && var20.func_75829_a(var3) < var4) {
            var1[var5++] = var20;
         }
      }

      return var5;
   }

   private double func_203246_a(BlockPos var1) {
      if (!this.field_186326_b.func_70090_H()) {
         BlockPos var2 = var1.func_177977_b();
         VoxelShape var3 = this.field_176169_a.func_180495_p(var2).func_196952_d(this.field_176169_a, var2);
         return (double)var2.func_177956_o() + (var3.func_197766_b() ? 0.0D : var3.func_197758_c(EnumFacing.Axis.Y));
      } else {
         return (double)var1.func_177956_o() + 0.5D;
      }
   }

   @Nullable
   private PathPoint func_203245_a(int var1, int var2, int var3, int var4, double var5) {
      PathPoint var7 = null;
      BlockPos var8 = new BlockPos(var1, var2, var3);
      double var9 = this.func_203246_a(var8);
      if (var9 - var5 > 1.125D) {
         return null;
      } else {
         PathNodeType var11 = this.func_186319_a(this.field_176169_a, var1, var2, var3, this.field_186326_b, this.field_176168_c, this.field_176165_d, this.field_176166_e, false, false);
         float var12 = this.field_186326_b.func_184643_a(var11);
         double var13 = (double)this.field_186326_b.field_70130_N / 2.0D;
         if (var12 >= 0.0F) {
            var7 = this.func_176159_a(var1, var2, var3);
            var7.field_186287_m = var11;
            var7.field_186286_l = Math.max(var7.field_186286_l, var12);
         }

         if (var11 != PathNodeType.WATER && var11 != PathNodeType.WALKABLE) {
            if (var7 == null && var4 > 0 && var11 != PathNodeType.FENCE && var11 != PathNodeType.TRAPDOOR) {
               var7 = this.func_203245_a(var1, var2 + 1, var3, var4 - 1, var5);
            }

            if (var11 == PathNodeType.OPEN) {
               AxisAlignedBB var15 = new AxisAlignedBB((double)var1 - var13 + 0.5D, (double)var2 + 0.001D, (double)var3 - var13 + 0.5D, (double)var1 + var13 + 0.5D, (double)((float)var2 + this.field_186326_b.field_70131_O), (double)var3 + var13 + 0.5D);
               if (!this.field_186326_b.field_70170_p.func_195586_b((Entity)null, var15)) {
                  return null;
               }

               PathNodeType var16 = this.func_186319_a(this.field_176169_a, var1, var2 - 1, var3, this.field_186326_b, this.field_176168_c, this.field_176165_d, this.field_176166_e, false, false);
               if (var16 == PathNodeType.BLOCKED) {
                  var7 = this.func_176159_a(var1, var2, var3);
                  var7.field_186287_m = PathNodeType.WALKABLE;
                  var7.field_186286_l = Math.max(var7.field_186286_l, var12);
                  return var7;
               }

               if (var16 == PathNodeType.WATER) {
                  var7 = this.func_176159_a(var1, var2, var3);
                  var7.field_186287_m = PathNodeType.WATER;
                  var7.field_186286_l = Math.max(var7.field_186286_l, var12);
                  return var7;
               }

               int var17 = 0;

               while(var2 > 0 && var11 == PathNodeType.OPEN) {
                  --var2;
                  if (var17++ >= this.field_186326_b.func_82143_as()) {
                     return null;
                  }

                  var11 = this.func_186319_a(this.field_176169_a, var1, var2, var3, this.field_186326_b, this.field_176168_c, this.field_176165_d, this.field_176166_e, false, false);
                  var12 = this.field_186326_b.func_184643_a(var11);
                  if (var11 != PathNodeType.OPEN && var12 >= 0.0F) {
                     var7 = this.func_176159_a(var1, var2, var3);
                     var7.field_186287_m = var11;
                     var7.field_186286_l = Math.max(var7.field_186286_l, var12);
                     break;
                  }

                  if (var12 < 0.0F) {
                     return null;
                  }
               }
            }

            return var7;
         } else {
            if (var2 < this.field_186326_b.field_70170_p.func_181545_F() - 10 && var7 != null) {
               ++var7.field_186286_l;
            }

            return var7;
         }
      }
   }

   public PathNodeType func_193577_a(IBlockReader var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, EnumSet<PathNodeType> var10, PathNodeType var11, BlockPos var12) {
      for(int var13 = 0; var13 < var5; ++var13) {
         for(int var14 = 0; var14 < var6; ++var14) {
            for(int var15 = 0; var15 < var7; ++var15) {
               int var16 = var13 + var2;
               int var17 = var14 + var3;
               int var18 = var15 + var4;
               PathNodeType var19 = this.func_186330_a(var1, var16, var17, var18);
               if (var19 == PathNodeType.RAIL && !(var1.func_180495_p(var12).func_177230_c() instanceof BlockRailBase) && !(var1.func_180495_p(var12.func_177977_b()).func_177230_c() instanceof BlockRailBase)) {
                  var19 = PathNodeType.FENCE;
               }

               if (var19 == PathNodeType.DOOR_OPEN || var19 == PathNodeType.DOOR_WOOD_CLOSED || var19 == PathNodeType.DOOR_IRON_CLOSED) {
                  var19 = PathNodeType.BLOCKED;
               }

               if (var13 == 0 && var14 == 0 && var15 == 0) {
                  var11 = var19;
               }

               var10.add(var19);
            }
         }
      }

      return var11;
   }

   public PathNodeType func_186330_a(IBlockReader var1, int var2, int var3, int var4) {
      PathNodeType var5 = this.func_189553_b(var1, var2, var3, var4);
      if (var5 == PathNodeType.WATER) {
         EnumFacing[] var11 = EnumFacing.values();
         int var12 = var11.length;

         for(int var8 = 0; var8 < var12; ++var8) {
            EnumFacing var9 = var11[var8];
            PathNodeType var10 = this.func_189553_b(var1, var2 + var9.func_82601_c(), var3 + var9.func_96559_d(), var4 + var9.func_82599_e());
            if (var10 == PathNodeType.BLOCKED) {
               return PathNodeType.WATER_BORDER;
            }
         }

         return PathNodeType.WATER;
      } else {
         if (var5 == PathNodeType.OPEN && var3 >= 1) {
            Block var6 = var1.func_180495_p(new BlockPos(var2, var3 - 1, var4)).func_177230_c();
            PathNodeType var7 = this.func_189553_b(var1, var2, var3 - 1, var4);
            if (var7 != PathNodeType.WALKABLE && var7 != PathNodeType.OPEN && var7 != PathNodeType.LAVA) {
               var5 = PathNodeType.WALKABLE;
            } else {
               var5 = PathNodeType.OPEN;
            }

            if (var7 == PathNodeType.DAMAGE_FIRE || var6 == Blocks.field_196814_hQ) {
               var5 = PathNodeType.DAMAGE_FIRE;
            }

            if (var7 == PathNodeType.DAMAGE_CACTUS) {
               var5 = PathNodeType.DAMAGE_CACTUS;
            }
         }

         var5 = this.func_193578_a(var1, var2, var3, var4, var5);
         return var5;
      }
   }
}
