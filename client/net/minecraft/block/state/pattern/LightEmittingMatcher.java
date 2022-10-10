package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LightEmittingMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private static final LightEmittingMatcher field_202074_a = new LightEmittingMatcher();

   public LightEmittingMatcher() {
      super();
   }

   public boolean test(@Nullable IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1 != null && var1.func_200016_a(var2, var3) == 0;
   }

   public static LightEmittingMatcher func_202073_a() {
      return field_202074_a;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1, IBlockReader var2, BlockPos var3) {
      return this.test((IBlockState)var1, var2, var3);
   }
}
