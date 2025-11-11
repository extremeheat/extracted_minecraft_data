package net.minecraft.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class HashOps implements DynamicOps<HashCode> {
   private static final byte TAG_EMPTY = 1;
   private static final byte TAG_MAP_START = 2;
   private static final byte TAG_MAP_END = 3;
   private static final byte TAG_LIST_START = 4;
   private static final byte TAG_LIST_END = 5;
   private static final byte TAG_BYTE = 6;
   private static final byte TAG_SHORT = 7;
   private static final byte TAG_INT = 8;
   private static final byte TAG_LONG = 9;
   private static final byte TAG_FLOAT = 10;
   private static final byte TAG_DOUBLE = 11;
   private static final byte TAG_STRING = 12;
   private static final byte TAG_BOOLEAN = 13;
   private static final byte TAG_BYTE_ARRAY_START = 14;
   private static final byte TAG_BYTE_ARRAY_END = 15;
   private static final byte TAG_INT_ARRAY_START = 16;
   private static final byte TAG_INT_ARRAY_END = 17;
   private static final byte TAG_LONG_ARRAY_START = 18;
   private static final byte TAG_LONG_ARRAY_END = 19;
   private static final byte[] EMPTY_PAYLOAD = new byte[]{1};
   private static final byte[] FALSE_PAYLOAD = new byte[]{13, 0};
   private static final byte[] TRUE_PAYLOAD = new byte[]{13, 1};
   public static final byte[] EMPTY_MAP_PAYLOAD = new byte[]{2, 3};
   public static final byte[] EMPTY_LIST_PAYLOAD = new byte[]{4, 5};
   private static final DataResult<Object> UNSUPPORTED_OPERATION_ERROR = DataResult.error(() -> "Unsupported operation");
   private static final Comparator<HashCode> HASH_COMPARATOR = Comparator.comparingLong(HashCode::padToLong);
   private static final Comparator<Map.Entry<HashCode, HashCode>> MAP_ENTRY_ORDER;
   private static final Comparator<Pair<HashCode, HashCode>> MAPLIKE_ENTRY_ORDER;
   public static final HashOps CRC32C_INSTANCE;
   final HashFunction hashFunction;
   final HashCode empty;
   private final HashCode emptyMap;
   private final HashCode emptyList;
   private final HashCode trueHash;
   private final HashCode falseHash;

   public HashOps(HashFunction var1) {
      super();
      this.hashFunction = var1;
      this.empty = var1.hashBytes(EMPTY_PAYLOAD);
      this.emptyMap = var1.hashBytes(EMPTY_MAP_PAYLOAD);
      this.emptyList = var1.hashBytes(EMPTY_LIST_PAYLOAD);
      this.falseHash = var1.hashBytes(FALSE_PAYLOAD);
      this.trueHash = var1.hashBytes(TRUE_PAYLOAD);
   }

   public HashCode empty() {
      return this.empty;
   }

   public HashCode emptyMap() {
      return this.emptyMap;
   }

   public HashCode emptyList() {
      return this.emptyList;
   }

   public HashCode createNumeric(Number var1) {
      Objects.requireNonNull(var1);
      byte var3 = 0;
      HashCode var10000;
      //$FF: var3->value
      //0->java/lang/Byte
      //1->java/lang/Short
      //2->java/lang/Integer
      //3->java/lang/Long
      //4->java/lang/Double
      //5->java/lang/Float
      switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
         case 0:
            Byte var4 = (Byte)var1;
            var10000 = this.createByte(var4);
            break;
         case 1:
            Short var5 = (Short)var1;
            var10000 = this.createShort(var5);
            break;
         case 2:
            Integer var6 = (Integer)var1;
            var10000 = this.createInt(var6);
            break;
         case 3:
            Long var7 = (Long)var1;
            var10000 = this.createLong(var7);
            break;
         case 4:
            Double var8 = (Double)var1;
            var10000 = this.createDouble(var8);
            break;
         case 5:
            Float var9 = (Float)var1;
            var10000 = this.createFloat(var9);
            break;
         default:
            var10000 = this.createDouble(var1.doubleValue());
      }

      return var10000;
   }

   public HashCode createByte(byte var1) {
      return this.hashFunction.newHasher(2).putByte((byte)6).putByte(var1).hash();
   }

   public HashCode createShort(short var1) {
      return this.hashFunction.newHasher(3).putByte((byte)7).putShort(var1).hash();
   }

   public HashCode createInt(int var1) {
      return this.hashFunction.newHasher(5).putByte((byte)8).putInt(var1).hash();
   }

   public HashCode createLong(long var1) {
      return this.hashFunction.newHasher(9).putByte((byte)9).putLong(var1).hash();
   }

   public HashCode createFloat(float var1) {
      return this.hashFunction.newHasher(5).putByte((byte)10).putFloat(var1).hash();
   }

   public HashCode createDouble(double var1) {
      return this.hashFunction.newHasher(9).putByte((byte)11).putDouble(var1).hash();
   }

   public HashCode createString(String var1) {
      return this.hashFunction.newHasher().putByte((byte)12).putInt(var1.length()).putUnencodedChars(var1).hash();
   }

   public HashCode createBoolean(boolean var1) {
      return var1 ? this.trueHash : this.falseHash;
   }

   private static Hasher hashMap(Hasher var0, Map<HashCode, HashCode> var1) {
      var0.putByte((byte)2);
      var1.entrySet().stream().sorted(MAP_ENTRY_ORDER).forEach((var1x) -> var0.putBytes(((HashCode)var1x.getKey()).asBytes()).putBytes(((HashCode)var1x.getValue()).asBytes()));
      var0.putByte((byte)3);
      return var0;
   }

   static Hasher hashMap(Hasher var0, Stream<Pair<HashCode, HashCode>> var1) {
      var0.putByte((byte)2);
      var1.sorted(MAPLIKE_ENTRY_ORDER).forEach((var1x) -> var0.putBytes(((HashCode)var1x.getFirst()).asBytes()).putBytes(((HashCode)var1x.getSecond()).asBytes()));
      var0.putByte((byte)3);
      return var0;
   }

   public HashCode createMap(Stream<Pair<HashCode, HashCode>> var1) {
      return hashMap(this.hashFunction.newHasher(), var1).hash();
   }

   public HashCode createMap(Map<HashCode, HashCode> var1) {
      return hashMap(this.hashFunction.newHasher(), var1).hash();
   }

   public HashCode createList(Stream<HashCode> var1) {
      Hasher var2 = this.hashFunction.newHasher();
      var2.putByte((byte)4);
      var1.forEach((var1x) -> var2.putBytes(var1x.asBytes()));
      var2.putByte((byte)5);
      return var2.hash();
   }

   public HashCode createByteList(ByteBuffer var1) {
      Hasher var2 = this.hashFunction.newHasher();
      var2.putByte((byte)14);
      var2.putBytes(var1);
      var2.putByte((byte)15);
      return var2.hash();
   }

   public HashCode createIntList(IntStream var1) {
      Hasher var2 = this.hashFunction.newHasher();
      var2.putByte((byte)16);
      Objects.requireNonNull(var2);
      var1.forEach(var2::putInt);
      var2.putByte((byte)17);
      return var2.hash();
   }

   public HashCode createLongList(LongStream var1) {
      Hasher var2 = this.hashFunction.newHasher();
      var2.putByte((byte)18);
      Objects.requireNonNull(var2);
      var1.forEach(var2::putLong);
      var2.putByte((byte)19);
      return var2.hash();
   }

   public HashCode remove(HashCode var1, String var2) {
      return var1;
   }

   public RecordBuilder<HashCode> mapBuilder() {
      return new MapHashBuilder();
   }

   public ListBuilder<HashCode> listBuilder() {
      return new ListHashBuilder();
   }

   public String toString() {
      return "Hash " + String.valueOf(this.hashFunction);
   }

   public <U> U convertTo(DynamicOps<U> var1, HashCode var2) {
      throw new UnsupportedOperationException("Can't convert from this type");
   }

   public Number getNumberValue(HashCode var1, Number var2) {
      return var2;
   }

   public HashCode set(HashCode var1, String var2, HashCode var3) {
      return var1;
   }

   public HashCode update(HashCode var1, String var2, Function<HashCode, HashCode> var3) {
      return var1;
   }

   public HashCode updateGeneric(HashCode var1, HashCode var2, Function<HashCode, HashCode> var3) {
      return var1;
   }

   private static <T> DataResult<T> unsupported() {
      return UNSUPPORTED_OPERATION_ERROR;
   }

   public DataResult<HashCode> get(HashCode var1, String var2) {
      return unsupported();
   }

   public DataResult<HashCode> getGeneric(HashCode var1, HashCode var2) {
      return unsupported();
   }

   public DataResult<Number> getNumberValue(HashCode var1) {
      return unsupported();
   }

   public DataResult<Boolean> getBooleanValue(HashCode var1) {
      return unsupported();
   }

   public DataResult<String> getStringValue(HashCode var1) {
      return unsupported();
   }

   boolean isEmpty(HashCode var1) {
      return var1.equals(this.empty);
   }

   public DataResult<HashCode> mergeToList(HashCode var1, HashCode var2) {
      return this.isEmpty(var1) ? DataResult.success(this.createList(Stream.of(var2))) : unsupported();
   }

   public DataResult<HashCode> mergeToList(HashCode var1, List<HashCode> var2) {
      return this.isEmpty(var1) ? DataResult.success(this.createList(var2.stream())) : unsupported();
   }

   public DataResult<HashCode> mergeToMap(HashCode var1, HashCode var2, HashCode var3) {
      return this.isEmpty(var1) ? DataResult.success(this.createMap(Map.of(var2, var3))) : unsupported();
   }

   public DataResult<HashCode> mergeToMap(HashCode var1, Map<HashCode, HashCode> var2) {
      return this.isEmpty(var1) ? DataResult.success(this.createMap(var2)) : unsupported();
   }

   public DataResult<HashCode> mergeToMap(HashCode var1, MapLike<HashCode> var2) {
      return this.isEmpty(var1) ? DataResult.success(this.createMap(var2.entries())) : unsupported();
   }

   public DataResult<Stream<Pair<HashCode, HashCode>>> getMapValues(HashCode var1) {
      return unsupported();
   }

   public DataResult<Consumer<BiConsumer<HashCode, HashCode>>> getMapEntries(HashCode var1) {
      return unsupported();
   }

   public DataResult<Stream<HashCode>> getStream(HashCode var1) {
      return unsupported();
   }

   public DataResult<Consumer<Consumer<HashCode>>> getList(HashCode var1) {
      return unsupported();
   }

   public DataResult<MapLike<HashCode>> getMap(HashCode var1) {
      return unsupported();
   }

   public DataResult<ByteBuffer> getByteBuffer(HashCode var1) {
      return unsupported();
   }

   public DataResult<IntStream> getIntStream(HashCode var1) {
      return unsupported();
   }

   public DataResult<LongStream> getLongStream(HashCode var1) {
      return unsupported();
   }

   // $FF: synthetic method
   public Object updateGeneric(final Object var1, final Object var2, final Function var3) {
      return this.updateGeneric((HashCode)var1, (HashCode)var2, var3);
   }

   // $FF: synthetic method
   public Object update(final Object var1, final String var2, final Function var3) {
      return this.update((HashCode)var1, var2, var3);
   }

   // $FF: synthetic method
   public Object set(final Object var1, final String var2, final Object var3) {
      return this.set((HashCode)var1, var2, (HashCode)var3);
   }

   // $FF: synthetic method
   public DataResult getGeneric(final Object var1, final Object var2) {
      return this.getGeneric((HashCode)var1, (HashCode)var2);
   }

   // $FF: synthetic method
   public DataResult get(final Object var1, final String var2) {
      return this.get((HashCode)var1, var2);
   }

   // $FF: synthetic method
   public Object remove(final Object var1, final String var2) {
      return this.remove((HashCode)var1, var2);
   }

   // $FF: synthetic method
   public Object createLongList(final LongStream var1) {
      return this.createLongList(var1);
   }

   // $FF: synthetic method
   public DataResult getLongStream(final Object var1) {
      return this.getLongStream((HashCode)var1);
   }

   // $FF: synthetic method
   public Object createIntList(final IntStream var1) {
      return this.createIntList(var1);
   }

   // $FF: synthetic method
   public DataResult getIntStream(final Object var1) {
      return this.getIntStream((HashCode)var1);
   }

   // $FF: synthetic method
   public Object createByteList(final ByteBuffer var1) {
      return this.createByteList(var1);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(final Object var1) {
      return this.getByteBuffer((HashCode)var1);
   }

   // $FF: synthetic method
   public Object createList(final Stream var1) {
      return this.createList(var1);
   }

   // $FF: synthetic method
   public DataResult getList(final Object var1) {
      return this.getList((HashCode)var1);
   }

   // $FF: synthetic method
   public DataResult getStream(final Object var1) {
      return this.getStream((HashCode)var1);
   }

   // $FF: synthetic method
   public Object createMap(final Map var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public DataResult getMap(final Object var1) {
      return this.getMap((HashCode)var1);
   }

   // $FF: synthetic method
   public Object createMap(final Stream var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(final Object var1) {
      return this.getMapEntries((HashCode)var1);
   }

   // $FF: synthetic method
   public DataResult getMapValues(final Object var1) {
      return this.getMapValues((HashCode)var1);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final MapLike var2) {
      return this.mergeToMap((HashCode)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final Map var2) {
      return this.mergeToMap((HashCode)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final Object var2, final Object var3) {
      return this.mergeToMap((HashCode)var1, (HashCode)var2, (HashCode)var3);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object var1, final List var2) {
      return this.mergeToList((HashCode)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object var1, final Object var2) {
      return this.mergeToList((HashCode)var1, (HashCode)var2);
   }

   // $FF: synthetic method
   public Object createString(final String var1) {
      return this.createString(var1);
   }

   // $FF: synthetic method
   public DataResult getStringValue(final Object var1) {
      return this.getStringValue((HashCode)var1);
   }

   // $FF: synthetic method
   public Object createBoolean(final boolean var1) {
      return this.createBoolean(var1);
   }

   // $FF: synthetic method
   public DataResult getBooleanValue(final Object var1) {
      return this.getBooleanValue((HashCode)var1);
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
   public Number getNumberValue(final Object var1, final Number var2) {
      return this.getNumberValue((HashCode)var1, var2);
   }

   // $FF: synthetic method
   public DataResult getNumberValue(final Object var1) {
      return this.getNumberValue((HashCode)var1);
   }

   // $FF: synthetic method
   public Object convertTo(final DynamicOps var1, final Object var2) {
      return this.convertTo(var1, (HashCode)var2);
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

   static {
      MAP_ENTRY_ORDER = Entry.comparingByKey(HASH_COMPARATOR).thenComparing(Entry.comparingByValue(HASH_COMPARATOR));
      MAPLIKE_ENTRY_ORDER = Comparator.comparing(Pair::getFirst, HASH_COMPARATOR).thenComparing(Pair::getSecond, HASH_COMPARATOR);
      CRC32C_INSTANCE = new HashOps(Hashing.crc32c());
   }

   final class MapHashBuilder extends RecordBuilder.AbstractUniversalBuilder<HashCode, List<Pair<HashCode, HashCode>>> {
      public MapHashBuilder() {
         super(HashOps.this);
      }

      protected List<Pair<HashCode, HashCode>> initBuilder() {
         return new ArrayList();
      }

      protected List<Pair<HashCode, HashCode>> append(HashCode var1, HashCode var2, List<Pair<HashCode, HashCode>> var3) {
         var3.add(Pair.of(var1, var2));
         return var3;
      }

      protected DataResult<HashCode> build(List<Pair<HashCode, HashCode>> var1, HashCode var2) {
         assert HashOps.this.isEmpty(var2);

         return DataResult.success(HashOps.hashMap(HashOps.this.hashFunction.newHasher(), var1.stream()).hash());
      }

      // $FF: synthetic method
      protected Object append(final Object var1, final Object var2, final Object var3) {
         return this.append((HashCode)var1, (HashCode)var2, (List)var3);
      }

      // $FF: synthetic method
      protected DataResult build(final Object var1, final Object var2) {
         return this.build((List)var1, (HashCode)var2);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }

   class ListHashBuilder extends AbstractListBuilder<HashCode, Hasher> {
      public ListHashBuilder() {
         super(HashOps.this);
      }

      protected Hasher initBuilder() {
         return HashOps.this.hashFunction.newHasher().putByte((byte)4);
      }

      protected Hasher append(Hasher var1, HashCode var2) {
         return var1.putBytes(var2.asBytes());
      }

      protected DataResult<HashCode> build(Hasher var1, HashCode var2) {
         assert var2.equals(HashOps.this.empty);

         var1.putByte((byte)5);
         return DataResult.success(var1.hash());
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }
}
