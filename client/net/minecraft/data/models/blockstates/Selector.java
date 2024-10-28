package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.properties.Property;

public final class Selector {
   private static final Selector EMPTY = new Selector(ImmutableList.of());
   private static final Comparator<Property.Value<?>> COMPARE_BY_NAME = Comparator.comparing((var0) -> {
      return var0.property().getName();
   });
   private final List<Property.Value<?>> values;

   public Selector extend(Property.Value<?> var1) {
      return new Selector(ImmutableList.builder().addAll(this.values).add(var1).build());
   }

   public Selector extend(Selector var1) {
      return new Selector(ImmutableList.builder().addAll(this.values).addAll(var1.values).build());
   }

   private Selector(List<Property.Value<?>> var1) {
      super();
      this.values = var1;
   }

   public static Selector empty() {
      return EMPTY;
   }

   public static Selector of(Property.Value<?>... var0) {
      return new Selector(ImmutableList.copyOf(var0));
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof Selector && this.values.equals(((Selector)var1).values);
   }

   public int hashCode() {
      return this.values.hashCode();
   }

   public String getKey() {
      return (String)this.values.stream().sorted(COMPARE_BY_NAME).map(Property.Value::toString).collect(Collectors.joining(","));
   }

   public String toString() {
      return this.getKey();
   }
}
