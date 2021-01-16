package com.mojang.serialization;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.UnaryOperator;

public interface ListBuilder<T> {
   DynamicOps<T> ops();

   DataResult<T> build(T var1);

   ListBuilder<T> add(T var1);

   ListBuilder<T> add(DataResult<T> var1);

   ListBuilder<T> withErrorsFrom(DataResult<?> var1);

   ListBuilder<T> mapError(UnaryOperator<String> var1);

   default DataResult<T> build(DataResult<T> var1) {
      return var1.flatMap(this::build);
   }

   default <E> ListBuilder<T> add(E var1, Encoder<E> var2) {
      return this.add(var2.encodeStart(this.ops(), var1));
   }

   default <E> ListBuilder<T> addAll(Iterable<E> var1, Encoder<E> var2) {
      var1.forEach((var2x) -> {
         var2.encode(var2x, this.ops(), this.ops().empty());
      });
      return this;
   }

   public static final class Builder<T> implements ListBuilder<T> {
      private final DynamicOps<T> ops;
      private DataResult<ImmutableList.Builder<T>> builder = DataResult.success(ImmutableList.builder(), Lifecycle.stable());

      public Builder(DynamicOps<T> var1) {
         super();
         this.ops = var1;
      }

      public DynamicOps<T> ops() {
         return this.ops;
      }

      public ListBuilder<T> add(T var1) {
         this.builder = this.builder.map((var1x) -> {
            return var1x.add(var1);
         });
         return this;
      }

      public ListBuilder<T> add(DataResult<T> var1) {
         this.builder = this.builder.apply2stable(ImmutableList.Builder::add, var1);
         return this;
      }

      public ListBuilder<T> withErrorsFrom(DataResult<?> var1) {
         this.builder = this.builder.flatMap((var1x) -> {
            return var1.map((var1xx) -> {
               return var1x;
            });
         });
         return this;
      }

      public ListBuilder<T> mapError(UnaryOperator<String> var1) {
         this.builder = this.builder.mapError(var1);
         return this;
      }

      public DataResult<T> build(T var1) {
         DataResult var2 = this.builder.flatMap((var2x) -> {
            return this.ops.mergeToList(var1, (List)var2x.build());
         });
         this.builder = DataResult.success(ImmutableList.builder(), Lifecycle.stable());
         return var2;
      }
   }
}
