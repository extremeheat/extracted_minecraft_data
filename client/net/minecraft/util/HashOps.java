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
   private final HashFunction hashFunction;
   private final HashCode empty;
   private final HashCode emptyMap;
   private final HashCode emptyList;
   private final HashCode trueHash;
   private final HashCode falseHash;

   public HashOps(final HashFunction hashFunction) {
      super();
      this.hashFunction = hashFunction;
      this.empty = hashFunction.hashBytes(EMPTY_PAYLOAD);
      this.emptyMap = hashFunction.hashBytes(EMPTY_MAP_PAYLOAD);
      this.emptyList = hashFunction.hashBytes(EMPTY_LIST_PAYLOAD);
      this.falseHash = hashFunction.hashBytes(FALSE_PAYLOAD);
      this.trueHash = hashFunction.hashBytes(TRUE_PAYLOAD);
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

   public HashCode createNumeric(final Number value) {
      Objects.requireNonNull(value);
      byte var3 = 0;
      HashCode var10000;
      //$FF: var3->value
      //0->java/lang/Byte
      //1->java/lang/Short
      //2->java/lang/Integer
      //3->java/lang/Long
      //4->java/lang/Double
      //5->java/lang/Float
      switch (value.typeSwitch<invokedynamic>(value, var3)) {
         case 0:
            Byte v = (Byte)value;
            var10000 = this.createByte(v);
            break;
         case 1:
            Short v = (Short)value;
            var10000 = this.createShort(v);
            break;
         case 2:
            Integer v = (Integer)value;
            var10000 = this.createInt(v);
            break;
         case 3:
            Long v = (Long)value;
            var10000 = this.createLong(v);
            break;
         case 4:
            Double v = (Double)value;
            var10000 = this.createDouble(v);
            break;
         case 5:
            Float v = (Float)value;
            var10000 = this.createFloat(v);
            break;
         default:
            var10000 = this.createDouble(value.doubleValue());
      }

      return var10000;
   }

   public HashCode createByte(final byte value) {
      return this.hashFunction.newHasher(2).putByte((byte)6).putByte(value).hash();
   }

   public HashCode createShort(final short value) {
      return this.hashFunction.newHasher(3).putByte((byte)7).putShort(value).hash();
   }

   public HashCode createInt(final int value) {
      return this.hashFunction.newHasher(5).putByte((byte)8).putInt(value).hash();
   }

   public HashCode createLong(final long value) {
      return this.hashFunction.newHasher(9).putByte((byte)9).putLong(value).hash();
   }

   public HashCode createFloat(final float value) {
      return this.hashFunction.newHasher(5).putByte((byte)10).putFloat(value).hash();
   }

   public HashCode createDouble(final double value) {
      return this.hashFunction.newHasher(9).putByte((byte)11).putDouble(value).hash();
   }

   public HashCode createString(final String value) {
      return this.hashFunction.newHasher().putByte((byte)12).putInt(value.length()).putUnencodedChars(value).hash();
   }

   public HashCode createBoolean(final boolean value) {
      return value ? this.trueHash : this.falseHash;
   }

   private static Hasher hashMap(final Hasher hasher, final Map<HashCode, HashCode> map) {
      hasher.putByte((byte)2);
      map.entrySet().stream().sorted(MAP_ENTRY_ORDER).forEach((e) -> hasher.putBytes(((HashCode)e.getKey()).asBytes()).putBytes(((HashCode)e.getValue()).asBytes()));
      hasher.putByte((byte)3);
      return hasher;
   }

   private static Hasher hashMap(final Hasher hasher, final Stream<Pair<HashCode, HashCode>> map) {
      hasher.putByte((byte)2);
      map.sorted(MAPLIKE_ENTRY_ORDER).forEach((e) -> hasher.putBytes(((HashCode)e.getFirst()).asBytes()).putBytes(((HashCode)e.getSecond()).asBytes()));
      hasher.putByte((byte)3);
      return hasher;
   }

   public HashCode createMap(final Stream<Pair<HashCode, HashCode>> map) {
      return hashMap(this.hashFunction.newHasher(), map).hash();
   }

   public HashCode createMap(final Map<HashCode, HashCode> map) {
      return hashMap(this.hashFunction.newHasher(), map).hash();
   }

   public HashCode createList(final Stream<HashCode> input) {
      Hasher hasher = this.hashFunction.newHasher();
      hasher.putByte((byte)4);
      input.forEach((value) -> hasher.putBytes(value.asBytes()));
      hasher.putByte((byte)5);
      return hasher.hash();
   }

   public HashCode createByteList(final ByteBuffer input) {
      Hasher hasher = this.hashFunction.newHasher();
      hasher.putByte((byte)14);
      hasher.putBytes(input);
      hasher.putByte((byte)15);
      return hasher.hash();
   }

   public HashCode createIntList(final IntStream input) {
      Hasher hasher = this.hashFunction.newHasher();
      hasher.putByte((byte)16);
      Objects.requireNonNull(hasher);
      input.forEach(hasher::putInt);
      hasher.putByte((byte)17);
      return hasher.hash();
   }

   public HashCode createLongList(final LongStream input) {
      Hasher hasher = this.hashFunction.newHasher();
      hasher.putByte((byte)18);
      Objects.requireNonNull(hasher);
      input.forEach(hasher::putLong);
      hasher.putByte((byte)19);
      return hasher.hash();
   }

   public HashCode remove(final HashCode input, final String key) {
      return input;
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

   public <U> U convertTo(final DynamicOps<U> outOps, final HashCode input) {
      throw new UnsupportedOperationException("Can't convert from this type");
   }

   public Number getNumberValue(final HashCode input, final Number defaultValue) {
      return defaultValue;
   }

   public HashCode set(final HashCode input, final String key, final HashCode value) {
      return input;
   }

   public HashCode update(final HashCode input, final String key, final Function<HashCode, HashCode> function) {
      return input;
   }

   public HashCode updateGeneric(final HashCode input, final HashCode key, final Function<HashCode, HashCode> function) {
      return input;
   }

   private static <T> DataResult<T> unsupported() {
      return UNSUPPORTED_OPERATION_ERROR;
   }

   public DataResult<HashCode> get(final HashCode input, final String key) {
      return unsupported();
   }

   public DataResult<HashCode> getGeneric(final HashCode input, final HashCode key) {
      return unsupported();
   }

   public DataResult<Number> getNumberValue(final HashCode input) {
      return unsupported();
   }

   public DataResult<Boolean> getBooleanValue(final HashCode input) {
      return unsupported();
   }

   public DataResult<String> getStringValue(final HashCode input) {
      return unsupported();
   }

   private boolean isEmpty(final HashCode value) {
      return value.equals(this.empty);
   }

   public DataResult<HashCode> mergeToList(final HashCode prefix, final HashCode value) {
      return this.isEmpty(prefix) ? DataResult.success(this.createList(Stream.of(value))) : unsupported();
   }

   public DataResult<HashCode> mergeToList(final HashCode prefix, final List<HashCode> values) {
      return this.isEmpty(prefix) ? DataResult.success(this.createList(values.stream())) : unsupported();
   }

   public DataResult<HashCode> mergeToMap(final HashCode prefix, final HashCode key, final HashCode value) {
      return this.isEmpty(prefix) ? DataResult.success(this.createMap(Map.of(key, value))) : unsupported();
   }

   public DataResult<HashCode> mergeToMap(final HashCode prefix, final Map<HashCode, HashCode> values) {
      return this.isEmpty(prefix) ? DataResult.success(this.createMap(values)) : unsupported();
   }

   public DataResult<HashCode> mergeToMap(final HashCode prefix, final MapLike<HashCode> values) {
      return this.isEmpty(prefix) ? DataResult.success(this.createMap(values.entries())) : unsupported();
   }

   public DataResult<Stream<Pair<HashCode, HashCode>>> getMapValues(final HashCode input) {
      return unsupported();
   }

   public DataResult<Consumer<BiConsumer<HashCode, HashCode>>> getMapEntries(final HashCode input) {
      return unsupported();
   }

   public DataResult<Stream<HashCode>> getStream(final HashCode input) {
      return unsupported();
   }

   public DataResult<Consumer<Consumer<HashCode>>> getList(final HashCode input) {
      return unsupported();
   }

   public DataResult<MapLike<HashCode>> getMap(final HashCode input) {
      return unsupported();
   }

   public DataResult<ByteBuffer> getByteBuffer(final HashCode input) {
      return unsupported();
   }

   public DataResult<IntStream> getIntStream(final HashCode input) {
      return unsupported();
   }

   public DataResult<LongStream> getLongStream(final HashCode input) {
      return unsupported();
   }

   static {
      MAP_ENTRY_ORDER = Entry.comparingByKey(HASH_COMPARATOR).thenComparing(Entry.comparingByValue(HASH_COMPARATOR));
      MAPLIKE_ENTRY_ORDER = Comparator.comparing(Pair::getFirst, HASH_COMPARATOR).thenComparing(Pair::getSecond, HASH_COMPARATOR);
      CRC32C_INSTANCE = new HashOps(Hashing.crc32c());
   }

   private final class MapHashBuilder extends RecordBuilder.AbstractUniversalBuilder<HashCode, List<Pair<HashCode, HashCode>>> {
      public MapHashBuilder() {
         Objects.requireNonNull(HashOps.this);
         super(HashOps.this);
      }

      protected List<Pair<HashCode, HashCode>> initBuilder() {
         return new ArrayList();
      }

      protected List<Pair<HashCode, HashCode>> append(final HashCode key, final HashCode value, final List<Pair<HashCode, HashCode>> builder) {
         builder.add(Pair.of(key, value));
         return builder;
      }

      protected DataResult<HashCode> build(final List<Pair<HashCode, HashCode>> builder, final HashCode prefix) {
         assert HashOps.this.isEmpty(prefix);

         return DataResult.success(HashOps.hashMap(HashOps.this.hashFunction.newHasher(), builder.stream()).hash());
      }
   }

   private class ListHashBuilder extends AbstractListBuilder<HashCode, Hasher> {
      public ListHashBuilder() {
         Objects.requireNonNull(HashOps.this);
         super(HashOps.this);
      }

      protected Hasher initBuilder() {
         return HashOps.this.hashFunction.newHasher().putByte((byte)4);
      }

      protected Hasher append(final Hasher hasher, final HashCode value) {
         return hasher.putBytes(value.asBytes());
      }

      protected DataResult<HashCode> build(final Hasher hasher, final HashCode prefix) {
         assert prefix.equals(HashOps.this.empty);

         hasher.putByte((byte)5);
         return DataResult.success(hasher.hash());
      }
   }
}
