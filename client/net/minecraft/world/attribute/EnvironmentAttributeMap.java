package net.minecraft.world.attribute;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import org.jspecify.annotations.Nullable;

public final class EnvironmentAttributeMap {
   public static final EnvironmentAttributeMap EMPTY = new EnvironmentAttributeMap(Map.of());
   public static final Codec<EnvironmentAttributeMap> CODEC = Codec.lazyInitialized(() -> Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(Entry::createCodec)).xmap(EnvironmentAttributeMap::new, (v) -> v.entries));
   public static final Codec<EnvironmentAttributeMap> NETWORK_CODEC;
   public static final Codec<EnvironmentAttributeMap> CODEC_ONLY_POSITIONAL;
   private final Map<EnvironmentAttribute<?>, Entry<?, ?>> entries;

   private static EnvironmentAttributeMap filterSyncable(final EnvironmentAttributeMap attributes) {
      return new EnvironmentAttributeMap(Map.copyOf(Maps.filterKeys(attributes.entries, EnvironmentAttribute::isSyncable)));
   }

   private EnvironmentAttributeMap(final Map<EnvironmentAttribute<?>, Entry<?, ?>> entries) {
      super();
      this.entries = entries;
   }

   public static Builder builder() {
      return new Builder();
   }

   public <Value> @Nullable Entry<Value, ?> get(final EnvironmentAttribute<Value> attribute) {
      return (Entry)this.entries.get(attribute);
   }

   public <Value> Value applyModifier(final EnvironmentAttribute<Value> attribute, final Value baseValue) {
      Entry<Value, ?> entry = this.get(attribute);
      return (Value)(entry != null ? entry.applyModifier(baseValue) : baseValue);
   }

   public boolean contains(final EnvironmentAttribute<?> attribute) {
      return this.entries.containsKey(attribute);
   }

   public Set<EnvironmentAttribute<?>> keySet() {
      return this.entries.keySet();
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof EnvironmentAttributeMap) {
            EnvironmentAttributeMap attributes = (EnvironmentAttributeMap)obj;
            if (this.entries.equals(attributes.entries)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.entries.hashCode();
   }

   public String toString() {
      return this.entries.toString();
   }

   static {
      NETWORK_CODEC = CODEC.xmap(EnvironmentAttributeMap::filterSyncable, EnvironmentAttributeMap::filterSyncable);
      CODEC_ONLY_POSITIONAL = CODEC.validate((map) -> {
         List<EnvironmentAttribute<?>> illegalAttributes = map.keySet().stream().filter((attribute) -> !attribute.isPositional()).toList();
         return !illegalAttributes.isEmpty() ? DataResult.error(() -> "The following attributes cannot be positional: " + String.valueOf(illegalAttributes)) : DataResult.success(map);
      });
   }

   public static record Entry<Value, Argument>(Argument argument, AttributeModifier<Value, Argument> modifier) {
      public Entry {
         super();
      }

      private static <Value> Codec<Entry<Value, ?>> createCodec(final EnvironmentAttribute<Value> attribute) {
         Codec<Entry<Value, ?>> fullCodec = attribute.type().modifierCodec().dispatch("modifier", Entry::modifier, Util.memoize((Function)((modifier) -> createFullCodec(attribute, modifier))));
         return Codec.either(attribute.valueCodec(), fullCodec).xmap((either) -> (Entry)either.map((value) -> new Entry(value, AttributeModifier.override()), (e) -> e), (entry) -> entry.modifier == AttributeModifier.override() ? Either.left(entry.argument()) : Either.right(entry));
      }

      private static <Value, Argument> MapCodec<Entry<Value, Argument>> createFullCodec(final EnvironmentAttribute<Value> attribute, final AttributeModifier<Value, Argument> modifier) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(modifier.argumentCodec(attribute).fieldOf("argument").forGetter(Entry::argument)).apply(i, (value) -> new Entry(value, modifier)));
      }

      public Value applyModifier(final Value subject) {
         return this.modifier.apply(subject, this.argument);
      }
   }

   public static class Builder {
      private final Map<EnvironmentAttribute<?>, Entry<?, ?>> entries = new HashMap();

      private Builder() {
         super();
      }

      public Builder putAll(final EnvironmentAttributeMap map) {
         this.entries.putAll(map.entries);
         return this;
      }

      public <Value, Parameter> Builder modify(final EnvironmentAttribute<Value> attribute, final AttributeModifier<Value, Parameter> modifier, final Parameter value) {
         attribute.type().checkAllowedModifier(modifier);
         this.entries.put(attribute, new Entry(value, modifier));
         return this;
      }

      public <Value> Builder set(final EnvironmentAttribute<Value> attribute, final Value value) {
         return this.modify(attribute, AttributeModifier.override(), value);
      }

      public EnvironmentAttributeMap build() {
         return this.entries.isEmpty() ? EnvironmentAttributeMap.EMPTY : new EnvironmentAttributeMap(Map.copyOf(this.entries));
      }
   }
}
