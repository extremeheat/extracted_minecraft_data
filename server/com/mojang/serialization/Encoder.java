package com.mojang.serialization;

import com.mojang.serialization.codecs.FieldEncoder;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Encoder<A> {
   <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3);

   default <T> DataResult<T> encodeStart(DynamicOps<T> var1, A var2) {
      return this.encode(var2, var1, var1.empty());
   }

   default MapEncoder<A> fieldOf(String var1) {
      return new FieldEncoder(var1, this);
   }

   default <B> Encoder<B> comap(final Function<? super B, ? extends A> var1) {
      return new Encoder<B>() {
         public <T> DataResult<T> encode(B var1x, DynamicOps<T> var2, T var3) {
            return Encoder.this.encode(var1.apply(var1x), var2, var3);
         }

         public String toString() {
            return Encoder.this.toString() + "[comapped]";
         }
      };
   }

   default <B> Encoder<B> flatComap(final Function<? super B, ? extends DataResult<? extends A>> var1) {
      return new Encoder<B>() {
         public <T> DataResult<T> encode(B var1x, DynamicOps<T> var2, T var3) {
            return ((DataResult)var1.apply(var1x)).flatMap((var3x) -> {
               return Encoder.this.encode(var3x, var2, var3);
            });
         }

         public String toString() {
            return Encoder.this.toString() + "[flatComapped]";
         }
      };
   }

   default Encoder<A> withLifecycle(final Lifecycle var1) {
      return new Encoder<A>() {
         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2, T var3) {
            return Encoder.this.encode(var1x, var2, var3).setLifecycle(var1);
         }

         public String toString() {
            return Encoder.this.toString();
         }
      };
   }

   static <A> MapEncoder<A> empty() {
      return new MapEncoder.Implementation<A>() {
         public <T> RecordBuilder<T> encode(A var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return var3;
         }

         public <T> Stream<T> keys(DynamicOps<T> var1) {
            return Stream.empty();
         }

         public String toString() {
            return "EmptyEncoder";
         }
      };
   }

   static <A> Encoder<A> error(final String var0) {
      return new Encoder<A>() {
         public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
            return DataResult.error(var0 + " " + var1);
         }

         public String toString() {
            return "ErrorEncoder[" + var0 + "]";
         }
      };
   }
}
