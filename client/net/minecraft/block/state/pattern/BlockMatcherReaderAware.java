package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockMatcherReaderAware implements IBlockMatcherReaderAware<IBlockState> {
   private final Block field_202082_a;

   public BlockMatcherReaderAware(Block var1) {
      super();
      this.field_202082_a = var1;
   }

   public static BlockMatcherReaderAware func_202081_a(Block var0) {
      return new BlockMatcherReaderAware(var0);
   }

   public boolean test(@Nullable IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1 != null && var1.func_177230_c() == this.field_202082_a;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1, IBlockReader var2, BlockPos var3) {
      return this.test((IBlockState)var1, var2, var3);
   }
}
