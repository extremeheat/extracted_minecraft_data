package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Streams;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class AndCondition implements Condition {
   public static final String TOKEN = "AND";
   private final Iterable<? extends Condition> conditions;

   public AndCondition(Iterable<? extends Condition> var1) {
      super();
      this.conditions = var1;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1) {
      return Util.allOf(Streams.stream(this.conditions).map((var1x) -> {
         return var1x.getPredicate(var1);
      }).toList());
   }
}
