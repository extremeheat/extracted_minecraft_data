package net.minecraft.client.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.world.level.block.state.properties.Property;

public record PropertyValueList(List<Property.Value<?>> values) {
   public static final PropertyValueList EMPTY = new PropertyValueList(List.of());
   private static final Comparator<Property.Value<?>> COMPARE_BY_NAME = Comparator.comparing((var0) -> var0.property().getName());

   public PropertyValueList(List<Property.Value<?>> var1) {
      super();
      this.values = var1;
   }

   public PropertyValueList extend(Property.Value<?> var1) {
      return new PropertyValueList(Util.copyAndAdd(this.values, var1));
   }

   public PropertyValueList extend(PropertyValueList var1) {
      return new PropertyValueList(ImmutableList.builder().addAll(this.values).addAll(var1.values).build());
   }

   public static PropertyValueList of(Property.Value<?>... var0) {
      return new PropertyValueList(List.of(var0));
   }

   public String getKey() {
      return (String)this.values.stream().sorted(COMPARE_BY_NAME).map(Property.Value::toString).collect(Collectors.joining(","));
   }

   public String toString() {
      return this.getKey();
   }
}
