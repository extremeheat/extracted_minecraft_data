package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public class StatePropertiesPredicate {
   public static final StatePropertiesPredicate ANY = new StatePropertiesPredicate(ImmutableList.of());
   private final List<StatePropertiesPredicate.PropertyMatcher> properties;

   private static StatePropertiesPredicate.PropertyMatcher fromJson(String var0, JsonElement var1) {
      if (var1.isJsonPrimitive()) {
         String var5 = var1.getAsString();
         return new StatePropertiesPredicate.ExactPropertyMatcher(var0, var5);
      } else {
         JsonObject var2 = GsonHelper.convertToJsonObject(var1, "value");
         String var3 = var2.has("min") ? getStringOrNull(var2.get("min")) : null;
         String var4 = var2.has("max") ? getStringOrNull(var2.get("max")) : null;
         return (StatePropertiesPredicate.PropertyMatcher)(var3 != null && var3.equals(var4)
            ? new StatePropertiesPredicate.ExactPropertyMatcher(var0, var3)
            : new StatePropertiesPredicate.RangedPropertyMatcher(var0, var3, var4));
      }
   }

   @Nullable
   private static String getStringOrNull(JsonElement var0) {
      return var0.isJsonNull() ? null : var0.getAsString();
   }

   StatePropertiesPredicate(List<StatePropertiesPredicate.PropertyMatcher> var1) {
      super();
      this.properties = ImmutableList.copyOf(var1);
   }

   public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> var1, S var2) {
      for(StatePropertiesPredicate.PropertyMatcher var4 : this.properties) {
         if (!var4.match(var1, (S)var2)) {
            return false;
         }
      }

      return true;
   }

   public boolean matches(BlockState var1) {
      return this.matches(var1.getBlock().getStateDefinition(), var1);
   }

   public boolean matches(FluidState var1) {
      return this.matches(var1.getType().getStateDefinition(), var1);
   }

   public void checkState(StateDefinition<?, ?> var1, Consumer<String> var2) {
      this.properties.forEach(var2x -> var2x.checkState(var1, var2));
   }

   public static StatePropertiesPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "properties");
         ArrayList var2 = Lists.newArrayList();

         for(Entry var4 : var1.entrySet()) {
            var2.add(fromJson((String)var4.getKey(), (JsonElement)var4.getValue()));
         }

         return new StatePropertiesPredicate(var2);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (!this.properties.isEmpty()) {
            this.properties.forEach(var1x -> var1.add(var1x.getName(), var1x.toJson()));
         }

         return var1;
      }
   }

   public static class Builder {
      private final List<StatePropertiesPredicate.PropertyMatcher> matchers = Lists.newArrayList();

      private Builder() {
         super();
      }

      public static StatePropertiesPredicate.Builder properties() {
         return new StatePropertiesPredicate.Builder();
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<?> var1, String var2) {
         this.matchers.add(new StatePropertiesPredicate.ExactPropertyMatcher(var1.getName(), var2));
         return this;
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<Integer> var1, int var2) {
         return this.hasProperty(var1, Integer.toString(var2));
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<Boolean> var1, boolean var2) {
         return this.hasProperty(var1, Boolean.toString(var2));
      }

      public <T extends Comparable<T> & StringRepresentable> StatePropertiesPredicate.Builder hasProperty(Property<T> var1, T var2) {
         return this.hasProperty(var1, ((StringRepresentable)var2).getSerializedName());
      }

      public StatePropertiesPredicate build() {
         return new StatePropertiesPredicate(this.matchers);
      }
   }

   static class ExactPropertyMatcher extends StatePropertiesPredicate.PropertyMatcher {
      private final String value;

      public ExactPropertyMatcher(String var1, String var2) {
         super(var1);
         this.value = var2;
      }

      @Override
      protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2) {
         Comparable var3 = var1.getValue(var2);
         Optional var4 = var2.getValue(this.value);
         return var4.isPresent() && var3.compareTo((Comparable)var4.get()) == 0;
      }

      @Override
      public JsonElement toJson() {
         return new JsonPrimitive(this.value);
      }
   }

   abstract static class PropertyMatcher {
      private final String name;

      public PropertyMatcher(String var1) {
         super();
         this.name = var1;
      }

      public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> var1, S var2) {
         Property var3 = var1.getProperty(this.name);
         return var3 == null ? false : this.match(var2, var3);
      }

      protected abstract <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2);

      public abstract JsonElement toJson();

      public String getName() {
         return this.name;
      }

      public void checkState(StateDefinition<?, ?> var1, Consumer<String> var2) {
         Property var3 = var1.getProperty(this.name);
         if (var3 == null) {
            var2.accept(this.name);
         }
      }
   }

   static class RangedPropertyMatcher extends StatePropertiesPredicate.PropertyMatcher {
      @Nullable
      private final String minValue;
      @Nullable
      private final String maxValue;

      public RangedPropertyMatcher(String var1, @Nullable String var2, @Nullable String var3) {
         super(var1);
         this.minValue = var2;
         this.maxValue = var3;
      }

      @Override
      protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2) {
         Comparable var3 = var1.getValue(var2);
         if (this.minValue != null) {
            Optional var4 = var2.getValue(this.minValue);
            if (!var4.isPresent() || var3.compareTo((Comparable)var4.get()) < 0) {
               return false;
            }
         }

         if (this.maxValue != null) {
            Optional var5 = var2.getValue(this.maxValue);
            if (!var5.isPresent() || var3.compareTo((Comparable)var5.get()) > 0) {
               return false;
            }
         }

         return true;
      }

      @Override
      public JsonElement toJson() {
         JsonObject var1 = new JsonObject();
         if (this.minValue != null) {
            var1.addProperty("min", this.minValue);
         }

         if (this.maxValue != null) {
            var1.addProperty("max", this.maxValue);
         }

         return var1;
      }
   }
}
