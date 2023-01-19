package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public interface Condition extends Supplier<JsonElement> {
   void validate(StateDefinition<?, ?> var1);

   static Condition.TerminalCondition condition() {
      return new Condition.TerminalCondition();
   }

   static Condition and(Condition... var0) {
      return new Condition.CompositeCondition(Condition.Operation.AND, Arrays.asList(var0));
   }

   static Condition or(Condition... var0) {
      return new Condition.CompositeCondition(Condition.Operation.OR, Arrays.asList(var0));
   }

   public static class CompositeCondition implements Condition {
      private final Condition.Operation operation;
      private final List<Condition> subconditions;

      CompositeCondition(Condition.Operation var1, List<Condition> var2) {
         super();
         this.operation = var1;
         this.subconditions = var2;
      }

      @Override
      public void validate(StateDefinition<?, ?> var1) {
         this.subconditions.forEach(var1x -> var1x.validate(var1));
      }

      public JsonElement get() {
         JsonArray var1 = new JsonArray();
         this.subconditions.stream().map(Supplier::get).forEach(var1::add);
         JsonObject var2 = new JsonObject();
         var2.add(this.operation.id, var1);
         return var2;
      }
   }

   public static enum Operation {
      AND("AND"),
      OR("OR");

      final String id;

      private Operation(String var3) {
         this.id = var3;
      }
   }

   public static class TerminalCondition implements Condition {
      private final Map<Property<?>, String> terms = Maps.newHashMap();

      public TerminalCondition() {
         super();
      }

      private static <T extends Comparable<T>> String joinValues(Property<T> var0, Stream<T> var1) {
         return var1.<String>map(var0::getName).collect(Collectors.joining("|"));
      }

      private static <T extends Comparable<T>> String getTerm(Property<T> var0, T var1, T[] var2) {
         return joinValues(var0, Stream.concat(Stream.of((T)var1), Stream.of((T[])var2)));
      }

      private <T extends Comparable<T>> void putValue(Property<T> var1, String var2) {
         String var3 = this.terms.put(var1, var2);
         if (var3 != null) {
            throw new IllegalStateException("Tried to replace " + var1 + " value from " + var3 + " to " + var2);
         }
      }

      public final <T extends Comparable<T>> Condition.TerminalCondition term(Property<T> var1, T var2) {
         this.putValue(var1, var1.getName((T)var2));
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> Condition.TerminalCondition term(Property<T> var1, T var2, T... var3) {
         this.putValue(var1, getTerm(var1, (T)var2, (T[])var3));
         return this;
      }

      public final <T extends Comparable<T>> Condition.TerminalCondition negatedTerm(Property<T> var1, T var2) {
         this.putValue(var1, "!" + var1.getName((T)var2));
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> Condition.TerminalCondition negatedTerm(Property<T> var1, T var2, T... var3) {
         this.putValue(var1, "!" + getTerm(var1, (T)var2, (T[])var3));
         return this;
      }

      public JsonElement get() {
         JsonObject var1 = new JsonObject();
         this.terms.forEach((var1x, var2) -> var1.addProperty(var1x.getName(), var2));
         return var1;
      }

      @Override
      public void validate(StateDefinition<?, ?> var1) {
         List var2 = this.terms.keySet().stream().filter(var1x -> var1.getProperty(var1x.getName()) != var1x).collect(Collectors.toList());
         if (!var2.isEmpty()) {
            throw new IllegalStateException("Properties " + var2 + " are missing from " + var1);
         }
      }
   }
}
