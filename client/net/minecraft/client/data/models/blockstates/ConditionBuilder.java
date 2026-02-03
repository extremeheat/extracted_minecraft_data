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

   private <T extends Comparable<T>> void putValue(final Property<T> property, final KeyValueCondition.Terms term) {
      this.terms.put(property.getName(), term);
   }

   public final <T extends Comparable<T>> ConditionBuilder term(final Property<T> property, final T value) {
      this.putValue(property, new KeyValueCondition.Terms(List.of(new KeyValueCondition.Term(property.getName(value), false))));
      return this;
   }

   @SafeVarargs
   public final <T extends Comparable<T>> ConditionBuilder term(final Property<T> property, final T value, final T... values) {
      Stream var10000 = Stream.concat(Stream.of(value), Stream.of(values));
      Objects.requireNonNull(property);
      List<KeyValueCondition.Term> terms = var10000.map(property::getName).sorted().distinct().map((v) -> new KeyValueCondition.Term(v, false)).toList();
      this.putValue(property, new KeyValueCondition.Terms(terms));
      return this;
   }

   public final <T extends Comparable<T>> ConditionBuilder negatedTerm(final Property<T> property, final T value) {
      this.putValue(property, new KeyValueCondition.Terms(List.of(new KeyValueCondition.Term(property.getName(value), true))));
      return this;
   }

   public Condition build() {
      return new KeyValueCondition(this.terms.buildOrThrow());
   }
}
