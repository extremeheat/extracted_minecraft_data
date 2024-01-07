package net.minecraft.nbt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.Objects;
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
      switch(var2.getId()) {
         case 0:
            return (U)var1.empty();
         case 1:
            return (U)var1.createByte(((NumericTag)var2).getAsByte());
         case 2:
            return (U)var1.createShort(((NumericTag)var2).getAsShort());
         case 3:
            return (U)var1.createInt(((NumericTag)var2).getAsInt());
         case 4:
            return (U)var1.createLong(((NumericTag)var2).getAsLong());
         case 5:
            return (U)var1.createFloat(((NumericTag)var2).getAsFloat());
         case 6:
            return (U)var1.createDouble(((NumericTag)var2).getAsDouble());
         case 7:
            return (U)var1.createByteList(ByteBuffer.wrap(((ByteArrayTag)var2).getAsByteArray()));
         case 8:
            return (U)var1.createString(var2.getAsString());
         case 9:
            return (U)this.convertList(var1, var2);
         case 10:
            return (U)this.convertMap(var1, var2);
         case 11:
            return (U)var1.createIntList(Arrays.stream(((IntArrayTag)var2).getAsIntArray()));
         case 12:
            return (U)var1.createLongList(Arrays.stream(((LongArrayTag)var2).getAsLongArray()));
         default:
            throw new IllegalStateException("Unknown tag type: " + var2);
      }
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
      return (DataResult<Tag>)createCollector(var1)
         .map(var1x -> DataResult.success(var1x.accept(var2).result()))
         .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + var1, var1));
   }

   public DataResult<Tag> mergeToList(Tag var1, List<Tag> var2) {
      return (DataResult<Tag>)createCollector(var1)
         .map(var1x -> DataResult.success(var1x.acceptAll(var2).result()))
         .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + var1, var1));
   }

   public DataResult<Tag> mergeToMap(Tag var1, Tag var2, Tag var3) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + var1, var1);
      } else if (!(var2 instanceof StringTag)) {
         return DataResult.error(() -> "key is not a string: " + var2, var1);
      } else {
         CompoundTag var4 = new CompoundTag();
         if (var1 instanceof CompoundTag var5) {
            ((CompoundTag)var5).getAllKeys().forEach(var2x -> var4.put(var2x, var5.get(var2x)));
         }

         var4.put(var2.getAsString(), var3);
         return DataResult.success(var4);
      }
   }

   public DataResult<Tag> mergeToMap(Tag var1, MapLike<Tag> var2) {
      if (!(var1 instanceof CompoundTag) && !(var1 instanceof EndTag)) {
         return DataResult.error(() -> "mergeToMap called with not a map: " + var1, var1);
      } else {
         CompoundTag var3 = new CompoundTag();
         if (var1 instanceof CompoundTag var4) {
            ((CompoundTag)var4).getAllKeys().forEach(var2x -> var3.put(var2x, var4.get(var2x)));
         }

         ArrayList var5 = Lists.newArrayList();
         var2.entries().forEach(var2x -> {
            Tag var3xx = (Tag)var2x.getFirst();
            if (!(var3xx instanceof StringTag)) {
               var5.add(var3xx);
            } else {
               var3.put(var3xx.getAsString(), (Tag)var2x.getSecond());
            }
         });
         return !var5.isEmpty() ? DataResult.error(() -> "some keys are not strings: " + var5, var3) : DataResult.success(var3);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag var1) {
      return var1 instanceof CompoundTag var2
         ? DataResult.success(var2.getAllKeys().stream().map(var2x -> Pair.of(this.createString(var2x), var2.get(var2x))))
         : DataResult.error(() -> "Not a map: " + var1);
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag var1) {
      return var1 instanceof CompoundTag var2
         ? DataResult.success((Consumer<BiConsumer>)var2x -> var2.getAllKeys().forEach(var3 -> var2x.accept(this.createString(var3), var2.get(var3))))
         : DataResult.error(() -> "Not a map: " + var1);
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
            return var2.getAllKeys().stream().map(var2xx -> Pair.of(NbtOps.this.createString(var2xx), var2.get(var2xx)));
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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public DataResult<Stream<Tag>> getStream(Tag var1) {
      if (var1 instanceof ListTag var3) {
         return var3.getElementType() == 10 ? DataResult.success(var3.stream().map(var0 -> tryUnwrap((CompoundTag)var0))) : DataResult.success(var3.stream());
      } else {
         return var1 instanceof CollectionTag var2 ? DataResult.success(var2.stream().map(var0 -> var0)) : DataResult.error(() -> "Not a list");
      }
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag var1) {
      if (var1 instanceof ListTag var3) {
         return ((ListTag)var3).getElementType() == 10
            ? DataResult.success((Consumer<Consumer>)var1x -> var3.forEach(var1xx -> var1x.accept(tryUnwrap((CompoundTag)var1xx))))
            : DataResult.success(var3::forEach);
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
         CompoundTag var4 = new CompoundTag();
         ((CompoundTag)var3).getAllKeys().stream().filter(var1x -> !Objects.equals(var1x, var2)).forEach(var2x -> var4.put(var2x, var3.get(var2x)));
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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static Optional<NbtOps.ListCollector> createCollector(Tag var0) {
      if (var0 instanceof EndTag) {
         return Optional.of(NbtOps.InitialListCollector.INSTANCE);
      } else {
         if (var0 instanceof CollectionTag var1) {
            if (var1.isEmpty()) {
               return Optional.of(NbtOps.InitialListCollector.INSTANCE);
            }

            if (var1 instanceof ListTag var5) {
               return switch(((ListTag)var5).getElementType()) {
                  case 0 -> Optional.of(NbtOps.InitialListCollector.INSTANCE);
                  case 10 -> Optional.of(new NbtOps.HeterogenousListCollector((Collection<Tag>)var5));
                  default -> Optional.of(new NbtOps.HomogenousListCollector((ListTag)var5));
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

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
         if (var0 instanceof CompoundTag var1 && !isWrapper((CompoundTag)var1)) {
            return (Tag)var1;
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

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Override
      public NbtOps.ListCollector accept(Tag var1) {
         if (var1 instanceof CompoundTag var5) {
            return new NbtOps.HeterogenousListCollector().accept((Tag)var5);
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

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
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

         for(Tag var4 : var1) {
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

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
         } else if (!(var2 instanceof CompoundTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + var2, var2);
         } else {
            CompoundTag var3 = (CompoundTag)var2;
            CompoundTag var4 = new CompoundTag(Maps.newHashMap(var3.entries()));

            for(Entry var6 : var1.entries().entrySet()) {
               var4.put((String)var6.getKey(), (Tag)var6.getValue());
            }

            return DataResult.success(var4);
         }
      }
   }
}
