package net.minecraft.nbt;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;

public class CompoundTag implements Tag {
   public static final Codec<CompoundTag> CODEC;
   private static final int SELF_SIZE_IN_BYTES = 48;
   private static final int MAP_ENTRY_SIZE_IN_BYTES = 32;
   public static final TagType<CompoundTag> TYPE;
   private final Map<String, Tag> tags;

   protected CompoundTag(Map<String, Tag> var1) {
      super();
      this.tags = var1;
   }

   public CompoundTag() {
      this(Maps.newHashMap());
   }

   public void write(DataOutput var1) throws IOException {
      Iterator var2 = this.tags.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         Tag var4 = (Tag)this.tags.get(var3);
         writeNamedTag(var3, var4, var1);
      }

      var1.writeByte(0);
   }

   public int sizeInBytes() {
      int var1 = 48;

      Map.Entry var3;
      for(Iterator var2 = this.tags.entrySet().iterator(); var2.hasNext(); var1 += ((Tag)var3.getValue()).sizeInBytes()) {
         var3 = (Map.Entry)var2.next();
         var1 += 28 + 2 * ((String)var3.getKey()).length();
         var1 += 36;
      }

      return var1;
   }

   public Set<String> getAllKeys() {
      return this.tags.keySet();
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

   public void putUUID(String var1, UUID var2) {
      this.tags.put(var1, NbtUtils.createUUID(var2));
   }

   public UUID getUUID(String var1) {
      return NbtUtils.loadUUID(this.get(var1));
   }

   public boolean hasUUID(String var1) {
      Tag var2 = this.get(var1);
      return var2 != null && var2.getType() == IntArrayTag.TYPE && ((IntArrayTag)var2).getAsIntArray().length == 4;
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

   public void putByteArray(String var1, List<Byte> var2) {
      this.tags.put(var1, new ByteArrayTag(var2));
   }

   public void putIntArray(String var1, int[] var2) {
      this.tags.put(var1, new IntArrayTag(var2));
   }

   public void putIntArray(String var1, List<Integer> var2) {
      this.tags.put(var1, new IntArrayTag(var2));
   }

   public void putLongArray(String var1, long[] var2) {
      this.tags.put(var1, new LongArrayTag(var2));
   }

   public void putLongArray(String var1, List<Long> var2) {
      this.tags.put(var1, new LongArrayTag(var2));
   }

   public void putBoolean(String var1, boolean var2) {
      this.tags.put(var1, ByteTag.valueOf(var2));
   }

   @Nullable
   public Tag get(String var1) {
      return (Tag)this.tags.get(var1);
   }

   public byte getTagType(String var1) {
      Tag var2 = (Tag)this.tags.get(var1);
      return var2 == null ? 0 : var2.getId();
   }

   public boolean contains(String var1) {
      return this.tags.containsKey(var1);
   }

   public boolean contains(String var1, int var2) {
      byte var3 = this.getTagType(var1);
      if (var3 == var2) {
         return true;
      } else if (var2 != 99) {
         return false;
      } else {
         return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6;
      }
   }

   public byte getByte(String var1) {
      try {
         if (this.contains(var1, 99)) {
            return ((NumericTag)this.tags.get(var1)).getAsByte();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public short getShort(String var1) {
      try {
         if (this.contains(var1, 99)) {
            return ((NumericTag)this.tags.get(var1)).getAsShort();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public int getInt(String var1) {
      try {
         if (this.contains(var1, 99)) {
            return ((NumericTag)this.tags.get(var1)).getAsInt();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public long getLong(String var1) {
      try {
         if (this.contains(var1, 99)) {
            return ((NumericTag)this.tags.get(var1)).getAsLong();
         }
      } catch (ClassCastException var3) {
      }

      return 0L;
   }

   public float getFloat(String var1) {
      try {
         if (this.contains(var1, 99)) {
            return ((NumericTag)this.tags.get(var1)).getAsFloat();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0F;
   }

   public double getDouble(String var1) {
      try {
         if (this.contains(var1, 99)) {
            return ((NumericTag)this.tags.get(var1)).getAsDouble();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0;
   }

   public String getString(String var1) {
      try {
         if (this.contains(var1, 8)) {
            return ((Tag)this.tags.get(var1)).getAsString();
         }
      } catch (ClassCastException var3) {
      }

      return "";
   }

   public byte[] getByteArray(String var1) {
      try {
         if (this.contains(var1, 7)) {
            return ((ByteArrayTag)this.tags.get(var1)).getAsByteArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(var1, ByteArrayTag.TYPE, var3));
      }

      return new byte[0];
   }

   public int[] getIntArray(String var1) {
      try {
         if (this.contains(var1, 11)) {
            return ((IntArrayTag)this.tags.get(var1)).getAsIntArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(var1, IntArrayTag.TYPE, var3));
      }

      return new int[0];
   }

   public long[] getLongArray(String var1) {
      try {
         if (this.contains(var1, 12)) {
            return ((LongArrayTag)this.tags.get(var1)).getAsLongArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(var1, LongArrayTag.TYPE, var3));
      }

      return new long[0];
   }

   public CompoundTag getCompound(String var1) {
      try {
         if (this.contains(var1, 10)) {
            return (CompoundTag)this.tags.get(var1);
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.createReport(var1, TYPE, var3));
      }

      return new CompoundTag();
   }

   public ListTag getList(String var1, int var2) {
      label28: {
         try {
            if (this.getTagType(var1) == 9) {
               ListTag var3 = (ListTag)this.tags.get(var1);
               if (!var3.isEmpty() && var3.getElementType() != var2) {
                  break label28;
               }

               return var3;
            }
         } catch (ClassCastException var5) {
            throw new ReportedException(this.createReport(var1, ListTag.TYPE, var5));
         }

         return new ListTag();
      }

      try {
         return new ListTag();
      } catch (ClassCastException var4) {
         throw new ReportedException(this.createReport(var1, ListTag.TYPE, var4));
      }
   }

   public boolean getBoolean(String var1) {
      return this.getByte(var1) != 0;
   }

   public void remove(String var1) {
      this.tags.remove(var1);
   }

   public String toString() {
      return this.getAsString();
   }

   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   private CrashReport createReport(String var1, TagType<?> var2, ClassCastException var3) {
      CrashReport var4 = CrashReport.forThrowable(var3, "Reading NBT data");
      CrashReportCategory var5 = var4.addCategory("Corrupt NBT tag", 1);
      var5.setDetail("Tag type found", () -> {
         return ((Tag)this.tags.get(var1)).getType().getName();
      });
      Objects.requireNonNull(var2);
      var5.setDetail("Tag type expected", var2::getName);
      var5.setDetail("Tag name", (Object)var1);
      return var4;
   }

   protected CompoundTag shallowCopy() {
      return new CompoundTag(new HashMap(this.tags));
   }

   public CompoundTag copy() {
      HashMap var1 = Maps.newHashMap(Maps.transformValues(this.tags, Tag::copy));
      return new CompoundTag(var1);
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
         var6.setDetail("Tag name", (Object)var1);
         var6.setDetail("Tag type", (Object)var0.getName());
         throw new ReportedNbtException(var5);
      }
   }

   public CompoundTag merge(CompoundTag var1) {
      Iterator var2 = var1.tags.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         Tag var4 = (Tag)var1.tags.get(var3);
         if (var4.getId() == 10) {
            if (this.contains(var3, 10)) {
               CompoundTag var5 = this.getCompound(var3);
               var5.merge((CompoundTag)var4);
            } else {
               this.put(var3, var4.copy());
            }
         } else {
            this.put(var3, var4.copy());
         }
      }

      return this;
   }

   public void accept(TagVisitor var1) {
      var1.visitCompound(this);
   }

   protected Set<Map.Entry<String, Tag>> entrySet() {
      return this.tags.entrySet();
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      Iterator var2 = this.tags.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
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
            return DataResult.error(() -> {
               return "Not a compound tag: " + String.valueOf(var1);
            });
         }
      }, (var0) -> {
         return new Dynamic(NbtOps.INSTANCE, var0.copy());
      });
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
