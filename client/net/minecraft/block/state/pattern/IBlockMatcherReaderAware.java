package net.minecraft.block.state.pattern;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IBlockMatcherReaderAware<T> {
   boolean test(T var1, IBlockReader var2, BlockPos var3);
}
