package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DelegatingOps<T> implements DynamicOps<T> {
   protected final DynamicOps<T> delegate;

   protected DelegatingOps(DynamicOps<T> var1) {
      super();
      this.delegate = var1;
   }

   public T empty() {
      return (T)this.delegate.empty();
   }

   public T emptyMap() {
      return (T)this.delegate.emptyMap();
   }

   public T emptyList() {
      return (T)this.delegate.emptyList();
   }

   public <U> U convertTo(DynamicOps<U> var1, T var2) {
      return Objects.equals(var1, this.delegate) ? var2 : this.delegate.convertTo(var1, var2);
   }

   public DataResult<Number> getNumberValue(T var1) {
      return this.delegate.getNumberValue(var1);
   }

   public T createNumeric(Number var1) {
      return (T)this.delegate.createNumeric(var1);
   }

   public T createByte(byte var1) {
      return (T)this.delegate.createByte(var1);
   }

   public T createShort(short var1) {
      return (T)this.delegate.createShort(var1);
   }

   public T createInt(int var1) {
      return (T)this.delegate.createInt(var1);
   }

   public T createLong(long var1) {
      return (T)this.delegate.createLong(var1);
   }

   public T createFloat(float var1) {
      return (T)this.delegate.createFloat(var1);
   }

   public T createDouble(double var1) {
      return (T)this.delegate.createDouble(var1);
   }

   public DataResult<Boolean> getBooleanValue(T var1) {
      return this.delegate.getBooleanValue(var1);
   }

   public T createBoolean(boolean var1) {
      return (T)this.delegate.createBoolean(var1);
   }

   public DataResult<String> getStringValue(T var1) {
      return this.delegate.getStringValue(var1);
   }

   public T createString(String var1) {
      return (T)this.delegate.createString(var1);
   }

   public DataResult<T> mergeToList(T var1, T var2) {
      return this.delegate.mergeToList(var1, var2);
   }

   public DataResult<T> mergeToList(T var1, List<T> var2) {
      return this.delegate.mergeToList(var1, var2);
   }

   public DataResult<T> mergeToMap(T var1, T var2, T var3) {
      return this.delegate.mergeToMap(var1, var2, var3);
   }

   public DataResult<T> mergeToMap(T var1, MapLike<T> var2) {
      return this.delegate.mergeToMap(var1, var2);
   }

   public DataResult<T> mergeToMap(T var1, Map<T, T> var2) {
      return this.delegate.mergeToMap(var1, var2);
   }

   public DataResult<T> mergeToPrimitive(T var1, T var2) {
      return this.delegate.mergeToPrimitive(var1, var2);
   }

   public DataResult<Stream<Pair<T, T>>> getMapValues(T var1) {
      return this.delegate.getMapValues(var1);
   }

   public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T var1) {
      return this.delegate.getMapEntries(var1);
   }

   public T createMap(Map<T, T> var1) {
      return (T)this.delegate.createMap(var1);
   }

   public T createMap(Stream<Pair<T, T>> var1) {
      return (T)this.delegate.createMap(var1);
   }

   public DataResult<MapLike<T>> getMap(T var1) {
      return this.delegate.getMap(var1);
   }

   public DataResult<Stream<T>> getStream(T var1) {
      return this.delegate.getStream(var1);
   }

   public DataResult<Consumer<Consumer<T>>> getList(T var1) {
      return this.delegate.getList(var1);
   }

   public T createList(Stream<T> var1) {
      return (T)this.delegate.createList(var1);
   }

   public DataResult<ByteBuffer> getByteBuffer(T var1) {
      return this.delegate.getByteBuffer(var1);
   }

   public T createByteList(ByteBuffer var1) {
      return (T)this.delegate.createByteList(var1);
   }

   public DataResult<IntStream> getIntStream(T var1) {
      return this.delegate.getIntStream(var1);
   }

   public T createIntList(IntStream var1) {
      return (T)this.delegate.createIntList(var1);
   }

   public DataResult<LongStream> getLongStream(T var1) {
      return this.delegate.getLongStream(var1);
   }

   public T createLongList(LongStream var1) {
      return (T)this.delegate.createLongList(var1);
   }

   public T remove(T var1, String var2) {
      return (T)this.delegate.remove(var1, var2);
   }

   public boolean compressMaps() {
      return this.delegate.compressMaps();
   }

   public ListBuilder<T> listBuilder() {
      return new DelegateListBuilder(this.delegate.listBuilder());
   }

   public RecordBuilder<T> mapBuilder() {
      return new DelegateRecordBuilder(this.delegate.mapBuilder());
   }

   protected class DelegateListBuilder implements ListBuilder<T> {
      private final ListBuilder<T> original;

      protected DelegateListBuilder(final ListBuilder<T> var2) {
         super();
         this.original = var2;
      }

      public DynamicOps<T> ops() {
         return DelegatingOps.this;
      }

      public DataResult<T> build(T var1) {
         return this.original.build(var1);
      }

      public ListBuilder<T> add(T var1) {
         this.original.add(var1);
         return this;
      }

      public ListBuilder<T> add(DataResult<T> var1) {
         this.original.add(var1);
         return this;
      }

      public <E> ListBuilder<T> add(E var1, Encoder<E> var2) {
         this.original.add(var2.encodeStart(this.ops(), var1));
         return this;
      }

      public <E> ListBuilder<T> addAll(Iterable<E> var1, Encoder<E> var2) {
         var1.forEach((var2x) -> this.original.add(var2.encode(var2x, this.ops(), this.ops().empty())));
         return this;
      }

      public ListBuilder<T> withErrorsFrom(DataResult<?> var1) {
         this.original.withErrorsFrom(var1);
         return this;
      }

      public ListBuilder<T> mapError(UnaryOperator<String> var1) {
         this.original.mapError(var1);
         return this;
      }

      public DataResult<T> build(DataResult<T> var1) {
         return this.original.build(var1);
      }
   }

   protected class DelegateRecordBuilder implements RecordBuilder<T> {
      private final RecordBuilder<T> original;

      protected DelegateRecordBuilder(final RecordBuilder<T> var2) {
         super();
         this.original = var2;
      }

      public DynamicOps<T> ops() {
         return DelegatingOps.this;
      }

      public RecordBuilder<T> add(T var1, T var2) {
         this.original.add(var1, var2);
         return this;
      }

      public RecordBuilder<T> add(T var1, DataResult<T> var2) {
         this.original.add(var1, var2);
         return this;
      }

      public RecordBuilder<T> add(DataResult<T> var1, DataResult<T> var2) {
         this.original.add(var1, var2);
         return this;
      }

      public RecordBuilder<T> add(String var1, T var2) {
         this.original.add(var1, var2);
         return this;
      }

      public RecordBuilder<T> add(String var1, DataResult<T> var2) {
         this.original.add(var1, var2);
         return this;
      }

      public <E> RecordBuilder<T> add(String var1, E var2, Encoder<E> var3) {
         return this.original.add(var1, var3.encodeStart(this.ops(), var2));
      }

      public RecordBuilder<T> withErrorsFrom(DataResult<?> var1) {
         this.original.withErrorsFrom(var1);
         return this;
      }

      public RecordBuilder<T> setLifecycle(Lifecycle var1) {
         this.original.setLifecycle(var1);
         return this;
      }

      public RecordBuilder<T> mapError(UnaryOperator<String> var1) {
         this.original.mapError(var1);
         return this;
      }

      public DataResult<T> build(T var1) {
         return this.original.build(var1);
      }

      public DataResult<T> build(DataResult<T> var1) {
         return this.original.build(var1);
      }
   }
}
