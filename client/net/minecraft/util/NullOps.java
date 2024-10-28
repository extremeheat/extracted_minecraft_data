package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
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
      return var1.empty();
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
      return DataResult.error(() -> {
         return "Not a number";
      });
   }

   public DataResult<Boolean> getBooleanValue(Unit var1) {
      return DataResult.error(() -> {
         return "Not a boolean";
      });
   }

   public DataResult<String> getStringValue(Unit var1) {
      return DataResult.error(() -> {
         return "Not a string";
      });
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
      return DataResult.error(() -> {
         return "Not a map";
      });
   }

   public DataResult<Consumer<BiConsumer<Unit, Unit>>> getMapEntries(Unit var1) {
      return DataResult.error(() -> {
         return "Not a map";
      });
   }

   public DataResult<MapLike<Unit>> getMap(Unit var1) {
      return DataResult.error(() -> {
         return "Not a map";
      });
   }

   public DataResult<Stream<Unit>> getStream(Unit var1) {
      return DataResult.error(() -> {
         return "Not a list";
      });
   }

   public DataResult<Consumer<Consumer<Unit>>> getList(Unit var1) {
      return DataResult.error(() -> {
         return "Not a list";
      });
   }

   public DataResult<ByteBuffer> getByteBuffer(Unit var1) {
      return DataResult.error(() -> {
         return "Not a byte list";
      });
   }

   public DataResult<IntStream> getIntStream(Unit var1) {
      return DataResult.error(() -> {
         return "Not an int list";
      });
   }

   public DataResult<LongStream> getLongStream(Unit var1) {
      return DataResult.error(() -> {
         return "Not a long list";
      });
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
      return new NullMapBuilder(this);
   }

   public String toString() {
      return "Null";
   }

   // $FF: synthetic method
   public Object remove(final Object var1, final String var2) {
      return this.remove((Unit)var1, var2);
   }

   // $FF: synthetic method
   public Object createLongList(final LongStream var1) {
      return this.createLongList(var1);
   }

   // $FF: synthetic method
   public DataResult getLongStream(final Object var1) {
      return this.getLongStream((Unit)var1);
   }

   // $FF: synthetic method
   public Object createIntList(final IntStream var1) {
      return this.createIntList(var1);
   }

   // $FF: synthetic method
   public DataResult getIntStream(final Object var1) {
      return this.getIntStream((Unit)var1);
   }

   // $FF: synthetic method
   public Object createByteList(final ByteBuffer var1) {
      return this.createByteList(var1);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(final Object var1) {
      return this.getByteBuffer((Unit)var1);
   }

   // $FF: synthetic method
   public Object createList(final Stream var1) {
      return this.createList(var1);
   }

   // $FF: synthetic method
   public DataResult getList(final Object var1) {
      return this.getList((Unit)var1);
   }

   // $FF: synthetic method
   public DataResult getStream(final Object var1) {
      return this.getStream((Unit)var1);
   }

   // $FF: synthetic method
   public Object createMap(final Map var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public DataResult getMap(final Object var1) {
      return this.getMap((Unit)var1);
   }

   // $FF: synthetic method
   public Object createMap(final Stream var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(final Object var1) {
      return this.getMapEntries((Unit)var1);
   }

   // $FF: synthetic method
   public DataResult getMapValues(final Object var1) {
      return this.getMapValues((Unit)var1);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final MapLike var2) {
      return this.mergeToMap((Unit)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final Map var2) {
      return this.mergeToMap((Unit)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final Object var2, final Object var3) {
      return this.mergeToMap((Unit)var1, (Unit)var2, (Unit)var3);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object var1, final List var2) {
      return this.mergeToList((Unit)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object var1, final Object var2) {
      return this.mergeToList((Unit)var1, (Unit)var2);
   }

   // $FF: synthetic method
   public Object createString(final String var1) {
      return this.createString(var1);
   }

   // $FF: synthetic method
   public DataResult getStringValue(final Object var1) {
      return this.getStringValue((Unit)var1);
   }

   // $FF: synthetic method
   public Object createBoolean(final boolean var1) {
      return this.createBoolean(var1);
   }

   // $FF: synthetic method
   public DataResult getBooleanValue(final Object var1) {
      return this.getBooleanValue((Unit)var1);
   }

   // $FF: synthetic method
   public Object createDouble(final double var1) {
      return this.createDouble(var1);
   }

   // $FF: synthetic method
   public Object createFloat(final float var1) {
      return this.createFloat(var1);
   }

   // $FF: synthetic method
   public Object createLong(final long var1) {
      return this.createLong(var1);
   }

   // $FF: synthetic method
   public Object createInt(final int var1) {
      return this.createInt(var1);
   }

   // $FF: synthetic method
   public Object createShort(final short var1) {
      return this.createShort(var1);
   }

   // $FF: synthetic method
   public Object createByte(final byte var1) {
      return this.createByte(var1);
   }

   // $FF: synthetic method
   public Object createNumeric(final Number var1) {
      return this.createNumeric(var1);
   }

   // $FF: synthetic method
   public DataResult getNumberValue(final Object var1) {
      return this.getNumberValue((Unit)var1);
   }

   // $FF: synthetic method
   public Object convertTo(final DynamicOps var1, final Object var2) {
      return this.convertTo(var1, (Unit)var2);
   }

   // $FF: synthetic method
   public Object emptyList() {
      return this.emptyList();
   }

   // $FF: synthetic method
   public Object emptyMap() {
      return this.emptyMap();
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }

   private static final class NullMapBuilder extends RecordBuilder.AbstractUniversalBuilder<Unit, Unit> {
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

      // $FF: synthetic method
      protected Object append(final Object var1, final Object var2, final Object var3) {
         return this.append((Unit)var1, (Unit)var2, (Unit)var3);
      }

      // $FF: synthetic method
      protected DataResult build(final Object var1, final Object var2) {
         return this.build((Unit)var1, (Unit)var2);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }
}
