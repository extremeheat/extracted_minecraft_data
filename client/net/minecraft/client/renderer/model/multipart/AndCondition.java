package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.StateContainer;

public class AndCondition implements ICondition {
   private final Iterable<? extends ICondition> field_188121_c;

   public AndCondition(Iterable<? extends ICondition> var1) {
      super();
      this.field_188121_c = var1;
   }

   public Predicate<IBlockState> getPredicate(StateContainer<Block, IBlockState> var1) {
      List var2 = (List)Streams.stream(this.field_188121_c).map((var1x) -> {
         return var1x.getPredicate(var1);
      }).collect(Collectors.toList());
      return (var1x) -> {
         return var2.stream().allMatch((var1) -> {
            return var1.test(var1x);
         });
      };
   }
}
