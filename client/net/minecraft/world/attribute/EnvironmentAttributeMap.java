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
   public static final Codec<EnvironmentAttributeMap> CODEC = Codec.lazyInitialized(() -> Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(Entry::createCodec)).xmap(EnvironmentAttributeMap::new, (var0) -> var0.entries));
   public static final Codec<EnvironmentAttributeMap> NETWORK_CODEC;
   public static final Codec<EnvironmentAttributeMap> CODEC_ONLY_POSITIONAL;
   final Map<EnvironmentAttribute<?>, Entry<?, ?>> entries;

   private static EnvironmentAttributeMap filterSyncable(EnvironmentAttributeMap var0) {
      return new EnvironmentAttributeMap(Map.copyOf(Maps.filterKeys(var0.entries, EnvironmentAttribute::isSyncable)));
   }

   EnvironmentAttributeMap(Map<EnvironmentAttribute<?>, Entry<?, ?>> var1) {
      super();
      this.entries = var1;
   }

   public static Builder builder() {
      return new Builder();
   }

   public <Value> @Nullable Entry<Value, ?> get(EnvironmentAttribute<Value> var1) {
      return (Entry)this.entries.get(var1);
   }

   public <Value> Value applyModifier(EnvironmentAttribute<Value> var1, Value var2) {
      Entry var3 = this.get(var1);
      return var3 != null ? var3.applyModifier(var2) : var2;
   }

   public boolean contains(EnvironmentAttribute<?> var1) {
      return this.entries.containsKey(var1);
   }

   public Set<EnvironmentAttribute<?>> keySet() {
      return this.entries.keySet();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof EnvironmentAttributeMap) {
            EnvironmentAttributeMap var2 = (EnvironmentAttributeMap)var1;
            if (this.entries.equals(var2.entries)) {
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
      CODEC_ONLY_POSITIONAL = CODEC.validate((var0) -> {
         List var1 = var0.keySet().stream().filter((var0x) -> !var0x.isPositional()).toList();
         return !var1.isEmpty() ? DataResult.error(() -> "The following attributes cannot be positional: " + String.valueOf(var1)) : DataResult.success(var0);
      });
   }

   public static record Entry<Value, Argument>(Argument argument, AttributeModifier<Value, Argument> modifier) {
      public Entry(Argument var1, AttributeModifier<Value, Argument> var2) {
         super();
         this.argument = var1;
         this.modifier = var2;
      }

      private static <Value> Codec<Entry<Value, ?>> createCodec(EnvironmentAttribute<Value> var0) {
         Codec var1 = var0.type().modifierCodec().dispatch("modifier", Entry::modifier, Util.memoize((Function)((var1x) -> createFullCodec(var0, var1x))));
         return Codec.either(var0.valueCodec(), var1).xmap((var0x) -> (Entry)var0x.map((var0) -> new Entry(var0, AttributeModifier.override()), (var0) -> var0), (var0x) -> var0x.modifier == AttributeModifier.override() ? Either.left(var0x.argument()) : Either.right(var0x));
      }

      private static <Value, Argument> MapCodec<Entry<Value, Argument>> createFullCodec(EnvironmentAttribute<Value> var0, AttributeModifier<Value, Argument> var1) {
         return RecordCodecBuilder.mapCodec((var2) -> var2.group(var1.argumentCodec(var0).fieldOf("argument").forGetter(Entry::argument)).apply(var2, (var1x) -> new Entry(var1x, var1)));
      }

      public Value applyModifier(Value var1) {
         return this.modifier.apply(var1, this.argument);
      }
   }

   public static class Builder {
      private final Map<EnvironmentAttribute<?>, Entry<?, ?>> entries = new HashMap();

      Builder() {
         super();
      }

      public Builder putAll(EnvironmentAttributeMap var1) {
         this.entries.putAll(var1.entries);
         return this;
      }

      public <Value, Parameter> Builder modify(EnvironmentAttribute<Value> var1, AttributeModifier<Value, Parameter> var2, Parameter var3) {
         var1.type().checkAllowedModifier(var2);
         this.entries.put(var1, new Entry(var3, var2));
         return this;
      }

      public <Value> Builder set(EnvironmentAttribute<Value> var1, Value var2) {
         return this.modify(var1, AttributeModifier.override(), var2);
      }

      public EnvironmentAttributeMap build() {
         return this.entries.isEmpty() ? EnvironmentAttributeMap.EMPTY : new EnvironmentAttributeMap(Map.copyOf(this.entries));
      }
   }
}
