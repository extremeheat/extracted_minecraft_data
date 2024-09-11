package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition<O, S extends StateHolder<O, S>> {
   static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final O owner;
   private final ImmutableSortedMap<String, Property<?>> propertiesByName;
   private final ImmutableList<S> states;

   protected StateDefinition(Function<O, S> var1, O var2, StateDefinition.Factory<O, S> var3, Map<String, Property<?>> var4) {
      super();
      this.owner = (O)var2;
      this.propertiesByName = ImmutableSortedMap.copyOf(var4);
      Supplier var5 = () -> (StateHolder)var1.apply(var2);
      MapCodec var6 = MapCodec.of(Encoder.empty(), Decoder.unit(var5));
      UnmodifiableIterator var7 = this.propertiesByName.entrySet().iterator();

      while (var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         var6 = appendPropertyCodec(var6, var5, (String)var8.getKey(), (Property)var8.getValue());
      }

      MapCodec var13 = var6;
      LinkedHashMap var14 = Maps.newLinkedHashMap();
      ArrayList var9 = Lists.newArrayList();
      Stream var10 = Stream.of(Collections.emptyList());
      UnmodifiableIterator var11 = this.propertiesByName.values().iterator();

      while (var11.hasNext()) {
         Property var12 = (Property)var11.next();
         var10 = var10.flatMap(var1x -> var12.getPossibleValues().stream().map(var2x -> {
               ArrayList var3x = Lists.newArrayList(var1x);
               var3x.add(Pair.of(var12, var2x));
               return var3x;
            }));
      }

      var10.forEach(var5x -> {
         Reference2ObjectArrayMap var6x = new Reference2ObjectArrayMap(var5x.size());

         for (Pair var8x : var5x) {
            var6x.put((Property)var8x.getFirst(), (Comparable)var8x.getSecond());
         }

         StateHolder var9x = (StateHolder)var3.create(var2, var6x, var13);
         var14.put(var6x, var9x);
         var9.add(var9x);
      });

      for (StateHolder var16 : var9) {
         var16.populateNeighbours(var14);
      }

      this.states = ImmutableList.copyOf(var9);
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> appendPropertyCodec(
      MapCodec<S> var0, Supplier<S> var1, String var2, Property<T> var3
   ) {
      return Codec.mapPair(var0, var3.valueCodec().fieldOf(var2).orElseGet(var0x -> {
         }, () -> var3.value((StateHolder<?, ?>)var1.get())))
         .xmap(
            var1x -> (StateHolder)((StateHolder)var1x.getFirst()).setValue(var3, ((Property.Value)var1x.getSecond()).value()),
            var1x -> Pair.of(var1x, var3.value(var1x))
         );
   }

   public ImmutableList<S> getPossibleStates() {
      return this.states;
   }

   public S any() {
      return (S)this.states.get(0);
   }

   public O getOwner() {
      return this.owner;
   }

   public Collection<Property<?>> getProperties() {
      return this.propertiesByName.values();
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("block", this.owner)
         .add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList()))
         .toString();
   }

   @Nullable
   public Property<?> getProperty(String var1) {
      return (Property<?>)this.propertiesByName.get(var1);
   }

   public static class Builder<O, S extends StateHolder<O, S>> {
      private final O owner;
      private final Map<String, Property<?>> properties = Maps.newHashMap();

      public Builder(O var1) {
         super();
         this.owner = (O)var1;
      }

      public StateDefinition.Builder<O, S> add(Property<?>... var1) {
         for (Property var5 : var1) {
            this.validateProperty(var5);
            this.properties.put(var5.getName(), var5);
         }

         return this;
      }

      private <T extends Comparable<T>> void validateProperty(Property<T> var1) {
         String var2 = var1.getName();
         if (!StateDefinition.NAME_PATTERN.matcher(var2).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + var2);
         } else {
            List var3 = var1.getPossibleValues();
            if (var3.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + var2 + " with <= 1 possible values");
            } else {
               for (Comparable var5 : var3) {
                  String var6 = var1.getName((T)var5);
                  if (!StateDefinition.NAME_PATTERN.matcher(var6).matches()) {
                     throw new IllegalArgumentException(this.owner + " has property: " + var2 + " with invalidly named value: " + var6);
                  }
               }

               if (this.properties.containsKey(var2)) {
                  throw new IllegalArgumentException(this.owner + " has duplicate property: " + var2);
               }
            }
         }
      }

      public StateDefinition<O, S> create(Function<O, S> var1, StateDefinition.Factory<O, S> var2) {
         return new StateDefinition<>(var1, this.owner, var2, this.properties);
      }
   }

   public interface Factory<O, S> {
      S create(O var1, Reference2ObjectArrayMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
   }
}
