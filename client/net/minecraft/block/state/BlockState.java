package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.state.AbstractStateHolder;
import net.minecraft.state.IProperty;

public class BlockState extends AbstractStateHolder<Block, IBlockState> implements IBlockState {
   public BlockState(Block var1, ImmutableMap<IProperty<?>, Comparable<?>> var2) {
      super(var1, var2);
   }

   public Block func_177230_c() {
      return (Block)this.field_206876_a;
   }
}
