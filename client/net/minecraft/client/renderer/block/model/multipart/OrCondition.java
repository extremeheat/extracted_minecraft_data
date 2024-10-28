package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class OrCondition implements Condition {
   public static final String TOKEN = "OR";
   private final Iterable<? extends Condition> conditions;

   public OrCondition(Iterable<? extends Condition> var1) {
      super();
      this.conditions = var1;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1) {
      List var2 = (List)Streams.stream(this.conditions).map((var1x) -> {
         return var1x.getPredicate(var1);
      }).collect(Collectors.toList());
      return (var1x) -> {
         return var2.stream().anyMatch((var1) -> {
            return var1.test(var1x);
         });
      };
   }
}
