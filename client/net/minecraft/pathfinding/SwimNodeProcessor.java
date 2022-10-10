package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class SwimNodeProcessor extends NodeProcessor {
   private final boolean field_205202_j;

   public SwimNodeProcessor(boolean var1) {
      super();
      this.field_205202_j = var1;
   }

   public PathPoint func_186318_b() {
      return super.func_176159_a(MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72340_a), MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72338_b + 0.5D), MathHelper.func_76128_c(this.field_186326_b.func_174813_aQ().field_72339_c));
   }

   public PathPoint func_186325_a(double var1, double var3, double var5) {
      return super.func_176159_a(MathHelper.func_76128_c(var1 - (double)(this.field_186326_b.field_70130_N / 2.0F)), MathHelper.func_76128_c(var3 + 0.5D), MathHelper.func_76128_c(var5 - (double)(this.field_186326_b.field_70130_N / 2.0F)));
   }

   public int func_186320_a(PathPoint[] var1, PathPoint var2, PathPoint var3, float var4) {
      int var5 = 0;
      EnumFacing[] var6 = EnumFacing.values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         PathPoint var10 = this.func_186328_b(var2.field_75839_a + var9.func_82601_c(), var2.field_75837_b + var9.func_96559_d(), var2.field_75838_c + var9.func_82599_e());
         if (var10 != null && !var10.field_75842_i && var10.func_75829_a(var3) < var4) {
            var1[var5++] = var10;
         }
      }

      return var5;
   }

   public PathNodeType func_186319_a(IBlockReader var1, int var2, int var3, int var4, EntityLiving var5, int var6, int var7, int var8, boolean var9, boolean var10) {
      return this.func_186330_a(var1, var2, var3, var4);
   }

   public PathNodeType func_186330_a(IBlockReader var1, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      IFluidState var6 = var1.func_204610_c(var5);
      IBlockState var7 = var1.func_180495_p(var5);
      if (var6.func_206888_e() && var7.func_196957_g(var1, var5.func_177977_b(), PathType.WATER) && var7.func_196958_f()) {
         return PathNodeType.BREACH;
      } else {
         return var6.func_206884_a(FluidTags.field_206959_a) && var7.func_196957_g(var1, var5, PathType.WATER) ? PathNodeType.WATER : PathNodeType.BLOCKED;
      }
   }

   @Nullable
   private PathPoint func_186328_b(int var1, int var2, int var3) {
      PathNodeType var4 = this.func_186327_c(var1, var2, var3);
      return (!this.field_205202_j || var4 != PathNodeType.BREACH) && var4 != PathNodeType.WATER ? null : this.func_176159_a(var1, var2, var3);
   }

   @Nullable
   protected PathPoint func_176159_a(int var1, int var2, int var3) {
      PathPoint var4 = null;
      PathNodeType var5 = this.func_186330_a(this.field_186326_b.field_70170_p, var1, var2, var3);
      float var6 = this.field_186326_b.func_184643_a(var5);
      if (var6 >= 0.0F) {
         var4 = super.func_176159_a(var1, var2, var3);
         var4.field_186287_m = var5;
         var4.field_186286_l = Math.max(var4.field_186286_l, var6);
         if (this.field_176169_a.func_204610_c(new BlockPos(var1, var2, var3)).func_206888_e()) {
            var4.field_186286_l += 8.0F;
         }
      }

      return var5 == PathNodeType.OPEN ? var4 : var4;
   }

   private PathNodeType func_186327_c(int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = var1; var5 < var1 + this.field_176168_c; ++var5) {
         for(int var6 = var2; var6 < var2 + this.field_176165_d; ++var6) {
            for(int var7 = var3; var7 < var3 + this.field_176166_e; ++var7) {
               IFluidState var8 = this.field_176169_a.func_204610_c(var4.func_181079_c(var5, var6, var7));
               IBlockState var9 = this.field_176169_a.func_180495_p(var4.func_181079_c(var5, var6, var7));
               if (var8.func_206888_e() && var9.func_196957_g(this.field_176169_a, var4.func_177977_b(), PathType.WATER) && var9.func_196958_f()) {
                  return PathNodeType.BREACH;
               }

               if (!var8.func_206884_a(FluidTags.field_206959_a)) {
                  return PathNodeType.BLOCKED;
               }
            }
         }
      }

      IBlockState var10 = this.field_176169_a.func_180495_p(var4);
      if (var10.func_196957_g(this.field_176169_a, var4, PathType.WATER)) {
         return PathNodeType.WATER;
      } else {
         return PathNodeType.BLOCKED;
      }
   }
}
