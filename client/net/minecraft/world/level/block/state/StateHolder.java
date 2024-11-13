package net.minecraft.world.level.block.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class StateHolder<O, S> {
   public static final String NAME_TAG = "Name";
   public static final String PROPERTIES_TAG = "Properties";
   private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Map.Entry<Property<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> var1) {
         if (var1 == null) {
            return "<NULL>";
         } else {
            Property var2 = (Property)var1.getKey();
            String var10000 = var2.getName();
            return var10000 + "=" + this.getName(var2, (Comparable)var1.getValue());
         }
      }

      private <T extends Comparable<T>> String getName(Property<T> var1, Comparable<?> var2) {
         return var1.getName(var2);
      }

      // $FF: synthetic method
      public Object apply(@Nullable final Object var1) {
         return this.apply((Map.Entry)var1);
      }
   };
   protected final O owner;
   private final Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;
   private Map<Property<?>, S[]> neighbours;
   protected final MapCodec<S> propertiesCodec;

   protected StateHolder(O var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3) {
      super();
      this.owner = var1;
      this.values = var2;
      this.propertiesCodec = var3;
   }

   public <T extends Comparable<T>> S cycle(Property<T> var1) {
      return (S)this.setValue(var1, (Comparable)findNextInCollection(var1.getPossibleValues(), this.getValue(var1)));
   }

   protected static <T> T findNextInCollection(List<T> var0, T var1) {
      int var2 = var0.indexOf(var1) + 1;
      return (T)(var2 == var0.size() ? var0.getFirst() : var0.get(var2));
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.owner);
      if (!this.getValues().isEmpty()) {
         var1.append('[');
         var1.append((String)this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         var1.append(']');
      }

      return var1.toString();
   }

   public Collection<Property<?>> getProperties() {
      return Collections.unmodifiableCollection(this.values.keySet());
   }

   public <T extends Comparable<T>> boolean hasProperty(Property<T> var1) {
      return this.values.containsKey(var1);
   }

   public <T extends Comparable<T>> T getValue(Property<T> var1) {
      Comparable var2 = (Comparable)this.values.get(var1);
      if (var2 == null) {
         String var10002 = String.valueOf(var1);
         throw new IllegalArgumentException("Cannot get property " + var10002 + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return (T)(var1.getValueClass().cast(var2));
      }
   }

   public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> var1) {
      return Optional.ofNullable(this.getNullableValue(var1));
   }

   public <T extends Comparable<T>> T getValueOrElse(Property<T> var1, T var2) {
      return (T)(Objects.requireNonNullElse(this.getNullableValue(var1), var2));
   }

   @Nullable
   public <T extends Comparable<T>> T getNullableValue(Property<T> var1) {
      Comparable var2 = (Comparable)this.values.get(var1);
      return (T)(var2 == null ? null : (Comparable)var1.getValueClass().cast(var2));
   }

   public <T extends Comparable<T>, V extends T> S setValue(Property<T> var1, V var2) {
      Comparable var3 = (Comparable)this.values.get(var1);
      if (var3 == null) {
         String var10002 = String.valueOf(var1);
         throw new IllegalArgumentException("Cannot set property " + var10002 + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return (S)this.setValueInternal(var1, var2, var3);
      }
   }

   public <T extends Comparable<T>, V extends T> S trySetValue(Property<T> var1, V var2) {
      Comparable var3 = (Comparable)this.values.get(var1);
      return (S)(var3 == null ? this : this.setValueInternal(var1, var2, var3));
   }

   private <T extends Comparable<T>, V extends T> S setValueInternal(Property<T> var1, V var2, Comparable<?> var3) {
      if (var3.equals(var2)) {
         return (S)this;
      } else {
         int var4 = var1.getInternalIndex(var2);
         if (var4 < 0) {
            String var10002 = String.valueOf(var1);
            throw new IllegalArgumentException("Cannot set property " + var10002 + " to " + String.valueOf(var2) + " on " + String.valueOf(this.owner) + ", it is not an allowed value");
         } else {
            return (S)((Object[])this.neighbours.get(var1))[var4];
         }
      }
   }

   public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> var1) {
      if (this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         Reference2ObjectArrayMap var2 = new Reference2ObjectArrayMap(this.values.size());
         ObjectIterator var3 = this.values.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            Property var5 = (Property)var4.getKey();
            var2.put(var5, var5.getPossibleValues().stream().map((var3x) -> var1.get(this.makeNeighbourValues(var5, var3x))).toArray());
         }

         this.neighbours = var2;
      }
   }

   private Map<Property<?>, Comparable<?>> makeNeighbourValues(Property<?> var1, Comparable<?> var2) {
      Reference2ObjectArrayMap var3 = new Reference2ObjectArrayMap(this.values);
      var3.put(var1, var2);
      return var3;
   }

   public Map<Property<?>, Comparable<?>> getValues() {
      return this.values;
   }

   protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> var0, Function<O, S> var1) {
      return var0.dispatch("Name", (var0x) -> var0x.owner, (var1x) -> {
         StateHolder var2 = (StateHolder)var1.apply(var1x);
         return var2.getValues().isEmpty() ? MapCodec.unit(var2) : var2.propertiesCodec.codec().lenientOptionalFieldOf("Properties").xmap((var1xx) -> (StateHolder)var1xx.orElse(var2), Optional::of);
      });
   }
}
