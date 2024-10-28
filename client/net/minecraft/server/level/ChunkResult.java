package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ChunkResult<T> {
   static <T> ChunkResult<T> of(T var0) {
      return new Success(var0);
   }

   static <T> ChunkResult<T> error(String var0) {
      return error(() -> {
         return var0;
      });
   }

   static <T> ChunkResult<T> error(Supplier<String> var0) {
      return new Fail(var0);
   }

   boolean isSuccess();

   @Nullable
   T orElse(@Nullable T var1);

   @Nullable
   static <R> R orElse(ChunkResult<? extends R> var0, @Nullable R var1) {
      Object var2 = var0.orElse((Object)null);
      return var2 != null ? var2 : var1;
   }

   @Nullable
   String getError();

   ChunkResult<T> ifSuccess(Consumer<T> var1);

   <R> ChunkResult<R> map(Function<T, R> var1);

   <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E;

   public static record Success<T>(T value) implements ChunkResult<T> {
      public Success(T var1) {
         super();
         this.value = var1;
      }

      public boolean isSuccess() {
         return true;
      }

      public T orElse(@Nullable T var1) {
         return this.value;
      }

      @Nullable
      public String getError() {
         return null;
      }

      public ChunkResult<T> ifSuccess(Consumer<T> var1) {
         var1.accept(this.value);
         return this;
      }

      public <R> ChunkResult<R> map(Function<T, R> var1) {
         return new Success(var1.apply(this.value));
      }

      public <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E {
         return this.value;
      }

      public T value() {
         return this.value;
      }
   }

   public static record Fail<T>(Supplier<String> error) implements ChunkResult<T> {
      public Fail(Supplier<String> var1) {
         super();
         this.error = var1;
      }

      public boolean isSuccess() {
         return false;
      }

      @Nullable
      public T orElse(@Nullable T var1) {
         return var1;
      }

      public String getError() {
         return (String)this.error.get();
      }

      public ChunkResult<T> ifSuccess(Consumer<T> var1) {
         return this;
      }

      public <R> ChunkResult<R> map(Function<T, R> var1) {
         return new Fail(this.error);
      }

      public <E extends Throwable> T orElseThrow(Supplier<E> var1) throws E {
         throw (Throwable)var1.get();
      }

      public Supplier<String> error() {
         return this.error;
      }
   }
}
