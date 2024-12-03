package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
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

public record StatePropertiesPredicate(List<PropertyMatcher> properties) {
   private static final Codec<List<PropertyMatcher>> PROPERTIES_CODEC;
   public static final Codec<StatePropertiesPredicate> CODEC;
   public static final StreamCodec<ByteBuf, StatePropertiesPredicate> STREAM_CODEC;

   public StatePropertiesPredicate(List<PropertyMatcher> var1) {
      super();
      this.properties = var1;
   }

   public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> var1, S var2) {
      for(PropertyMatcher var4 : this.properties) {
         if (!var4.match(var1, var2)) {
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
      for(PropertyMatcher var3 : this.properties) {
         Optional var4 = var3.checkState(var1);
         if (var4.isPresent()) {
            return var4;
         }
      }

      return Optional.empty();
   }

   static {
      PROPERTIES_CODEC = Codec.unboundedMap(Codec.STRING, StatePropertiesPredicate.ValueMatcher.CODEC).xmap((var0) -> var0.entrySet().stream().map((var0x) -> new PropertyMatcher((String)var0x.getKey(), (ValueMatcher)var0x.getValue())).toList(), (var0) -> (Map)var0.stream().collect(Collectors.toMap(PropertyMatcher::name, PropertyMatcher::valueMatcher)));
      CODEC = PROPERTIES_CODEC.xmap(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);
      STREAM_CODEC = StatePropertiesPredicate.PropertyMatcher.STREAM_CODEC.apply(ByteBufCodecs.list()).map(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);
   }

   static record PropertyMatcher(String name, ValueMatcher valueMatcher) {
      public static final StreamCodec<ByteBuf, PropertyMatcher> STREAM_CODEC;

      PropertyMatcher(String var1, ValueMatcher var2) {
         super();
         this.name = var1;
         this.valueMatcher = var2;
      }

      public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> var1, S var2) {
         Property var3 = var1.getProperty(this.name);
         return var3 != null && this.valueMatcher.match(var2, var3);
      }

      public Optional<String> checkState(StateDefinition<?, ?> var1) {
         Property var2 = var1.getProperty(this.name);
         return var2 != null ? Optional.empty() : Optional.of(this.name);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, PropertyMatcher::name, StatePropertiesPredicate.ValueMatcher.STREAM_CODEC, PropertyMatcher::valueMatcher, PropertyMatcher::new);
      }
   }

   interface ValueMatcher {
      Codec<ValueMatcher> CODEC = Codec.either(StatePropertiesPredicate.ExactMatcher.CODEC, StatePropertiesPredicate.RangedMatcher.CODEC).xmap(Either::unwrap, (var0) -> {
         if (var0 instanceof ExactMatcher var1) {
            return Either.left(var1);
         } else if (var0 instanceof RangedMatcher var2) {
            return Either.right(var2);
         } else {
            throw new UnsupportedOperationException();
         }
      });
      StreamCodec<ByteBuf, ValueMatcher> STREAM_CODEC = ByteBufCodecs.either(StatePropertiesPredicate.ExactMatcher.STREAM_CODEC, StatePropertiesPredicate.RangedMatcher.STREAM_CODEC).map(Either::unwrap, (var0) -> {
         if (var0 instanceof ExactMatcher var1) {
            return Either.left(var1);
         } else if (var0 instanceof RangedMatcher var2) {
            return Either.right(var2);
         } else {
            throw new UnsupportedOperationException();
         }
      });

      <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2);
   }

   static record ExactMatcher(String value) implements ValueMatcher {
      public static final Codec<ExactMatcher> CODEC;
      public static final StreamCodec<ByteBuf, ExactMatcher> STREAM_CODEC;

      ExactMatcher(String var1) {
         super();
         this.value = var1;
      }

      public <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2) {
         Comparable var3 = var1.getValue(var2);
         Optional var4 = var2.getValue(this.value);
         return var4.isPresent() && var3.compareTo((Comparable)var4.get()) == 0;
      }

      static {
         CODEC = Codec.STRING.xmap(ExactMatcher::new, ExactMatcher::value);
         STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ExactMatcher::new, ExactMatcher::value);
      }
   }

   static record RangedMatcher(Optional<String> minValue, Optional<String> maxValue) implements ValueMatcher {
      public static final Codec<RangedMatcher> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.optionalFieldOf("min").forGetter(RangedMatcher::minValue), Codec.STRING.optionalFieldOf("max").forGetter(RangedMatcher::maxValue)).apply(var0, RangedMatcher::new));
      public static final StreamCodec<ByteBuf, RangedMatcher> STREAM_CODEC;

      private RangedMatcher(Optional<String> var1, Optional<String> var2) {
         super();
         this.minValue = var1;
         this.maxValue = var2;
      }

      public <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2) {
         Comparable var3 = var1.getValue(var2);
         if (this.minValue.isPresent()) {
            Optional var4 = var2.getValue((String)this.minValue.get());
            if (var4.isEmpty() || var3.compareTo((Comparable)var4.get()) < 0) {
               return false;
            }
         }

         if (this.maxValue.isPresent()) {
            Optional var5 = var2.getValue((String)this.maxValue.get());
            if (var5.isEmpty() || var3.compareTo((Comparable)var5.get()) > 0) {
               return false;
            }
         }

         return true;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), RangedMatcher::minValue, ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), RangedMatcher::maxValue, RangedMatcher::new);
      }
   }

   public static class Builder {
      private final ImmutableList.Builder<PropertyMatcher> matchers = ImmutableList.builder();

      private Builder() {
         super();
      }

      public static Builder properties() {
         return new Builder();
      }

      public Builder hasProperty(Property<?> var1, String var2) {
         this.matchers.add(new PropertyMatcher(var1.getName(), new ExactMatcher(var2)));
         return this;
      }

      public Builder hasProperty(Property<Integer> var1, int var2) {
         return this.hasProperty(var1, Integer.toString(var2));
      }

      public Builder hasProperty(Property<Boolean> var1, boolean var2) {
         return this.hasProperty(var1, Boolean.toString(var2));
      }

      public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(Property<T> var1, T var2) {
         return this.hasProperty(var1, ((StringRepresentable)var2).getSerializedName());
      }

      public Optional<StatePropertiesPredicate> build() {
         return Optional.of(new StatePropertiesPredicate(this.matchers.build()));
      }
   }
}
