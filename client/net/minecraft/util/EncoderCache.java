package net.minecraft.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.Tag;

public class EncoderCache {
   final LoadingCache<Key<?, ?>, DataResult<?>> cache;

   public EncoderCache(int var1) {
      super();
      this.cache = CacheBuilder.newBuilder().maximumSize((long)var1).concurrencyLevel(1).softValues().build(new CacheLoader<Key<?, ?>, DataResult<?>>(this) {
         public DataResult<?> load(Key<?, ?> var1) {
            return var1.resolve();
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((Key)var1);
         }
      });
   }

   public <A> Codec<A> wrap(final Codec<A> var1) {
      return new Codec<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1.decode(var1x, var2);
         }

         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2, T var3) {
            return ((DataResult)EncoderCache.this.cache.getUnchecked(new Key(var1, var1x, var2))).map((var0) -> {
               if (var0 instanceof Tag var1x) {
                  return var1x.copy();
               } else {
                  return var0;
               }
            });
         }
      };
   }

   private static record Key<A, T>(Codec<A> codec, A value, DynamicOps<T> ops) {
      Key(Codec<A> codec, A value, DynamicOps<T> ops) {
         super();
         this.codec = codec;
         this.value = value;
         this.ops = ops;
      }

      public DataResult<T> resolve() {
         return this.codec.encodeStart(this.ops, this.value);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Key)) {
            return false;
         } else {
            Key var2 = (Key)var1;
            return this.codec == var2.codec && this.value.equals(var2.value) && this.ops.equals(var2.ops);
         }
      }

      public int hashCode() {
         int var1 = System.identityHashCode(this.codec);
         var1 = 31 * var1 + this.value.hashCode();
         var1 = 31 * var1 + this.ops.hashCode();
         return var1;
      }

      public Codec<A> codec() {
         return this.codec;
      }

      public A value() {
         return this.value;
      }

      public DynamicOps<T> ops() {
         return this.ops;
      }
   }
}
