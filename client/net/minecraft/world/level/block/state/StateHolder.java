package net.minecraft.world.level.block.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class StateHolder<O, S> {
   public static final String NAME_TAG = "Name";
   public static final String PROPERTIES_TAG = "Properties";
   private static final Function<Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Entry<Property<?>, Comparable<?>>, String>(
      
   ) {
      public String apply(@Nullable Entry<Property<?>, Comparable<?>> var1) {
         if (var1 == null) {
            return "<NULL>";
         } else {
            Property var2 = (Property)var1.getKey();
            return var2.getName() + "=" + this.getName(var2, (Comparable<?>)var1.getValue());
         }
      }

      private <T extends Comparable<T>> String getName(Property<T> var1, Comparable<?> var2) {
         return var1.getName((T)var2);
      }
   };
   protected final O owner;
   private final Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;
   private Table<Property<?>, Comparable<?>, S> neighbours;
   protected final MapCodec<S> propertiesCodec;

   protected StateHolder(O var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3) {
      super();
      this.owner = (O)var1;
      this.values = var2;
      this.propertiesCodec = var3;
   }

   public <T extends Comparable<T>> S cycle(Property<T> var1) {
      return this.setValue(var1, findNextInCollection(var1.getPossibleValues(), this.getValue(var1)));
   }

   protected static <T> T findNextInCollection(Collection<T> var0, T var1) {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         if (var2.next().equals(var1)) {
            if (var2.hasNext()) {
               return (T)var2.next();
            }

            return (T)var0.iterator().next();
         }
      }

      return (T)var2.next();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.owner);
      if (!this.getValues().isEmpty()) {
         var1.append('[');
         var1.append(this.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
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
         throw new IllegalArgumentException("Cannot get property " + var1 + " as it does not exist in " + this.owner);
      } else {
         return (T)var1.getValueClass().cast(var2);
      }
   }

   public <T extends Comparable<T>> Optional<T> getOptionalValue(Property<T> var1) {
      Comparable var2 = (Comparable)this.values.get(var1);
      return var2 == null ? Optional.empty() : Optional.of((T)var1.getValueClass().cast(var2));
   }

   public <T extends Comparable<T>, V extends T> S setValue(Property<T> var1, V var2) {
      Comparable var3 = (Comparable)this.values.get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("Cannot set property " + var1 + " as it does not exist in " + this.owner);
      } else if (var3.equals(var2)) {
         return (S)this;
      } else {
         Object var4 = this.neighbours.get(var1, var2);
         if (var4 == null) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " to " + var2 + " on " + this.owner + ", it is not an allowed value");
         } else {
            return (S)var4;
         }
      }
   }

   public <T extends Comparable<T>, V extends T> S trySetValue(Property<T> var1, V var2) {
      Comparable var3 = (Comparable)this.values.get(var1);
      if (var3 != null && !var3.equals(var2)) {
         Object var4 = this.neighbours.get(var1, var2);
         if (var4 == null) {
            throw new IllegalArgumentException("Cannot set property " + var1 + " to " + var2 + " on " + this.owner + ", it is not an allowed value");
         } else {
            return (S)var4;
         }
      } else {
         return (S)this;
      }
   }

   public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> var1) {
      if (this.neighbours != null) {
         throw new IllegalStateException();
      } else {
         HashBasedTable var2 = HashBasedTable.create();
         ObjectIterator var3 = this.values.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            Property var5 = (Property)var4.getKey();

            for(Comparable var7 : var5.getPossibleValues()) {
               if (!var7.equals(var4.getValue())) {
                  var2.put(var5, var7, var1.get(this.makeNeighbourValues(var5, var7)));
               }
            }
         }

         this.neighbours = (Table<Property<?>, Comparable<?>, S>)(var2.isEmpty() ? var2 : ArrayTable.create(var2));
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
      return var0.dispatch(
         "Name",
         var0x -> var0x.owner,
         var1x -> {
            StateHolder var2 = (StateHolder)var1.apply(var1x);
            return var2.getValues().isEmpty()
               ? MapCodec.unit(var2)
               : var2.propertiesCodec.codec().lenientOptionalFieldOf("Properties").xmap(var1xx -> var1xx.orElse(var2), Optional::of);
         }
      );
   }
}
