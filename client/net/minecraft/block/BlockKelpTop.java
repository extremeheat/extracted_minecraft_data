package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockKelpTop extends Block implements ILiquidContainer {
   public static final IntegerProperty field_203163_a;
   protected static final VoxelShape field_207797_b;

   protected BlockKelpTop(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_203163_a, 0));
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_207797_b;
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IFluidState var2 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      return var2.func_206884_a(FluidTags.field_206959_a) && var2.func_206882_g() == 8 ? this.func_209906_a(var1.func_195991_k()) : null;
   }

   public IBlockState func_209906_a(IWorld var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_203163_a, var1.func_201674_k().nextInt(25));
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return Fluids.field_204546_a.func_207204_a(false);
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var1.func_196955_c(var2, var3)) {
         var2.func_175655_b(var3, true);
      } else {
         BlockPos var5 = var3.func_177984_a();
         IBlockState var6 = var2.func_180495_p(var5);
         if (var6.func_177230_c() == Blocks.field_150355_j && (Integer)var1.func_177229_b(field_203163_a) < 25 && var4.nextDouble() < 0.14D) {
            var2.func_175656_a(var5, (IBlockState)var1.func_177231_a(field_203163_a));
         }

      }
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      BlockPos var4 = var3.func_177977_b();
      IBlockState var5 = var2.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      if (var6 == Blocks.field_196814_hQ) {
         return false;
      } else {
         return var6 == this || var6 == Blocks.field_203215_jy || Block.func_208061_a(var5.func_196952_d(var2, var4), EnumFacing.UP);
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (!var1.func_196955_c(var4, var5)) {
         if (var2 == EnumFacing.DOWN) {
            return Blocks.field_150350_a.func_176223_P();
         }

         var4.func_205220_G_().func_205360_a(var5, this, 1);
      }

      if (var2 == EnumFacing.UP && var3.func_177230_c() == this) {
         return Blocks.field_203215_jy.func_176223_P();
      } else {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_203163_a);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return false;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      return false;
   }

   static {
      field_203163_a = BlockStateProperties.field_208172_Y;
      field_207797_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   }
}
