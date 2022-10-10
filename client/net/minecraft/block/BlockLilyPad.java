package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
   protected static final VoxelShape field_185523_a = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   protected BlockLilyPad(Block.Properties var1) {
      super(var1);
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      super.func_196262_a(var1, var2, var3, var4);
      if (var4 instanceof EntityBoat) {
         var2.func_175655_b(new BlockPos(var3), true);
      }

   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_185523_a;
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      IFluidState var4 = var2.func_204610_c(var3);
      return var4.func_206886_c() == Fluids.field_204546_a || var1.func_185904_a() == Material.field_151588_w;
   }
}
