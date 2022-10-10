package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockClay extends Block {
   public BlockClay(Block.Properties var1) {
      super(var1);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_151119_aD;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 4;
   }
}
