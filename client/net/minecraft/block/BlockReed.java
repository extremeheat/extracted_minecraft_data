package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
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

public class BlockReed extends Block {
   public static final IntegerProperty field_176355_a;
   protected static final VoxelShape field_196503_b;

   protected BlockReed(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176355_a, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196503_b;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var1.func_196955_c(var2, var3) && var2.func_175623_d(var3.func_177984_a())) {
         int var5;
         for(var5 = 1; var2.func_180495_p(var3.func_177979_c(var5)).func_177230_c() == this; ++var5) {
         }

         if (var5 < 3) {
            int var6 = (Integer)var1.func_177229_b(field_176355_a);
            if (var6 == 15) {
               var2.func_175656_a(var3.func_177984_a(), this.func_176223_P());
               var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176355_a, 0), 4);
            } else {
               var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176355_a, var6 + 1), 4);
            }
         }
      }

   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      Block var4 = var2.func_180495_p(var3.func_177977_b()).func_177230_c();
      if (var4 == this) {
         return true;
      } else {
         if (var4 == Blocks.field_196658_i || var4 == Blocks.field_150346_d || var4 == Blocks.field_196660_k || var4 == Blocks.field_196661_l || var4 == Blocks.field_150354_m || var4 == Blocks.field_196611_F) {
            BlockPos var5 = var3.func_177977_b();
            Iterator var6 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var6.hasNext()) {
               EnumFacing var7 = (EnumFacing)var6.next();
               IBlockState var8 = var2.func_180495_p(var5.func_177972_a(var7));
               IFluidState var9 = var2.func_204610_c(var5.func_177972_a(var7));
               if (var9.func_206884_a(FluidTags.field_206959_a) || var8.func_177230_c() == Blocks.field_185778_de) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176355_a);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176355_a = BlockStateProperties.field_208171_X;
      field_196503_b = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   }
}
