package net.minecraft.block.state.pattern;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LiquidBlockMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private static final LiquidBlockMatcher field_206903_a = new LiquidBlockMatcher();

   public LiquidBlockMatcher() {
      super();
   }

   public boolean test(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return !var1.func_204520_s().func_206888_e();
   }

   public static LiquidBlockMatcher func_206902_a() {
      return field_206903_a;
   }

   // $FF: synthetic method
   public boolean test(Object var1, IBlockReader var2, BlockPos var3) {
      return this.test((IBlockState)var1, var2, var3);
   }
}
