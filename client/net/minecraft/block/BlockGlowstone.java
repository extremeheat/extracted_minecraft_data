package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockGlowstone extends Block {
   public BlockGlowstone(Block.Properties var1) {
      super(var1);
   }

   public int func_196251_a(IBlockState var1, int var2, World var3, BlockPos var4, Random var5) {
      return MathHelper.func_76125_a(this.func_196264_a(var1, var5) + var5.nextInt(var2 + 1), 1, 4);
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 2 + var2.nextInt(3);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_151114_aO;
   }
}
