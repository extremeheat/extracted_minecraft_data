package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public interface Condition extends Supplier<JsonElement> {
   void validate(StateDefinition<?, ?> var1);

   static TerminalCondition condition() {
      return new TerminalCondition();
   }

   static Condition and(Condition... var0) {
      return new CompositeCondition(Condition.Operation.AND, Arrays.asList(var0));
   }

   static Condition or(Condition... var0) {
      return new CompositeCondition(Condition.Operation.OR, Arrays.asList(var0));
   }

   public static class TerminalCondition implements Condition {
      private final Map<Property<?>, String> terms = Maps.newHashMap();

      public TerminalCondition() {
         super();
      }

      private static <T extends Comparable<T>> String joinValues(Property<T> var0, Stream<T> var1) {
         Objects.requireNonNull(var0);
         return (String)var1.map(var0::getName).collect(Collectors.joining("|"));
      }

      private static <T extends Comparable<T>> String getTerm(Property<T> var0, T var1, T[] var2) {
         return joinValues(var0, Stream.concat(Stream.of(var1), Stream.of(var2)));
      }

      private <T extends Comparable<T>> void putValue(Property<T> var1, String var2) {
         String var3 = (String)this.terms.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Tried to replace " + String.valueOf(var1) + " value from " + var3 + " to " + var2);
         }
      }

      public final <T extends Comparable<T>> TerminalCondition term(Property<T> var1, T var2) {
         this.putValue(var1, var1.getName(var2));
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> TerminalCondition term(Property<T> var1, T var2, T... var3) {
         this.putValue(var1, getTerm(var1, var2, var3));
         return this;
      }

      public final <T extends Comparable<T>> TerminalCondition negatedTerm(Property<T> var1, T var2) {
         String var10002 = var1.getName(var2);
         this.putValue(var1, "!" + var10002);
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> TerminalCondition negatedTerm(Property<T> var1, T var2, T... var3) {
         String var10002 = getTerm(var1, var2, var3);
         this.putValue(var1, "!" + var10002);
         return this;
      }

      public JsonElement get() {
         JsonObject var1 = new JsonObject();
         this.terms.forEach((var1x, var2) -> {
            var1.addProperty(var1x.getName(), var2);
         });
         return var1;
      }

      public void validate(StateDefinition<?, ?> var1) {
         List var2 = (List)this.terms.keySet().stream().filter((var1x) -> {
            return var1.getProperty(var1x.getName()) != var1x;
         }).collect(Collectors.toList());
         if (!var2.isEmpty()) {
            String var10002 = String.valueOf(var2);
            throw new IllegalStateException("Properties " + var10002 + " are missing from " + String.valueOf(var1));
         }
      }

      // $FF: synthetic method
      public Object get() {
         return this.get();
      }
   }

   public static class CompositeCondition implements Condition {
      private final Operation operation;
      private final List<Condition> subconditions;

      CompositeCondition(Operation var1, List<Condition> var2) {
         super();
         this.operation = var1;
         this.subconditions = var2;
      }

      public void validate(StateDefinition<?, ?> var1) {
         this.subconditions.forEach((var1x) -> {
            var1x.validate(var1);
         });
      }

      public JsonElement get() {
         JsonArray var1 = new JsonArray();
         Stream var10000 = this.subconditions.stream().map(Supplier::get);
         Objects.requireNonNull(var1);
         var10000.forEach(var1::add);
         JsonObject var2 = new JsonObject();
         var2.add(this.operation.id, var1);
         return var2;
      }

      // $FF: synthetic method
      public Object get() {
         return this.get();
      }
   }

   public static enum Operation {
      AND("AND"),
      OR("OR");

      final String id;

      private Operation(final String var3) {
         this.id = var3;
      }

      // $FF: synthetic method
      private static Operation[] $values() {
         return new Operation[]{AND, OR};
      }
   }
}
