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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.jspecify.annotations.Nullable;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();

   private NbtOps() {
      super();
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public <U> U convertTo(DynamicOps<U> var1, Tag var2) {
      Objects.requireNonNull(var2);
      byte var4 = 0;
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
      switch (var2.typeSwitch<invokedynamic>(var2, var4)) {
         case 0:
            EndTag var5 = (EndTag)var2;
            var10000 = (StringTag)(var1.empty());
            break;
         case 1:
            ByteTag var6 = (ByteTag)var2;
            ByteTag var53 = var6;

            try {
               var54 = var53.value();
            } catch (Throwable var33) {
               throw new MatchException(var33.toString(), var33);
            }

            byte var34 = var54;
            var10000 = (StringTag)(var1.createByte(var34));
            break;
         case 2:
            ShortTag var8 = (ShortTag)var2;
            ShortTag var51 = var8;

            try {
               var52 = var51.value();
            } catch (Throwable var32) {
               throw new MatchException(var32.toString(), var32);
            }

            short var35 = var52;
            var10000 = (StringTag)(var1.createShort(var35));
            break;
         case 3:
            IntTag var10 = (IntTag)var2;
            IntTag var49 = var10;

            try {
               var50 = var49.value();
            } catch (Throwable var31) {
               throw new MatchException(var31.toString(), var31);
            }

            int var36 = var50;
            var10000 = (StringTag)(var1.createInt(var36));
            break;
         case 4:
            LongTag var12 = (LongTag)var2;
            LongTag var47 = var12;

            try {
               var48 = var47.value();
            } catch (Throwable var30) {
               throw new MatchException(var30.toString(), var30);
            }

            long var37 = var48;
            var10000 = (StringTag)(var1.createLong(var37));
            break;
         case 5:
            FloatTag var15 = (FloatTag)var2;
            FloatTag var45 = var15;

            try {
               var46 = var45.value();
            } catch (Throwable var29) {
               throw new MatchException(var29.toString(), var29);
            }

            float var38 = var46;
            var10000 = (StringTag)(var1.createFloat(var38));
            break;
         case 6:
            DoubleTag var17 = (DoubleTag)var2;
            DoubleTag var43 = var17;

            try {
               var44 = var43.value();
            } catch (Throwable var28) {
               throw new MatchException(var28.toString(), var28);
            }

            double var39 = var44;
            var10000 = (StringTag)(var1.createDouble(var39));
            break;
         case 7:
            ByteArrayTag var20 = (ByteArrayTag)var2;
            var10000 = (StringTag)(var1.createByteList(ByteBuffer.wrap(var20.getAsByteArray())));
            break;
         case 8:
            StringTag var21 = (StringTag)var2;
            var10000 = var21;

            try {
               var42 = var10000.value();
            } catch (Throwable var27) {
               throw new MatchException(var27.toString(), var27);
            }

            String var40 = var42;
            var10000 = (StringTag)(var1.createString(var40));
            break;
         case 9:
            ListTag var23 = (ListTag)var2;
            var10000 = (StringTag)(this.convertList(var1, var23));
            break;
         case 10:
            CompoundTag var24 = (CompoundTag)var2;
            var10000 = (StringTag)(this.convertMap(var1, var24));
            break;
         case 11:
            IntArrayTag var25 = (IntArrayTag)var2;
            var10000 = (StringTag)(var1.createIntList(Arrays.stream(var25.getAsIntArray())));
            break;
         case 12:
            LongArrayTag var26 = (LongArrayTag)var2;
            var10000 = (StringTag)(var1.createLongList(Arrays.stream(var26.getAsLongArray())));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (U)var10000;
   }

   public DataResult<Number> getNumberValue(Tag var1) {
      return (DataResult)var1.asNumber().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Not a number"));
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
         StringTag var10000 = var2;

         try {
            var6 = var10000.value();
         } catch (Throwable var5) {
            throw new MatchException(var5.toString(), var5);
         }

         String var4 = var6;
         return DataResult.success(var4);
      } else {
         return DataResult.error(() -> "Not a string");
      }
   }

   public Tag createString(String var1) {
      return StringTag.valueOf(var1);
   }

   public DataResult<Tag> mergeToList(Tag var1, Tag var2) {
      return (DataResult)createCollector(var1).map((var1x) -> DataResult.success(var1x.accept(var2).result())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + String.valueOf(var1), var1));
   }

   public DataResult<Tag> mergeToList(Tag var1, List<Tag> var2) {
      return (DataResult)createCollector(var1).map((var1x) -> DataResult.success(var1x.acceptAll(var2).result())).orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + String.valueOf(var1), var1));
   }

   public DataResult<Tag> mergeToMap(Tag var1, Tag var2, Tag var3) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(var1), var1);
      } else if (var2 instanceof StringTag) {
         StringTag var5 = (StringTag)var2;
         StringTag var10000 = var5;

         try {
            var10 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String var6 = var10;
         String var4 = var6;
         CompoundTag var11;
         if (var1 instanceof CompoundTag) {
            CompoundTag var9 = (CompoundTag)var1;
            var11 = var9.shallowCopy();
         } else {
            var11 = new CompoundTag();
         }

         CompoundTag var8 = var11;
         var8.put(var4, var3);
         return DataResult.success(var8);
      } else {
         return DataResult.error(() -> "key is not a string: " + String.valueOf(var2), var1);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, MapLike<Tag> var2) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(var1), var1);
      } else {
         CompoundTag var10000;
         if (var1 instanceof CompoundTag) {
            CompoundTag var4 = (CompoundTag)var1;
            var10000 = var4.shallowCopy();
         } else {
            var10000 = new CompoundTag();
         }

         CompoundTag var3 = var10000;
         ArrayList var5 = new ArrayList();
         var2.entries().forEach((var2x) -> {
            Tag var3x = (Tag)var2x.getFirst();
            if (var3x instanceof StringTag var5x) {
               StringTag var10000 = var5x;

               try {
                  var8 = var10000.value();
               } catch (Throwable var7) {
                  throw new MatchException(var7.toString(), var7);
               }

               String var6 = var8;
               var3.put(var6, (Tag)var2x.getSecond());
            } else {
               var5.add(var3x);
            }
         });
         return !var5.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + String.valueOf(var5), var3) : DataResult.success(var3);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, Map<Tag, Tag> var2) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(var1), var1);
      } else {
         CompoundTag var10000;
         if (var1 instanceof CompoundTag) {
            CompoundTag var4 = (CompoundTag)var1;
            var10000 = var4.shallowCopy();
         } else {
            var10000 = new CompoundTag();
         }

         CompoundTag var3 = var10000;
         ArrayList var12 = new ArrayList();

         for(Map.Entry var6 : var2.entrySet()) {
            Tag var7 = (Tag)var6.getKey();
            if (var7 instanceof StringTag) {
               StringTag var8 = (StringTag)var7;
               StringTag var13 = var8;

               try {
                  var14 = var13.value();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               String var10 = var14;
               var3.put(var10, (Tag)var6.getValue());
            } else {
               var12.add(var7);
            }
         }

         if (!var12.isEmpty()) {
            return DataResult.error(() -> "some keys are not strings: " + String.valueOf(var12), var3);
         } else {
            return DataResult.success(var3);
         }
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag var1) {
      if (var1 instanceof CompoundTag var2) {
         return DataResult.success(var2.entrySet().stream().map((var1x) -> Pair.of(this.createString((String)var1x.getKey()), (Tag)var1x.getValue())));
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(var1));
      }
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag var1) {
      if (var1 instanceof CompoundTag var2) {
         return DataResult.success((Consumer)(var2x) -> {
            for(Map.Entry var4 : var2.entrySet()) {
               var2x.accept(this.createString((String)var4.getKey()), (Tag)var4.getValue());
            }

         });
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(var1));
      }
   }

   public DataResult<MapLike<Tag>> getMap(Tag var1) {
      if (var1 instanceof final CompoundTag var2) {
         return DataResult.success(new MapLike<Tag>() {
            public @Nullable Tag get(Tag var1) {
               if (var1 instanceof StringTag var2x) {
                  StringTag var10000 = var2x;

                  try {
                     var6 = var10000.value();
                  } catch (Throwable var5) {
                     throw new MatchException(var5.toString(), var5);
                  }

                  String var4 = var6;
                  return var2.get(var4);
               } else {
                  throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + String.valueOf(var1));
               }
            }

            public @Nullable Tag get(String var1) {
               return var2.get(var1);
            }

            public Stream<Pair<Tag, Tag>> entries() {
               return var2.entrySet().stream().map((var1) -> Pair.of(NbtOps.this.createString((String)var1.getKey()), (Tag)var1.getValue()));
            }

            public String toString() {
               return "MapLike[" + String.valueOf(var2) + "]";
            }

            // $FF: synthetic method
            public @Nullable Object get(final String var1) {
               return this.get(var1);
            }

            // $FF: synthetic method
            public @Nullable Object get(final Object var1) {
               return this.get((Tag)var1);
            }
         });
      } else {
         return DataResult.error(() -> "Not a map: " + String.valueOf(var1));
      }
   }

   public Tag createMap(Stream<Pair<Tag, Tag>> var1) {
      CompoundTag var2 = new CompoundTag();
      var1.forEach((var1x) -> {
         Tag var2x = (Tag)var1x.getFirst();
         Tag var3 = (Tag)var1x.getSecond();
         if (var2x instanceof StringTag var5) {
            StringTag var10000 = var5;

            try {
               var8 = var10000.value();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            String var6 = var8;
            var2.put(var6, var3);
         } else {
            throw new UnsupportedOperationException("Cannot create map with non-string key: " + String.valueOf(var2x));
         }
      });
      return var2;
   }

   public DataResult<Stream<Tag>> getStream(Tag var1) {
      if (var1 instanceof CollectionTag var2) {
         return DataResult.success(var2.stream());
      } else {
         return DataResult.error(() -> "Not a list");
      }
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag var1) {
      if (var1 instanceof CollectionTag var2) {
         Objects.requireNonNull(var2);
         return DataResult.success(var2::forEach);
      } else {
         return DataResult.error(() -> "Not a list: " + String.valueOf(var1));
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
      return new ListTag((List)var1.collect(Util.toMutableList()));
   }

   public Tag remove(Tag var1, String var2) {
      if (var1 instanceof CompoundTag var3) {
         CompoundTag var4 = var3.shallowCopy();
         var4.remove(var2);
         return var4;
      } else {
         return var1;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder<Tag> mapBuilder() {
      return new NbtRecordBuilder();
   }

   private static Optional<ListCollector> createCollector(Tag var0) {
      if (var0 instanceof EndTag) {
         return Optional.of(new GenericListCollector());
      } else if (var0 instanceof CollectionTag) {
         CollectionTag var1 = (CollectionTag)var0;
         if (var1.isEmpty()) {
            return Optional.of(new GenericListCollector());
         } else {
            Objects.requireNonNull(var1);
            byte var3 = 0;
            Optional var10000;
            //$FF: var3->value
            //0->net/minecraft/nbt/ListTag
            //1->net/minecraft/nbt/ByteArrayTag
            //2->net/minecraft/nbt/IntArrayTag
            //3->net/minecraft/nbt/LongArrayTag
            switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
               case 0:
                  ListTag var4 = (ListTag)var1;
                  var10000 = Optional.of(new GenericListCollector(var4));
                  break;
               case 1:
                  ByteArrayTag var5 = (ByteArrayTag)var1;
                  var10000 = Optional.of(new ByteListCollector(var5.getAsByteArray()));
                  break;
               case 2:
                  IntArrayTag var6 = (IntArrayTag)var1;
                  var10000 = Optional.of(new IntListCollector(var6.getAsIntArray()));
                  break;
               case 3:
                  LongArrayTag var7 = (LongArrayTag)var1;
                  var10000 = Optional.of(new LongListCollector(var7.getAsLongArray()));
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

   // $FF: synthetic method
   public Object remove(final Object var1, final String var2) {
      return this.remove((Tag)var1, var2);
   }

   // $FF: synthetic method
   public Object createLongList(final LongStream var1) {
      return this.createLongList(var1);
   }

   // $FF: synthetic method
   public DataResult getLongStream(final Object var1) {
      return this.getLongStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createIntList(final IntStream var1) {
      return this.createIntList(var1);
   }

   // $FF: synthetic method
   public DataResult getIntStream(final Object var1) {
      return this.getIntStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createByteList(final ByteBuffer var1) {
      return this.createByteList(var1);
   }

   // $FF: synthetic method
   public DataResult getByteBuffer(final Object var1) {
      return this.getByteBuffer((Tag)var1);
   }

   // $FF: synthetic method
   public Object createList(final Stream var1) {
      return this.createList(var1);
   }

   // $FF: synthetic method
   public DataResult getList(final Object var1) {
      return this.getList((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult getStream(final Object var1) {
      return this.getStream((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult getMap(final Object var1) {
      return this.getMap((Tag)var1);
   }

   // $FF: synthetic method
   public Object createMap(final Stream var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public DataResult getMapEntries(final Object var1) {
      return this.getMapEntries((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult getMapValues(final Object var1) {
      return this.getMapValues((Tag)var1);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final MapLike var2) {
      return this.mergeToMap((Tag)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final Map var2) {
      return this.mergeToMap((Tag)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToMap(final Object var1, final Object var2, final Object var3) {
      return this.mergeToMap((Tag)var1, (Tag)var2, (Tag)var3);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object var1, final List var2) {
      return this.mergeToList((Tag)var1, var2);
   }

   // $FF: synthetic method
   public DataResult mergeToList(final Object var1, final Object var2) {
      return this.mergeToList((Tag)var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object createString(final String var1) {
      return this.createString(var1);
   }

   // $FF: synthetic method
   public DataResult getStringValue(final Object var1) {
      return this.getStringValue((Tag)var1);
   }

   // $FF: synthetic method
   public Object createBoolean(final boolean var1) {
      return this.createBoolean(var1);
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
      return this.getNumberValue((Tag)var1);
   }

   // $FF: synthetic method
   public Object convertTo(final DynamicOps var1, final Object var2) {
      return this.convertTo(var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }

   class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
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
               return DataResult.error(() -> "mergeToMap called with not a map: " + String.valueOf(var2), var2);
            } else {
               CompoundTag var3 = (CompoundTag)var2;
               CompoundTag var4 = var3.shallowCopy();

               for(Map.Entry var6 : var1.entrySet()) {
                  var4.put((String)var6.getKey(), (Tag)var6.getValue());
               }

               return DataResult.success(var4);
            }
         } else {
            return DataResult.success(var1);
         }
      }

      // $FF: synthetic method
      protected Object append(final String var1, final Object var2, final Object var3) {
         return this.append(var1, (Tag)var2, (CompoundTag)var3);
      }

      // $FF: synthetic method
      protected DataResult build(final Object var1, final Object var2) {
         return this.build((CompoundTag)var1, (Tag)var2);
      }

      // $FF: synthetic method
      protected Object initBuilder() {
         return this.initBuilder();
      }
   }

   interface ListCollector {
      ListCollector accept(Tag var1);

      default ListCollector acceptAll(Iterable<Tag> var1) {
         ListCollector var2 = this;

         for(Tag var4 : var1) {
            var2 = var2.accept(var4);
         }

         return var2;
      }

      default ListCollector acceptAll(Stream<Tag> var1) {
         Objects.requireNonNull(var1);
         return this.acceptAll(var1::iterator);
      }

      Tag result();
   }

   static class GenericListCollector implements ListCollector {
      private final ListTag result = new ListTag();

      GenericListCollector() {
         super();
      }

      GenericListCollector(ListTag var1) {
         super();
         this.result.addAll(var1);
      }

      public GenericListCollector(IntArrayList var1) {
         super();
         var1.forEach((var1x) -> this.result.add(IntTag.valueOf(var1x)));
      }

      public GenericListCollector(ByteArrayList var1) {
         super();
         var1.forEach((var1x) -> this.result.add(ByteTag.valueOf(var1x)));
      }

      public GenericListCollector(LongArrayList var1) {
         super();
         var1.forEach((var1x) -> this.result.add(LongTag.valueOf(var1x)));
      }

      public ListCollector accept(Tag var1) {
         this.result.add(var1);
         return this;
      }

      public Tag result() {
         return this.result;
      }
   }

   static class IntListCollector implements ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(int[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof IntTag var2) {
            this.values.add(var2.intValue());
            return this;
         } else {
            return (new GenericListCollector(this.values)).accept(var1);
         }
      }

      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   static class ByteListCollector implements ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(byte[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof ByteTag var2) {
            this.values.add(var2.byteValue());
            return this;
         } else {
            return (new GenericListCollector(this.values)).accept(var1);
         }
      }

      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   static class LongListCollector implements ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(long[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      public ListCollector accept(Tag var1) {
         if (var1 instanceof LongTag var2) {
            this.values.add(var2.longValue());
            return this;
         } else {
            return (new GenericListCollector(this.values)).accept(var1);
         }
      }

      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
   }
}
