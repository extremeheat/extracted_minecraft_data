package net.minecraft.block;

import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockFourWay extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_196409_a;
   public static final BooleanProperty field_196411_b;
   public static final BooleanProperty field_196413_c;
   public static final BooleanProperty field_196414_y;
   public static final BooleanProperty field_204514_u;
   protected static final Map<EnumFacing, BooleanProperty> field_196415_z;
   protected final VoxelShape[] field_196410_A;
   protected final VoxelShape[] field_196412_B;

   protected BlockFourWay(float var1, float var2, float var3, float var4, float var5, Block.Properties var6) {
      super(var6);
      this.field_196410_A = this.func_196408_a(var1, var2, var5, 0.0F, var5);
      this.field_196412_B = this.func_196408_a(var1, var2, var3, 0.0F, var4);
   }

   protected VoxelShape[] func_196408_a(float var1, float var2, float var3, float var4, float var5) {
      float var6 = 8.0F - var1;
      float var7 = 8.0F + var1;
      float var8 = 8.0F - var2;
      float var9 = 8.0F + var2;
      VoxelShape var10 = Block.func_208617_a((double)var6, 0.0D, (double)var6, (double)var7, (double)var3, (double)var7);
      VoxelShape var11 = Block.func_208617_a((double)var8, (double)var4, 0.0D, (double)var9, (double)var5, (double)var9);
      VoxelShape var12 = Block.func_208617_a((double)var8, (double)var4, (double)var8, (double)var9, (double)var5, 16.0D);
      VoxelShape var13 = Block.func_208617_a(0.0D, (double)var4, (double)var8, (double)var9, (double)var5, (double)var9);
      VoxelShape var14 = Block.func_208617_a((double)var8, (double)var4, (double)var8, 16.0D, (double)var5, (double)var9);
      VoxelShape var15 = VoxelShapes.func_197872_a(var11, var14);
      VoxelShape var16 = VoxelShapes.func_197872_a(var12, var13);
      VoxelShape[] var17 = new VoxelShape[]{VoxelShapes.func_197880_a(), var12, var13, var16, var11, VoxelShapes.func_197872_a(var12, var11), VoxelShapes.func_197872_a(var13, var11), VoxelShapes.func_197872_a(var16, var11), var14, VoxelShapes.func_197872_a(var12, var14), VoxelShapes.func_197872_a(var13, var14), VoxelShapes.func_197872_a(var16, var14), var15, VoxelShapes.func_197872_a(var12, var15), VoxelShapes.func_197872_a(var13, var15), VoxelShapes.func_197872_a(var16, var15)};

      for(int var18 = 0; var18 < 16; ++var18) {
         var17[var18] = VoxelShapes.func_197872_a(var10, var17[var18]);
      }

      return var17;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.field_196412_B[this.func_196406_i(var1)];
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.field_196410_A[this.func_196406_i(var1)];
   }

   private static int func_196407_a(EnumFacing var0) {
      return 1 << var0.func_176736_b();
   }

   protected int func_196406_i(IBlockState var1) {
      int var2 = 0;
      if ((Boolean)var1.func_177229_b(field_196409_a)) {
         var2 |= func_196407_a(EnumFacing.NORTH);
      }

      if ((Boolean)var1.func_177229_b(field_196411_b)) {
         var2 |= func_196407_a(EnumFacing.EAST);
      }

      if ((Boolean)var1.func_177229_b(field_196413_c)) {
         var2 |= func_196407_a(EnumFacing.SOUTH);
      }

      if ((Boolean)var1.func_177229_b(field_196414_y)) {
         var2 |= func_196407_a(EnumFacing.WEST);
      }

      return var2;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204514_u)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204514_u, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204514_u) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204514_u) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204514_u) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204514_u, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_196409_a, var1.func_177229_b(field_196413_c))).func_206870_a(field_196411_b, var1.func_177229_b(field_196414_y))).func_206870_a(field_196413_c, var1.func_177229_b(field_196409_a))).func_206870_a(field_196414_y, var1.func_177229_b(field_196411_b));
      case COUNTERCLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_196409_a, var1.func_177229_b(field_196411_b))).func_206870_a(field_196411_b, var1.func_177229_b(field_196413_c))).func_206870_a(field_196413_c, var1.func_177229_b(field_196414_y))).func_206870_a(field_196414_y, var1.func_177229_b(field_196409_a));
      case CLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_196409_a, var1.func_177229_b(field_196414_y))).func_206870_a(field_196411_b, var1.func_177229_b(field_196409_a))).func_206870_a(field_196413_c, var1.func_177229_b(field_196411_b))).func_206870_a(field_196414_y, var1.func_177229_b(field_196413_c));
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      switch(var2) {
      case LEFT_RIGHT:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_196409_a, var1.func_177229_b(field_196413_c))).func_206870_a(field_196413_c, var1.func_177229_b(field_196409_a));
      case FRONT_BACK:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_196411_b, var1.func_177229_b(field_196414_y))).func_206870_a(field_196414_y, var1.func_177229_b(field_196411_b));
      default:
         return super.func_185471_a(var1, var2);
      }
   }

   static {
      field_196409_a = BlockSixWay.field_196488_a;
      field_196411_b = BlockSixWay.field_196490_b;
      field_196413_c = BlockSixWay.field_196492_c;
      field_196414_y = BlockSixWay.field_196495_y;
      field_204514_u = BlockStateProperties.field_208198_y;
      field_196415_z = (Map)BlockSixWay.field_196491_B.entrySet().stream().filter((var0) -> {
         return ((EnumFacing)var0.getKey()).func_176740_k().func_176722_c();
      }).collect(Util.func_199749_a());
   }
}
