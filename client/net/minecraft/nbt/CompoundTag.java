package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import org.slf4j.Logger;

public final class CompoundTag implements Tag {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<CompoundTag> CODEC;
   private static final int SELF_SIZE_IN_BYTES = 48;
   private static final int MAP_ENTRY_SIZE_IN_BYTES = 32;
   public static final TagType<CompoundTag> TYPE;
   private final Map<String, Tag> tags;

   CompoundTag(Map<String, Tag> var1) {
      super();
      this.tags = var1;
   }

   public CompoundTag() {
      this(new HashMap());
   }

   public void write(DataOutput var1) throws IOException {
      for(String var3 : this.tags.keySet()) {
         Tag var4 = (Tag)this.tags.get(var3);
         writeNamedTag(var3, var4, var1);
      }

      var1.writeByte(0);
   }

   public int sizeInBytes() {
      int var1 = 48;

      for(Map.Entry var3 : this.tags.entrySet()) {
         var1 += 28 + 2 * ((String)var3.getKey()).length();
         var1 += 36;
         var1 += ((Tag)var3.getValue()).sizeInBytes();
      }

      return var1;
   }

   public Set<String> keySet() {
      return this.tags.keySet();
   }

   public Set<Map.Entry<String, Tag>> entrySet() {
      return this.tags.entrySet();
   }

   public Collection<Tag> values() {
      return this.tags.values();
   }

   public void forEach(BiConsumer<String, Tag> var1) {
      this.tags.forEach(var1);
   }

   public byte getId() {
      return 10;
   }

   public TagType<CompoundTag> getType() {
      return TYPE;
   }

   public int size() {
      return this.tags.size();
   }

   @Nullable
   public Tag put(String var1, Tag var2) {
      return (Tag)this.tags.put(var1, var2);
   }

   public void putByte(String var1, byte var2) {
      this.tags.put(var1, ByteTag.valueOf(var2));
   }

   public void putShort(String var1, short var2) {
      this.tags.put(var1, ShortTag.valueOf(var2));
   }

   public void putInt(String var1, int var2) {
      this.tags.put(var1, IntTag.valueOf(var2));
   }

   public void putLong(String var1, long var2) {
      this.tags.put(var1, LongTag.valueOf(var2));
   }

   public void putFloat(String var1, float var2) {
      this.tags.put(var1, FloatTag.valueOf(var2));
   }

   public void putDouble(String var1, double var2) {
      this.tags.put(var1, DoubleTag.valueOf(var2));
   }

   public void putString(String var1, String var2) {
      this.tags.put(var1, StringTag.valueOf(var2));
   }

   public void putByteArray(String var1, byte[] var2) {
      this.tags.put(var1, new ByteArrayTag(var2));
   }

   public void putIntArray(String var1, int[] var2) {
      this.tags.put(var1, new IntArrayTag(var2));
   }

   public void putLongArray(String var1, long[] var2) {
      this.tags.put(var1, new LongArrayTag(var2));
   }

   public void putBoolean(String var1, boolean var2) {
      this.tags.put(var1, ByteTag.valueOf(var2));
   }

   @Nullable
   public Tag get(String var1) {
      return (Tag)this.tags.get(var1);
   }

   public boolean contains(String var1) {
      return this.tags.containsKey(var1);
   }

   private Optional<Tag> getOptional(String var1) {
      return Optional.ofNullable((Tag)this.tags.get(var1));
   }

   public Optional<Byte> getByte(String var1) {
      return this.getOptional(var1).flatMap(Tag::asByte);
   }

   public byte getByteOr(String var1, byte var2) {
      Object var4 = this.tags.get(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.byteValue();
      } else {
         return var2;
      }
   }

   public Optional<Short> getShort(String var1) {
      return this.getOptional(var1).flatMap(Tag::asShort);
   }

   public short getShortOr(String var1, short var2) {
      Object var4 = this.tags.get(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.shortValue();
      } else {
         return var2;
      }
   }

   public Optional<Integer> getInt(String var1) {
      return this.getOptional(var1).flatMap(Tag::asInt);
   }

