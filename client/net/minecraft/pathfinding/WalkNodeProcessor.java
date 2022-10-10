package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class WalkNodeProcessor extends NodeProcessor {
   protected float field_176183_h;

   public WalkNodeProcessor() {
      super();
   }

   public void func_186315_a(IBlockReader var1, EntityLiving var2) {
      super.func_186315_a(var1, var2);
      this.field_176183_h = var2.func_184643_a(PathNodeType.WATER);
   }

   public void func_176163_a() {
      this.field_186326_b.func_184644_a(PathNodeType.WATER, this.field_176183_h);
      super.func_176163_a();
   }

   public PathPoint func_186318_b() {
      int var1;
      BlockPos var2;
      if (this.func_186322_e() && this.field_186326_b.func_70090_H()) {
         var1 = (int)this.field_186326_b.func_174813_aQ().field_72338_b;
         BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(this.field_186326_b.field_70165_t), var1, MathHelper.func_76128_c(this.field_186326_b.field_70161_v));

         for(Block var3 = this.field_176169_a.func_180495_p(var8).func_177230_c(); var3 == Blocks.field_150355_j; var3 = this.field_176169_a.func_180495_p(var8).func_177230_c()) {
            ++var1;
            var8.func_181079_c(MathHelper.func_76128_c(this.field_186326_b.field_70165_t), var1, MathHelper.func_76128_c(this.field_186326_b.field_70161_v));
         }

         --var1;
      } else if (this.field_186326_b.field_70122_E) {
         var1 = MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72338_b + 0.5D);
      } else {
         for(var2 = new BlockPos(this.field_186326_b); (this.field_176169_a.func_180495_p(var2).func_196958_f() || this.field_176169_a.func_180495_p(var2).func_196957_g(this.field_176169_a, var2, PathType.LAND)) && var2.func_177956_o() > 0; var2 = var2.func_177977_b()) {
         }

         var1 = var2.func_177984_a().func_177956_o();
      }

      var2 = new BlockPos(this.field_186326_b);
      PathNodeType var9 = this.func_186331_a(this.field_186326_b, var2.func_177958_n(), var1, var2.func_177952_p());
      if (this.field_186326_b.func_184643_a(var9) < 0.0F) {
         HashSet var4 = Sets.newHashSet();
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72340_a, (double)var1, this.field_186326_b.func_174813_aQ().field_72339_c));
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72340_a, (double)var1, this.field_186326_b.func_174813_aQ().field_72334_f));
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72336_d, (double)var1, this.field_186326_b.func_174813_aQ().field_72339_c));
         var4.add(new BlockPos(this.field_186326_b.func_174813_aQ().field_72336_d, (double)var1, this.field_186326_b.func_174813_aQ().field_72334_f));
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            PathNodeType var7 = this.func_186329_a(this.field_186326_b, var6);
            if (this.field_186326_b.func_184643_a(var7) >= 0.0F) {
               return this.func_176159_a(var6.func_177958_n(), var6.func_177956_o(), var6.func_177952_p());
            }
         }
      }

      return this.func_176159_a(var2.func_177958_n(), var1, var2.func_177952_p());
   }

   public PathPoint func_186325_a(double var1, double var3, double var5) {
      return this.func_176159_a(MathHelper.func_76128_c(var1), MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var5));
   }

   public int func_186320_a(PathPoint[] var1, PathPoint var2, PathPoint var3, float var4) {
      int var5 = 0;
      int var6 = 0;
      PathNodeType var7 = this.func_186331_a(this.field_186326_b, var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c);
      if (this.field_186326_b.func_184643_a(var7) >= 0.0F) {
         var6 = MathHelper.func_76141_d(Math.max(1.0F, this.field_186326_b.field_70138_W));
      }

      double var8 = func_197682_a(this.field_176169_a, new BlockPos(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c));
      PathPoint var10 = this.func_186332_a(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c + 1, var6, var8, EnumFacing.SOUTH);
      PathPoint var11 = this.func_186332_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c, var6, var8, EnumFacing.WEST);
      PathPoint var12 = this.func_186332_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c, var6, var8, EnumFacing.EAST);
      PathPoint var13 = this.func_186332_a(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c - 1, var6, var8, EnumFacing.NORTH);
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

      boolean var14 = var13 == null || var13.field_186287_m == PathNodeType.OPEN || var13.field_186286_l != 0.0F;
      boolean var15 = var10 == null || var10.field_186287_m == PathNodeType.OPEN || var10.field_186286_l != 0.0F;
      boolean var16 = var12 == null || var12.field_186287_m == PathNodeType.OPEN || var12.field_186286_l != 0.0F;
      boolean var17 = var11 == null || var11.field_186287_m == PathNodeType.OPEN || var11.field_186286_l != 0.0F;
      PathPoint var18;
      if (var14 && var17) {
         var18 = this.func_186332_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c - 1, var6, var8, EnumFacing.NORTH);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var14 && var16) {
         var18 = this.func_186332_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c - 1, var6, var8, EnumFacing.NORTH);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var15 && var17) {
         var18 = this.func_186332_a(var2.field_75839_a - 1, var2.field_75837_b, var2.field_75838_c + 1, var6, var8, EnumFacing.SOUTH);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      if (var15 && var16) {
         var18 = this.func_186332_a(var2.field_75839_a + 1, var2.field_75837_b, var2.field_75838_c + 1, var6, var8, EnumFacing.SOUTH);
         if (var18 != null && !var18.field_75842_i && var18.func_75829_a(var3) < var4) {
            var1[var5++] = var18;
         }
      }

      return var5;
   }

   @Nullable
   private PathPoint func_186332_a(int var1, int var2, int var3, int var4, double var5, EnumFacing var7) {
      PathPoint var8 = null;
      BlockPos var9 = new BlockPos(var1, var2, var3);
      double var10 = func_197682_a(this.field_176169_a, var9);
      if (var10 - var5 > 1.125D) {
         return null;
      } else {
         PathNodeType var12 = this.func_186331_a(this.field_186326_b, var1, var2, var3);
         float var13 = this.field_186326_b.func_184643_a(var12);
         double var14 = (double)this.field_186326_b.field_70130_N / 2.0D;
         if (var13 >= 0.0F) {
            var8 = this.func_176159_a(var1, var2, var3);
            var8.field_186287_m = var12;
            var8.field_186286_l = Math.max(var8.field_186286_l, var13);
         }

         if (var12 == PathNodeType.WALKABLE) {
            return var8;
         } else {
            if (var8 == null && var4 > 0 && var12 != PathNodeType.FENCE && var12 != PathNodeType.TRAPDOOR) {
               var8 = this.func_186332_a(var1, var2 + 1, var3, var4 - 1, var5, var7);
               if (var8 != null && (var8.field_186287_m == PathNodeType.OPEN || var8.field_186287_m == PathNodeType.WALKABLE) && this.field_186326_b.field_70130_N < 1.0F) {
                  double var16 = (double)(var1 - var7.func_82601_c()) + 0.5D;
                  double var18 = (double)(var3 - var7.func_82599_e()) + 0.5D;
                  AxisAlignedBB var20 = new AxisAlignedBB(var16 - var14, (double)var2 + 0.001D, var18 - var14, var16 + var14, (double)this.field_186326_b.field_70131_O + func_197682_a(this.field_176169_a, var9.func_177984_a()) - 0.002D, var18 + var14);
                  if (!this.field_186326_b.field_70170_p.func_195586_b((Entity)null, var20)) {
                     var8 = null;
                  }
               }
            }

            if (var12 == PathNodeType.WATER && !this.func_186322_e()) {
               if (this.func_186331_a(this.field_186326_b, var1, var2 - 1, var3) != PathNodeType.WATER) {
                  return var8;
               }

               while(var2 > 0) {
                  --var2;
                  var12 = this.func_186331_a(this.field_186326_b, var1, var2, var3);
                  if (var12 != PathNodeType.WATER) {
                     return var8;
                  }

                  var8 = this.func_176159_a(var1, var2, var3);
                  var8.field_186287_m = var12;
                  var8.field_186286_l = Math.max(var8.field_186286_l, this.field_186326_b.func_184643_a(var12));
               }
            }

            if (var12 == PathNodeType.OPEN) {
               AxisAlignedBB var21 = new AxisAlignedBB((double)var1 - var14 + 0.5D, (double)var2 + 0.001D, (double)var3 - var14 + 0.5D, (double)var1 + var14 + 0.5D, (double)((float)var2 + this.field_186326_b.field_70131_O), (double)var3 + var14 + 0.5D);
               if (!this.field_186326_b.field_70170_p.func_195586_b((Entity)null, var21)) {
                  return null;
               }

               if (this.field_186326_b.field_70130_N >= 1.0F) {
                  PathNodeType var17 = this.func_186331_a(this.field_186326_b, var1, var2 - 1, var3);
                  if (var17 == PathNodeType.BLOCKED) {
                     var8 = this.func_176159_a(var1, var2, var3);
                     var8.field_186287_m = PathNodeType.WALKABLE;
                     var8.field_186286_l = Math.max(var8.field_186286_l, var13);
                     return var8;
                  }
               }

               int var22 = 0;

               while(var2 > 0 && var12 == PathNodeType.OPEN) {
                  --var2;
                  if (var22++ >= this.field_186326_b.func_82143_as()) {
                     return null;
                  }

                  var12 = this.func_186331_a(this.field_186326_b, var1, var2, var3);
                  var13 = this.field_186326_b.func_184643_a(var12);
                  if (var12 != PathNodeType.OPEN && var13 >= 0.0F) {
                     var8 = this.func_176159_a(var1, var2, var3);
                     var8.field_186287_m = var12;
                     var8.field_186286_l = Math.max(var8.field_186286_l, var13);
                     break;
                  }

                  if (var13 < 0.0F) {
                     return null;
                  }
               }
            }

            return var8;
         }
      }
   }

   public static double func_197682_a(IBlockReader var0, BlockPos var1) {
      BlockPos var2 = var1.func_177977_b();
      VoxelShape var3 = var0.func_180495_p(var2).func_196952_d(var0, var2);
      return (double)var2.func_177956_o() + (var3.func_197766_b() ? 0.0D : var3.func_197758_c(EnumFacing.Axis.Y));
   }

   public PathNodeType func_186319_a(IBlockReader var1, int var2, int var3, int var4, EntityLiving var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet var11 = EnumSet.noneOf(PathNodeType.class);
      PathNodeType var12 = PathNodeType.BLOCKED;
      double var13 = (double)var5.field_70130_N / 2.0D;
      BlockPos var15 = new BlockPos(var5);
      var12 = this.func_193577_a(var1, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var15);
      if (var11.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType var16 = PathNodeType.BLOCKED;
         Iterator var17 = var11.iterator();

         while(var17.hasNext()) {
            PathNodeType var18 = (PathNodeType)var17.next();
            if (var5.func_184643_a(var18) < 0.0F) {
               return var18;
            }

            if (var5.func_184643_a(var18) >= var5.func_184643_a(var16)) {
               var16 = var18;
            }
         }

         if (var12 == PathNodeType.OPEN && var5.func_184643_a(var16) == 0.0F) {
            return PathNodeType.OPEN;
         } else {
            return var16;
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
               if (var19 == PathNodeType.DOOR_WOOD_CLOSED && var8 && var9) {
                  var19 = PathNodeType.WALKABLE;
               }

               if (var19 == PathNodeType.DOOR_OPEN && !var9) {
                  var19 = PathNodeType.BLOCKED;
               }

               if (var19 == PathNodeType.RAIL && !(var1.func_180495_p(var12).func_177230_c() instanceof BlockRailBase) && !(var1.func_180495_p(var12.func_177977_b()).func_177230_c() instanceof BlockRailBase)) {
                  var19 = PathNodeType.FENCE;
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

   private PathNodeType func_186329_a(EntityLiving var1, BlockPos var2) {
      return this.func_186331_a(var1, var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
   }

   private PathNodeType func_186331_a(EntityLiving var1, int var2, int var3, int var4) {
      return this.func_186319_a(this.field_176169_a, var2, var3, var4, var1, this.field_176168_c, this.field_176165_d, this.field_176166_e, this.func_186324_d(), this.func_186323_c());
   }

   public PathNodeType func_186330_a(IBlockReader var1, int var2, int var3, int var4) {
      PathNodeType var5 = this.func_189553_b(var1, var2, var3, var4);
      if (var5 == PathNodeType.OPEN && var3 >= 1) {
         Block var6 = var1.func_180495_p(new BlockPos(var2, var3 - 1, var4)).func_177230_c();
         PathNodeType var7 = this.func_189553_b(var1, var2, var3 - 1, var4);
         var5 = var7 != PathNodeType.WALKABLE && var7 != PathNodeType.OPEN && var7 != PathNodeType.WATER && var7 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
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

   public PathNodeType func_193578_a(IBlockReader var1, int var2, int var3, int var4, PathNodeType var5) {
      if (var5 == PathNodeType.WALKABLE) {
         BlockPos.PooledMutableBlockPos var6 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var7 = null;

         try {
            for(int var8 = -1; var8 <= 1; ++var8) {
               for(int var9 = -1; var9 <= 1; ++var9) {
                  if (var8 != 0 || var9 != 0) {
                     Block var10 = var1.func_180495_p(var6.func_181079_c(var8 + var2, var3, var9 + var4)).func_177230_c();
                     if (var10 == Blocks.field_150434_aF) {
                        var5 = PathNodeType.DANGER_CACTUS;
                     } else if (var10 == Blocks.field_150480_ab) {
                        var5 = PathNodeType.DANGER_FIRE;
                     }
                  }
               }
            }
         } catch (Throwable var18) {
            var7 = var18;
            throw var18;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var17) {
                     var7.addSuppressed(var17);
                  }
               } else {
                  var6.close();
               }
            }

         }
      }

      return var5;
   }

   protected PathNodeType func_189553_b(IBlockReader var1, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      IBlockState var6 = var1.func_180495_p(var5);
      Block var7 = var6.func_177230_c();
      Material var8 = var6.func_185904_a();
      if (var6.func_196958_f()) {
         return PathNodeType.OPEN;
      } else if (!var7.func_203417_a(BlockTags.field_212185_E) && var7 != Blocks.field_196651_dG) {
         if (var7 == Blocks.field_150480_ab) {
            return PathNodeType.DAMAGE_FIRE;
         } else if (var7 == Blocks.field_150434_aF) {
            return PathNodeType.DAMAGE_CACTUS;
         } else if (var7 instanceof BlockDoor && var8 == Material.field_151575_d && !(Boolean)var6.func_177229_b(BlockDoor.field_176519_b)) {
            return PathNodeType.DOOR_WOOD_CLOSED;
         } else if (var7 instanceof BlockDoor && var8 == Material.field_151573_f && !(Boolean)var6.func_177229_b(BlockDoor.field_176519_b)) {
            return PathNodeType.DOOR_IRON_CLOSED;
         } else if (var7 instanceof BlockDoor && (Boolean)var6.func_177229_b(BlockDoor.field_176519_b)) {
            return PathNodeType.DOOR_OPEN;
         } else if (var7 instanceof BlockRailBase) {
            return PathNodeType.RAIL;
         } else if (var7 instanceof BlockFence || var7 instanceof BlockWall || var7 instanceof BlockFenceGate && !(Boolean)var6.func_177229_b(BlockFenceGate.field_176466_a)) {
            return PathNodeType.FENCE;
         } else {
            IFluidState var9 = var1.func_204610_c(var5);
            if (var9.func_206884_a(FluidTags.field_206959_a)) {
               return PathNodeType.WATER;
            } else if (var9.func_206884_a(FluidTags.field_206960_b)) {
               return PathNodeType.LAVA;
            } else {
               return var6.func_196957_g(var1, var5, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
            }
         }
      } else {
         return PathNodeType.TRAPDOOR;
      }
   }
}
