package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSoulSand extends Block {
   protected static final VoxelShape field_196509_a = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

   public BlockSoulSand(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196509_a;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      var4.field_70159_w *= 0.4D;
      var4.field_70179_y *= 0.4D;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      BlockBubbleColumn.func_203159_a(var2, var3.func_177984_a(), false);
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 20;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }
}
