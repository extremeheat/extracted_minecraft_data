package net.minecraft.client.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch {
   private final Map<Selector, List<Variant>> values = Maps.newHashMap();

   public PropertyDispatch() {
      super();
   }

   protected void putValue(Selector var1, List<Variant> var2) {
      List var3 = (List)this.values.put(var1, var2);
      if (var3 != null) {
         throw new IllegalStateException("Value " + String.valueOf(var1) + " is already defined");
      }
   }

   Map<Selector, List<Variant>> getEntries() {
      this.verifyComplete();
      return ImmutableMap.copyOf(this.values);
   }

   private void verifyComplete() {
      List var1 = this.getDefinedProperties();
      Stream var2 = Stream.of(Selector.empty());

      for(Property var4 : var1) {
         var2 = var2.flatMap((var1x) -> {
            Stream var10000 = var4.getAllValues();
            Objects.requireNonNull(var1x);
            return var10000.map(var1x::extend);
         });
      }

      List var5 = (List)var2.filter((var1x) -> !this.values.containsKey(var1x)).collect(Collectors.toList());
      if (!var5.isEmpty()) {
         throw new IllegalStateException("Missing definition for properties: " + String.valueOf(var5));
      }
   }

   abstract List<Property<?>> getDefinedProperties();

   public static <T1 extends Comparable<T1>> C1<T1> property(Property<T1> var0) {
      return new C1<T1>(var0);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<T1, T2> properties(Property<T1> var0, Property<T2> var1) {
      return new C2<T1, T2>(var0, var1);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<T1, T2, T3> properties(Property<T1> var0, Property<T2> var1, Property<T3> var2) {
      return new C3<T1, T2, T3>(var0, var1, var2);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<T1, T2, T3, T4> properties(Property<T1> var0, Property<T2> var1, Property<T3> var2, Property<T4> var3) {
      return new C4<T1, T2, T3, T4>(var0, var1, var2, var3);
   }

   public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<T1, T2, T3, T4, T5> properties(Property<T1> var0, Property<T2> var1, Property<T3> var2, Property<T4> var3, Property<T5> var4) {
      return new C5<T1, T2, T3, T4, T5>(var0, var1, var2, var3, var4);
   }

   public static class C1<T1 extends Comparable<T1>> extends PropertyDispatch {
      private final Property<T1> property1;

      C1(Property<T1> var1) {
         super();
         this.property1 = var1;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1);
      }

      public C1<T1> select(T1 var1, List<Variant> var2) {
         Selector var3 = Selector.of(this.property1.value(var1));
         this.putValue(var3, var2);
         return this;
      }

      public C1<T1> select(T1 var1, Variant var2) {
         return this.select(var1, Collections.singletonList(var2));
      }

      public PropertyDispatch generate(Function<T1, Variant> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.select(var2, (Variant)var1.apply(var2)));
         return this;
      }

      public PropertyDispatch generateList(Function<T1, List<Variant>> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.select(var2, (List)var1.apply(var2)));
         return this;
      }
   }

   public static class C2<T1 extends Comparable<T1>, T2 extends Comparable<T2>> extends PropertyDispatch {
      private final Property<T1> property1;
      private final Property<T2> property2;

      C2(Property<T1> var1, Property<T2> var2) {
         super();
         this.property1 = var1;
         this.property2 = var2;
      }

      public List<Property<?>> getDefinedProperties() {
         return ImmutableList.of(this.property1, this.property2);
      }

      public C2<T1, T2> select(T1 var1, T2 var2, List<Variant> var3) {
         Selector var4 = Selector.of(this.property1.value(var1), this.property2.value(var2));
         this.putValue(var4, var3);
         return this;
      }

      public C2<T1, T2> select(T1 var1, T2 var2, Variant var3) {
         return this.select(var1, var2, Collections.singletonList(var3));
      }

      public PropertyDispatch generate(BiFunction<T1, T2, Variant> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.select(var2, var3, (Variant)var1.apply(var2, var3))));
         return this;
      }

      public PropertyDispatch generateList(BiFunction<T1, T2, List<Variant>> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.select(var2, var3, (List)var1.apply(var2, var3))));
         return this;
      }
   }

   public static class C3<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> extends PropertyDispatch {
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
         return ImmutableList.of(this.property1, this.property2, this.property3);
      }

      public C3<T1, T2, T3> select(T1 var1, T2 var2, T3 var3, List<Variant> var4) {
         Selector var5 = Selector.of(this.property1.value(var1), this.property2.value(var2), this.property3.value(var3));
         this.putValue(var5, var4);
         return this;
      }

      public C3<T1, T2, T3> select(T1 var1, T2 var2, T3 var3, Variant var4) {
         return this.select(var1, var2, var3, Collections.singletonList(var4));
      }

      public PropertyDispatch generate(TriFunction<T1, T2, T3, Variant> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.select(var2, var3, var4, (Variant)var1.apply(var2, var3, var4)))));
         return this;
      }

      public PropertyDispatch generateList(TriFunction<T1, T2, T3, List<Variant>> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.select(var2, var3, var4, (List)var1.apply(var2, var3, var4)))));
         return this;
      }
   }

   public static class C4<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> extends PropertyDispatch {
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
         return ImmutableList.of(this.property1, this.property2, this.property3, this.property4);
      }

      public C4<T1, T2, T3, T4> select(T1 var1, T2 var2, T3 var3, T4 var4, List<Variant> var5) {
         Selector var6 = Selector.of(this.property1.value(var1), this.property2.value(var2), this.property3.value(var3), this.property4.value(var4));
         this.putValue(var6, var5);
         return this;
      }

      public C4<T1, T2, T3, T4> select(T1 var1, T2 var2, T3 var3, T4 var4, Variant var5) {
         return this.select(var1, var2, var3, var4, Collections.singletonList(var5));
      }

      public PropertyDispatch generate(QuadFunction<T1, T2, T3, T4, Variant> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.property4.getPossibleValues().forEach((var5) -> this.select(var2, var3, var4, var5, (Variant)var1.apply(var2, var3, var4, var5))))));
         return this;
      }

      public PropertyDispatch generateList(QuadFunction<T1, T2, T3, T4, List<Variant>> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.property4.getPossibleValues().forEach((var5) -> this.select(var2, var3, var4, var5, (List)var1.apply(var2, var3, var4, var5))))));
         return this;
      }
   }

   public static class C5<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> extends PropertyDispatch {
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
         return ImmutableList.of(this.property1, this.property2, this.property3, this.property4, this.property5);
      }

      public C5<T1, T2, T3, T4, T5> select(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, List<Variant> var6) {
         Selector var7 = Selector.of(this.property1.value(var1), this.property2.value(var2), this.property3.value(var3), this.property4.value(var4), this.property5.value(var5));
         this.putValue(var7, var6);
         return this;
      }

      public C5<T1, T2, T3, T4, T5> select(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5, Variant var6) {
         return this.select(var1, var2, var3, var4, var5, Collections.singletonList(var6));
      }

      public PropertyDispatch generate(PentaFunction<T1, T2, T3, T4, T5, Variant> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.property4.getPossibleValues().forEach((var5) -> this.property5.getPossibleValues().forEach((var6) -> this.select(var2, var3, var4, var5, var6, (Variant)var1.apply(var2, var3, var4, var5, var6)))))));
         return this;
      }

      public PropertyDispatch generateList(PentaFunction<T1, T2, T3, T4, T5, List<Variant>> var1) {
         this.property1.getPossibleValues().forEach((var2) -> this.property2.getPossibleValues().forEach((var3) -> this.property3.getPossibleValues().forEach((var4) -> this.property4.getPossibleValues().forEach((var5) -> this.property5.getPossibleValues().forEach((var6) -> this.select(var2, var3, var4, var5, var6, (List)var1.apply(var2, var3, var4, var5, var6)))))));
         return this;
      }
   }

   @FunctionalInterface
   public interface PentaFunction<P1, P2, P3, P4, P5, R> {
      R apply(P1 var1, P2 var2, P3 var3, P4 var4, P5 var5);
   }

   @FunctionalInterface
   public interface QuadFunction<P1, P2, P3, P4, R> {
      R apply(P1 var1, P2 var2, P3 var3, P4 var4);
   }

   @FunctionalInterface
   public interface TriFunction<P1, P2, P3, R> {
      R apply(P1 var1, P2 var2, P3 var3);
   }
}
