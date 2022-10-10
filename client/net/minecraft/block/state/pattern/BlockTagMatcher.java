package net.minecraft.block.state.pattern;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tags.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockTagMatcher implements IBlockMatcherReaderAware<IBlockState> {
   private final Tag<Block> field_206905_a;

   public BlockTagMatcher(Tag<Block> var1) {
      super();
      this.field_206905_a = var1;
   }

   public static BlockTagMatcher func_206904_a(Tag<Block> var0) {
      return new BlockTagMatcher(var0);
   }

   public boolean test(@Nullable IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1 != null && var1.func_203425_a(this.field_206905_a);
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1, IBlockReader var2, BlockPos var3) {
      return this.test((IBlockState)var1, var2, var3);
   }
}
