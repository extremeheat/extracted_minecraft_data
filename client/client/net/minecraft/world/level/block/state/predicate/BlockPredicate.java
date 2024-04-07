package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate implements Predicate<BlockState> {
   private final Block block;

   public BlockPredicate(Block var1) {
      super();
      this.block = var1;
   }

   public static BlockPredicate forBlock(Block var0) {
      return new BlockPredicate(var0);
   }

   public boolean test(@Nullable BlockState var1) {
      return var1 != null && var1.is(this.block);
   }
}
