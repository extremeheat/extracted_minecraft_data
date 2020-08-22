package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class NbtOps implements DynamicOps {
   public static final NbtOps INSTANCE = new NbtOps();

   protected NbtOps() {
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public Type getType(Tag var1) {
      switch(var1.getId()) {
      case 0:
         return DSL.nilType();
      case 1:
         return DSL.byteType();
      case 2:
         return DSL.shortType();
      case 3:
         return DSL.intType();
      case 4:
         return DSL.longType();
      case 5:
         return DSL.floatType();
      case 6:
         return DSL.doubleType();
      case 7:
         return DSL.list(DSL.byteType());
      case 8:
         return DSL.string();
      case 9:
         return DSL.list(DSL.remainderType());
      case 10:
         return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
      case 11:
         return DSL.list(DSL.intType());
      case 12:
         return DSL.list(DSL.longType());
      default:
         return DSL.remainderType();
      }
   }

   public Optional getNumberValue(Tag var1) {
      return var1 instanceof NumericTag ? Optional.of(((NumericTag)var1).getAsNumber()) : Optional.empty();
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

   public Optional getStringValue(Tag var1) {
      return var1 instanceof StringTag ? Optional.of(var1.getAsString()) : Optional.empty();
   }

   public Tag createString(String var1) {
      return StringTag.valueOf(var1);
   }

   public Tag mergeInto(Tag var1, Tag var2) {
      if (var2 instanceof EndTag) {
         return var1;
      } else if (!(var1 instanceof CompoundTag)) {
         if (var1 instanceof EndTag) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
         } else if (var1 instanceof CollectionTag) {
            ListTag var3 = new ListTag();
            CollectionTag var9 = (CollectionTag)var1;
            var3.addAll(var9);
            var3.add(var2);
            return var3;
         } else {
            return var1;
         }
      } else if (!(var2 instanceof CompoundTag)) {
         return var1;
      } else {
         CompoundTag var4 = new CompoundTag();
         CompoundTag var5 = (CompoundTag)var1;
         Iterator var6 = var5.getAllKeys().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var4.put(var7, var5.get(var7));
         }

         CompoundTag var10 = (CompoundTag)var2;
         Iterator var11 = var10.getAllKeys().iterator();

         while(var11.hasNext()) {
            String var8 = (String)var11.next();
            var4.put(var8, var10.get(var8));
         }

         return var4;
      }
   }

   public Tag mergeInto(Tag var1, Tag var2, Tag var3) {
      CompoundTag var4;
      if (var1 instanceof EndTag) {
         var4 = new CompoundTag();
      } else {
         if (!(var1 instanceof CompoundTag)) {
            return var1;
         }

         CompoundTag var5 = (CompoundTag)var1;
         var4 = new CompoundTag();
         var5.getAllKeys().forEach((var2x) -> {
            var4.put(var2x, var5.get(var2x));
         });
      }

      var4.put(var2.getAsString(), var3);
      return var4;
   }

   public Tag merge(Tag var1, Tag var2) {
      if (var1 instanceof EndTag) {
         return var2;
      } else if (var2 instanceof EndTag) {
         return var1;
      } else {
         if (var1 instanceof CompoundTag && var2 instanceof CompoundTag) {
            CompoundTag var3 = (CompoundTag)var1;
            CompoundTag var4 = (CompoundTag)var2;
            CompoundTag var5 = new CompoundTag();
            var3.getAllKeys().forEach((var2x) -> {
               var5.put(var2x, var3.get(var2x));
            });
            var4.getAllKeys().forEach((var2x) -> {
               var5.put(var2x, var4.get(var2x));
            });
         }

         if (var1 instanceof CollectionTag && var2 instanceof CollectionTag) {
            ListTag var6 = new ListTag();
            var6.addAll((CollectionTag)var1);
            var6.addAll((CollectionTag)var2);
            return var6;
         } else {
            throw new IllegalArgumentException("Could not merge " + var1 + " and " + var2);
         }
      }
   }

   public Optional getMapValues(Tag var1) {
      if (var1 instanceof CompoundTag) {
         CompoundTag var2 = (CompoundTag)var1;
         return Optional.of(var2.getAllKeys().stream().map((var2x) -> {
            return Pair.of(this.createString(var2x), var2.get(var2x));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      } else {
         return Optional.empty();
      }
   }

   public Tag createMap(Map var1) {
      CompoundTag var2 = new CompoundTag();
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.put(((Tag)var4.getKey()).getAsString(), (Tag)var4.getValue());
      }

      return var2;
   }

   public Optional getStream(Tag var1) {
      return var1 instanceof CollectionTag ? Optional.of(((CollectionTag)var1).stream().map((var0) -> {
         return var0;
      })) : Optional.empty();
   }

   public Optional getByteBuffer(Tag var1) {
      return var1 instanceof ByteArrayTag ? Optional.of(ByteBuffer.wrap(((ByteArrayTag)var1).getAsByteArray())) : super.getByteBuffer(var1);
   }

   public Tag createByteList(ByteBuffer var1) {
      return new ByteArrayTag(DataFixUtils.toArray(var1));
   }

   public Optional getIntStream(Tag var1) {
      return var1 instanceof IntArrayTag ? Optional.of(Arrays.stream(((IntArrayTag)var1).getAsIntArray())) : super.getIntStream(var1);
   }

   public Tag createIntList(IntStream var1) {
      return new IntArrayTag(var1.toArray());
   }

   public Optional getLongStream(Tag var1) {
      return var1 instanceof LongArrayTag ? Optional.of(Arrays.stream(((LongArrayTag)var1).getAsLongArray())) : super.getLongStream(var1);
   }

   public Tag createLongList(LongStream var1) {
      return new LongArrayTag(var1.toArray());
   }

   public Tag createList(Stream var1) {
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

   // $FF: synthetic method
   public Object remove(Object var1, String var2) {
      return this.remove((Tag)var1, var2);
   }

   // $FF: synthetic method
   public Object createLongList(LongStream var1) {
      return this.createLongList(var1);
   }

   // $FF: synthetic method
   public Optional getLongStream(Object var1) {
      return this.getLongStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createIntList(IntStream var1) {
      return this.createIntList(var1);
   }

   // $FF: synthetic method
   public Optional getIntStream(Object var1) {
      return this.getIntStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createByteList(ByteBuffer var1) {
      return this.createByteList(var1);
   }

   // $FF: synthetic method
   public Optional getByteBuffer(Object var1) {
      return this.getByteBuffer((Tag)var1);
   }

   // $FF: synthetic method
   public Object createList(Stream var1) {
      return this.createList(var1);
   }

   // $FF: synthetic method
   public Optional getStream(Object var1) {
      return this.getStream((Tag)var1);
   }

   // $FF: synthetic method
   public Object createMap(Map var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public Optional getMapValues(Object var1) {
      return this.getMapValues((Tag)var1);
   }

   // $FF: synthetic method
   public Object merge(Object var1, Object var2) {
      return this.merge((Tag)var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object mergeInto(Object var1, Object var2, Object var3) {
      return this.mergeInto((Tag)var1, (Tag)var2, (Tag)var3);
   }

   // $FF: synthetic method
   public Object mergeInto(Object var1, Object var2) {
      return this.mergeInto((Tag)var1, (Tag)var2);
   }

   // $FF: synthetic method
   public Object createString(String var1) {
      return this.createString(var1);
   }

   // $FF: synthetic method
   public Optional getStringValue(Object var1) {
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
   public Optional getNumberValue(Object var1) {
      return this.getNumberValue((Tag)var1);
   }

   // $FF: synthetic method
   public Type getType(Object var1) {
      return this.getType((Tag)var1);
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }
}
