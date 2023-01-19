package net.minecraft.client.renderer.block.model.multipart;

import java.util.function.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@FunctionalInterface
public interface Condition {
   Condition TRUE = var0 -> var0x -> true;
   Condition FALSE = var0 -> var0x -> false;

   Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1);
}
