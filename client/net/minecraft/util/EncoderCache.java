package net.minecraft.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import net.minecraft.nbt.Tag;

public class EncoderCache {
   private final LoadingCache<Key<?, ?>, DataResult<?>> cache;

   public EncoderCache(final int maximumSize) {
      super();
      this.cache = CacheBuilder.newBuilder().maximumSize((long)maximumSize).concurrencyLevel(1).softValues().build(new CacheLoader<Key<?, ?>, DataResult<?>>() {
         {
            Objects.requireNonNull(EncoderCache.this);
         }

         public DataResult<?> load(final Key<?, ?> key) {
            return key.resolve();
         }
      });
   }

   public <A> Codec<A> wrap(final Codec<A> codec) {
      return new Codec<A>() {
         {
            Objects.requireNonNull(EncoderCache.this);
         }

         public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input) {
            return codec.decode(ops, input);
         }

         public <T> DataResult<T> encode(final A input, final DynamicOps<T> ops, final T prefix) {
            return ((DataResult)EncoderCache.this.cache.getUnchecked(new Key(codec, input, ops))).map((value) -> {
               if (value instanceof Tag tag) {
                  return tag.copy();
               } else {
                  return value;
               }
            });
         }
      };
   }

   private static record Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
      private Key {
         super();
      }

      public DataResult<T> resolve() {
         return this.codec.encodeStart(this.ops, this.value);
      }

      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof Key)) {
            return false;
         } else {
            Key<?, ?> key = (Key)obj;
            return this.codec == key.codec && this.value.equals(key.value) && this.ops.equals(key.ops);
         }
      }

      public int hashCode() {
         int result = System.identityHashCode(this.codec);
         result = 31 * result + this.value.hashCode();
         result = 31 * result + this.ops.hashCode();
         return result;
      }
   }
}
