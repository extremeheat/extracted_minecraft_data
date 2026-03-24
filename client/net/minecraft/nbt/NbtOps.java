package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();

   private NbtOps() {
      super();
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public Tag emptyList() {
      return new ListTag();
   }

   public Tag emptyMap() {
      return new CompoundTag();
   }

   public <U> U convertTo(final DynamicOps<U> outOps, final Tag input) {
      Objects.requireNonNull(input);
      Tag var3 = input;
      byte var4 = 0;

      while(true) {
         Object var10000;
         //$FF: var4->value
         //0->net/minecraft/nbt/EndTag
         //1->net/minecraft/nbt/ByteTag
         //2->net/minecraft/nbt/ShortTag
         //3->net/minecraft/nbt/IntTag
         //4->net/minecraft/nbt/LongTag
         //5->net/minecraft/nbt/FloatTag
         //6->net/minecraft/nbt/DoubleTag
         //7->net/minecraft/nbt/ByteArrayTag
         //8->net/minecraft/nbt/StringTag
         //9->net/minecraft/nbt/ListTag
         //10->net/minecraft/nbt/CompoundTag
         //11->net/minecraft/nbt/IntArrayTag
         //12->net/minecraft/nbt/LongArrayTag
         switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
            case 0:
               EndTag ignored = (EndTag)var3;
               var10000 = (StringTag)outOps.empty();
               break;
            case 1:
               ByteTag var6 = (ByteTag)var3;
               ByteTag var53 = var6;

               try {
                  var54 = var53.value();
               } catch (Throwable var33) {
                  throw new MatchException(var33.toString(), var33);
               }

               byte value = var54;
               if (false) {
                  var4 = 2;
                  continue;
               }

               var10000 = (StringTag)outOps.createByte(value);
               break;
            case 2:
               ShortTag value = (ShortTag)var3;
               ShortTag var51 = value;

               try {
                  var52 = var51.value();
               } catch (Throwable var32) {
                  throw new MatchException(var32.toString(), var32);
               }

               short value = var52;
               if (false) {
                  var4 = 3;
                  continue;
               }

               var10000 = (StringTag)outOps.createShort(value);
               break;
            case 3:
               IntTag value = (IntTag)var3;
               IntTag var49 = value;

               try {
                  var50 = var49.value();
               } catch (Throwable var31) {
                  throw new MatchException(var31.toString(), var31);
               }

               int value = var50;
               if (false) {
                  var4 = 4;
                  continue;
               }

               var10000 = (StringTag)outOps.createInt(value);
               break;
            case 4:
               LongTag value = (LongTag)var3;
               LongTag var47 = value;

               try {
                  var48 = var47.value();
               } catch (Throwable var30) {
                  throw new MatchException(var30.toString(), var30);
               }

               long value = var48;
               if (false) {
                  var4 = 5;
                  continue;
               }

               var10000 = (StringTag)outOps.createLong(value);
               break;
            case 5:
               FloatTag value = (FloatTag)var3;
               FloatTag var45 = value;

               try {
                  var46 = var45.value();
               } catch (Throwable var29) {
                  throw new MatchException(var29.toString(), var29);
               }

               float value = var46;
               if (false) {
                  var4 = 6;
                  continue;
               }

               var10000 = (StringTag)outOps.createFloat(value);
               break;
            case 6:
               DoubleTag value = (DoubleTag)var3;
               DoubleTag var43 = value;

               try {
                  var44 = var43.value();
               } catch (Throwable var28) {
                  throw new MatchException(var28.toString(), var28);
               }

               double value = var44;
               if (false) {
                  var4 = 7;
                  continue;
               }

               var10000 = (StringTag)outOps.createDouble(value);
               break;
            case 7:
               ByteArrayTag byteArrayTag = (ByteArrayTag)var3;
               var10000 = (StringTag)outOps.createByteList(ByteBuffer.wrap(byteArrayTag.getAsByteArray()));
               break;
            case 8:
               StringTag var21 = (StringTag)var3;
               var10000 = var21;

               try {
                  var42 = var10000.value();
               } catch (Throwable var27) {
                  throw new MatchException(var27.toString(), var27);
               }

               String value = var42;
               var10000 = (StringTag)outOps.createString(value);
               break;
            case 9:
               ListTag listTag = (ListTag)var3;
               var10000 = (StringTag)this.convertList(outOps, listTag);
               break;
            case 10:
               CompoundTag compoundTag = (CompoundTag)var3;
               var10000 = (StringTag)this.convertMap(outOps, compoundTag);
               break;
            case 11:
               IntArrayTag intArrayTag = (IntArrayTag)var3;
               var10000 = (StringTag)outOps.createIntList(Arrays.stream(intArrayTag.getAsIntArray()));
               break;
            case 12:
               LongArrayTag longArrayTag = (LongArrayTag)var3;
               var10000 = (StringTag)outOps.createLongList(Arrays.stream(longArrayTag.getAsLongArray()));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return (U)var10000;
      }
   }

   public DataResult<Number> getNumberValue(final Tag input) {
      return (DataResult)input.asNumber().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Not a number"));
   }

   public Tag createNumeric(final Number i) {
      return DoubleTag.valueOf(i.doubleValue());
   }

   public Tag createByte(final byte value) {
      return ByteTag.valueOf(value);
   }

   public Tag createShort(final short value) {
      return ShortTag.valueOf(value);
   }

   public Tag createInt(final int value) {
      return IntTag.valueOf(value);
   }

   public Tag createLong(final long value) {
      return LongTag.valueOf(value);
   }

   public Tag createFloat(final float value) {
      return FloatTag.valueOf(value);
   }

   public Tag createDouble(final double value) {
      return DoubleTag.valueOf(value);
   }

   public Tag createBoolean(final boolean value) {
      return ByteTag.valueOf(value);
   }

   public DataResult<String> getStringValue(final Tag input) {
      if (input instanceof StringTag var2) {
         StringTag var10000 = var2;

         try {
            var6 = var10000.value();
         } catch (Throwable var5) {
            throw new MatchException(var5.toString(), var5);
         }

         String value = var6;
         return DataResult.success(value);
      } else {
         return DataResult.error(() -> "Not a string");
      }
   }

   public Tag createString(final String value) {
      return StringTag.valueOf(value);
   }

   public DataResult<Tag> mergeToList(final Tag list, final Tag value) {
      return (DataResult)createCollector(list).map((collector) -> DataResult.success(collector.accept(value).result())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + String.valueOf(list), list));
   }

   public DataResult<Tag> mergeToList(final Tag list, final List<Tag> values) {
      return (DataResult)createCollector(list).map((collector) -> DataResult.success(collector.acceptAll(values).result())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + String.valueOf(list), list));
   }

   public DataResult<Tag> mergeToMap(final Tag map, final Tag key, final Tag value) {
      if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(map), map);
      } else if (key instanceof StringTag) {
         StringTag var5 = (StringTag)key;
         StringTag var10000 = var5;

         try {
            var10 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String var6 = var10;
         String stringKey = var6;
         CompoundTag var11;
         if (map instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)map;
            var11 = tag.shallowCopy();
         } else {
            var11 = new CompoundTag();
         }

         CompoundTag output = var11;
         output.put(stringKey, value);
         return DataResult.success(output);
      } else {
         return DataResult.error(() -> "key is not a string: " + String.valueOf(key), map);
      }
   }

   public DataResult<Tag> mergeToMap(final Tag map, final MapLike<Tag> values) {
      if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(map), map);
      } else {
         Iterator<Pair<Tag, Tag>> valuesIterator = values.entries().iterator();
         if (!valuesIterator.hasNext()) {
            return map == this.empty() ? DataResult.success(this.emptyMap()) : DataResult.success(map);
         } else {
            CompoundTag var10000;
            if (map instanceof CompoundTag) {
               CompoundTag tag = (CompoundTag)map;
               var10000 = tag.shallowCopy();
            } else {
               var10000 = new CompoundTag();
            }

            CompoundTag output = var10000;
            List<Tag> missed = new ArrayList();
            valuesIterator.forEachRemaining((entry) -> {
               Tag key = (Tag)entry.getFirst();
               if (key instanceof StringTag $b$0) {
                  StringTag var10000 = $b$0;

                  try {
                     var8 = var10000.value();
                  } catch (Throwable var7) {
                     throw new MatchException(var7.toString(), var7);
                  }

                  String patt1$temp = var8;
                  output.put(patt1$temp, (Tag)entry.getSecond());
               } else {
                  missed.add(key);
               }
            });
            return !missed.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + String.valueOf(missed), output) : DataResult.success(output);
         }
      }
   }

   public DataResult<Tag> mergeToMap(final Tag map, final Map<Tag, Tag> values) {
      if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(map), map);
      } else if (values.isEmpty()) {
         return map == this.empty() ? DataResult.success(this.emptyMap()) : DataResult.success(map);
      } else {
         CompoundTag var10000;
         if (map instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)map;
            var10000 = tag.shallowCopy();
         } else {
            var10000 = new CompoundTag();
         }

         CompoundTag output = var10000;
         List<Tag> missed = new ArrayList();

         for(Map.Entry<Tag, Tag> entry : values.entrySet()) {
            Tag key = (Tag)entry.getKey();
            if (key instanceof StringTag) {
               StringTag var8 = (StringTag)key;
               StringTag var13 = var8;

               try {
                  var14 = var13.value();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               String stringKey = var14;
               output.put(stringKey, (Tag)entry.getValue());
            } else {
               missed.add(key);
            }
         }

         if (!missed.isEmpty()) {
            return DataResult.error(() -> "some keys are not strings: " + String.valueOf(missed), output);
         } else {
            return DataResult.success(output);
         }
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(final Tag input) {
      if (input instanceof CompoundTag tag) {
         return DataResult.success(tag.entrySet().stream().map((entry) -> Pair.of(this.createString((String)entry.getKey()), (Tag)entry.getValue())));
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(input));
      }
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(final Tag input) {
      if (input instanceof CompoundTag tag) {
         return DataResult.success((Consumer)(c) -> {
            for(Map.Entry<String, Tag> entry : tag.entrySet()) {
               c.accept(this.createString((String)entry.getKey()), (Tag)entry.getValue());
            }

         });
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(input));
      }
   }

   public DataResult<MapLike<Tag>> getMap(final Tag input) {
      if (input instanceof final CompoundTag tag) {
         return DataResult.success(new MapLike<Tag>() {
            {
               Objects.requireNonNull(NbtOps.this);
            }

            public @Nullable Tag get(final Tag key) {
               if (key instanceof StringTag var2) {
                  StringTag var10000 = var2;

                  try {
                     var6 = var10000.value();
                  } catch (Throwable var5) {
                     throw new MatchException(var5.toString(), var5);
                  }

                  String stringKey = var6;
                  return tag.get(stringKey);
               } else {
                  throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + String.valueOf(key));
               }
            }

            public @Nullable Tag get(final String key) {
               return tag.get(key);
            }

            public Stream<Pair<Tag, Tag>> entries() {
               return tag.entrySet().stream().map((entry) -> Pair.of(NbtOps.this.createString((String)entry.getKey()), (Tag)entry.getValue()));
            }

            public String toString() {
               return "MapLike[" + String.valueOf(tag) + "]";
            }
         });
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(input));
      }
   }

   public Tag createMap(final Stream<Pair<Tag, Tag>> map) {
      CompoundTag tag = new CompoundTag();
      map.forEach((entry) -> {
         Tag key = (Tag)entry.getFirst();
         Tag value = (Tag)entry.getSecond();
         if (key instanceof StringTag $b$0) {
            StringTag var10000 = $b$0;

            try {
               var8 = var10000.value();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            String patt1$temp = var8;
            tag.put(patt1$temp, value);
         } else {
            throw new UnsupportedOperationException("Cannot create map with non-string key: " + String.valueOf(key));
         }
      });
      return tag;
   }

   public DataResult<Stream<Tag>> getStream(final Tag input) {
      if (input instanceof CollectionTag collection) {
         return DataResult.success(collection.stream());
      } else {
         return DataResult.error(() -> "Not a list");
      }
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(final Tag input) {
      if (input instanceof CollectionTag collection) {
         Objects.requireNonNull(collection);
         return DataResult.success(collection::forEach);
      } else {
         return DataResult.error(() -> "Not a list: " + String.valueOf(input));
      }
   }

   public DataResult<ByteBuffer> getByteBuffer(final Tag input) {
      if (input instanceof ByteArrayTag array) {
         return DataResult.success(ByteBuffer.wrap(array.getAsByteArray()));
      } else {
         return super.getByteBuffer(input);
      }
   }

   public Tag createByteList(final ByteBuffer input) {
      ByteBuffer wholeBuffer = input.duplicate().clear();
      byte[] bytes = new byte[input.capacity()];
      wholeBuffer.get(0, bytes, 0, bytes.length);
      return new ByteArrayTag(bytes);
   }

   public DataResult<IntStream> getIntStream(final Tag input) {
      if (input instanceof IntArrayTag array) {
         return DataResult.success(Arrays.stream(array.getAsIntArray()));
      } else {
         return super.getIntStream(input);
      }
   }

   public Tag createIntList(final IntStream input) {
      return new IntArrayTag(input.toArray());
   }

   public DataResult<LongStream> getLongStream(final Tag input) {
      if (input instanceof LongArrayTag array) {
         return DataResult.success(Arrays.stream(array.getAsLongArray()));
      } else {
         return super.getLongStream(input);
      }
   }

   public Tag createLongList(final LongStream input) {
      return new LongArrayTag(input.toArray());
   }

   public Tag createList(final Stream<Tag> input) {
      return new ListTag((List)input.collect(Util.toMutableList()));
   }

   public Tag remove(final Tag input, final String key) {
      if (input instanceof CompoundTag tag) {
         CompoundTag result = tag.shallowCopy();
         result.remove(key);
         return result;
      } else {
         return input;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder<Tag> mapBuilder() {
      return new NbtRecordBuilder();
   }

   private static Optional<ListCollector> createCollector(final Tag tag) {
      if (tag instanceof EndTag) {
         return Optional.of(new GenericListCollector());
      } else if (tag instanceof CollectionTag) {
         CollectionTag collection = (CollectionTag)tag;
         if (collection.isEmpty()) {
            return Optional.of(new GenericListCollector());
         } else {
            Objects.requireNonNull(collection);
            byte var3 = 0;
            Optional var10000;
            //$FF: var3->value
            //0->net/minecraft/nbt/ListTag
            //1->net/minecraft/nbt/ByteArrayTag
            //2->net/minecraft/nbt/IntArrayTag
            //3->net/minecraft/nbt/LongArrayTag
            switch (collection.typeSwitch<invokedynamic>(collection, var3)) {
               case 0:
                  ListTag list = (ListTag)collection;
                  var10000 = Optional.of(new GenericListCollector(list));
                  break;
               case 1:
                  ByteArrayTag array = (ByteArrayTag)collection;
                  var10000 = Optional.of(new ByteListCollector(array.getAsByteArray()));
                  break;
               case 2:
                  IntArrayTag array = (IntArrayTag)collection;
                  var10000 = Optional.of(new IntListCollector(array.getAsIntArray()));
                  break;
               case 3:
                  LongArrayTag array = (LongArrayTag)collection;
                  var10000 = Optional.of(new LongListCollector(array.getAsLongArray()));
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         }
      } else {
         return Optional.empty();
      }
   }

   private class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
      protected NbtRecordBuilder() {
         Objects.requireNonNull(NbtOps.this);
         super(NbtOps.this);
      }

      protected CompoundTag initBuilder() {
         return new CompoundTag();
      }

      protected CompoundTag append(final String key, final Tag value, final CompoundTag builder) {
         builder.put(key, value);
         return builder;
      }

      protected DataResult<Tag> build(final CompoundTag builder, final Tag prefix) {
         if (prefix != null && prefix != EndTag.INSTANCE) {
            if (!(prefix instanceof CompoundTag)) {
               return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(prefix), prefix);
            } else {
               CompoundTag compound = (CompoundTag)prefix;
               CompoundTag result = compound.shallowCopy();

               for(Map.Entry<String, Tag> entry : builder.entrySet()) {
                  result.put((String)entry.getKey(), (Tag)entry.getValue());
               }

               return DataResult.success(result);
            }
         } else {
            return DataResult.success(builder);
         }
      }
   }

   private interface ListCollector {
      ListCollector accept(Tag t);

      default ListCollector acceptAll(final Iterable<Tag> tags) {
         ListCollector collector = this;

         for(Tag tag : tags) {
            collector = collector.accept(tag);
         }

         return collector;
      }

      Tag result();
   }

   private static class GenericListCollector implements ListCollector {
      private final ListTag result = new ListTag();

      private GenericListCollector() {
         super();
      }

      private GenericListCollector(final ListTag initial) {
         super();
         this.result.addAll(initial);
      }

      public GenericListCollector(final IntArrayList initials) {
         super();
         initials.forEach((v) -> this.result.add(IntTag.valueOf(v)));
      }

      public GenericListCollector(final ByteArrayList initials) {
         super();
         initials.forEach((v) -> this.result.add(ByteTag.valueOf(v)));
      }

      public GenericListCollector(final LongArrayList initials) {
         super();
         initials.forEach((v) -> this.result.add(LongTag.valueOf(v)));
      }

      public ListCollector accept(final Tag tag) {
         this.result.add(tag);
         return this;
      }

      public Tag result() {
         return this.result;
      }
   }

   private static class IntListCollector implements ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(final int[] initialValues) {
         super();
         this.values.addElements(0, initialValues);
      }

      public ListCollector accept(final Tag tag) {
         if (tag instanceof IntTag intTag) {
            this.values.add(intTag.intValue());
            return this;
         } else {
            return (new GenericListCollector(this.values)).accept(tag);
         }
      }

      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   private static class ByteListCollector implements ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(final byte[] initialValues) {
         super();
         this.values.addElements(0, initialValues);
      }

      public ListCollector accept(final Tag tag) {
         if (tag instanceof ByteTag byteTag) {
            this.values.add(byteTag.byteValue());
            return this;
         } else {
            return (new GenericListCollector(this.values)).accept(tag);
         }
      }

      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   private static class LongListCollector implements ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(final long[] initialValues) {
         super();
         this.values.addElements(0, initialValues);
      }

      public ListCollector accept(final Tag tag) {
         if (tag instanceof LongTag longTag) {
            this.values.add(longTag.longValue());
            return this;
         } else {
            return (new GenericListCollector(this.values)).accept(tag);
         }
      }

      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
   }
}
