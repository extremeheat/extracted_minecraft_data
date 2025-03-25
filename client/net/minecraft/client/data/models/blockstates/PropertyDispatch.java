package net.minecraft.client.data.models.blockstates;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch<V> {
   private final Map<PropertyValueList, V> values = new HashMap();

   public PropertyDispatch() {
      super();
   }

   protected void putValue(PropertyValueList var1, V var2) {
      Object var3 = this.values.put(var1, var2);
      if (var3 != null) {
         throw new IllegalStateException("Value " + String.valueOf(var1) + " is already defined");
      }
   }

   Map<PropertyValueList, V> getEntries() {
      this.verifyComplete();
      return Map.copyOf(this.values);
   }

   private void verifyComplete() {
      List var1 = this.getDefinedProperties();
      Stream var2 = Stream.of(PropertyValueList.EMPTY);

      for(Property var4 : var1) {
         var2 = var2.flatMap((var1x) -> {
            Stream var10000 = var4.getAllValues();
            Objects.requireNonNull(var1x);
            return var10000.map(var1x::extend);
         });
      }

      List var5 = var2.filter((var1x) -> !this.values.containsKey(var1x)).toList();
      if (!var5.isEmpty()) {
         throw new IllegalStateException("Missing definition for properties: " + String.valueOf(var5));
      }
   }

   abstract List<Property<?>> getDefinedProperties();

   public static <T1 extends Comparable<T1>> C1<MultiVariant, T1> initial(Property<T1> var0) {
      return new C1<MultiVariant, T1>(var0);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<MultiVariant, T1, T2> initial(Property<T1> var0, Property<T2> var1) {
      return new C2<MultiVariant, T1, T2>(var0, var1);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<MultiVariant, T1, T2, T3> initial(Property<T1> var0, Property<T2> var1, Property<T3> var2) {
      return new C3<MultiVariant, T1, T2, T3>(var0, var1, var2);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<MultiVariant, T1, T2, T3, T4> initial(Property<T1> var0, Property<T2> var1, Property<T3> var2, Property<T4> var3) {
      return new C4<MultiVariant, T1, T2, T3, T4>(var0, var1, var2, var3);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<MultiVariant, T1, T2, T3, T4, T5> initial(Property<T1> var0, Property<T2> var1, Property<T3> var2, Property<T4> var3, Property<T5> var4) {
      return new C5<MultiVariant, T1, T2, T3, T4, T5>(var0, var1, var2, var3, var4);
   }

   public static <T1 extends Comparable<T1>> C1<VariantMutator, T1> modify(Property<T1> var0) {
      return new C1<VariantMutator, T1>(var0);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<VariantMutator, T1, T2> modify(Property<T1> var0, Property<T2> var1) {
      return new C2<VariantMutator, T1, T2>(var0, var1);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<VariantMutator, T1, T2, T3> modify(Property<T1> var0, Property<T2> var1, Property<T3> var2) {
      return new C3<VariantMutator, T1, T2, T3>(var0, var1, var2);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<VariantMutator, T1, T2, T3, T4> modify(Property<T1> var0, Property<T2> var1, Property<T3> var2, Property<T4> var3) {
      return new C4<VariantMutator, T1, T2, T3, T4>(var0, var1, var2, var3);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<VariantMutator, T1, T2, T3, T4, T5> modify(Property<T1> var0, Property<T2> var1, Property<T3> var2, Property<T4> var3, Property<T5> var4) {
      return new C5<VariantMutator, T1, T2, T3, T4, T5>(var0, var1, var2, var3, var4);
   }

   public static class C1<V, T1 extends Comparable<T1>> extends PropertyDispatch<V> {
      private final Property<T1> property1;

      C1(Property<T1> var1) {
         super();
         this.property1 = var1;
      }

      public List<Property<?>> getDefinedProperties() {
         return List.of(this.property1);
      }

      public C1<V, T1> select(T1 var1, V var2) {
         PropertyValueList var3 = PropertyValueList.of(this.property1.value(var1));
         this.putValue(var3, var2);
         return this;
      }

      public PropertyDispatch<V> generate(Function<T1, V> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.select(var2, var1.apply(var2)));
         return this;
      }
   }

   public static class C2<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends PropertyDispatch<V> {
      private final Property<T1> property1;
      private final Property<T2> property2;

      C2(Property<T1> var1, Property<T2> var2) {
         super();
         this.property1 = var1;
         this.property2 = var2;
      }

      public List<Property<?>> getDefinedProperties() {
         return List.of(this.property1, this.property2);
      }

      public C2<V, T1, T2> select(T1 var1, T2 var2, V var3) {
         PropertyValueList var4 = PropertyValueList.of(this.property1.value(var1), this.property2.value(var2));
         this.putValue(var4, var3);
         return this;
      }

      public PropertyDispatch<V> generate(BiFunction<T1, T2, V> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.select(var2, var3, var1.apply(var2, var3))));
         return this;
      }
   }

   public static class C3<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> extends PropertyDispatch<V> {
      private final Property<T1> property1;
      private final Property<T2> property2;
      private final Property<T3> property3;

      C3(Property<T1> var1, Property<T2> var2, Property<T3> var3) {
         super();
         this.property1 = var1;
         this.property2 = var2;
         this.property3 = var3;
      }

      public List<Property<?>> getDefinedProperties() {
         return List.of(this.property1, this.property2, this.property3);
      }

      public C3<V, T1, T2, T3> select(T1 var1, T2 var2, T3 var3, V var4) {
         PropertyValueList var5 = PropertyValueList.of(this.property1.value(var1), this.property2.value(var2), this.property3.value(var3));
         this.putValue(var5, var4);
         return this;
      }

      public PropertyDispatch<V> generate(Function3<T1, T2, T3, V> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.select(var2, var3, var4, var1.apply(var2, var3, var4)))));
         return this;
      }
   }

   public static class C4<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> extends PropertyDispatch<V> {
      private final Property<T1> property1;
      private final Property<T2> property2;
      private final Property<T3> property3;
      private final Property<T4> property4;

      C4(Property<T1> var1, Property<T2> var2, Property<T3> var3, Property<T4> var4) {
         super();
         this.property1 = var1;
         this.property2 = var2;
         this.property3 = var3;
         this.property4 = var4;
      }

      public List<Property<?>> getDefinedProperties() {
         return List.of(this.property1, this.property2, this.property3, this.property4);
      }

      public C4<V, T1, T2, T3, T4> select(T1 var1, T2 var2, T3 var3, T4 var4, V var5) {
         PropertyValueList var6 = PropertyValueList.of(this.property1.value(var1), this.property2.value(var2), this.property3.value(var3), this.property4.value(var4));
         this.putValue(var6, var5);
         return this;
      }

      public PropertyDispatch<V> generate(Function4<T1, T2, T3, T4, V> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.property4.getPossibleValues().forEach((var5) -> this.select(var2, var3, var4, var5, var1.apply(var2, var3, var4, var5))))));
         return this;
      }
   }

   public static class C5<V, T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> extends PropertyDispatch<V> {
      private final Property<T1> property1;
      private final Property<T2> property2;
      private final Property<T3> property3;
      private final Property<T4> property4;
      private final Property<T5> property5;

      C5(Property<T1> var1, Property<T2> var2, Property<T3> var3, Property<T4> var4, Property<T5> var5) {
         super();
         this.property1 = var1;
         this.property2 = var2;
         this.property3 = var3;
         this.property4 = var4;
         this.property5 = var5;
      }

      public List<Property<?>> getDefinedProperties() {
         return List.of(this.property1, this.property2, this.property3, this.property4, this.property5);
      }

      public C5<V, T1, T2, T3, T4, T5> select(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, V var6) {
         PropertyValueList var7 = PropertyValueList.of(this.property1.value(var1), this.property2.value(var2), this.property3.value(var3), this.property4.value(var4), this.property5.value(var5));
         this.putValue(var7, var6);
         return this;
      }

      public PropertyDispatch<V> generate(Function5<T1, T2, T3, T4, T5, V> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.property4.getPossibleValues().forEach((var5) -> this.property5.getPossibleValues().forEach((var6) -> this.select(var2, var3, var4, var5, var6, var1.apply(var2, var3, var4, var5, var6)))))));
         return this;
      }
   }
}
