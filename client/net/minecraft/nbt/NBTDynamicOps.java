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

public class NBTDynamicOps implements DynamicOps<INBTBase> {
   public static final NBTDynamicOps field_210820_a = new NBTDynamicOps();

   protected NBTDynamicOps() {
      super();
   }

   public INBTBase empty() {
      return new NBTTagEnd();
   }

   public Type<?> getType(INBTBase var1) {
      switch(var1.func_74732_a()) {
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

   public Optional<Number> getNumberValue(INBTBase var1) {
      return var1 instanceof NBTPrimitive ? Optional.of(((NBTPrimitive)var1).func_209908_j()) : Optional.empty();
   }

   public INBTBase createNumeric(Number var1) {
      return new NBTTagDouble(var1.doubleValue());
   }

   public INBTBase createByte(byte var1) {
      return new NBTTagByte(var1);
   }

   public INBTBase createShort(short var1) {
      return new NBTTagShort(var1);
   }

   public INBTBase createInt(int var1) {
      return new NBTTagInt(var1);
   }

   public INBTBase createLong(long var1) {
      return new NBTTagLong(var1);
   }

   public INBTBase createFloat(float var1) {
      return new NBTTagFloat(var1);
   }

   public INBTBase createDouble(double var1) {
      return new NBTTagDouble(var1);
   }

   public Optional<String> getStringValue(INBTBase var1) {
      return var1 instanceof NBTTagString ? Optional.of(var1.func_150285_a_()) : Optional.empty();
   }

   public INBTBase createString(String var1) {
      return new NBTTagString(var1);
   }

   public INBTBase mergeInto(INBTBase var1, INBTBase var2) {
      if (var2 instanceof NBTTagEnd) {
         return var1;
      } else if (!(var1 instanceof NBTTagCompound)) {
         if (var1 instanceof NBTTagEnd) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
         } else if (var1 instanceof NBTTagCollection) {
            NBTTagList var3 = new NBTTagList();
            NBTTagCollection var9 = (NBTTagCollection)var1;
            var3.addAll(var9);
            var3.add(var2);
            return var3;
         } else {
            return var1;
         }
      } else if (!(var2 instanceof NBTTagCompound)) {
         return var1;
      } else {
         NBTTagCompound var4 = new NBTTagCompound();
         NBTTagCompound var5 = (NBTTagCompound)var1;
         Iterator var6 = var5.func_150296_c().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var4.func_74782_a(var7, var5.func_74781_a(var7));
         }

         NBTTagCompound var10 = (NBTTagCompound)var2;
         Iterator var11 = var10.func_150296_c().iterator();

         while(var11.hasNext()) {
            String var8 = (String)var11.next();
            var4.func_74782_a(var8, var10.func_74781_a(var8));
         }

         return var4;
      }
   }

   public INBTBase mergeInto(INBTBase var1, INBTBase var2, INBTBase var3) {
      NBTTagCompound var4;
      if (var1 instanceof NBTTagEnd) {
         var4 = new NBTTagCompound();
      } else {
         if (!(var1 instanceof NBTTagCompound)) {
            return var1;
         }

         NBTTagCompound var5 = (NBTTagCompound)var1;
         var4 = new NBTTagCompound();
         var5.func_150296_c().forEach((var2x) -> {
            var4.func_74782_a(var2x, var5.func_74781_a(var2x));
         });
      }

      var4.func_74782_a(var2.func_150285_a_(), var3);
      return var4;
   }

   public INBTBase merge(INBTBase var1, INBTBase var2) {
      if (var1 instanceof NBTTagEnd) {
         return var2;
      } else if (var2 instanceof NBTTagEnd) {
         return var1;
      } else {
         if (var1 instanceof NBTTagCompound && var2 instanceof NBTTagCompound) {
            NBTTagCompound var3 = (NBTTagCompound)var1;
            NBTTagCompound var4 = (NBTTagCompound)var2;
            NBTTagCompound var5 = new NBTTagCompound();
            var3.func_150296_c().forEach((var2x) -> {
               var5.func_74782_a(var2x, var3.func_74781_a(var2x));
            });
            var4.func_150296_c().forEach((var2x) -> {
               var5.func_74782_a(var2x, var4.func_74781_a(var2x));
            });
         }

         if (var1 instanceof NBTTagCollection && var2 instanceof NBTTagCollection) {
            NBTTagList var6 = new NBTTagList();
            var6.addAll((NBTTagCollection)var1);
            var6.addAll((NBTTagCollection)var2);
            return var6;
         } else {
            throw new IllegalArgumentException("Could not merge " + var1 + " and " + var2);
         }
      }
   }

