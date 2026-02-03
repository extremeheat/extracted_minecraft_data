package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;

public final class MemoryMap implements Iterable<Value<?>> {
   private static final Codec<MemoryModuleType<?>> SERIALIZABLE_MEMORY_MODULE_CODEC;
   public static final Codec<MemoryMap> CODEC;
   public static final MemoryMap EMPTY;
   private final Map<MemoryModuleType<?>, ExpirableValue<?>> memories;

   private MemoryMap(final Map<MemoryModuleType<?>, ExpirableValue<?>> memories) {
      super();
      this.memories = Map.copyOf(memories);
   }

   public static MemoryMap of(final Stream<Value<?>> memories) {
      return new MemoryMap((Map)memories.collect(Collectors.toMap(Value::type, Value::value)));
   }

   public <U> @Nullable ExpirableValue<U> get(final MemoryModuleType<U> type) {
      return (ExpirableValue)this.memories.get(type);
   }

   public boolean equals(final Object obj) {
      boolean var10000;
      if (obj instanceof MemoryMap map) {
         if (this.memories.equals(map.memories)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.memories.hashCode();
   }

   public String toString() {
      return this.memories.toString();
   }

   public Iterator<Value<?>> iterator() {
      return Iterators.transform(this.memories.entrySet().iterator(), (entry) -> MemoryMap.Value.createUnchecked((MemoryModuleType)entry.getKey(), (ExpirableValue)entry.getValue()));
   }

   static {
      SERIALIZABLE_MEMORY_MODULE_CODEC = BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().validate((type) -> type.getCodec().isPresent() ? DataResult.success(type) : DataResult.error(() -> "Memory module " + String.valueOf(type) + " cannot be encoded"));
      CODEC = Codec.dispatchedMap(SERIALIZABLE_MEMORY_MODULE_CODEC, (type) -> (Codec)type.getCodec().orElseThrow()).xmap(MemoryMap::new, (m) -> m.memories);
      EMPTY = new MemoryMap(Map.of());
   }

   public static record Value<U>(MemoryModuleType<U> type, ExpirableValue<U> value) {
      public Value {
         super();
      }

      public static <U> Value<U> createUnchecked(final MemoryModuleType<U> type, final ExpirableValue<?> value) {
         return new Value<U>(type, value);
      }
   }
}
