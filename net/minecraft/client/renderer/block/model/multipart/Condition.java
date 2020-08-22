package net.minecraft.client.renderer.block.model.multipart;

import java.util.function.Predicate;
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

   Predicate getPredicate(StateDefinition var1);
}
