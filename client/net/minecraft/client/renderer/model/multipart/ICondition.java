package net.minecraft.client.renderer.model.multipart;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.StateContainer;

public interface ICondition {
   ICondition TRUE = (var0) -> {
      return (var0x) -> {
         return true;
      };
   };
   ICondition FALSE = (var0) -> {
      return (var0x) -> {
         return false;
      };
   };

   Predicate<IBlockState> getPredicate(StateContainer<Block, IBlockState> var1);
}
