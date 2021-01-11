package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockHelper implements Predicate<IBlockState> {
   private final Block field_177644_a;

   private BlockHelper(Block var1) {
      super();
      this.field_177644_a = var1;
   }

   public static BlockHelper func_177642_a(Block var0) {
      return new BlockHelper(var0);
   }

   public boolean apply(IBlockState var1) {
      return var1 != null && var1.func_177230_c() == this.field_177644_a;
   }

   // $FF: synthetic method
   public boolean apply(Object var1) {
      return this.apply((IBlockState)var1);
   }
}
