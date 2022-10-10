package net.minecraft.block.state.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockMatcher implements Predicate<IBlockState> {
   private final Block field_177644_a;

   public BlockMatcher(Block var1) {
      super();
      this.field_177644_a = var1;
   }

   public static BlockMatcher func_177642_a(Block var0) {
      return new BlockMatcher(var0);
   }

   public boolean test(@Nullable IBlockState var1) {
      return var1 != null && var1.func_177230_c() == this.field_177644_a;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((IBlockState)var1);
   }
}