   public Optional<Map<INBTBase, INBTBase>> getMapValues(INBTBase var1) {
      if (var1 instanceof NBTTagCompound) {
         NBTTagCompound var2 = (NBTTagCompound)var1;
         return Optional.of(var2.func_150296_c().stream().map((var2x) -> {
            return Pair.of(this.createString(var2x), var2.func_74781_a(var2x));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      } else {
         return Optional.empty();
      }
   }

   public INBTBase createMap(Map<INBTBase, INBTBase> var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.func_74782_a(((INBTBase)var4.getKey()).func_150285_a_(), (INBTBase)var4.getValue());
      }

      return var2;
   }

   public Optional<Stream<INBTBase>> getStream(INBTBase var1) {
      return var1 instanceof NBTTagCollection ? Optional.of(((NBTTagCollection)var1).stream().map((var0) -> {
         return var0;
      })) : Optional.empty();
   }

   public Optional<ByteBuffer> getByteBuffer(INBTBase var1) {
      return var1 instanceof NBTTagByteArray ? Optional.of(ByteBuffer.wrap(((NBTTagByteArray)var1).func_150292_c())) : super.getByteBuffer(var1);
   }

   public INBTBase createByteList(ByteBuffer var1) {
      return new NBTTagByteArray(DataFixUtils.toArray(var1));
   }

   public Optional<IntStream> getIntStream(INBTBase var1) {
      return var1 instanceof NBTTagIntArray ? Optional.of(Arrays.stream(((NBTTagIntArray)var1).func_150302_c())) : super.getIntStream(var1);
   }

   public INBTBase createIntList(IntStream var1) {
      return new NBTTagIntArray(var1.toArray());
   }

   public Optional<LongStream> getLongStream(INBTBase var1) {
      return var1 instanceof NBTTagLongArray ? Optional.of(Arrays.stream(((NBTTagLongArray)var1).func_197652_h())) : super.getLongStream(var1);
   }

   public INBTBase createLongList(LongStream var1) {
      return new NBTTagLongArray(var1.toArray());
   }

   public INBTBase createList(Stream<INBTBase> var1) {
      PeekingIterator var2 = Iterators.peekingIterator(var1.iterator());
      if (!var2.hasNext()) {
         return new NBTTagList();
      } else {
         INBTBase var3 = (INBTBase)var2.peek();
         ArrayList var6;
         if (var3 instanceof NBTTagByte) {
            var6 = Lists.newArrayList(Iterators.transform(var2, (var0) -> {
               return ((NBTTagByte)var0).func_150290_f();
            }));
            return new NBTTagByteArray(var6);
         } else if (var3 instanceof NBTTagInt) {
            var6 = Lists.newArrayList(Iterators.transform(var2, (var0) -> {
               return ((NBTTagInt)var0).func_150287_d();
            }));
            return new NBTTagIntArray(var6);
         } else if (var3 instanceof NBTTagLong) {
            var6 = Lists.newArrayList(Iterators.transform(var2, (var0) -> {
               return ((NBTTagLong)var0).func_150291_c();
            }));
            return new NBTTagLongArray(var6);
         } else {
            NBTTagList var4 = new NBTTagList();

            while(var2.hasNext()) {
               INBTBase var5 = (INBTBase)var2.next();
               if (!(var5 instanceof NBTTagEnd)) {
                  var4.add(var5);
               }
            }

            return var4;
         }
      }
   }

   public INBTBase remove(INBTBase var1, String var2) {
      if (var1 instanceof NBTTagCompound) {
         NBTTagCompound var3 = (NBTTagCompound)var1;
         NBTTagCompound var4 = new NBTTagCompound();
         var3.func_150296_c().stream().filter((var1x) -> {
            return !Objects.equals(var1x, var2);
         }).forEach((var2x) -> {
            var4.func_74782_a(var2x, var3.func_74781_a(var2x));
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
      return this.remove((INBTBase)var1, var2);
   }

   // $FF: synthetic method
   public Object createLongList(LongStream var1) {
      return this.createLongList(var1);
   }

   // $FF: synthetic method
   public Optional getLongStream(Object var1) {
      return this.getLongStream((INBTBase)var1);
   }

   // $FF: synthetic method
   public Object createIntList(IntStream var1) {
      return this.createIntList(var1);
   }

   // $FF: synthetic method
   public Optional getIntStream(Object var1) {
      return this.getIntStream((INBTBase)var1);
   }

   // $FF: synthetic method
   public Object createByteList(ByteBuffer var1) {
      return this.createByteList(var1);
   }

   // $FF: synthetic method
   public Optional getByteBuffer(Object var1) {
      return this.getByteBuffer((INBTBase)var1);
   }

   // $FF: synthetic method
   public Object createList(Stream var1) {
      return this.createList(var1);
   }

   // $FF: synthetic method
   public Optional getStream(Object var1) {
      return this.getStream((INBTBase)var1);
   }

   // $FF: synthetic method
   public Object createMap(Map var1) {
      return this.createMap(var1);
   }

   // $FF: synthetic method
   public Optional getMapValues(Object var1) {
      return this.getMapValues((INBTBase)var1);
   }

   // $FF: synthetic method
   public Object merge(Object var1, Object var2) {
      return this.merge((INBTBase)var1, (INBTBase)var2);
   }

   // $FF: synthetic method
   public Object mergeInto(Object var1, Object var2, Object var3) {
      return this.mergeInto((INBTBase)var1, (INBTBase)var2, (INBTBase)var3);
   }

   // $FF: synthetic method
   public Object mergeInto(Object var1, Object var2) {
      return this.mergeInto((INBTBase)var1, (INBTBase)var2);
   }

   // $FF: synthetic method
   public Object createString(String var1) {
      return this.createString(var1);
   }

   // $FF: synthetic method
   public Optional getStringValue(Object var1) {
      return this.getStringValue((INBTBase)var1);
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
      return this.getNumberValue((INBTBase)var1);
   }

   // $FF: synthetic method
   public Type getType(Object var1) {
      return this.getType((INBTBase)var1);
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }
}
