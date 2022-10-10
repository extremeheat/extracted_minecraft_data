package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockKelp extends Block implements ILiquidContainer {
   final BlockKelpTop field_209904_a;

   protected BlockKelp(BlockKelpTop var1, Block.Properties var2) {
      super(var2);
      this.field_209904_a = var1;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
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

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (!var1.func_196955_c(var4, var5)) {
         return Blocks.field_150350_a.func_176223_P();
      } else {
         if (var2 == EnumFacing.UP) {
            Block var7 = var3.func_177230_c();
            if (var7 != this && var7 != this.field_209904_a) {
               return this.field_209904_a.func_209906_a(var4);
            }
         }

         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      BlockPos var4 = var3.func_177977_b();
      IBlockState var5 = var2.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      return var6 != Blocks.field_196814_hQ && (var6 == this || Block.func_208061_a(var5.func_196952_d(var2, var4), EnumFacing.UP));
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Blocks.field_203214_jx;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(Blocks.field_203214_jx);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return false;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      return false;
   }
}