   public int getIntOr(String var1, int var2) {
      Object var4 = this.tags.get(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.intValue();
      } else {
         return var2;
      }
   }

   public Optional<Long> getLong(String var1) {
      return this.getOptional(var1).flatMap(Tag::asLong);
   }

   public long getLongOr(String var1, long var2) {
      Object var5 = this.tags.get(var1);
      if (var5 instanceof NumericTag var4) {
         return var4.longValue();
      } else {
         return var2;
      }
   }

   public Optional<Float> getFloat(String var1) {
      return this.getOptional(var1).flatMap(Tag::asFloat);
   }

   public float getFloatOr(String var1, float var2) {
      Object var4 = this.tags.get(var1);
      if (var4 instanceof NumericTag var3) {
         return var3.floatValue();
      } else {
         return var2;
      }
   }

   public Optional<Double> getDouble(String var1) {
      return this.getOptional(var1).flatMap(Tag::asDouble);
   }

   public double getDoubleOr(String var1, double var2) {
      Object var5 = this.tags.get(var1);
      if (var5 instanceof NumericTag var4) {
         return var4.doubleValue();
      } else {
         return var2;
      }
   }

   public Optional<String> getString(String var1) {
      return this.getOptional(var1).flatMap(Tag::asString);
   }

   public String getStringOr(String var1, String var2) {
      Object var5 = this.tags.get(var1);
      if (var5 instanceof StringTag var3) {
         StringTag var10000 = var3;

         try {
            var8 = var10000.value();
         } catch (Throwable var7) {
            throw new MatchException(var7.toString(), var7);
         }

         String var6 = var8;
         return var6;
      } else {
         return var2;
      }
   }

