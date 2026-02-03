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
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public abstract class StateHolder<O, S> {
   public static final String NAME_TAG = "Name";
   public static final String PROPERTIES_TAG = "Properties";
   private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Map.Entry<Property<?>, Comparable<?>>, String>() {
      public String apply(final Map.@Nullable Entry<Property<?>, Comparable<?>> entry) {
         if (entry == null) {
            return "<NULL>";
         } else {
            Property<?> property = (Property)entry.getKey();
            String var10000 = property.getName();
            return var10000 + "=" + this.getName(property, (Comparable)entry.getValue());
         }
      }

      private <T extends Comparable<T>> String getName(final Property<T> property, final Comparable<?> value) {
         return property.getName(value);
      }
   };
   protected final O owner;
   private final Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;
   private Map<Property<?>, S[]> neighbours;
   protected final MapCodec<S> propertiesCodec;

   protected StateHolder(final O owner, final Reference2ObjectArrayMap<Property<?>, Comparable<?>> values, final MapCodec<S> propertiesCodec) {
      super();
      this.owner = owner;
      this.values = values;
      this.propertiesCodec = propertiesCodec;
   }

   public <T extends Comparable<T>> S cycle(final Property<T> property) {
      return (S)this.setValue(property, (Comparable)findNextInCollection(property.getPossibleValues(), this.getValue(property)));
   }

   protected static <T> T findNextInCollection(final List<T> values, final T current) {
      int nextIndex = values.indexOf(current) + 1;
      return (T)(nextIndex == values.size() ? values.getFirst() : values.get(nextIndex));
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.owner);
      if (!this.getValues().isEmpty()) {
         builder.append('[');
         builder.append((String)this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
         builder.append(']');
      }

      return builder.toString();
   }

   public final boolean equals(final Object obj) {
      return super.equals(obj);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public Collection<Property<?>> getProperties() {
      return Collections.unmodifiableCollection(this.values.keySet());
   }

   public boolean hasProperty(final Property<?> property) {
      return this.values.containsKey(property);
   }

   public <T extends Comparable<T>> T getValue(final Property<T> property) {
      Comparable<?> value = (Comparable)this.values.get(property);
      if (value == null) {
         String var10002 = String.valueOf(property);
         throw new IllegalArgumentException("Cannot get property " + var10002 + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return (T)(property.getValueClass().cast(value));
      }
   }

   public <T extends Comparable<T>> Optional<T> getOptionalValue(final Property<T> property) {
      return Optional.ofNullable(this.getNullableValue(property));
   }

   public <T extends Comparable<T>> T getValueOrElse(final Property<T> property, final T defaultValue) {
      return (T)(Objects.requireNonNullElse(this.getNullableValue(property), defaultValue));
   }

   private <T extends Comparable<T>> @Nullable T getNullableValue(final Property<T> property) {
      Comparable<?> value = (Comparable)this.values.get(property);
      return (T)(value == null ? null : (Comparable)property.getValueClass().cast(value));
   }

   public <T extends Comparable<T>, V extends T> S setValue(final Property<T> property, final V value) {
      Comparable<?> oldValue = (Comparable)this.values.get(property);
      if (oldValue == null) {
         String var10002 = String.valueOf(property);
         throw new IllegalArgumentException("Cannot set property " + var10002 + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return (S)this.setValueInternal(property, value, oldValue);
      }
   }

   public <T extends Comparable<T>, V extends T> S trySetValue(final Property<T> property, final V value) {
      Comparable<?> oldValue = (Comparable)this.values.get(property);
      return (S)(oldValue == null ? this : this.setValueInternal(property, value, oldValue));
   }

   private <T extends Comparable<T>, V extends T> S setValueInternal(final Property<T> property, final V value, final Comparable<?> oldValue) {
      if (oldValue.equals(value)) {
         return (S)this;
      } else {
         int internalIndex = property.getInternalIndex(value);
         if (internalIndex < 0) {
            String var10002 = String.valueOf(property);
            throw new IllegalArgumentException("Cannot set property " + var10002 + " to " + String.valueOf(value) + " on " + String.valueOf(this.owner) + ", it is not an allowed value");
         } else {
            return (S)((Object[])this.neighbours.get(property))[internalIndex];
         }
      }
   }

   public void populateNeighbours(final Map<Map<Property<?>, Comparable<?>>, S> statesByValues) {
      if (this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         Map<Property<?>, S[]> neighbours = new Reference2ObjectArrayMap(this.values.size());
         ObjectIterator var3 = this.values.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<Property<?>, Comparable<?>> entry = (Map.Entry)var3.next();
            Property<?> property = (Property)entry.getKey();
            neighbours.put(property, property.getPossibleValues().stream().map((value) -> statesByValues.get(this.makeNeighbourValues(property, value))).toArray());
         }

         this.neighbours = neighbours;
      }
   }

   private Map<Property<?>, Comparable<?>> makeNeighbourValues(final Property<?> property, final Comparable<?> value) {
      Map<Property<?>, Comparable<?>> neighbour = new Reference2ObjectArrayMap(this.values);
      neighbour.put(property, value);
      return neighbour;
   }

   public Map<Property<?>, Comparable<?>> getValues() {
      return this.values;
   }

   protected static <O, S extends StateHolder<O, S>> Codec<S> codec(final Codec<O> ownerCodec, final Function<O, S> defaultState) {
      return ownerCodec.dispatch("Name", (s) -> s.owner, (o) -> {
         S defaultValue = (StateHolder)defaultState.apply(o);
         return defaultValue.getValues().isEmpty() ? MapCodec.unit(defaultValue) : defaultValue.propertiesCodec.codec().lenientOptionalFieldOf("Properties").xmap((oo) -> (StateHolder)oo.orElse(defaultValue), Optional::of);
      });
   }
}
