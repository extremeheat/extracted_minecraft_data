package net.minecraft.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.function.UnaryOperator;

abstract class AbstractListBuilder<T, B> implements ListBuilder<T> {
   private final DynamicOps<T> ops;
   protected DataResult<B> builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

   protected AbstractListBuilder(DynamicOps<T> var1) {
      super();
      this.ops = var1;
   }

   public DynamicOps<T> ops() {
      return this.ops;
   }

   protected abstract B initBuilder();

   protected abstract B append(B var1, T var2);

   protected abstract DataResult<T> build(B var1, T var2);

   public ListBuilder<T> add(T var1) {
      this.builder = this.builder.map((var2) -> this.append(var2, var1));
      return this;
   }

   public ListBuilder<T> add(DataResult<T> var1) {
      this.builder = this.builder.apply2stable(this::append, var1);
      return this;
   }

   public ListBuilder<T> withErrorsFrom(DataResult<?> var1) {
      this.builder = this.builder.flatMap((var1x) -> var1.map((var1xx) -> var1x));
      return this;
   }

   public ListBuilder<T> mapError(UnaryOperator<String> var1) {
      this.builder = this.builder.mapError(var1);
      return this;
   }

   public DataResult<T> build(T var1) {
      DataResult var2 = this.builder.flatMap((var2x) -> this.build(var2x, var1));
      this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
      return var2;
   }
}
