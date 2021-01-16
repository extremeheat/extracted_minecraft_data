package com.mojang.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface MapEncoder<A> extends Keyable {
   <T> RecordBuilder<T> encode(A var1, DynamicOps<T> var2, RecordBuilder<T> var3);

   default <T> RecordBuilder<T> compressedBuilder(DynamicOps<T> var1) {
      return var1.compressMaps() ? makeCompressedBuilder(var1, this.compressor(var1)) : var1.mapBuilder();
   }

   <T> KeyCompressor<T> compressor(DynamicOps<T> var1);

   default <B> MapEncoder<B> comap(final Function<? super B, ? extends A> var1) {
      return new MapEncoder.Implementation<B>() {
         public <T> RecordBuilder<T> encode(B var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return MapEncoder.this.encode(var1.apply(var1x), var2, var3);
         }

         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapEncoder.this.keys(var1x);
         }

         public String toString() {
            return MapEncoder.this.toString() + "[comapped]";
         }
      };
   }

   default <B> MapEncoder<B> flatComap(final Function<? super B, ? extends DataResult<? extends A>> var1) {
      return new MapEncoder.Implementation<B>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapEncoder.this.keys(var1x);
         }

         public <T> RecordBuilder<T> encode(B var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            DataResult var4 = (DataResult)var1.apply(var1x);
            RecordBuilder var5 = var3.withErrorsFrom(var4);
            return (RecordBuilder)var4.map((var3x) -> {
               return MapEncoder.this.encode(var3x, var2, var5);
            }).result().orElse(var5);
         }

         public String toString() {
            return MapEncoder.this.toString() + "[flatComapped]";
         }
      };
   }

   default Encoder<A> encoder() {
      return new Encoder<A>() {
         public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
            return MapEncoder.this.encode(var1, var2, MapEncoder.this.compressedBuilder(var2)).build(var3);
         }

         public String toString() {
            return MapEncoder.this.toString();
         }
      };
   }

   default MapEncoder<A> withLifecycle(final Lifecycle var1) {
      return new MapEncoder.Implementation<A>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapEncoder.this.keys(var1x);
         }

         public <T> RecordBuilder<T> encode(A var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return MapEncoder.this.encode(var1x, var2, var3).setLifecycle(var1);
         }

         public String toString() {
            return MapEncoder.this.toString();
         }
      };
   }

   static <T> RecordBuilder<T> makeCompressedBuilder(final DynamicOps<T> var0, final KeyCompressor<T> var1) {
      class 1CompressedRecordBuilder extends RecordBuilder.AbstractUniversalBuilder<T, List<T>> {
         _CompressedRecordBuilder/* $FF was: 1CompressedRecordBuilder*/() {
            super(var0);
         }

         protected List<T> initBuilder() {
            ArrayList var1x = new ArrayList(var1.size());

            for(int var2 = 0; var2 < var1.size(); ++var2) {
               var1x.add((Object)null);
            }

            return var1x;
         }

         protected List<T> append(T var1x, T var2, List<T> var3) {
            var3.set(var1.compress(var1x), var2);
            return var3;
         }

         protected DataResult<T> build(List<T> var1x, T var2) {
            return this.ops().mergeToList(var2, var1x);
         }
      }

      return new 1CompressedRecordBuilder();
   }

   public abstract static class Implementation<A> extends CompressorHolder implements MapEncoder<A> {
      public Implementation() {
         super();
      }
   }
}
