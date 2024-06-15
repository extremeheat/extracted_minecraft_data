package net.minecraft.nbt;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
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
      return (U)(switch (var2.getId()) {
         case 0 -> (Object)var1.empty();
         case 1 -> (Object)var1.createByte(((NumericTag)var2).getAsByte());
         case 2 -> (Object)var1.createShort(((NumericTag)var2).getAsShort());
         case 3 -> (Object)var1.createInt(((NumericTag)var2).getAsInt());
         case 4 -> (Object)var1.createLong(((NumericTag)var2).getAsLong());
         case 5 -> (Object)var1.createFloat(((NumericTag)var2).getAsFloat());
         case 6 -> (Object)var1.createDouble(((NumericTag)var2).getAsDouble());
         case 7 -> (Object)var1.createByteList(ByteBuffer.wrap(((ByteArrayTag)var2).getAsByteArray()));
         case 8 -> (Object)var1.createString(var2.getAsString());
         case 9 -> (Object)this.convertList(var1, var2);
         case 10 -> (Object)this.convertMap(var1, var2);
         case 11 -> (Object)var1.createIntList(Arrays.stream(((IntArrayTag)var2).getAsIntArray()));
         case 12 -> (Object)var1.createLongList(Arrays.stream(((LongArrayTag)var2).getAsLongArray()));
         default -> throw new IllegalStateException("Unknown tag type: " + var2);
      });
   }

   public DataResult<Number> getNumberValue(Tag var1) {
      return var1 instanceof NumericTag var2 ? DataResult.success(var2.getAsNumber()) : DataResult.error(() -> "Not a number");
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
      return var1 instanceof StringTag var2 ? DataResult.success(var2.getAsString()) : DataResult.error(() -> "Not a string");
   }

   public Tag createString(String var1) {
      return StringTag.valueOf(var1);
   }

   public DataResult<Tag> mergeToList(Tag var1, Tag var2) {
      return createCollector(var1)
         .map(var1x -> DataResult.success(var1x.accept(var2).result()))
         .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + var1, var1));
   }

   public DataResult<Tag> mergeToList(Tag var1, List<Tag> var2) {
      return createCollector(var1)
         .map(var1x -> DataResult.success(var1x.acceptAll(var2).result()))
         .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + var1, var1));
   }

   public DataResult<Tag> mergeToMap(Tag var1, Tag var2, Tag var3) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + var1, var1);
      } else if (!(var2 instanceof StringTag)) {
         return DataResult.error(() -> "key is not a string: " + var2, var1);
      } else {
         CompoundTag var4 = var1 instanceof CompoundTag var5 ? var5.shallowCopy() : new CompoundTag();
         var4.put(var2.getAsString(), var3);
         return DataResult.success(var4);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, MapLike<Tag> var2) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + var1, var1);
      } else {
         CompoundTag var3 = var1 instanceof CompoundTag var4 ? var4.shallowCopy() : new CompoundTag();
         ArrayList var5 = new ArrayList();
         var2.entries().forEach(var2x -> {
            Tag var3x = (Tag)var2x.getFirst();
            if (!(var3x instanceof StringTag)) {
               var5.add(var3x);
            } else {
               var3.put(var3x.getAsString(), (Tag)var2x.getSecond());
            }
         });
         return !var5.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + var5, var3) : DataResult.success(var3);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, Map<Tag, Tag> var2) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + var1, var1);
      } else {
         CompoundTag var3 = var1 instanceof CompoundTag var4 ? var4.shallowCopy() : new CompoundTag();
         ArrayList var8 = new ArrayList();

         for (Entry var6 : var2.entrySet()) {
            Tag var7 = (Tag)var6.getKey();
            if (var7 instanceof StringTag) {
               var3.put(var7.getAsString(), (Tag)var6.getValue());
            } else {
               var8.add(var7);
            }
         }

         return !var8.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + var8, var3) : DataResult.success(var3);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag var1) {
      return var1 instanceof CompoundTag var2
         ? DataResult.success(var2.entrySet().stream().map(var1x -> Pair.of(this.createString(var1x.getKey()), var1x.getValue())))
         : DataResult.error(() -> "Not a map: " + var1);
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag var1) {
      return var1 instanceof CompoundTag var2 ? DataResult.success((Consumer<BiConsumer>)var2x -> {
         for (Entry var4 : var2.entrySet()) {
            var2x.accept(this.createString((String)var4.getKey()), (Tag)var4.getValue());
         }
      }) : DataResult.error(() -> "Not a map: " + var1);
   }

   public DataResult<MapLike<Tag>> getMap(Tag var1) {
      return var1 instanceof CompoundTag var2 ? DataResult.success(new MapLike<Tag>() {
         @Nullable
         public Tag get(Tag var1) {
            return var2.get(var1.getAsString());
         }

         @Nullable
         public Tag get(String var1) {
            return var2.get(var1);
         }

         public Stream<Pair<Tag, Tag>> entries() {
            return var2.entrySet().stream().map(var1 -> Pair.of(NbtOps.this.createString(var1.getKey()), var1.getValue()));
         }

         @Override
         public String toString() {
            return "MapLike[" + var2 + "]";
         }
      }) : DataResult.error(() -> "Not a map: " + var1);
   }

   public Tag createMap(Stream<Pair<Tag, Tag>> var1) {
      CompoundTag var2 = new CompoundTag();
      var1.forEach(var1x -> var2.put(((Tag)var1x.getFirst()).getAsString(), (Tag)var1x.getSecond()));
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
         return var3.getElementType() == 10 ? DataResult.success(var3.stream().map(var0 -> tryUnwrap((CompoundTag)var0))) : DataResult.success(var3.stream());
      } else {
         return var1 instanceof CollectionTag var2 ? DataResult.success(var2.stream().map(var0 -> (Tag)var0)) : DataResult.error(() -> "Not a list");
      }
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag var1) {
      if (var1 instanceof ListTag var3) {
         return var3.getElementType() == 10 ? DataResult.success((Consumer<Consumer>)var1x -> {
            for (Tag var3x : var3) {
               var1x.accept(tryUnwrap((CompoundTag)var3x));
            }
         }) : DataResult.success(var3::forEach);
      } else {
         return var1 instanceof CollectionTag var2 ? DataResult.success(var2::forEach) : DataResult.error(() -> "Not a list: " + var1);
      }
   }

   public DataResult<ByteBuffer> getByteBuffer(Tag var1) {
      return var1 instanceof ByteArrayTag var2 ? DataResult.success(ByteBuffer.wrap(var2.getAsByteArray())) : super.getByteBuffer(var1);
   }

   public Tag createByteList(ByteBuffer var1) {
      ByteBuffer var2 = var1.duplicate().clear();
      byte[] var3 = new byte[var1.capacity()];
      var2.get(0, var3, 0, var3.length);
      return new ByteArrayTag(var3);
   }

   public DataResult<IntStream> getIntStream(Tag var1) {
      return var1 instanceof IntArrayTag var2 ? DataResult.success(Arrays.stream(var2.getAsIntArray())) : super.getIntStream(var1);
   }

   public Tag createIntList(IntStream var1) {
      return new IntArrayTag(var1.toArray());
   }

   public DataResult<LongStream> getLongStream(Tag var1) {
      return var1 instanceof LongArrayTag var2 ? DataResult.success(Arrays.stream(var2.getAsLongArray())) : super.getLongStream(var1);
   }

   public Tag createLongList(LongStream var1) {
      return new LongArrayTag(var1.toArray());
   }

   public Tag createList(Stream<Tag> var1) {
      return NbtOps.InitialListCollector.INSTANCE.acceptAll(var1).result();
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

   @Override
   public String toString() {
      return "NBT";
   }

   public RecordBuilder<Tag> mapBuilder() {
      return new NbtOps.NbtRecordBuilder();
   }

   private static Optional<NbtOps.ListCollector> createCollector(Tag var0) {
      if (var0 instanceof EndTag) {
         return Optional.of(NbtOps.InitialListCollector.INSTANCE);
      } else {
         if (var0 instanceof CollectionTag var1) {
            if (var1.isEmpty()) {
               return Optional.of(NbtOps.InitialListCollector.INSTANCE);
            }

            if (var1 instanceof ListTag var5) {
               return switch (var5.getElementType()) {
                  case 0 -> Optional.of(NbtOps.InitialListCollector.INSTANCE);
                  case 10 -> Optional.of(new NbtOps.HeterogenousListCollector(var5));
                  default -> Optional.of(new NbtOps.HomogenousListCollector(var5));
               };
            }

            if (var1 instanceof ByteArrayTag var4) {
               return Optional.of(new NbtOps.ByteListCollector(var4.getAsByteArray()));
            }

            if (var1 instanceof IntArrayTag var3) {
               return Optional.of(new NbtOps.IntListCollector(var3.getAsIntArray()));
            }

            if (var1 instanceof LongArrayTag var2) {
               return Optional.of(new NbtOps.LongListCollector(var2.getAsLongArray()));
            }
         }

         return Optional.empty();
      }
   }

   static class ByteListCollector implements NbtOps.ListCollector {
      private final ByteArrayList values = new ByteArrayList();

      public ByteListCollector(byte var1) {
         super();
         this.values.add(var1);
      }

      public ByteListCollector(byte[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         if (var1 instanceof ByteTag var2) {
            this.values.add(var2.getAsByte());
            return this;
         } else {
            return new NbtOps.HeterogenousListCollector(this.values).accept(var1);
         }
      }

      @Override
      public Tag result() {
         return new ByteArrayTag(this.values.toByteArray());
      }
   }

   static class HeterogenousListCollector implements NbtOps.ListCollector {
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
         var1.forEach(var1x -> this.result.add(wrapElement(IntTag.valueOf(var1x))));
      }

      public HeterogenousListCollector(ByteArrayList var1) {
         super();
         var1.forEach(var1x -> this.result.add(wrapElement(ByteTag.valueOf(var1x))));
      }

      public HeterogenousListCollector(LongArrayList var1) {
         super();
         var1.forEach(var1x -> this.result.add(wrapElement(LongTag.valueOf(var1x))));
      }

      private static boolean isWrapper(CompoundTag var0) {
         return var0.size() == 1 && var0.contains("");
      }

      private static Tag wrapIfNeeded(Tag var0) {
         if (var0 instanceof CompoundTag var1 && !isWrapper(var1)) {
            return var1;
         }

         return wrapElement(var0);
      }

      private static CompoundTag wrapElement(Tag var0) {
         CompoundTag var1 = new CompoundTag();
         var1.put("", var0);
         return var1;
      }

      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         this.result.add(wrapIfNeeded(var1));
         return this;
      }

      @Override
      public Tag result() {
         return this.result;
      }
   }

   static class HomogenousListCollector implements NbtOps.ListCollector {
      private final ListTag result = new ListTag();

      HomogenousListCollector(Tag var1) {
         super();
         this.result.add(var1);
      }

      HomogenousListCollector(ListTag var1) {
         super();
         this.result.addAll(var1);
      }

      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         if (var1.getId() != this.result.getElementType()) {
            return new NbtOps.HeterogenousListCollector().acceptAll(this.result).accept(var1);
         } else {
            this.result.add(var1);
            return this;
         }
      }

      @Override
      public Tag result() {
         return this.result;
      }
   }

   static class InitialListCollector implements NbtOps.ListCollector {
      public static final NbtOps.InitialListCollector INSTANCE = new NbtOps.InitialListCollector();

      private InitialListCollector() {
         super();
      }

      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         if (var1 instanceof CompoundTag var5) {
            return new NbtOps.HeterogenousListCollector().accept(var5);
         } else if (var1 instanceof ByteTag var4) {
            return new NbtOps.ByteListCollector(var4.getAsByte());
         } else if (var1 instanceof IntTag var3) {
            return new NbtOps.IntListCollector(var3.getAsInt());
         } else {
            return (NbtOps.ListCollector)(var1 instanceof LongTag var2
               ? new NbtOps.LongListCollector(var2.getAsLong())
               : new NbtOps.HomogenousListCollector(var1));
         }
      }

      @Override
      public Tag result() {
         return new ListTag();
      }
   }

   static class IntListCollector implements NbtOps.ListCollector {
      private final IntArrayList values = new IntArrayList();

      public IntListCollector(int var1) {
         super();
         this.values.add(var1);
      }

      public IntListCollector(int[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         if (var1 instanceof IntTag var2) {
            this.values.add(var2.getAsInt());
            return this;
         } else {
            return new NbtOps.HeterogenousListCollector(this.values).accept(var1);
         }
      }

      @Override
      public Tag result() {
         return new IntArrayTag(this.values.toIntArray());
      }
   }

   interface ListCollector {
      NbtOps.ListCollector accept(Tag var1);

      default NbtOps.ListCollector acceptAll(Iterable<Tag> var1) {
         NbtOps.ListCollector var2 = this;

         for (Tag var4 : var1) {
            var2 = var2.accept(var4);
         }

         return var2;
      }

      default NbtOps.ListCollector acceptAll(Stream<Tag> var1) {
         return this.acceptAll(var1::iterator);
      }

      Tag result();
   }

   static class LongListCollector implements NbtOps.ListCollector {
      private final LongArrayList values = new LongArrayList();

      public LongListCollector(long var1) {
         super();
         this.values.add(var1);
      }

      public LongListCollector(long[] var1) {
         super();
         this.values.addElements(0, var1);
      }

      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         if (var1 instanceof LongTag var2) {
            this.values.add(var2.getAsLong());
            return this;
         } else {
            return new NbtOps.HeterogenousListCollector(this.values).accept(var1);
         }
      }

      @Override
      public Tag result() {
         return new LongArrayTag(this.values.toLongArray());
      }
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
         if (var2 == null || var2 == EndTag.INSTANCE) {
            return DataResult.success(var1);
         } else if (!(var2 instanceof CompoundTag var3)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + var2, var2);
         } else {
            CompoundTag var4 = var3.shallowCopy();

            for (Entry var6 : var1.entrySet()) {
               var4.put((String)var6.getKey(), (Tag)var6.getValue());
            }

            return DataResult.success(var4);
         }
      }
   }
}
