package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.codecs.FieldDecoder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Decoder<A> {
   <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2);

   default <T> DataResult<A> parse(DynamicOps<T> var1, T var2) {
      return this.decode(var1, var2).map(Pair::getFirst);
   }

   default <T> DataResult<Pair<A, T>> decode(Dynamic<T> var1) {
      return this.decode(var1.getOps(), var1.getValue());
   }

   default <T> DataResult<A> parse(Dynamic<T> var1) {
      return this.decode(var1).map(Pair::getFirst);
   }

   default Decoder.Terminal<A> terminal() {
      return this::parse;
   }

   default Decoder.Boxed<A> boxed() {
      return this::decode;
   }

   default Decoder.Simple<A> simple() {
      return this::parse;
   }

   default MapDecoder<A> fieldOf(String var1) {
      return new FieldDecoder(var1, this);
   }

   default <B> Decoder<B> flatMap(final Function<? super A, ? extends DataResult<? extends B>> var1) {
      return new Decoder<B>() {
         public <T> DataResult<Pair<B, T>> decode(DynamicOps<T> var1x, T var2) {
            return Decoder.this.decode(var1x, var2).flatMap((var1xx) -> {
               return ((DataResult)var1.apply(var1xx.getFirst())).map((var1x) -> {
                  return Pair.of(var1x, var1xx.getSecond());
               });
            });
         }

         public String toString() {
            return Decoder.this.toString() + "[flatMapped]";
         }
      };
   }

   default <B> Decoder<B> map(final Function<? super A, ? extends B> var1) {
      return new Decoder<B>() {
         public <T> DataResult<Pair<B, T>> decode(DynamicOps<T> var1x, T var2) {
            return Decoder.this.decode(var1x, var2).map((var1xx) -> {
               return var1xx.mapFirst(var1);
            });
         }

         public String toString() {
            return Decoder.this.toString() + "[mapped]";
         }
      };
   }

   default Decoder<A> promotePartial(final Consumer<String> var1) {
      return new Decoder<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return Decoder.this.decode(var1x, var2).promotePartial(var1);
         }

         public String toString() {
            return Decoder.this.toString() + "[promotePartial]";
         }
      };
   }

   default Decoder<A> withLifecycle(final Lifecycle var1) {
      return new Decoder<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return Decoder.this.decode(var1x, var2).setLifecycle(var1);
         }

         public String toString() {
            return Decoder.this.toString();
         }
      };
   }

   static <A> Decoder<A> ofTerminal(Decoder.Terminal<? extends A> var0) {
      return var0.decoder().map(Function.identity());
   }

   static <A> Decoder<A> ofBoxed(Decoder.Boxed<? extends A> var0) {
      return var0.decoder().map(Function.identity());
   }

   static <A> Decoder<A> ofSimple(Decoder.Simple<? extends A> var0) {
      return var0.decoder().map(Function.identity());
   }

   static <A> MapDecoder<A> unit(A var0) {
      return unit(() -> {
         return var0;
      });
   }

   static <A> MapDecoder<A> unit(final Supplier<A> var0) {
      return new MapDecoder.Implementation<A>() {
         public <T> DataResult<A> decode(DynamicOps<T> var1, MapLike<T> var2) {
            return DataResult.success(var0.get());
         }

         public <T> Stream<T> keys(DynamicOps<T> var1) {
            return Stream.empty();
         }

         public String toString() {
            return "UnitDecoder[" + var0.get() + "]";
         }
      };
   }

   static <A> Decoder<A> error(final String var0) {
      return new Decoder<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
            return DataResult.error(var0);
         }

         public String toString() {
            return "ErrorDecoder[" + var0 + ']';
         }
      };
   }

   public interface Simple<A> {
      <T> DataResult<A> decode(Dynamic<T> var1);

      default Decoder<A> decoder() {
         return new Decoder<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
               return Simple.this.decode(new Dynamic(var1, var2)).map((var1x) -> {
                  return Pair.of(var1x, var1.empty());
               });
            }

            public String toString() {
               return "SimpleDecoder[" + Simple.this + "]";
            }
         };
      }
   }

   public interface Boxed<A> {
      <T> DataResult<Pair<A, T>> decode(Dynamic<T> var1);

      default Decoder<A> decoder() {
         return new Decoder<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
               return Boxed.this.decode(new Dynamic(var1, var2));
            }

            public String toString() {
               return "BoxedDecoder[" + Boxed.this + "]";
            }
         };
      }
   }

   public interface Terminal<A> {
      <T> DataResult<A> decode(DynamicOps<T> var1, T var2);

      default Decoder<A> decoder() {
         return new Decoder<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
               return Terminal.this.decode(var1, var2).map((var1x) -> {
                  return Pair.of(var1x, var1.empty());
               });
            }

            public String toString() {
               return "TerminalDecoder[" + Terminal.this + "]";
            }
         };
      }
   }
}
