package net.minecraft.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class EncoderCache {
   final LoadingCache<EncoderCache.Key<?, ?>, DataResult<?>> cache;

   public EncoderCache(int var1) {
      super();
      this.cache = CacheBuilder.newBuilder()
         .maximumSize((long)var1)
         .concurrencyLevel(1)
         .softValues()
         .build(new CacheLoader<EncoderCache.Key<?, ?>, DataResult<?>>() {
            public DataResult<?> load(EncoderCache.Key<?, ?> var1) {
               return var1.resolve();
            }
         });
   }

   public <A> Codec<A> wrap(final Codec<A> var1) {
      return new Codec<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1.decode(var1x, var2);
         }

         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2, T var3) {
            return (DataResult<T>)EncoderCache.this.cache.getUnchecked(new EncoderCache.Key<>(var1, var1x, var2));
         }
      };
   }

   static record Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
      Key(Codec<A> codec, A value, DynamicOps<T> ops) {
         super();
         this.codec = codec;
         this.value = (A)value;
         this.ops = ops;
      }

      public DataResult<T> resolve() {
         return this.codec.encodeStart(this.ops, this.value);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof EncoderCache.Key var2) ? false : this.codec == var2.codec && this.value.equals(var2.value) && this.ops.equals(var2.ops);
         }
      }

      public int hashCode() {
         int var1 = System.identityHashCode(this.codec);
         var1 = 31 * var1 + this.value.hashCode();
         return 31 * var1 + this.ops.hashCode();
      }
   }
}
