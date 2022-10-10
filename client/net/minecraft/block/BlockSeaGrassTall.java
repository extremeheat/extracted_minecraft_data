package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSeaGrassTall extends BlockShearableDoublePlant implements ILiquidContainer {
   public static final EnumProperty<DoubleBlockHalf> field_208065_c;
   protected static final VoxelShape field_207799_b;

   public BlockSeaGrassTall(Block var1, Block.Properties var2) {
      super(var1, var2);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_207799_b;
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return Block.func_208061_a(var1.func_196952_d(var2, var3), EnumFacing.UP) && var1.func_177230_c() != Blocks.field_196814_hQ;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(Blocks.field_203198_aQ);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = super.func_196258_a(var1);
      if (var2 != null) {
         IFluidState var3 = var1.func_195991_k().func_204610_c(var1.func_195995_a().func_177984_a());
         if (var3.func_206884_a(FluidTags.field_206959_a) && var3.func_206882_g() == 8) {
            return var2;
         }
      }

      return null;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      if (var1.func_177229_b(field_208065_c) == DoubleBlockHalf.UPPER) {
         IBlockState var5 = var2.func_180495_p(var3.func_177977_b());
         return var5.func_177230_c() == this && var5.func_177229_b(field_208065_c) == DoubleBlockHalf.LOWER;
      } else {
         IFluidState var4 = var2.func_204610_c(var3);
         return super.func_196260_a(var1, var2, var3) && var4.func_206884_a(FluidTags.field_206959_a) && var4.func_206882_g() == 8;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return Fluids.field_204546_a.func_207204_a(false);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return false;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      return false;
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return Blocks.field_150355_j.func_176223_P().func_200016_a(var2, var3);
   }

   static {
      field_208065_c = BlockShearableDoublePlant.field_208063_b;
      field_207799_b = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   }
}
