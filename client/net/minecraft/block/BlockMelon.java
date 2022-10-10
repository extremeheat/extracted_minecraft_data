package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMelon extends BlockStemGrown {
   protected BlockMelon(Block.Properties var1) {
      super(var1);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_151127_ba;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 3 + var2.nextInt(5);
   }

   public int func_196251_a(IBlockState var1, int var2, World var3, BlockPos var4, Random var5) {
      return Math.min(9, this.func_196264_a(var1, var5) + var5.nextInt(1 + var2));
   }

   public BlockStem func_196524_d() {
      return (BlockStem)Blocks.field_150394_bc;
   }

   public BlockAttachedStem func_196523_e() {
      return (BlockAttachedStem)Blocks.field_196713_dt;
   }
}
