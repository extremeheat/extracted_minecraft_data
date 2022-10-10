package net.minecraft.world;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeInt;
import net.minecraft.util.math.shapes.VoxelShapePartBitSet;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;

public interface IWorldReaderBase extends IBlockReader {
   boolean func_175623_d(BlockPos var1);

   Biome func_180494_b(BlockPos var1);

   int func_175642_b(EnumLightType var1, BlockPos var2);

   default boolean func_175710_j(BlockPos var1) {
      if (var1.func_177956_o() >= this.func_181545_F()) {
         return this.func_175678_i(var1);
      } else {
         BlockPos var2 = new BlockPos(var1.func_177958_n(), this.func_181545_F(), var1.func_177952_p());
         if (!this.func_175678_i(var2)) {
            return false;
         } else {
            for(var2 = var2.func_177977_b(); var2.func_177956_o() > var1.func_177956_o(); var2 = var2.func_177977_b()) {
               IBlockState var3 = this.func_180495_p(var2);
               if (var3.func_200016_a(this, var2) > 0 && !var3.func_185904_a().func_76224_d()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   int func_201669_a(BlockPos var1, int var2);

   boolean func_175680_a(int var1, int var2, boolean var3);

   boolean func_175678_i(BlockPos var1);

   default BlockPos func_205770_a(Heightmap.Type var1, BlockPos var2) {
      return new BlockPos(var2.func_177958_n(), this.func_201676_a(var1, var2.func_177958_n(), var2.func_177952_p()), var2.func_177952_p());
   }

   int func_201676_a(Heightmap.Type var1, int var2, int var3);

   default float func_205052_D(BlockPos var1) {
      return this.func_201675_m().func_177497_p()[this.func_201696_r(var1)];
   }

   @Nullable
   default EntityPlayer func_72890_a(Entity var1, double var2) {
      return this.func_184137_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2, false);
   }

   @Nullable
   default EntityPlayer func_184136_b(Entity var1, double var2) {
      return this.func_184137_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2, true);
   }

   @Nullable
   default EntityPlayer func_184137_a(double var1, double var3, double var5, double var7, boolean var9) {
      Predicate var10 = var9 ? EntitySelectors.field_188444_d : EntitySelectors.field_180132_d;
      return this.func_190525_a(var1, var3, var5, var7, var10);
   }

   @Nullable
   EntityPlayer func_190525_a(double var1, double var3, double var5, double var7, Predicate<Entity> var9);

   int func_175657_ab();

   WorldBorder func_175723_af();

   boolean func_195585_a(@Nullable Entity var1, VoxelShape var2);

   int func_175627_a(BlockPos var1, EnumFacing var2);

   boolean func_201670_d();

   int func_181545_F();

   default boolean func_195584_a(IBlockState var1, BlockPos var2) {
      VoxelShape var3 = var1.func_196952_d(this, var2);
      return var3.func_197766_b() || this.func_195585_a((Entity)null, var3.func_197751_a((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p()));
   }

   default boolean func_195587_c(@Nullable Entity var1, AxisAlignedBB var2) {
      return this.func_195585_a(var1, VoxelShapes.func_197881_a(var2));
   }

   default Stream<VoxelShape> func_212391_a(VoxelShape var1, VoxelShape var2, boolean var3) {
      int var4 = MathHelper.func_76128_c(var1.func_197762_b(EnumFacing.Axis.X)) - 1;
      int var5 = MathHelper.func_76143_f(var1.func_197758_c(EnumFacing.Axis.X)) + 1;
      int var6 = MathHelper.func_76128_c(var1.func_197762_b(EnumFacing.Axis.Y)) - 1;
      int var7 = MathHelper.func_76143_f(var1.func_197758_c(EnumFacing.Axis.Y)) + 1;
      int var8 = MathHelper.func_76128_c(var1.func_197762_b(EnumFacing.Axis.Z)) - 1;
      int var9 = MathHelper.func_76143_f(var1.func_197758_c(EnumFacing.Axis.Z)) + 1;
      WorldBorder var10 = this.func_175723_af();
      boolean var11 = var10.func_177726_b() < (double)var4 && (double)var5 < var10.func_177728_d() && var10.func_177736_c() < (double)var8 && (double)var9 < var10.func_177733_e();
      VoxelShapePartBitSet var12 = new VoxelShapePartBitSet(var5 - var4, var7 - var6, var9 - var8);
      Predicate var13 = (var1x) -> {
         return !var1x.func_197766_b() && VoxelShapes.func_197879_c(var1, var1x, IBooleanFunction.AND);
      };
      Stream var14 = StreamSupport.stream(BlockPos.MutableBlockPos.func_191531_b(var4, var6, var8, var5 - 1, var7 - 1, var9 - 1).spliterator(), false).map((var12x) -> {
         int var13 = var12x.func_177958_n();
         int var14 = var12x.func_177956_o();
         int var15 = var12x.func_177952_p();
         boolean var16 = var13 == var4 || var13 == var5 - 1;
         boolean var17 = var14 == var6 || var14 == var7 - 1;
         boolean var18 = var15 == var8 || var15 == var9 - 1;
         if ((!var16 || !var17) && (!var17 || !var18) && (!var18 || !var16) && this.func_175667_e(var12x)) {
            VoxelShape var19;
            if (var3 && !var11 && !var10.func_177746_a(var12x)) {
               var19 = VoxelShapes.func_197868_b();
            } else {
               var19 = this.func_180495_p(var12x).func_196952_d(this, var12x);
            }

            VoxelShape var20 = var2.func_197751_a((double)(-var13), (double)(-var14), (double)(-var15));
            if (VoxelShapes.func_197879_c(var20, var19, IBooleanFunction.AND)) {
               return VoxelShapes.func_197880_a();
            } else if (var19 == VoxelShapes.func_197868_b()) {
               var12.func_199625_a(var13 - var4, var14 - var6, var15 - var8, true, true);
               return VoxelShapes.func_197880_a();
            } else {
               return var19.func_197751_a((double)var13, (double)var14, (double)var15);
            }
         } else {
            return VoxelShapes.func_197880_a();
         }
      }).filter(var13);
      return Stream.concat(var14, Stream.generate(() -> {
         return new VoxelShapeInt(var12, var4, var6, var8);
      }).limit(1L).filter(var13));
   }

   default Stream<VoxelShape> func_199406_a(@Nullable Entity var1, AxisAlignedBB var2, double var3, double var5, double var7) {
      return this.func_212389_a(var1, var2, Collections.emptySet(), var3, var5, var7);
   }

   default Stream<VoxelShape> func_212389_a(@Nullable Entity var1, AxisAlignedBB var2, Set<Entity> var3, double var4, double var6, double var8) {
      double var10 = 1.0E-7D;
      VoxelShape var12 = VoxelShapes.func_197881_a(var2);
      VoxelShape var13 = VoxelShapes.func_197881_a(var2.func_72317_d(var4 > 0.0D ? -1.0E-7D : 1.0E-7D, var6 > 0.0D ? -1.0E-7D : 1.0E-7D, var8 > 0.0D ? -1.0E-7D : 1.0E-7D));
      VoxelShape var14 = VoxelShapes.func_197882_b(VoxelShapes.func_197881_a(var2.func_72321_a(var4, var6, var8).func_186662_g(1.0E-7D)), var13, IBooleanFunction.ONLY_FIRST);
      return this.func_212392_a(var1, var14, var12, var3);
   }

   default Stream<VoxelShape> func_212388_b(@Nullable Entity var1, AxisAlignedBB var2) {
      return this.func_212392_a(var1, VoxelShapes.func_197881_a(var2), VoxelShapes.func_197880_a(), Collections.emptySet());
   }

   default Stream<VoxelShape> func_212392_a(@Nullable Entity var1, VoxelShape var2, VoxelShape var3, Set<Entity> var4) {
      boolean var5 = var1 != null && var1.func_174832_aS();
      boolean var6 = var1 != null && this.func_191503_g(var1);
      if (var1 != null && var5 == var6) {
         var1.func_174821_h(!var6);
      }

      return this.func_212391_a(var2, var3, var6);
   }

   default boolean func_191503_g(Entity var1) {
      WorldBorder var2 = this.func_175723_af();
      double var3 = var2.func_177726_b();
      double var5 = var2.func_177736_c();
      double var7 = var2.func_177728_d();
      double var9 = var2.func_177733_e();
      if (var1.func_174832_aS()) {
         ++var3;
         ++var5;
         --var7;
         --var9;
      } else {
         --var3;
         --var5;
         ++var7;
         ++var9;
      }

      return var1.field_70165_t > var3 && var1.field_70165_t < var7 && var1.field_70161_v > var5 && var1.field_70161_v < var9;
   }

   default boolean func_211156_a(@Nullable Entity var1, AxisAlignedBB var2, Set<Entity> var3) {
      return this.func_212392_a(var1, VoxelShapes.func_197881_a(var2), VoxelShapes.func_197880_a(), var3).allMatch(VoxelShape::func_197766_b);
   }

   default boolean func_195586_b(@Nullable Entity var1, AxisAlignedBB var2) {
      return this.func_211156_a(var1, var2, Collections.emptySet());
   }

   default boolean func_201671_F(BlockPos var1) {
      return this.func_204610_c(var1).func_206884_a(FluidTags.field_206959_a);
   }

   default boolean func_72953_d(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76143_f(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76143_f(var1.field_72337_e);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76143_f(var1.field_72334_f);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  IBlockState var13 = this.func_180495_p(var8.func_181079_c(var10, var11, var12));
                  if (!var13.func_204520_s().func_206888_e()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }

      return false;
   }

   default int func_201696_r(BlockPos var1) {
      return this.func_205049_d(var1, this.func_175657_ab());
   }

   default int func_205049_d(BlockPos var1, int var2) {
      if (var1.func_177958_n() >= -30000000 && var1.func_177952_p() >= -30000000 && var1.func_177958_n() < 30000000 && var1.func_177952_p() < 30000000) {
         if (this.func_180495_p(var1).func_200130_c(this, var1)) {
            int var3 = this.func_201669_a(var1.func_177984_a(), var2);
            int var4 = this.func_201669_a(var1.func_177974_f(), var2);
            int var5 = this.func_201669_a(var1.func_177976_e(), var2);
            int var6 = this.func_201669_a(var1.func_177968_d(), var2);
            int var7 = this.func_201669_a(var1.func_177978_c(), var2);
            if (var4 > var3) {
               var3 = var4;
            }

            if (var5 > var3) {
               var3 = var5;
            }

            if (var6 > var3) {
               var3 = var6;
            }

            if (var7 > var3) {
               var3 = var7;
            }

            return var3;
         } else {
            return this.func_201669_a(var1, var2);
         }
      } else {
         return 15;
      }
   }

   default boolean func_175667_e(BlockPos var1) {
      return this.func_175668_a(var1, true);
   }

   default boolean func_175668_a(BlockPos var1, boolean var2) {
      return this.func_175680_a(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4, var2);
   }

   default boolean func_205050_e(BlockPos var1, int var2) {
      return this.func_175648_a(var1, var2, true);
   }

   default boolean func_175648_a(BlockPos var1, int var2, boolean var3) {
      return this.func_175663_a(var1.func_177958_n() - var2, var1.func_177956_o() - var2, var1.func_177952_p() - var2, var1.func_177958_n() + var2, var1.func_177956_o() + var2, var1.func_177952_p() + var2, var3);
   }

   default boolean func_175707_a(BlockPos var1, BlockPos var2) {
      return this.func_175706_a(var1, var2, true);
   }

   default boolean func_175706_a(BlockPos var1, BlockPos var2, boolean var3) {
      return this.func_175663_a(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p(), var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p(), var3);
   }

   default boolean func_175711_a(MutableBoundingBox var1) {
      return this.func_175639_b(var1, true);
   }

   default boolean func_175639_b(MutableBoundingBox var1, boolean var2) {
      return this.func_175663_a(var1.field_78897_a, var1.field_78895_b, var1.field_78896_c, var1.field_78893_d, var1.field_78894_e, var1.field_78892_f, var2);
   }

   default boolean func_175663_a(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      if (var5 >= 0 && var2 < 256) {
         var1 >>= 4;
         var3 >>= 4;
         var4 >>= 4;
         var6 >>= 4;

         for(int var8 = var1; var8 <= var4; ++var8) {
            for(int var9 = var3; var9 <= var6; ++var9) {
               if (!this.func_175680_a(var8, var9, var7)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   Dimension func_201675_m();
}
