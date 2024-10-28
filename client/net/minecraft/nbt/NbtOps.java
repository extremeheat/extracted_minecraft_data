package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Collection;
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
import javax.annotation.Nullable;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();
   private static final String WRAPPER_MARKER = "";

   protected NbtOps() {
      super();
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public <U> U convertTo(DynamicOps<U> var1, Tag var2) {
      switch (var2.getId()) {
         case 0 -> {
            return var1.empty();
         }
         case 1 -> {
            return var1.createByte(((NumericTag)var2).getAsByte());
         }
         case 2 -> {
            return var1.createShort(((NumericTag)var2).getAsShort());
         }
         case 3 -> {
            return var1.createInt(((NumericTag)var2).getAsInt());
         }
         case 4 -> {
            return var1.createLong(((NumericTag)var2).getAsLong());
         }
         case 5 -> {
            return var1.createFloat(((NumericTag)var2).getAsFloat());
         }
         case 6 -> {
            return var1.createDouble(((NumericTag)var2).getAsDouble());
         }
         case 7 -> {
            return var1.createByteList(ByteBuffer.wrap(((ByteArrayTag)var2).getAsByteArray()));
         }
         case 8 -> {
            return var1.createString(var2.getAsString());
         }
         case 9 -> {
            return this.convertList(var1, var2);
         }
         case 10 -> {
            return this.convertMap(var1, var2);
         }
         case 11 -> {
            return var1.createIntList(Arrays.stream(((IntArrayTag)var2).getAsIntArray()));
         }
         case 12 -> {
            return var1.createLongList(Arrays.stream(((LongArrayTag)var2).getAsLongArray()));
         }
         default -> throw new IllegalStateException("Unknown tag type: " + String.valueOf(var2));
      }
   }

   public DataResult<Number> getNumberValue(Tag var1) {
      if (var1 instanceof NumericTag var2) {
         return DataResult.success(var2.getAsNumber());
      } else {
         return DataResult.error(() -> {
            return "Not a number";
         });
      }
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
      if (var1 instanceof StringTag var2) {
         return DataResult.success(var2.getAsString());
      } else {
         return DataResult.error(() -> {
            return "Not a string";
         });
      }
   }

   public Tag createString(String var1) {
      return StringTag.valueOf(var1);
   }

   public DataResult<Tag> mergeToList(Tag var1, Tag var2) {
      return (DataResult)createCollector(var1).map((var1x) -> {
         return DataResult.success(var1x.accept(var2).result());
      }).orElseGet(() -> {
         return DataResult.error(() -> {
            return "mergeToList called with not a list: " + String.valueOf(var1);
         }, var1);
      });
   }

   public DataResult<Tag> mergeToList(Tag var1, List<Tag> var2) {
      return (DataResult)createCollector(var1).map((var1x) -> {
         return DataResult.success(var1x.acceptAll((Iterable)var2).result());
      }).orElseGet(() -> {
         return DataResult.error(() -> {
            return "mergeToList called with not a list: " + String.valueOf(var1);
         }, var1);
      });
   }

   public DataResult<Tag> mergeToMap(Tag var1, Tag var2, Tag var3) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + String.valueOf(var1);
         }, var1);
      } else if (!(var2 instanceof StringTag)) {
         return DataResult.error(() -> {
            return "key is not a string: " + String.valueOf(var2);
         }, var1);
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
         return DataResult.error(() -> {
            return "mergeToMap called with not a map: " + String.valueOf(var1);
         }, var1);
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
         return !var5.isEmpty() ? DataResult.error(() -> {
            return "some keys are not strings: " + String.valueOf(var5);
         }, var3) : DataResult.success(var3);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag var1) {
      if (var1 instanceof CompoundTag var2) {
         return DataResult.success(var2.getAllKeys().stream().map((var2x) -> {
            return Pair.of(this.createString(var2x), var2.get(var2x));
         }));
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + String.valueOf(var1);
         });
      }
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag var1) {
      if (var1 instanceof CompoundTag var2) {
         return DataResult.success((var2x) -> {
            var2.getAllKeys().forEach((var3) -> {
               var2x.accept(this.createString(var3), var2.get(var3));
            });
         });
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + String.valueOf(var1);
         });
      }
   }

   public DataResult<MapLike<Tag>> getMap(Tag var1) {
      if (var1 instanceof final CompoundTag var2) {
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
               return "MapLike[" + String.valueOf(var2) + "]";
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
      } else {
         return DataResult.error(() -> {
            return "Not a map: " + String.valueOf(var1);
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

   private static Tag tryUnwrap(CompoundTag var0) {
      if (var0.size() == 1) {
         Tag var1 = var0.get("");
         if (var1 != null) {
            return var1;
         }
      }

      return var0;
   }

   public DataResult<Stream<Tag>> getStream(Tag var1) {
      if (var1 instanceof ListTag var3) {
         return var3.getElementType() == 10 ? DataResult.success(var3.stream().map((var0) -> {
            return tryUnwrap((CompoundTag)var0);
         })) : DataResult.success(var3.stream());
      } else if (var1 instanceof CollectionTag var2) {
         return DataResult.success(var2.stream().map((var0) -> {
            return var0;
         }));
      } else {
         return DataResult.error(() -> {
            return "Not a list";
         });
      }
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag var1) {
      if (var1 instanceof ListTag var3) {
         if (var3.getElementType() == 10) {
            return DataResult.success((var1x) -> {
               var3.forEach((var1) -> {
                  var1x.accept(tryUnwrap((CompoundTag)var1));
               });
            });
         } else {
            Objects.requireNonNull(var3);
            return DataResult.success(var3::forEach);
         }
      } else if (var1 instanceof CollectionTag var2) {
         Objects.requireNonNull(var2);
         return DataResult.success(var2::forEach);
      } else {
         return DataResult.error(() -> {
            return "Not a list: " + String.valueOf(var1);
         });
      }
   }

   public DataResult<ByteBuffer> getByteBuffer(Tag var1) {
      if (var1 instanceof ByteArrayTag var2) {
         return DataResult.success(ByteBuffer.wrap(var2.getAsByteArray()));
      } else {
         return super.getByteBuffer(var1);
      }
   }

   public Tag createByteList(ByteBuffer var1) {
      ByteBuffer var2 = var1.duplicate().clear();
      byte[] var3 = new byte[var1.capacity()];
      var2.get(0, var3, 0, var3.length);
      return new ByteArrayTag(var3);
   }

   public DataResult<IntStream> getIntStream(Tag var1) {
      if (var1 instanceof IntArrayTag var2) {
         return DataResult.success(Arrays.stream(var2.getAsIntArray()));
      } else {
         return super.getIntStream(var1);
      }
   }

   public Tag createIntList(IntStream var1) {
      return new IntArrayTag(var1.toArray());
   }

   public DataResult<LongStream> getLongStream(Tag var1) {
      if (var1 instanceof LongArrayTag var2) {
         return DataResult.success(Arrays.stream(var2.getAsLongArray()));
      } else {
         return super.getLongStream(var1);
      }
   }

   public Tag createLongList(LongStream var1) {
      return new LongArrayTag(var1.toArray());
   }

   public Tag createList(Stream<Tag> var1) {
      return NbtOps.InitialListCollector.INSTANCE.acceptAll(var1).result();
   }

   public Tag remove(Tag var1, String var2) {
      if (var1 instanceof CompoundTag var3) {
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
      return new NbtRecordBuilder(this);
   }

   private static Optional<ListCollector> createCollector(Tag var0) {
      if (var0 instanceof EndTag) {
         return Optional.of(NbtOps.InitialListCollector.INSTANCE);
      } else {
         if (var0 instanceof CollectionTag) {
            CollectionTag var1 = (CollectionTag)var0;
            if (var1.isEmpty()) {
               return Optional.of(NbtOps.InitialListCollector.INSTANCE);
            }

            if (var1 instanceof ListTag) {
               ListTag var5 = (ListTag)var1;
               Optional var10000;
               switch (var5.getElementType()) {
                  case 0 -> var10000 = Optional.of(NbtOps.InitialListCollector.INSTANCE);
                  case 10 -> var10000 = Optional.of(new HeterogenousListCollector(var5));
                  default -> var10000 = Optional.of(new HomogenousListCollector(var5));
               }

               return var10000;
            }

            if (var1 instanceof ByteArrayTag) {
               ByteArrayTag var4 = (ByteArrayTag)var1;
               return Optional.of(new ByteListCollector(var4.getAsByteArray()));
            }

            if (var1 instanceof IntArrayTag) {
               IntArrayTag var3 = (IntArrayTag)var1;
               return Optional.of(new IntListCollector(var3.getAsIntArray()));
            }

            if (var1 instanceof LongArrayTag) {
               LongArrayTag var2 = (LongArrayTag)var1;
               return Optional.of(new LongListCollector(var2.getAsLongArray()));
            }
         }

         return Optional.empty();
      }
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

   private static class InitialListCollector implements ListCollector {
      public static final InitialListCollector INSTANCE = new InitialListCollector();

      private InitialListCollector() {
         super();
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof CompoundTag var5) {
            return (new HeterogenousListCollector()).accept(var5);
         } else if (var1 instanceof ByteTag var4) {
            return new ByteListCollector(var4.getAsByte());
         } else if (var1 instanceof IntTag var3) {
            return new IntListCollector(var3.getAsInt());
         } else if (var1 instanceof LongTag var2) {
            return new LongListCollector(var2.getAsLong());
         } else {
            return new HomogenousListCollector(var1);
         }
      }

      public Tag result() {
         return new ListTag();
      }
   }

   private interface ListCollector {
      ListCollector accept(Tag var1);

      default ListCollector acceptAll(Iterable<Tag> var1) {
         ListCollector var2 = this;

         Tag var4;
         for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 = var2.accept(var4)) {
            var4 = (Tag)var3.next();
         }

         return var2;
      }

      default ListCollector acceptAll(Stream<Tag> var1) {
         Objects.requireNonNull(var1);
         return this.acceptAll(var1::iterator);
      }

      Tag result();
   }

   class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
      protected NbtRecordBuilder(NbtOps var1) {
         super(var1);
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
               return DataResult.error(() -> {
                  return "mergeToMap called with not a map: " + String.valueOf(var2);
               }, var2);
            } else {
               CompoundTag var3 = (CompoundTag)var2;
               CompoundTag var4 = new CompoundTag(Maps.newHashMap(var3.entries()));
               Iterator var5 = var1.entries().entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var5.next();
                  var4.put((String)var6.getKey(), (Tag)var6.getValue());
               }

               return DataResult.success(var4);
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

   private static class HeterogenousListCollector implements ListCollector {
      private final ListTag result = new ListTag();

      public HeterogenousListCollector() {
         super();
      }

      public HeterogenousListCollector(Collection<Tag> var1) {
         super();
         this.result.addAll(var1);
      }

      public HeterogenousListCollector(IntArrayList var1) {
         super();
         var1.forEach((var1x) -> {
            this.result.add(wrapElement(IntTag.valueOf(var1x)));
         });
      }

      public HeterogenousListCollector(ByteArrayList var1) {
         super();
         var1.forEach((var1x) -> {
            this.result.add(wrapElement(ByteTag.valueOf(var1x)));
         });
      }

      public HeterogenousListCollector(LongArrayList var1) {
         super();
         var1.forEach((var1x) -> {
            this.result.add(wrapElement(LongTag.valueOf(var1x)));
         });
      }

      private static boolean isWrapper(CompoundTag var0) {
         return var0.size() == 1 && var0.contains("");
      }

      private static Tag wrapIfNeeded(Tag var0) {
         if (var0 instanceof CompoundTag var1) {
            if (!isWrapper(var1)) {
               return var1;
            }
         }

         return wrapElement(var0);
      }

      private static CompoundTag wrapElement(Tag var0) {
         CompoundTag var1 = new CompoundTag();
         var1.put("", var0);
         return var1;
      }

      public ListCollector accept(Tag var1) {
         this.result.add(wrapIfNeeded(var1));
         return this;
      }

      public Tag result() {
         return this.result;
      }
   }

   private static class HomogenousListCollector implements ListCollector {
      private final ListTag result = new ListTag();

      HomogenousListCollector(Tag var1) {
         super();
         this.result.add(var1);
      }

      HomogenousListCollector(ListTag var1) {
         super();
         this.result.addAll(var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1.getId() != this.result.getElementType()) {
            return (new HeterogenousListCollector()).acceptAll(this.result).accept(var1);
         } else {
            this.result.add(var1);
            return this;
         }
      }

      public Tag result() {
         return this.result;
      }
   }

   private static class ByteListCollector implements ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(byte var1) {
         super();
         this.values.add(var1);
      }

      public ByteListCollector(byte[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof ByteTag var2) {
            this.values.add(var2.getAsByte());
            return this;
         } else {
            return (new HeterogenousListCollector(this.values)).accept(var1);
         }
      }

      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   static class IntListCollector implements ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(int var1) {
         super();
         this.values.add(var1);
      }

      public IntListCollector(int[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof IntTag var2) {
            this.values.add(var2.getAsInt());
            return this;
         } else {
            return (new HeterogenousListCollector(this.values)).accept(var1);
         }
      }

      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   static class LongListCollector implements ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(long var1) {
         super();
         this.values.add(var1);
      }

      public LongListCollector(long[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof LongTag var2) {
            this.values.add(var2.getAsLong());
            return this;
         } else {
            return (new HeterogenousListCollector(this.values)).accept(var1);
         }
      }

      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
   }
}
