package net.minecraft.client.data.models.blockstates;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.world.level.block.state.properties.Property;

public class ConditionBuilder {
   private final ImmutableMap.Builder<String, KeyValueCondition.Terms> terms = ImmutableMap.builder();

   public ConditionBuilder() {
      super();
   }

   private <T extends Comparable<T>> void putValue(Property<T> var1, KeyValueCondition.Terms var2) {
      this.terms.put(var1.getName(), var2);
   }

   public final <T extends Comparable<T>> ConditionBuilder term(Property<T> var1, T var2) {
      this.putValue(var1, new KeyValueCondition.Terms(List.of(new KeyValueCondition.Term(var1.getName(var2), false))));
      return this;
   }

   @SafeVarargs
   public final <T extends Comparable<T>> ConditionBuilder term(Property<T> var1, T var2, T... var3) {
      Stream var10000 = Stream.concat(Stream.of(var2), Stream.of(var3));
      Objects.requireNonNull(var1);
      List var4 = var10000.map(var1::getName).sorted().distinct().map((var0) -> new KeyValueCondition.Term(var0, false)).toList();
      this.putValue(var1, new KeyValueCondition.Terms(var4));
      return this;
   }

   public final <T extends Comparable<T>> ConditionBuilder negatedTerm(Property<T> var1, T var2) {
      this.putValue(var1, new KeyValueCondition.Terms(List.of(new KeyValueCondition.Term(var1.getName(var2), true))));
      return this;
   }

   public Condition build() {
      return new KeyValueCondition(this.terms.buildOrThrow());
   }
}
