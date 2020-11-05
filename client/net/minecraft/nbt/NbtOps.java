package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();

   protected NbtOps() {
      super();
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public <U> U convertTo(DynamicOps<U> var1, Tag var2) {
      switch(var2.getId()) {
      case 0:
         return var1.empty();
      case 1:
         return var1.createByte(((NumericTag)var2).getAsByte());
      case 2:
         return var1.createShort(((NumericTag)var2).getAsShort());
      case 3:
         return var1.createInt(((NumericTag)var2).getAsInt());
      case 4:
         return var1.createLong(((NumericTag)var2).getAsLong());
      case 5:
         return var1.createFloat(((NumericTag)var2).getAsFloat());
      case 6:
         return var1.createDouble(((NumericTag)var2).getAsDouble());
      case 7:
         return var1.createByteList(ByteBuffer.wrap(((ByteArrayTag)var2).getAsByteArray()));
      case 8:
         return var1.createString(var2.getAsString());
      case 9:
         return this.convertList(var1, var2);
      case 10:
         return this.convertMap(var1, var2);
      case 11:
         return var1.createIntList(Arrays.stream(((IntArrayTag)var2).getAsIntArray()));
      case 12:
         return var1.createLongList(Arrays.stream(((LongArrayTag)var2).getAsLongArray()));
      default:
         throw new IllegalStateException("Unknown tag type: " + var2);
      }
   }

   public DataResult<Number> getNumberValue(Tag var1) {
      return var1 instanceof NumericTag ? DataResult.success(((NumericTag)var1).getAsNumber()) : DataResult.error("Not a number");
   }

   public Tag createNumeric(Number var1) {
      return DoubleTag.valueOf(var1.doubleValue());
   }

   public Tag createByte(byte var1) {
      return ByteTag.valueOf(var1);
   }

   public Tag createShort(short var1) {
      return ShortTag.valueOf(var1);
   }

   public Tag createInt(int var1) {
      return IntTag.valueOf(var1);
   }

   public Tag createLong(long var1) {
      return LongTag.valueOf(var1);
   }

   public Tag createFloat(float var1) {
      return FloatTag.valueOf(var1);
   }

   public Tag createDouble(double var1) {
      return DoubleTag.valueOf(var1);
   }

   public Tag createBoolean(boolean var1) {
      return ByteTag.valueOf(var1);
   }

   public DataResult<String> getStringValue(Tag var1) {
      return var1 instanceof StringTag ? DataResult.success(var1.getAsString()) : DataResult.error("Not a string");
   }

   public Tag createString(String var1) {
      return StringTag.valueOf(var1);
   }

   private static CollectionTag<?> createGenericList(byte var0, byte var1) {
      if (typesMatch(var0, var1, (byte)4)) {
         return new LongArrayTag(new long[0]);
      } else if (typesMatch(var0, var1, (byte)1)) {
         return new ByteArrayTag(new byte[0]);
      } else {
         return (CollectionTag)(typesMatch(var0, var1, (byte)3) ? new IntArrayTag(new int[0]) : new ListTag());
      }
   }

   private static boolean typesMatch(byte var0, byte var1, byte var2) {
      return var0 == var2 && (var1 == var2 || var1 == 0);
   }

   private static <T extends Tag> void fillOne(CollectionTag<T> var0, Tag var1, Tag var2) {
      if (var1 instanceof CollectionTag) {
         CollectionTag var3 = (CollectionTag)var1;
         var3.forEach((var1x) -> {
            var0.add(var1x);
         });
      }

      var0.add(var2);
   }

   private static <T extends Tag> void fillMany(CollectionTag<T> var0, Tag var1, List<Tag> var2) {
      if (var1 instanceof CollectionTag) {
         CollectionTag var3 = (CollectionTag)var1;
         var3.forEach((var1x) -> {
            var0.add(var1x);
         });
      }

      var2.forEach((var1x) -> {
         var0.add(var1x);
      });
   }

   public DataResult<Tag> mergeToList(Tag var1, Tag var2) {
      if (!(var1 instanceof CollectionTag) && !(var1 instanceof EndTag)) {
         return DataResult.error("mergeToList called with not a list: " + var1, var1);
      } else {
         CollectionTag var3 = createGenericList(var1 instanceof CollectionTag ? ((CollectionTag)var1).getElementType() : 0, var2.getId());
         fillOne(var3, var1, var2);
         return DataResult.success(var3);
      }
   }

   public DataResult<Tag> mergeToList(Tag var1, List<Tag> var2) {
      if (!(var1 instanceof CollectionTag) && !(var1 instanceof EndTag)) {
         return DataResult.error("mergeToList called with not a list: " + var1, var1);
      } else {
         CollectionTag var3 = createGenericList(var1 instanceof CollectionTag ? ((CollectionTag)var1).getElementType() : 0, (Byte)var2.stream().findFirst().map(Tag::getId).orElse((byte)0));
         fillMany(var3, var1, var2);
         return DataResult.success(var3);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, Tag var2, Tag var3) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error("mergeToMap called with not a map: " + var1, var1);
      } else if (!(var2 instanceof StringTag)) {
         return DataResult.error("key is not a string: " + var2, var1);
      } else {
         CompoundTag var4 = new CompoundTag();
         if (var1 instanceof CompoundTag) {
            CompoundTag var5 = (CompoundTag)var1;
            var5.getAllKeys().forEach((var2x) -> {
               var4.put(var2x, var5.get(var2x));
            });
         }

         var4.put(var2.getAsString(), var3);
         return DataResult.success(var4);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, MapLike<Tag> var2) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error("mergeToMap called with not a map: " + var1, var1);
      } else {
         CompoundTag var3 = new CompoundTag();
         if (var1 instanceof CompoundTag) {
            CompoundTag var4 = (CompoundTag)var1;
            var4.getAllKeys().forEach((var2x) -> {
               var3.put(var2x, var4.get(var2x));
            });
         }

         ArrayList var5 = Lists.newArrayList();
         var2.entries().forEach((var2x) -> {
            Tag var3x = (Tag)var2x.getFirst();
            if (!(var3x instanceof StringTag)) {
               var5.add(var3x);
            } else {
               var3.put(var3x.getAsString(), (Tag)var2x.getSecond());
            }
         });
         return !var5.isEmpty() ? DataResult.error("some keys are not strings: " + var5, var3) : DataResult.success(var3);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag var1) {
      if (!(var1 instanceof CompoundTag)) {
         return DataResult.error("Not a map: " + var1);
      } else {
         CompoundTag var2 = (CompoundTag)var1;
         return DataResult.success(var2.getAllKeys().stream().map((var2x) -> {
            return Pair.of(this.createString(var2x), var2.get(var2x));
         }));
      }
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag var1) {
      if (!(var1 instanceof CompoundTag)) {
         return DataResult.error("Not a map: " + var1);
      } else {
         CompoundTag var2 = (CompoundTag)var1;
         return DataResult.success((var2x) -> {
            var2.getAllKeys().forEach((var3) -> {
               var2x.accept(this.createString(var3), var2.get(var3));
            });
         });
      }
   }

   public DataResult<MapLike<Tag>> getMap(Tag var1) {
      if (!(var1 instanceof CompoundTag)) {
         return DataResult.error("Not a map: " + var1);
      } else {
         final CompoundTag var2 = (CompoundTag)var1;
         return DataResult.success(new MapLike<Tag>() {
            @Nullable
            public Tag get(Tag var1) {
               return var2.get(var1.getAsString());
            }

            @Nullable
            public Tag get(String var1) {
               return var2.get(var1);
            }

            public Stream<Pair<Tag, Tag>> entries() {
               return var2.getAllKeys().stream().map((var2x) -> {
                  return Pair.of(NbtOps.this.createString(var2x), var2.get(var2x));
               });
            }

            public String toString() {
               return "MapLike[" + var2 + "]";
            }

            // $FF: synthetic method
            @Nullable
            public Object get(String var1) {
               return this.get(var1);
            }

            // $FF: synthetic method
            @Nullable
            public Object get(Object var1) {
               return this.get((Tag)var1);
            }
         });
      }
   }

   public Tag createMap(Stream<Pair<Tag, Tag>> var1) {
      CompoundTag var2 = new CompoundTag();
      var1.forEach((var1x) -> {
         var2.put(((Tag)var1x.getFirst()).getAsString(), (Tag)var1x.getSecond());
      });
      return var2;
   }

   public DataResult<Stream<Tag>> getStream(Tag var1) {
      return var1 instanceof CollectionTag ? DataResult.success(((CollectionTag)var1).stream().map((var0) -> {
         return var0;
      })) : DataResult.error("Not a list");
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag var1) {
      if (var1 instanceof CollectionTag) {
         CollectionTag var2 = (CollectionTag)var1;
         var2.getClass();
         return DataResult.success(var2::forEach);
      } else {
         return DataResult.error("Not a list: " + var1);
      }
   }

   public DataResult<ByteBuffer> getByteBuffer(Tag var1) {
      return var1 instanceof ByteArrayTag ? DataResult.success(ByteBuffer.wrap(((ByteArrayTag)var1).getAsByteArray())) : super.getByteBuffer(var1);
   }

   public Tag createByteList(ByteBuffer var1) {
      return new ByteArrayTag(DataFixUtils.toArray(var1));
   }

   public DataResult<IntStream> getIntStream(Tag var1) {
      return var1 instanceof IntArrayTag ? DataResult.success(Arrays.stream(((IntArrayTag)var1).getAsIntArray())) : super.getIntStream(var1);
   }

   public Tag createIntList(IntStream var1) {
      return new IntArrayTag(var1.toArray());
   }

   public DataResult<LongStream> getLongStream(Tag var1) {
      return var1 instanceof LongArrayTag ? DataResult.success(Arrays.stream(((LongArrayTag)var1).getAsLongArray())) : super.getLongStream(var1);
   }

   public Tag createLongList(LongStream var1) {
      return new LongArrayTag(var1.toArray());
   }

   public Tag createList(Stream<Tag> var1) {
      PeekingIterator var2 = Iterators.peekingIterator(var1.iterator());
      if (!var2.hasNext()) {
         return new ListTag();
      } else {
         Tag var3 = (Tag)var2.peek();
         ArrayList var6;
         if (var3 instanceof ByteTag) {
            var6 = Lists.newArrayList(Iterators.transform(var2, (var0) -> {
               return ((ByteTag)var0).getAsByte();
            }));
            return new ByteArrayTag(var6);
         } else if (var3 instanceof IntTag) {
            var6 = Lists.newArrayList(Iterators.transform(var2, (var0) -> {
               return ((IntTag)var0).getAsInt();
            }));
            return new IntArrayTag(var6);
         } else if (var3 instanceof LongTag) {
            var6 = Lists.newArrayList(Iterators.transform(var2, (var0) -> {
               return ((LongTag)var0).getAsLong();
            }));
            return new LongArrayTag(var6);
         } else {
            ListTag var4 = new ListTag();

            while(var2.hasNext()) {
               Tag var5 = (Tag)var2.next();
               if (!(var5 instanceof EndTag)) {
                  var4.add(var5);
               }
            }

            return var4;
         }
      }
   }

   public Tag remove(Tag var1, String var2) {
      if (var1 instanceof CompoundTag) {
         CompoundTag var3 = (CompoundTag)var1;
         CompoundTag var4 = new CompoundTag();
         var3.getAllKeys().stream().filter((var1x) -> {
            return !Objects.equals(var1x, var2);
         }).forEach((var2x) -> {
            var4.put(var2x, var3.get(var2x));
         });
         return var4;
      } else {
         return var1;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder<Tag> mapBuilder() {
      return new NbtOps.NbtRecordBuilder();
   }

   // $FF: synthetic method
   public Object remove(Object var1, String var2) {
      return this.remove((Tag)var1, var2);
   }

   // $FF: synthetic method
   public Object createLongList(LongStream var1) {
      return this.createLongList(var1);
   }

   // $FF: synthetic method
   public DataResult getLongStream(Object var1) {
      return this.getLongStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createIntList(IntStream var1) {
      return this.createIntList(var1);
   }

   // $FF: synthetic method
   public DataResult getIntStream(Object var1) {
      return this.getIntStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createByteList(ByteBuffer var1) {
      return this.createByteList(var1);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(Object var1) {
      return this.getByteBuffer((Tag)var1);
   }

   // $FF: synthetic method
   public Object createList(Stream var1) {
      return this.createList(var1);
   }

   // $FF: synthetic method
   public DataResult getList(Object var1) {
      return this.getList((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult getStream(Object var1) {
      return this.getStream((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult getMap(Object var1) {
      return this.getMap((Tag)var1);
   }

   // $FF: synthetic method
   public Object createMap(Stream var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(Object var1) {
      return this.getMapEntries((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult getMapValues(Object var1) {
      return this.getMapValues((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(Object var1, MapLike var2) {
      return this.mergeToMap((Tag)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(Object var1, Object var2, Object var3) {
      return this.mergeToMap((Tag)var1, (Tag)var2, (Tag)var3);
   }

   // $FF: synthetic method
   public DataResult mergeToList(Object var1, List var2) {
      return this.mergeToList((Tag)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToList(Object var1, Object var2) {
      return this.mergeToList((Tag)var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object createString(String var1) {
      return this.createString(var1);
   }

   // $FF: synthetic method
   public DataResult getStringValue(Object var1) {
      return this.getStringValue((Tag)var1);
   }

   // $FF: synthetic method
   public Object createBoolean(boolean var1) {
      return this.createBoolean(var1);
   }

   // $FF: synthetic method
   public Object createDouble(double var1) {
      return this.createDouble(var1);
   }

   // $FF: synthetic method
   public Object createFloat(float var1) {
      return this.createFloat(var1);
   }

   // $FF: synthetic method
   public Object createLong(long var1) {
      return this.createLong(var1);
   }

   // $FF: synthetic method
   public Object createInt(int var1) {
      return this.createInt(var1);
   }

   // $FF: synthetic method
   public Object createShort(short var1) {
      return this.createShort(var1);
   }

   // $FF: synthetic method
   public Object createByte(byte var1) {
      return this.createByte(var1);
   }

   // $FF: synthetic method
   public Object createNumeric(Number var1) {
      return this.createNumeric(var1);
   }

   // $FF: synthetic method
   public DataResult getNumberValue(Object var1) {
      return this.getNumberValue((Tag)var1);
   }

   // $FF: synthetic method
   public Object convertTo(DynamicOps var1, Object var2) {
      return this.convertTo(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }

   class NbtRecordBuilder extends AbstractStringBuilder<Tag, CompoundTag> {
      protected NbtRecordBuilder() {
         super(NbtOps.this);
      }

      protected CompoundTag initBuilder() {
         return new CompoundTag();
      }

      protected CompoundTag append(String var1, Tag var2, CompoundTag var3) {
         var3.put(var1, var2);
         return var3;
      }

      protected DataResult<Tag> build(CompoundTag var1, Tag var2) {
         if (var2 != null && var2 != EndTag.INSTANCE) {
            if (!(var2 instanceof CompoundTag)) {
               return DataResult.error("mergeToMap called with not a map: " + var2, var2);
            } else {
               CompoundTag var3 = new CompoundTag(Maps.newHashMap(((CompoundTag)var2).entries()));
               Iterator var4 = var1.entries().entrySet().iterator();

               while(var4.hasNext()) {
                  Entry var5 = (Entry)var4.next();
                  var3.put((String)var5.getKey(), (Tag)var5.getValue());
               }

               return DataResult.success(var3);
            }
         } else {
            return DataResult.success(var1);
         }
      }

      // $FF: synthetic method
      protected Object append(String var1, Object var2, Object var3) {
         return this.append(var1, (Tag)var2, (CompoundTag)var3);
      }

      // $FF: synthetic method
      protected DataResult build(Object var1, Object var2) {
         return this.build((CompoundTag)var1, (Tag)var2);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }
}
