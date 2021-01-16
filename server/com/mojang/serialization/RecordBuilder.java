package com.mojang.serialization;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public interface RecordBuilder<T> {
   DynamicOps<T> ops();

   RecordBuilder<T> add(T var1, T var2);

   RecordBuilder<T> add(T var1, DataResult<T> var2);

   RecordBuilder<T> add(DataResult<T> var1, DataResult<T> var2);

   RecordBuilder<T> withErrorsFrom(DataResult<?> var1);

   RecordBuilder<T> setLifecycle(Lifecycle var1);

   RecordBuilder<T> mapError(UnaryOperator<String> var1);

   DataResult<T> build(T var1);

   default DataResult<T> build(DataResult<T> var1) {
      return var1.flatMap(this::build);
   }

   default RecordBuilder<T> add(String var1, T var2) {
      return this.add(this.ops().createString(var1), var2);
   }

   default RecordBuilder<T> add(String var1, DataResult<T> var2) {
      return this.add(this.ops().createString(var1), var2);
   }

   default <E> RecordBuilder<T> add(String var1, E var2, Encoder<E> var3) {
      return this.add(var1, var3.encodeStart(this.ops(), var2));
   }

   public static final class MapBuilder<T> extends RecordBuilder.AbstractUniversalBuilder<T, ImmutableMap.Builder<T, T>> {
      public MapBuilder(DynamicOps<T> var1) {
         super(var1);
      }

      protected ImmutableMap.Builder<T, T> initBuilder() {
         return ImmutableMap.builder();
      }

      protected ImmutableMap.Builder<T, T> append(T var1, T var2, ImmutableMap.Builder<T, T> var3) {
         return var3.put(var1, var2);
      }

      protected DataResult<T> build(ImmutableMap.Builder<T, T> var1, T var2) {
         return this.ops().mergeToMap(var2, (Map)var1.build());
      }
   }

   public abstract static class AbstractUniversalBuilder<T, R> extends RecordBuilder.AbstractBuilder<T, R> {
      protected AbstractUniversalBuilder(DynamicOps<T> var1) {
         super(var1);
      }

      protected abstract R append(T var1, T var2, R var3);

      public RecordBuilder<T> add(T var1, T var2) {
         this.builder = this.builder.map((var3) -> {
            return this.append(var1, var2, var3);
         });
         return this;
      }

      public RecordBuilder<T> add(T var1, DataResult<T> var2) {
         this.builder = this.builder.apply2stable((var2x, var3) -> {
            return this.append(var1, var3, var2x);
         }, var2);
         return this;
      }

      public RecordBuilder<T> add(DataResult<T> var1, DataResult<T> var2) {
         this.builder = this.builder.ap(var1.apply2stable((var1x, var2x) -> {
            return (var3) -> {
               return this.append(var1x, var2x, var3);
            };
         }, var2));
         return this;
      }
   }

   public abstract static class AbstractStringBuilder<T, R> extends RecordBuilder.AbstractBuilder<T, R> {
      protected AbstractStringBuilder(DynamicOps<T> var1) {
         super(var1);
      }

      protected abstract R append(String var1, T var2, R var3);

      public RecordBuilder<T> add(String var1, T var2) {
         this.builder = this.builder.map((var3) -> {
            return this.append(var1, var2, var3);
         });
         return this;
      }

      public RecordBuilder<T> add(String var1, DataResult<T> var2) {
         this.builder = this.builder.apply2stable((var2x, var3) -> {
            return this.append(var1, var3, var2x);
         }, var2);
         return this;
      }

      public RecordBuilder<T> add(T var1, T var2) {
         this.builder = this.ops().getStringValue(var1).flatMap((var2x) -> {
            this.add(var2x, var2);
            return this.builder;
         });
         return this;
      }

      public RecordBuilder<T> add(T var1, DataResult<T> var2) {
         this.builder = this.ops().getStringValue(var1).flatMap((var2x) -> {
            this.add(var2x, var2);
            return this.builder;
         });
         return this;
      }

      public RecordBuilder<T> add(DataResult<T> var1, DataResult<T> var2) {
         DynamicOps var10002 = this.ops();
         var10002.getClass();
         this.builder = var1.flatMap(var10002::getStringValue).flatMap((var2x) -> {
            this.add(var2x, var2);
            return this.builder;
         });
         return this;
      }
   }

   public abstract static class AbstractBuilder<T, R> implements RecordBuilder<T> {
      private final DynamicOps<T> ops;
      protected DataResult<R> builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

      protected AbstractBuilder(DynamicOps<T> var1) {
         super();
         this.ops = var1;
      }

      public DynamicOps<T> ops() {
         return this.ops;
      }

      protected abstract R initBuilder();

      protected abstract DataResult<T> build(R var1, T var2);

      public DataResult<T> build(T var1) {
         DataResult var2 = this.builder.flatMap((var2x) -> {
            return this.build(var2x, var1);
         });
         this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
         return var2;
      }

      public RecordBuilder<T> withErrorsFrom(DataResult<?> var1) {
         this.builder = this.builder.flatMap((var1x) -> {
            return var1.map((var1xx) -> {
               return var1x;
            });
         });
         return this;
      }

      public RecordBuilder<T> setLifecycle(Lifecycle var1) {
         this.builder = this.builder.setLifecycle(var1);
         return this;
      }

      public RecordBuilder<T> mapError(UnaryOperator<String> var1) {
         this.builder = this.builder.mapError(var1);
         return this;
      }
   }
}
