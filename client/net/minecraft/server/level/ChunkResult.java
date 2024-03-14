package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ChunkResult<T> {
   static <T> ChunkResult<T> of(T var0) {
      return new ChunkResult.Success<>((T)var0);
   }

   static <T> ChunkResult<T> error(String var0) {
      return error(() -> var0);
   }

   static <T> ChunkResult<T> error(Supplier<String> var0) {
      return new ChunkResult.Fail<>(var0);
   }

   boolean isSuccess();

   @Nullable
   T orElse(@Nullable T var1);

   @Nullable
   static <R> R orElse(ChunkResult<? extends R> var0, @Nullable R var1) {
      Object var2 = var0.orElse((T)null);
      return (R)(var2 != null ? var2 : var1);
   }

   @Nullable
   String getError();

   ChunkResult<T> ifSuccess(Consumer<T> var1);

   <R> ChunkResult<R> map(Function<T, R> var1);

   <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E;

   public static record Fail<T>(Supplier<String> a) implements ChunkResult<T> {
      private final Supplier<String> error;

      public Fail(Supplier<String> var1) {
         super();
         this.error = var1;
      }

      @Override
      public boolean isSuccess() {
         return false;
      }

      @Nullable
      @Override
      public T orElse(@Nullable T var1) {
         return (T)var1;
      }

      @Override
      public String getError() {
         return this.error.get();
      }

      @Override
      public ChunkResult<T> ifSuccess(Consumer<T> var1) {
         return this;
      }

      @Override
      public <R> ChunkResult<R> map(Function<T, R> var1) {
         return new ChunkResult.Fail<>(this.error);
      }

      @Override
      public <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E {
         throw (Throwable)var1.get();
      }
   }

   public static record Success<T>(T a) implements ChunkResult<T> {
      private final T value;

      public Success(T var1) {
         super();
         this.value = (T)var1;
      }

      @Override
      public boolean isSuccess() {
         return true;
      }

      @Override
      public T orElse(@Nullable T var1) {
         return this.value;
      }

      @Nullable
      @Override
      public String getError() {
         return null;
      }

      @Override
      public ChunkResult<T> ifSuccess(Consumer<T> var1) {
         var1.accept(this.value);
         return this;
      }

      @Override
      public <R> ChunkResult<R> map(Function<T, R> var1) {
         return new ChunkResult.Success<>((R)var1.apply(this.value));
      }

      @Override
      public <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E {
         return this.value;
      }
   }
}
