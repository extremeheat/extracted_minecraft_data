package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockWall extends BlockFourWay {
   public static final BooleanProperty field_176256_a;
   private final VoxelShape[] field_196422_D;
   private final VoxelShape[] field_196423_E;

   public BlockWall(Block.Properties var1) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176256_a, true)).func_206870_a(field_196409_a, false)).func_206870_a(field_196411_b, false)).func_206870_a(field_196413_c, false)).func_206870_a(field_196414_y, false)).func_206870_a(field_204514_u, false));
      this.field_196422_D = this.func_196408_a(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.field_196423_E = this.func_196408_a(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (Boolean)var1.func_177229_b(field_176256_a) ? this.field_196422_D[this.func_196406_i(var1)] : super.func_196244_b(var1, var2, var3);
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (Boolean)var1.func_177229_b(field_176256_a) ? this.field_196423_E[this.func_196406_i(var1)] : super.func_196268_f(var1, var2, var3);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   private boolean func_196421_a(IBlockState var1, BlockFaceShape var2) {
      Block var3 = var1.func_177230_c();
      boolean var4 = var2 == BlockFaceShape.MIDDLE_POLE_THICK || var2 == BlockFaceShape.MIDDLE_POLE && var3 instanceof BlockFenceGate;
      return !func_194143_e(var3) && var2 == BlockFaceShape.SOLID || var4;
   }

   public static boolean func_194143_e(Block var0) {
      return Block.func_193382_c(var0) || var0 == Blocks.field_180401_cv || var0 == Blocks.field_150440_ba || var0 == Blocks.field_150423_aK || var0 == Blocks.field_196625_cS || var0 == Blocks.field_196628_cT || var0 == Blocks.field_185778_de || var0 == Blocks.field_150335_W;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      IFluidState var4 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      BlockPos var5 = var3.func_177978_c();
      BlockPos var6 = var3.func_177974_f();
      BlockPos var7 = var3.func_177968_d();
      BlockPos var8 = var3.func_177976_e();
      IBlockState var9 = var2.func_180495_p(var5);
      IBlockState var10 = var2.func_180495_p(var6);
      IBlockState var11 = var2.func_180495_p(var7);
      IBlockState var12 = var2.func_180495_p(var8);
      boolean var13 = this.func_196421_a(var9, var9.func_193401_d(var2, var5, EnumFacing.SOUTH));
      boolean var14 = this.func_196421_a(var10, var10.func_193401_d(var2, var6, EnumFacing.WEST));
      boolean var15 = this.func_196421_a(var11, var11.func_193401_d(var2, var7, EnumFacing.NORTH));
      boolean var16 = this.func_196421_a(var12, var12.func_193401_d(var2, var8, EnumFacing.EAST));
      boolean var17 = (!var13 || var14 || !var15 || var16) && (var13 || !var14 || var15 || !var16);
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176256_a, var17 || !var2.func_175623_d(var3.func_177984_a()))).func_206870_a(field_196409_a, var13)).func_206870_a(field_196411_b, var14)).func_206870_a(field_196413_c, var15)).func_206870_a(field_196414_y, var16)).func_206870_a(field_204514_u, var4.func_206886_c() == Fluids.field_204546_a);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204514_u)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      if (var2 == EnumFacing.DOWN) {
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var7 = var2 == EnumFacing.NORTH ? this.func_196421_a(var3, var3.func_193401_d(var4, var6, var2.func_176734_d())) : (Boolean)var1.func_177229_b(field_196409_a);
         boolean var8 = var2 == EnumFacing.EAST ? this.func_196421_a(var3, var3.func_193401_d(var4, var6, var2.func_176734_d())) : (Boolean)var1.func_177229_b(field_196411_b);
         boolean var9 = var2 == EnumFacing.SOUTH ? this.func_196421_a(var3, var3.func_193401_d(var4, var6, var2.func_176734_d())) : (Boolean)var1.func_177229_b(field_196413_c);
         boolean var10 = var2 == EnumFacing.WEST ? this.func_196421_a(var3, var3.func_193401_d(var4, var6, var2.func_176734_d())) : (Boolean)var1.func_177229_b(field_196414_y);
         boolean var11 = (!var7 || var8 || !var9 || var10) && (var7 || !var8 || var9 || !var10);
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176256_a, var11 || !var4.func_175623_d(var5.func_177984_a()))).func_206870_a(field_196409_a, var7)).func_206870_a(field_196411_b, var8)).func_206870_a(field_196413_c, var9)).func_206870_a(field_196414_y, var10);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176256_a, field_196409_a, field_196411_b, field_196414_y, field_196413_c, field_204514_u);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 != EnumFacing.UP && var4 != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
   }

   static {
      field_176256_a = BlockStateProperties.field_208149_B;
   }
}