   public Optional<byte[]> getByteArray(String var1) {
      Object var3 = this.tags.get(var1);
      if (var3 instanceof ByteArrayTag var2) {
         return Optional.of(var2.getAsByteArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<int[]> getIntArray(String var1) {
      Object var3 = this.tags.get(var1);
      if (var3 instanceof IntArrayTag var2) {
         return Optional.of(var2.getAsIntArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<long[]> getLongArray(String var1) {
      Object var3 = this.tags.get(var1);
      if (var3 instanceof LongArrayTag var2) {
         return Optional.of(var2.getAsLongArray());
      } else {
         return Optional.empty();
      }
   }

   public Optional<CompoundTag> getCompound(String var1) {
      Object var3 = this.tags.get(var1);
      if (var3 instanceof CompoundTag var2) {
         return Optional.of(var2);
      } else {
         return Optional.empty();
      }
   }

   public CompoundTag getCompoundOrEmpty(String var1) {
      return (CompoundTag)this.getCompound(var1).orElseGet(CompoundTag::new);
   }

   public Optional<ListTag> getList(String var1) {
      Object var3 = this.tags.get(var1);
      if (var3 instanceof ListTag var2) {
         return Optional.of(var2);
      } else {
         return Optional.empty();
      }
   }

   public ListTag getListOrEmpty(String var1) {
      return (ListTag)this.getList(var1).orElseGet(ListTag::new);
   }

   public Optional<Boolean> getBoolean(String var1) {
      return this.getOptional(var1).flatMap(Tag::asBoolean);
   }

   public boolean getBooleanOr(String var1, boolean var2) {
      return this.getByteOr(var1, (byte)(var2 ? 1 : 0)) != 0;
   }

   public void remove(String var1) {
      this.tags.remove(var1);
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitCompound(this);
      return var1.build();
   }

   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   protected CompoundTag shallowCopy() {
      return new CompoundTag(new HashMap(this.tags));
   }

   public CompoundTag copy() {
      return new CompoundTag(Util.mapValues(this.tags, Tag::copy));
   }

   public Optional<CompoundTag> asCompound() {
      return Optional.of(this);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof CompoundTag && Objects.equals(this.tags, ((CompoundTag)var1).tags);
      }
   }

   public int hashCode() {
      return this.tags.hashCode();
   }

   private static void writeNamedTag(String var0, Tag var1, DataOutput var2) throws IOException {
      var2.writeByte(var1.getId());
      if (var1.getId() != 0) {
         var2.writeUTF(var0);
         var1.write(var2);
      }
   }

   static Tag readNamedTagData(TagType<?> var0, String var1, DataInput var2, NbtAccounter var3) {
      try {
         return var0.load(var2, var3);
      } catch (IOException var7) {
         CrashReport var5 = CrashReport.forThrowable(var7, "Loading NBT data");
         CrashReportCategory var6 = var5.addCategory("NBT Tag");
         var6.setDetail("Tag name", var1);
         var6.setDetail("Tag type", var0.getName());
         throw new ReportedNbtException(var5);
      }
   }

   public CompoundTag merge(CompoundTag var1) {
      for(String var3 : var1.tags.keySet()) {
         Tag var4 = (Tag)var1.tags.get(var3);
         if (var4 instanceof CompoundTag var5) {
            Object var7 = this.tags.get(var3);
            if (var7 instanceof CompoundTag var6) {
               var6.merge(var5);
               continue;
            }
         }

         this.put(var3, var4.copy());
      }

      return this;
   }

   public void accept(TagVisitor var1) {
      var1.visitCompound(this);
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      for(Map.Entry var3 : this.tags.entrySet()) {
         Tag var4 = (Tag)var3.getValue();
         TagType var5 = var4.getType();
         StreamTagVisitor.EntryResult var6 = var1.visitEntry(var5);
         switch (var6) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               return var1.visitContainerEnd();
            case SKIP:
               break;
            default:
               var6 = var1.visitEntry(var5, (String)var3.getKey());
               switch (var6) {
                  case HALT:
                     return StreamTagVisitor.ValueResult.HALT;
                  case BREAK:
                     return var1.visitContainerEnd();
                  case SKIP:
                     break;
                  default:
                     StreamTagVisitor.ValueResult var7 = var4.accept(var1);
                     switch (var7) {
                        case HALT -> {
                           return StreamTagVisitor.ValueResult.HALT;
                        }
                        case BREAK -> {
                           return var1.visitContainerEnd();
                        }
                     }
               }
         }
      }

      return var1.visitContainerEnd();
   }

   public <T> void store(String var1, Codec<T> var2, T var3) {
      this.store(var1, var2, NbtOps.INSTANCE, var3);
   }

   public <T> void storeNullable(String var1, Codec<T> var2, @Nullable T var3) {
      if (var3 != null) {
         this.store(var1, var2, var3);
      }

   }

   public <T> void store(String var1, Codec<T> var2, DynamicOps<Tag> var3, T var4) {
      this.put(var1, (Tag)var2.encodeStart(var3, var4).getOrThrow());
   }

   public <T> void storeNullable(String var1, Codec<T> var2, DynamicOps<Tag> var3, @Nullable T var4) {
      if (var4 != null) {
         this.store(var1, var2, var3, var4);
      }

   }

   public <T> void store(MapCodec<T> var1, T var2) {
      this.store((MapCodec)var1, NbtOps.INSTANCE, var2);
   }

   public <T> void store(MapCodec<T> var1, DynamicOps<Tag> var2, T var3) {
      this.merge((CompoundTag)var1.encoder().encodeStart(var2, var3).getOrThrow());
   }

   public <T> Optional<T> read(String var1, Codec<T> var2) {
      return this.<T>read(var1, var2, NbtOps.INSTANCE);
   }

   public <T> Optional<T> read(String var1, Codec<T> var2, DynamicOps<Tag> var3) {
      Tag var4 = this.get(var1);
      return var4 == null ? Optional.empty() : var2.parse(var3, var4).resultOrPartial((var2x) -> LOGGER.error("Failed to read field ({}={}): {}", new Object[]{var1, var4, var2x}));
   }

   public <T> Optional<T> read(MapCodec<T> var1) {
      return this.read((MapCodec)var1, NbtOps.INSTANCE);
   }

   public <T> Optional<T> read(MapCodec<T> var1, DynamicOps<Tag> var2) {
      return var1.decode(var2, (MapLike)var2.getMap(this).getOrThrow()).resultOrPartial((var1x) -> LOGGER.error("Failed to read value ({}): {}", this, var1x));
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   static {
      CODEC = Codec.PASSTHROUGH.comapFlatMap((var0) -> {
         Tag var1 = (Tag)var0.convert(NbtOps.INSTANCE).getValue();
         if (var1 instanceof CompoundTag var2) {
            return DataResult.success(var2 == var0.getValue() ? var2.copy() : var2);
         } else {
            return DataResult.error(() -> "Not a compound tag: " + String.valueOf(var1));
         }
      }, (var0) -> new Dynamic(NbtOps.INSTANCE, var0.copy()));
      TYPE = new TagType.VariableSize<CompoundTag>() {
         public CompoundTag load(DataInput var1, NbtAccounter var2) throws IOException {
            var2.pushDepth();

            CompoundTag var3;
            try {
               var3 = loadCompound(var1, var2);
            } finally {
               var2.popDepth();
            }

            return var3;
         }

         private static CompoundTag loadCompound(DataInput var0, NbtAccounter var1) throws IOException {
            var1.accountBytes(48L);
            HashMap var2 = Maps.newHashMap();

            byte var3;
            while((var3 = var0.readByte()) != 0) {
               String var4 = readString(var0, var1);
               Tag var5 = CompoundTag.readNamedTagData(TagTypes.getType(var3), var4, var0, var1);
               if (var2.put(var4, var5) == null) {
                  var1.accountBytes(36L);
               }
            }

            return new CompoundTag(var2);
         }

         public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
            var3.pushDepth();

            StreamTagVisitor.ValueResult var4;
            try {
               var4 = parseCompound(var1, var2, var3);
            } finally {
               var3.popDepth();
            }

            return var4;
         }

         private static StreamTagVisitor.ValueResult parseCompound(DataInput var0, StreamTagVisitor var1, NbtAccounter var2) throws IOException {
            var2.accountBytes(48L);

            while(true) {
               byte var3;
               if ((var3 = var0.readByte()) != 0) {
                  TagType var4 = TagTypes.getType(var3);
                  switch (var1.visitEntry(var4)) {
                     case HALT:
                        return StreamTagVisitor.ValueResult.HALT;
                     case BREAK:
                        StringTag.skipString(var0);
                        var4.skip(var0, var2);
                        break;
                     case SKIP:
                        StringTag.skipString(var0);
                        var4.skip(var0, var2);
                        continue;
                     default:
                        String var5 = readString(var0, var2);
                        switch (var1.visitEntry(var4, var5)) {
                           case HALT:
                              return StreamTagVisitor.ValueResult.HALT;
                           case BREAK:
                              var4.skip(var0, var2);
                              break;
                           case SKIP:
                              var4.skip(var0, var2);
                              continue;
                           default:
                              var2.accountBytes(36L);
                              switch (var4.parse(var0, var1, var2)) {
                                 case HALT:
                                    return StreamTagVisitor.ValueResult.HALT;
                                 case BREAK:
                                 default:
                                    continue;
                              }
                        }
                  }
               }

               if (var3 != 0) {
                  while((var3 = var0.readByte()) != 0) {
                     StringTag.skipString(var0);
                     TagTypes.getType(var3).skip(var0, var2);
                  }
               }

               return var1.visitContainerEnd();
            }
         }

         private static String readString(DataInput var0, NbtAccounter var1) throws IOException {
            String var2 = var0.readUTF();
            var1.accountBytes(28L);
            var1.accountBytes(2L, (long)var2.length());
            return var2;
         }

         public void skip(DataInput var1, NbtAccounter var2) throws IOException {
            var2.pushDepth();

            byte var3;
            try {
               while((var3 = var1.readByte()) != 0) {
                  StringTag.skipString(var1);
                  TagTypes.getType(var3).skip(var1, var2);
               }
            } finally {
               var2.popDepth();
            }

         }

         public String getName() {
            return "COMPOUND";
         }

         public String getPrettyName() {
            return "TAG_Compound";
         }

         // $FF: synthetic method
         public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
            return this.load(var1, var2);
         }
      };
   }
}
