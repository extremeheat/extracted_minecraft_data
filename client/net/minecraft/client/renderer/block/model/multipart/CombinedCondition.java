package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;

public record CombinedCondition(Operation operation, List<Condition> terms) implements Condition {
   public CombinedCondition(Operation var1, List<Condition> var2) {
      super();
      this.operation = var1;
      this.terms = var2;
   }

   public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> var1) {
      return this.operation.<S>apply(Lists.transform(this.terms, (var1x) -> var1x.instantiate(var1)));
   }

   public static enum Operation implements StringRepresentable {
      AND("AND") {
         public <V> Predicate<V> apply(List<Predicate<V>> var1) {
            return Util.allOf(var1);
         }
      },
      OR("OR") {
         public <V> Predicate<V> apply(List<Predicate<V>> var1) {
            return Util.anyOf(var1);
         }
      };

      public static final Codec<Operation> CODEC = StringRepresentable.<Operation>fromEnum(Operation::values);
      private final String name;

      Operation(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract <V> Predicate<V> apply(List<Predicate<V>> var1);

      // $FF: synthetic method
      private static Operation[] $values() {
         return new Operation[]{AND, OR};
      }
   }
}
