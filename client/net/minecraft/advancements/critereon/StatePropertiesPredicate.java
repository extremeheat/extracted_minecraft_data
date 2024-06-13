package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public record StatePropertiesPredicate(List<StatePropertiesPredicate.PropertyMatcher> properties) {
   private static final Codec<List<StatePropertiesPredicate.PropertyMatcher>> PROPERTIES_CODEC = Codec.unboundedMap(
         Codec.STRING, StatePropertiesPredicate.ValueMatcher.CODEC
      )
      .xmap(
         var0 -> var0.entrySet()
               .stream()
               .map(var0x -> new StatePropertiesPredicate.PropertyMatcher((String)var0x.getKey(), (StatePropertiesPredicate.ValueMatcher)var0x.getValue()))
               .toList(),
         var0 -> var0.stream()
               .collect(Collectors.toMap(StatePropertiesPredicate.PropertyMatcher::name, StatePropertiesPredicate.PropertyMatcher::valueMatcher))
      );
   public static final Codec<StatePropertiesPredicate> CODEC = PROPERTIES_CODEC.xmap(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);
   public static final StreamCodec<ByteBuf, StatePropertiesPredicate> STREAM_CODEC = StatePropertiesPredicate.PropertyMatcher.STREAM_CODEC
      .apply(ByteBufCodecs.list())
      .map(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);

   public StatePropertiesPredicate(List<StatePropertiesPredicate.PropertyMatcher> properties) {
      super();
      this.properties = (List<StatePropertiesPredicate.PropertyMatcher>)properties;
   }

   public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> var1, S var2) {
      for (StatePropertiesPredicate.PropertyMatcher var4 : this.properties) {
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

   public Optional<String> checkState(StateDefinition<?, ?> var1) {
      for (StatePropertiesPredicate.PropertyMatcher var3 : this.properties) {
         Optional var4 = var3.checkState(var1);
         if (var4.isPresent()) {
            return var4;
         }
      }

      return Optional.empty();
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<StatePropertiesPredicate.PropertyMatcher> matchers = ImmutableList.builder();

      private Builder() {
         super();
      }

      public static StatePropertiesPredicate.Builder properties() {
         return new StatePropertiesPredicate.Builder();
      }

      public StatePropertiesPredicate.Builder hasProperty(Property<?> var1, String var2) {
         this.matchers.add(new StatePropertiesPredicate.PropertyMatcher(var1.getName(), new StatePropertiesPredicate.ExactMatcher(var2)));
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

      public Optional<StatePropertiesPredicate> build() {
         return Optional.of(new StatePropertiesPredicate(this.matchers.build()));
      }
   }

   static record ExactMatcher(String value) implements StatePropertiesPredicate.ValueMatcher {
      public static final Codec<StatePropertiesPredicate.ExactMatcher> CODEC = Codec.STRING
         .xmap(StatePropertiesPredicate.ExactMatcher::new, StatePropertiesPredicate.ExactMatcher::value);
      public static final StreamCodec<ByteBuf, StatePropertiesPredicate.ExactMatcher> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
         .map(StatePropertiesPredicate.ExactMatcher::new, StatePropertiesPredicate.ExactMatcher::value);

      ExactMatcher(String value) {
         super();
         this.value = value;
      }

      @Override
      public <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2) {
         Comparable var3 = var1.getValue(var2);
         Optional var4 = var2.getValue(this.value);
         return var4.isPresent() && var3.compareTo((Comparable)var4.get()) == 0;
      }
   }

   static record PropertyMatcher(String name, StatePropertiesPredicate.ValueMatcher valueMatcher) {
      public static final StreamCodec<ByteBuf, StatePropertiesPredicate.PropertyMatcher> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8,
         StatePropertiesPredicate.PropertyMatcher::name,
         StatePropertiesPredicate.ValueMatcher.STREAM_CODEC,
         StatePropertiesPredicate.PropertyMatcher::valueMatcher,
         StatePropertiesPredicate.PropertyMatcher::new
      );

      PropertyMatcher(String name, StatePropertiesPredicate.ValueMatcher valueMatcher) {
         super();
         this.name = name;
         this.valueMatcher = valueMatcher;
      }

      public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> var1, S var2) {
         Property var3 = var1.getProperty(this.name);
         return var3 != null && this.valueMatcher.match(var2, var3);
      }

      public Optional<String> checkState(StateDefinition<?, ?> var1) {
         Property var2 = var1.getProperty(this.name);
         return var2 != null ? Optional.empty() : Optional.of(this.name);
      }
   }

   static record RangedMatcher(Optional<String> minValue, Optional<String> maxValue) implements StatePropertiesPredicate.ValueMatcher {
      public static final Codec<StatePropertiesPredicate.RangedMatcher> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.STRING.optionalFieldOf("min").forGetter(StatePropertiesPredicate.RangedMatcher::minValue),
                  Codec.STRING.optionalFieldOf("max").forGetter(StatePropertiesPredicate.RangedMatcher::maxValue)
               )
               .apply(var0, StatePropertiesPredicate.RangedMatcher::new)
      );
      public static final StreamCodec<ByteBuf, StatePropertiesPredicate.RangedMatcher> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
         StatePropertiesPredicate.RangedMatcher::minValue,
         ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
         StatePropertiesPredicate.RangedMatcher::maxValue,
         StatePropertiesPredicate.RangedMatcher::new
      );

      private RangedMatcher(Optional<String> minValue, Optional<String> maxValue) {
         super();
         this.minValue = minValue;
         this.maxValue = maxValue;
      }

      @Override
      public <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2) {
         Comparable var3 = var1.getValue(var2);
         if (this.minValue.isPresent()) {
            Optional var4 = var2.getValue(this.minValue.get());
            if (var4.isEmpty() || var3.compareTo((Comparable)var4.get()) < 0) {
               return false;
            }
         }

         if (this.maxValue.isPresent()) {
            Optional var5 = var2.getValue(this.maxValue.get());
            if (var5.isEmpty() || var3.compareTo((Comparable)var5.get()) > 0) {
               return false;
            }
         }

         return true;
      }
   }

   interface ValueMatcher {
      Codec<StatePropertiesPredicate.ValueMatcher> CODEC = Codec.either(
            StatePropertiesPredicate.ExactMatcher.CODEC, StatePropertiesPredicate.RangedMatcher.CODEC
         )
         .xmap(Either::unwrap, var0 -> {
            if (var0 instanceof StatePropertiesPredicate.ExactMatcher var1) {
               return Either.left(var1);
            } else if (var0 instanceof StatePropertiesPredicate.RangedMatcher var2) {
               return Either.right(var2);
            } else {
               throw new UnsupportedOperationException();
            }
         });
      StreamCodec<ByteBuf, StatePropertiesPredicate.ValueMatcher> STREAM_CODEC = ByteBufCodecs.either(
            StatePropertiesPredicate.ExactMatcher.STREAM_CODEC, StatePropertiesPredicate.RangedMatcher.STREAM_CODEC
         )
         .map(Either::unwrap, var0 -> {
            if (var0 instanceof StatePropertiesPredicate.ExactMatcher var1) {
               return Either.left(var1);
            } else if (var0 instanceof StatePropertiesPredicate.RangedMatcher var2) {
               return Either.right(var2);
            } else {
               throw new UnsupportedOperationException();
            }
         });

      <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2);
   }
}
