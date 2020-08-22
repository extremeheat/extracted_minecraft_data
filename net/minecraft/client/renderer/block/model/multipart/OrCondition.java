package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.StateDefinition;

public class OrCondition implements Condition {
   private final Iterable conditions;

   public OrCondition(Iterable var1) {
      this.conditions = var1;
   }

   public Predicate getPredicate(StateDefinition var1) {
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
