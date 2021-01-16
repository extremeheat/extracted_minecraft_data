package com.mojang.serialization;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DataResult<R> implements App<DataResult.Mu, R> {
   private final Either<R, DataResult.PartialResult<R>> result;
   private final Lifecycle lifecycle;

   public static <R> DataResult<R> unbox(App<DataResult.Mu, R> var0) {
      return (DataResult)var0;
   }

   public static <R> DataResult<R> success(R var0) {
      return success(var0, Lifecycle.experimental());
   }

   public static <R> DataResult<R> error(String var0, R var1) {
      return error(var0, var1, Lifecycle.experimental());
   }

   public static <R> DataResult<R> error(String var0) {
      return error(var0, Lifecycle.experimental());
   }

   public static <R> DataResult<R> success(R var0, Lifecycle var1) {
      return new DataResult(Either.left(var0), var1);
   }

   public static <R> DataResult<R> error(String var0, R var1, Lifecycle var2) {
      return new DataResult(Either.right(new DataResult.PartialResult(var0, Optional.of(var1))), var2);
   }

   public static <R> DataResult<R> error(String var0, Lifecycle var1) {
      return new DataResult(Either.right(new DataResult.PartialResult(var0, Optional.empty())), var1);
   }

   public static <K, V> Function<K, DataResult<V>> partialGet(Function<K, V> var0, Supplier<String> var1) {
      return (var2) -> {
         return (DataResult)Optional.ofNullable(var0.apply(var2)).map(DataResult::success).orElseGet(() -> {
            return error((String)var1.get() + var2);
         });
      };
   }

   private static <R> DataResult<R> create(Either<R, DataResult.PartialResult<R>> var0, Lifecycle var1) {
      return new DataResult(var0, var1);
   }

   private DataResult(Either<R, DataResult.PartialResult<R>> var1, Lifecycle var2) {
      super();
      this.result = var1;
      this.lifecycle = var2;
   }

   public Either<R, DataResult.PartialResult<R>> get() {
      return this.result;
   }

   public Optional<R> result() {
      return this.result.left();
   }

   public Lifecycle lifecycle() {
      return this.lifecycle;
   }

   public Optional<R> resultOrPartial(Consumer<String> var1) {
      return (Optional)this.result.map(Optional::of, (var1x) -> {
         var1.accept(var1x.message);
         return var1x.partialResult;
      });
   }

   public R getOrThrow(boolean var1, Consumer<String> var2) {
      return this.result.map((var0) -> {
         return var0;
      }, (var2x) -> {
         var2.accept(var2x.message);
         if (var1 && var2x.partialResult.isPresent()) {
            return var2x.partialResult.get();
         } else {
            throw new RuntimeException(var2x.message);
         }
      });
   }

   public Optional<DataResult.PartialResult<R>> error() {
      return this.result.right();
   }

   public <T> DataResult<T> map(Function<? super R, ? extends T> var1) {
      return create(this.result.mapBoth(var1, (var1x) -> {
         return new DataResult.PartialResult(var1x.message, var1x.partialResult.map(var1));
      }), this.lifecycle);
   }

   public DataResult<R> promotePartial(Consumer<String> var1) {
      return (DataResult)this.result.map((var1x) -> {
         return new DataResult(Either.left(var1x), this.lifecycle);
      }, (var2) -> {
         var1.accept(var2.message);
         return (DataResult)var2.partialResult.map((var1x) -> {
            return new DataResult(Either.left(var1x), this.lifecycle);
         }).orElseGet(() -> {
            return create(Either.right(var2), this.lifecycle);
         });
      });
   }

   private static String appendMessages(String var0, String var1) {
      return var0 + "; " + var1;
   }

   public <R2> DataResult<R2> flatMap(Function<? super R, ? extends DataResult<R2>> var1) {
      return (DataResult)this.result.map((var2) -> {
         DataResult var3 = (DataResult)var1.apply(var2);
         return create(var3.get(), this.lifecycle.add(var3.lifecycle));
      }, (var2) -> {
         return (DataResult)var2.partialResult.map((var3) -> {
            DataResult var4 = (DataResult)var1.apply(var3);
            return create(Either.right(var4.get().map((var1x) -> {
               return new DataResult.PartialResult(var2.message, Optional.of(var1x));
            }, (var1x) -> {
               return new DataResult.PartialResult(appendMessages(var2.message, var1x.message), var1x.partialResult);
            })), this.lifecycle.add(var4.lifecycle));
         }).orElseGet(() -> {
            return create(Either.right(new DataResult.PartialResult(var2.message, Optional.empty())), this.lifecycle);
         });
      });
   }

   public <R2> DataResult<R2> ap(DataResult<Function<R, R2>> var1) {
      return create((Either)this.result.map((var1x) -> {
         return var1.result.mapBoth((var1xx) -> {
            return var1xx.apply(var1x);
         }, (var1xx) -> {
            return new DataResult.PartialResult(var1xx.message, var1xx.partialResult.map((var1) -> {
               return var1.apply(var1x);
            }));
         });
      }, (var1x) -> {
         return Either.right(var1.result.map((var1xx) -> {
            return new DataResult.PartialResult(var1x.message, var1x.partialResult.map(var1xx));
         }, (var1xx) -> {
            return new DataResult.PartialResult(appendMessages(var1x.message, var1xx.message), var1x.partialResult.flatMap((var1) -> {
               return var1xx.partialResult.map((var1x) -> {
                  return var1x.apply(var1);
               });
            }));
         }));
      }), this.lifecycle.add(var1.lifecycle));
   }

   public <R2, S> DataResult<S> apply2(BiFunction<R, R2, S> var1, DataResult<R2> var2) {
      return unbox(instance().apply2(var1, this, var2));
   }

   public <R2, S> DataResult<S> apply2stable(BiFunction<R, R2, S> var1, DataResult<R2> var2) {
      DataResult.Instance var3 = instance();
      DataResult var4 = unbox(var3.point(var1)).setLifecycle(Lifecycle.stable());
      return unbox(var3.ap2(var4, this, var2));
   }

   public <R2, R3, S> DataResult<S> apply3(Function3<R, R2, R3, S> var1, DataResult<R2> var2, DataResult<R3> var3) {
      return unbox(instance().apply3(var1, this, var2, var3));
   }

   public DataResult<R> setPartial(Supplier<R> var1) {
      return create(this.result.mapRight((var1x) -> {
         return new DataResult.PartialResult(var1x.message, Optional.of(var1.get()));
      }), this.lifecycle);
   }

   public DataResult<R> setPartial(R var1) {
      return create(this.result.mapRight((var1x) -> {
         return new DataResult.PartialResult(var1x.message, Optional.of(var1));
      }), this.lifecycle);
   }

   public DataResult<R> mapError(UnaryOperator<String> var1) {
      return create(this.result.mapRight((var1x) -> {
         return new DataResult.PartialResult((String)var1.apply(var1x.message), var1x.partialResult);
      }), this.lifecycle);
   }

   public DataResult<R> setLifecycle(Lifecycle var1) {
      return create(this.result, var1);
   }

   public DataResult<R> addLifecycle(Lifecycle var1) {
      return create(this.result, this.lifecycle.add(var1));
   }

   public static DataResult.Instance instance() {
      return DataResult.Instance.INSTANCE;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         DataResult var2 = (DataResult)var1;
         return Objects.equals(this.result, var2.result);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.result});
   }

   public String toString() {
      return "DataResult[" + this.result + ']';
   }

   // $FF: synthetic method
   DataResult(Either var1, Lifecycle var2, Object var3) {
      this(var1, var2);
   }

   public static enum Instance implements Applicative<DataResult.Mu, DataResult.Instance.Mu> {
      INSTANCE;

      private Instance() {
      }

      public <T, R> App<DataResult.Mu, R> map(Function<? super T, ? extends R> var1, App<DataResult.Mu, T> var2) {
         return DataResult.unbox(var2).map(var1);
      }

      public <A> App<DataResult.Mu, A> point(A var1) {
         return DataResult.success(var1);
      }

      public <A, R> Function<App<DataResult.Mu, A>, App<DataResult.Mu, R>> lift1(App<DataResult.Mu, Function<A, R>> var1) {
         return (var2) -> {
            return this.ap(var1, var2);
         };
      }

      public <A, R> App<DataResult.Mu, R> ap(App<DataResult.Mu, Function<A, R>> var1, App<DataResult.Mu, A> var2) {
         return DataResult.unbox(var2).ap(DataResult.unbox(var1));
      }

      public <A, B, R> App<DataResult.Mu, R> ap2(App<DataResult.Mu, BiFunction<A, B, R>> var1, App<DataResult.Mu, A> var2, App<DataResult.Mu, B> var3) {
         DataResult var4 = DataResult.unbox(var1);
         DataResult var5 = DataResult.unbox(var2);
         DataResult var6 = DataResult.unbox(var3);
         return (App)(var4.result.left().isPresent() && var5.result.left().isPresent() && var6.result.left().isPresent() ? new DataResult(Either.left(((BiFunction)var4.result.left().get()).apply(var5.result.left().get(), var6.result.left().get())), var4.lifecycle.add(var5.lifecycle).add(var6.lifecycle)) : Applicative.super.ap2(var1, var2, var3));
      }

      public <T1, T2, T3, R> App<DataResult.Mu, R> ap3(App<DataResult.Mu, Function3<T1, T2, T3, R>> var1, App<DataResult.Mu, T1> var2, App<DataResult.Mu, T2> var3, App<DataResult.Mu, T3> var4) {
         DataResult var5 = DataResult.unbox(var1);
         DataResult var6 = DataResult.unbox(var2);
         DataResult var7 = DataResult.unbox(var3);
         DataResult var8 = DataResult.unbox(var4);
         return (App)(var5.result.left().isPresent() && var6.result.left().isPresent() && var7.result.left().isPresent() && var8.result.left().isPresent() ? new DataResult(Either.left(((Function3)var5.result.left().get()).apply(var6.result.left().get(), var7.result.left().get(), var8.result.left().get())), var5.lifecycle.add(var6.lifecycle).add(var7.lifecycle).add(var8.lifecycle)) : Applicative.super.ap3(var1, var2, var3, var4));
      }

      public static final class Mu implements Applicative.Mu {
         public Mu() {
            super();
         }
      }
   }

   public static class PartialResult<R> {
      private final String message;
      private final Optional<R> partialResult;

      public PartialResult(String var1, Optional<R> var2) {
         super();
         this.message = var1;
         this.partialResult = var2;
      }

      public <R2> DataResult.PartialResult<R2> map(Function<? super R, ? extends R2> var1) {
         return new DataResult.PartialResult(this.message, this.partialResult.map(var1));
      }

      public <R2> DataResult.PartialResult<R2> flatMap(Function<R, DataResult.PartialResult<R2>> var1) {
         if (this.partialResult.isPresent()) {
            DataResult.PartialResult var2 = (DataResult.PartialResult)var1.apply(this.partialResult.get());
            return new DataResult.PartialResult(DataResult.appendMessages(this.message, var2.message), var2.partialResult);
         } else {
            return new DataResult.PartialResult(this.message, Optional.empty());
         }
      }

      public String message() {
         return this.message;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            DataResult.PartialResult var2 = (DataResult.PartialResult)var1;
            return Objects.equals(this.message, var2.message) && Objects.equals(this.partialResult, var2.partialResult);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.message, this.partialResult});
      }

      public String toString() {
         return "DynamicException[" + this.message + ' ' + this.partialResult + ']';
      }
   }

   public static final class Mu implements K1 {
      public Mu() {
         super();
      }
   }
}
