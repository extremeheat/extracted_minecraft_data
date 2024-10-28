package net.minecraft.client.renderer.block.model.multipart;

import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@FunctionalInterface
public interface Condition {
   Condition TRUE = (var0) -> {
      return (var0x) -> {
         return true;
      };
   };
   Condition FALSE = (var0) -> {
      return (var0x) -> {
         return false;
      };
   };

   Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1);
}
