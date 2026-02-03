package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public record CombinedCondition(Operation operation, List<Condition> terms) implements Condition {
   public CombinedCondition {
      super();
   }

   public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(final StateDefinition<O, S> definition) {
      return this.operation.<S>apply(Lists.transform(this.terms, (c) -> c.instantiate(definition)));
   }

   public static enum Operation implements StringRepresentable {
      AND("AND") {
         public <V> Predicate<V> apply(final List<Predicate<V>> terms) {
            return Util.allOf(terms);
         }
      },
      OR("OR") {
         public <V> Predicate<V> apply(final List<Predicate<V>> terms) {
            return Util.anyOf(terms);
         }
      };

      public static final Codec<Operation> CODEC = StringRepresentable.<Operation>fromEnum(Operation::values);
      private final String name;

      private Operation(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract <V> Predicate<V> apply(List<Predicate<V>> terms);

      // $FF: synthetic method
      private static Operation[] $values() {
         return new Operation[]{AND, OR};
      }
   }
}
