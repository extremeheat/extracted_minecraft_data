package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class AllowsMovementMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private static final AllowsMovementMatcher field_202080_a = new AllowsMovementMatcher();

   public AllowsMovementMatcher() {
      super();
   }

   public boolean test(@Nullable IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1 != null && !var1.func_185904_a().func_76230_c();
   }

   public static AllowsMovementMatcher func_202079_a() {
      return field_202080_a;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1, IBlockReader var2, BlockPos var3) {
      return this.test((IBlockState)var1, var2, var3);
   }
}
