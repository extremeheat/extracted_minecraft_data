package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface MapDecoder<A> extends Keyable {
   <T> DataResult<A> decode(DynamicOps<T> var1, MapLike<T> var2);

   default <T> DataResult<A> compressedDecode(DynamicOps<T> var1, T var2) {
      if (var1.compressMaps()) {
         Optional var3 = var1.getList(var2).result();
         if (!var3.isPresent()) {
            return DataResult.error("Input is not a list");
         } else {
            final KeyCompressor var4 = this.compressor(var1);
            final ArrayList var5 = new ArrayList();
            ((Consumer)var3.get()).accept(var5::add);
            MapLike var6 = new MapLike<T>() {
               @Nullable
               public T get(T var1) {
                  return var5.get(var4.compress(var1));
               }

               @Nullable
               public T get(String var1) {
                  return var5.get(var4.compress(var1));
               }

               public Stream<Pair<T, T>> entries() {
                  return IntStream.range(0, var5.size()).mapToObj((var2) -> {
                     return Pair.of(var4.decompress(var2), var5.get(var2));
                  }).filter((var0) -> {
                     return var0.getSecond() != null;
                  });
               }
            };
            return this.decode(var1, var6);
         }
      } else {
         return var1.getMap(var2).setLifecycle(Lifecycle.stable()).flatMap((var2x) -> {
            return this.decode(var1, var2x);
         });
      }
   }

   <T> KeyCompressor<T> compressor(DynamicOps<T> var1);

   default Decoder<A> decoder() {
      return new Decoder<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
            return MapDecoder.this.compressedDecode(var1, var2).map((var1x) -> {
               return Pair.of(var1x, var2);
            });
         }

         public String toString() {
            return MapDecoder.this.toString();
         }
      };
   }

   default <B> MapDecoder<B> flatMap(final Function<? super A, ? extends DataResult<? extends B>> var1) {
      return new MapDecoder.Implementation<B>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapDecoder.this.keys(var1x);
         }

         public <T> DataResult<B> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return MapDecoder.this.decode(var1x, var2).flatMap((var1xx) -> {
               return ((DataResult)var1.apply(var1xx)).map(Function.identity());
            });
         }

         public String toString() {
            return MapDecoder.this.toString() + "[flatMapped]";
         }
      };
   }

   default <B> MapDecoder<B> map(final Function<? super A, ? extends B> var1) {
      return new MapDecoder.Implementation<B>() {
         public <T> DataResult<B> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return MapDecoder.this.decode(var1x, var2).map(var1);
         }

         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapDecoder.this.keys(var1x);
         }

         public String toString() {
            return MapDecoder.this.toString() + "[mapped]";
         }
      };
   }

   default <E> MapDecoder<E> ap(final MapDecoder<Function<? super A, ? extends E>> var1) {
      return new MapDecoder.Implementation<E>() {
         public <T> DataResult<E> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return MapDecoder.this.decode(var1x, var2).flatMap((var3) -> {
               return var1.decode(var1x, var2).map((var1xx) -> {
                  return var1xx.apply(var3);
               });
            });
         }

         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return Stream.concat(MapDecoder.this.keys(var1x), var1.keys(var1x));
         }

         public String toString() {
            return var1.toString() + " * " + MapDecoder.this.toString();
         }
      };
   }

   default MapDecoder<A> withLifecycle(final Lifecycle var1) {
      return new MapDecoder.Implementation<A>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapDecoder.this.keys(var1x);
         }

         public <T> DataResult<A> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return MapDecoder.this.decode(var1x, var2).setLifecycle(var1);
         }

         public String toString() {
            return MapDecoder.this.toString();
         }
      };
   }

   public abstract static class Implementation<A> extends CompressorHolder implements MapDecoder<A> {
      public Implementation() {
         super();
      }
   }
}
