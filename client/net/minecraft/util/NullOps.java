package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractUniversalBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class NullOps implements DynamicOps<Unit> {
   public static final NullOps INSTANCE = new NullOps();

   private NullOps() {
      super();
   }

   public <U> U convertTo(DynamicOps<U> var1, Unit var2) {
      return (U)var1.empty();
   }

   public Unit empty() {
      return Unit.INSTANCE;
   }

   public Unit emptyMap() {
      return Unit.INSTANCE;
   }

   public Unit emptyList() {
      return Unit.INSTANCE;
   }

   public Unit createNumeric(Number var1) {
      return Unit.INSTANCE;
   }

   public Unit createByte(byte var1) {
      return Unit.INSTANCE;
   }

   public Unit createShort(short var1) {
      return Unit.INSTANCE;
   }

   public Unit createInt(int var1) {
      return Unit.INSTANCE;
   }

   public Unit createLong(long var1) {
      return Unit.INSTANCE;
   }

   public Unit createFloat(float var1) {
      return Unit.INSTANCE;
   }

   public Unit createDouble(double var1) {
      return Unit.INSTANCE;
   }

   public Unit createBoolean(boolean var1) {
      return Unit.INSTANCE;
   }

   public Unit createString(String var1) {
      return Unit.INSTANCE;
   }

   public DataResult<Number> getNumberValue(Unit var1) {
      return DataResult.error(() -> "Not a number");
   }

   public DataResult<Boolean> getBooleanValue(Unit var1) {
      return DataResult.error(() -> "Not a boolean");
   }

   public DataResult<String> getStringValue(Unit var1) {
      return DataResult.error(() -> "Not a string");
   }

   public DataResult<Unit> mergeToList(Unit var1, Unit var2) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToList(Unit var1, List<Unit> var2) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToMap(Unit var1, Unit var2, Unit var3) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToMap(Unit var1, Map<Unit, Unit> var2) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Unit> mergeToMap(Unit var1, MapLike<Unit> var2) {
      return DataResult.success(Unit.INSTANCE);
   }

   public DataResult<Stream<Pair<Unit, Unit>>> getMapValues(Unit var1) {
      return DataResult.error(() -> "Not a map");
   }

   public DataResult<Consumer<BiConsumer<Unit, Unit>>> getMapEntries(Unit var1) {
      return DataResult.error(() -> "Not a map");
   }

   public DataResult<MapLike<Unit>> getMap(Unit var1) {
      return DataResult.error(() -> "Not a map");
   }

   public DataResult<Stream<Unit>> getStream(Unit var1) {
      return DataResult.error(() -> "Not a list");
   }

   public DataResult<Consumer<Consumer<Unit>>> getList(Unit var1) {
      return DataResult.error(() -> "Not a list");
   }

   public DataResult<ByteBuffer> getByteBuffer(Unit var1) {
      return DataResult.error(() -> "Not a byte list");
   }

   public DataResult<IntStream> getIntStream(Unit var1) {
      return DataResult.error(() -> "Not an int list");
   }

   public DataResult<LongStream> getLongStream(Unit var1) {
      return DataResult.error(() -> "Not a long list");
   }

   public Unit createMap(Stream<Pair<Unit, Unit>> var1) {
      return Unit.INSTANCE;
   }

   public Unit createMap(Map<Unit, Unit> var1) {
      return Unit.INSTANCE;
   }

   public Unit createList(Stream<Unit> var1) {
      return Unit.INSTANCE;
   }

   public Unit createByteList(ByteBuffer var1) {
      return Unit.INSTANCE;
   }

   public Unit createIntList(IntStream var1) {
      return Unit.INSTANCE;
   }

   public Unit createLongList(LongStream var1) {
      return Unit.INSTANCE;
   }

   public Unit remove(Unit var1, String var2) {
      return var1;
   }

   public RecordBuilder<Unit> mapBuilder() {
      return new NullOps.NullMapBuilder(this);
   }

   @Override
   public String toString() {
      return "Null";
   }

   static final class NullMapBuilder extends AbstractUniversalBuilder<Unit, Unit> {
      public NullMapBuilder(DynamicOps<Unit> var1) {
         super(var1);
      }

      protected Unit initBuilder() {
         return Unit.INSTANCE;
      }

      protected Unit append(Unit var1, Unit var2, Unit var3) {
         return var3;
      }

      protected DataResult<Unit> build(Unit var1, Unit var2) {
         return DataResult.success(var2);
      }
   }
}
