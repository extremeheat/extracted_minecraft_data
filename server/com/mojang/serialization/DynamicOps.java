package com.mojang.serialization;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface DynamicOps<T> {
   T empty();

   default T emptyMap() {
      return this.createMap((Map)ImmutableMap.of());
   }

   default T emptyList() {
      return this.createList(Stream.empty());
   }

   <U> U convertTo(DynamicOps<U> var1, T var2);

   DataResult<Number> getNumberValue(T var1);

   default Number getNumberValue(T var1, Number var2) {
      return (Number)this.getNumberValue(var1).result().orElse(var2);
   }

   T createNumeric(Number var1);

   default T createByte(byte var1) {
      return this.createNumeric(var1);
   }

   default T createShort(short var1) {
      return this.createNumeric(var1);
   }

   default T createInt(int var1) {
      return this.createNumeric(var1);
   }

   default T createLong(long var1) {
      return this.createNumeric(var1);
   }

   default T createFloat(float var1) {
      return this.createNumeric(var1);
   }

   default T createDouble(double var1) {
      return this.createNumeric(var1);
   }

   default DataResult<Boolean> getBooleanValue(T var1) {
      return this.getNumberValue(var1).map((var0) -> {
         return var0.byteValue() != 0;
      });
   }

   default T createBoolean(boolean var1) {
      return this.createByte((byte)(var1 ? 1 : 0));
   }

   DataResult<String> getStringValue(T var1);

   T createString(String var1);

   DataResult<T> mergeToList(T var1, T var2);

   default DataResult<T> mergeToList(T var1, List<T> var2) {
      DataResult var3 = DataResult.success(var1);

      Object var5;
      for(Iterator var4 = var2.iterator(); var4.hasNext(); var3 = var3.flatMap((var2x) -> {
         return this.mergeToList(var2x, var5);
      })) {
         var5 = var4.next();
      }

      return var3;
   }

   DataResult<T> mergeToMap(T var1, T var2, T var3);

   default DataResult<T> mergeToMap(T var1, Map<T, T> var2) {
      return this.mergeToMap(var1, MapLike.forMap(var2, this));
   }

   default DataResult<T> mergeToMap(T var1, MapLike<T> var2) {
      MutableObject var3 = new MutableObject(DataResult.success(var1));
      var2.entries().forEach((var2x) -> {
         var3.setValue(((DataResult)var3.getValue()).flatMap((var2) -> {
            return this.mergeToMap(var2, var2x.getFirst(), var2x.getSecond());
         }));
      });
      return (DataResult)var3.getValue();
   }

   default DataResult<T> mergeToPrimitive(T var1, T var2) {
      return !Objects.equals(var1, this.empty()) ? DataResult.error("Do not know how to append a primitive value " + var2 + " to " + var1, var2) : DataResult.success(var2);
   }

   DataResult<Stream<Pair<T, T>>> getMapValues(T var1);

   default DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T var1) {
      return this.getMapValues(var1).map((var0) -> {
         return (var1) -> {
            var0.forEach((var1x) -> {
               var1.accept(var1x.getFirst(), var1x.getSecond());
            });
         };
      });
   }

   T createMap(Stream<Pair<T, T>> var1);

   default DataResult<MapLike<T>> getMap(T var1) {
      return this.getMapValues(var1).flatMap((var1x) -> {
         try {
            return DataResult.success(MapLike.forMap((Map)var1x.collect(Pair.toMap()), this));
         } catch (IllegalStateException var3) {
            return DataResult.error("Error while building map: " + var3.getMessage());
         }
      });
   }

   default T createMap(Map<T, T> var1) {
      return this.createMap(var1.entrySet().stream().map((var0) -> {
         return Pair.of(var0.getKey(), var0.getValue());
      }));
   }

   DataResult<Stream<T>> getStream(T var1);

   default DataResult<Consumer<Consumer<T>>> getList(T var1) {
      return this.getStream(var1).map((var0) -> {
         return var0::forEach;
      });
   }

   T createList(Stream<T> var1);

   default DataResult<ByteBuffer> getByteBuffer(T var1) {
      return this.getStream(var1).flatMap((var2) -> {
         List var3 = (List)var2.collect(Collectors.toList());
         if (!var3.stream().allMatch((var1x) -> {
            return this.getNumberValue(var1x).result().isPresent();
         })) {
            return DataResult.error("Some elements are not bytes: " + var1);
         } else {
            ByteBuffer var4 = ByteBuffer.wrap(new byte[var3.size()]);

            for(int var5 = 0; var5 < var3.size(); ++var5) {
               var4.put(var5, ((Number)this.getNumberValue(var3.get(var5)).result().get()).byteValue());
            }

            return DataResult.success(var4);
         }
      });
   }

   default T createByteList(ByteBuffer var1) {
      return this.createList(IntStream.range(0, var1.capacity()).mapToObj((var2) -> {
         return this.createByte(var1.get(var2));
      }));
   }

   default DataResult<IntStream> getIntStream(T var1) {
      return this.getStream(var1).flatMap((var2) -> {
         List var3 = (List)var2.collect(Collectors.toList());
         return var3.stream().allMatch((var1x) -> {
            return this.getNumberValue(var1x).result().isPresent();
         }) ? DataResult.success(var3.stream().mapToInt((var1x) -> {
            return ((Number)this.getNumberValue(var1x).result().get()).intValue();
         })) : DataResult.error("Some elements are not ints: " + var1);
      });
   }

   default T createIntList(IntStream var1) {
      return this.createList(var1.mapToObj(this::createInt));
   }

   default DataResult<LongStream> getLongStream(T var1) {
      return this.getStream(var1).flatMap((var2) -> {
         List var3 = (List)var2.collect(Collectors.toList());
         return var3.stream().allMatch((var1x) -> {
            return this.getNumberValue(var1x).result().isPresent();
         }) ? DataResult.success(var3.stream().mapToLong((var1x) -> {
            return ((Number)this.getNumberValue(var1x).result().get()).longValue();
         })) : DataResult.error("Some elements are not longs: " + var1);
      });
   }

   default T createLongList(LongStream var1) {
      return this.createList(var1.mapToObj(this::createLong));
   }

   T remove(T var1, String var2);

   default boolean compressMaps() {
      return false;
   }

   default DataResult<T> get(T var1, String var2) {
      return this.getGeneric(var1, this.createString(var2));
   }

   default DataResult<T> getGeneric(T var1, T var2) {
      return this.getMap(var1).flatMap((var2x) -> {
         return (DataResult)Optional.ofNullable(var2x.get(var2)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("No element " + var2 + " in the map " + var1);
         });
      });
   }

   default T set(T var1, String var2, T var3) {
      return this.mergeToMap(var1, this.createString(var2), var3).result().orElse(var1);
   }

   default T update(T var1, String var2, Function<T, T> var3) {
      return this.get(var1, var2).map((var4) -> {
         return this.set(var1, var2, var3.apply(var4));
      }).result().orElse(var1);
   }

   default T updateGeneric(T var1, T var2, Function<T, T> var3) {
      return this.getGeneric(var1, var2).flatMap((var4) -> {
         return this.mergeToMap(var1, var2, var3.apply(var4));
      }).result().orElse(var1);
   }

   default ListBuilder<T> listBuilder() {
      return new ListBuilder.Builder(this);
   }

   default RecordBuilder<T> mapBuilder() {
      return new RecordBuilder.MapBuilder(this);
   }

   default <E> Function<E, DataResult<T>> withEncoder(Encoder<E> var1) {
      return (var2) -> {
         return var1.encodeStart(this, var2);
      };
   }

   default <E> Function<T, DataResult<Pair<E, T>>> withDecoder(Decoder<E> var1) {
      return (var2) -> {
         return var1.decode(this, var2);
      };
   }

   default <E> Function<T, DataResult<E>> withParser(Decoder<E> var1) {
      return (var2) -> {
         return var1.parse(this, var2);
      };
   }

   default <U> U convertList(DynamicOps<U> var1, T var2) {
      return var1.createList(((Stream)this.getStream(var2).result().orElse(Stream.empty())).map((var2x) -> {
         return this.convertTo(var1, var2x);
      }));
   }

   default <U> U convertMap(DynamicOps<U> var1, T var2) {
      return var1.createMap(((Stream)this.getMapValues(var2).result().orElse(Stream.empty())).map((var2x) -> {
         return Pair.of(this.convertTo(var1, var2x.getFirst()), this.convertTo(var1, var2x.getSecond()));
      }));
   }
}
