package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;

public final class LongArrayTag implements CollectionTag {
   private static final int SELF_SIZE_IN_BYTES = 24;
   public static final TagType<LongArrayTag> TYPE = new TagType.VariableSize<LongArrayTag>() {
      public LongArrayTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return new LongArrayTag(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static long[] readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(24L);
         int var2 = var0.readInt();
         var1.accountBytes(8L, (long)var2);
         long[] var3 = new long[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var0.readLong();
         }

         return var3;
      }

      public void skip(DataInput var1, NbtAccounter var2) throws IOException {
         var1.skipBytes(var1.readInt() * 8);
      }

      public String getName() {
         return "LONG[]";
      }

      public String getPrettyName() {
         return "TAG_Long_Array";
      }

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };
   private long[] data;

   public LongArrayTag(long[] var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);

      for(long var5 : this.data) {
         var1.writeLong(var5);
      }

   }

   public int sizeInBytes() {
      return 24 + 8 * this.data.length;
   }

   public byte getId() {
      return 12;
   }

   public TagType<LongArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitLongArray(this);
      return var1.build();
   }

   public LongArrayTag copy() {
      long[] var1 = new long[this.data.length];
      System.arraycopy(this.data, 0, var1, 0, this.data.length);
      return new LongArrayTag(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)var1).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public void accept(TagVisitor var1) {
      var1.visitLongArray(this);
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongTag get(int var1) {
      return LongTag.valueOf(this.data[var1]);
   }

   public boolean setTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag var3) {
         this.data[var1] = var3.longValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag var3) {
         this.data = ArrayUtils.add(this.data, var1, var3.longValue());
         return true;
      } else {
         return false;
      }
   }

   public LongTag remove(int var1) {
      long var2 = this.data[var1];
      this.data = ArrayUtils.remove(this.data, var1);
      return LongTag.valueOf(var2);
   }

   public void clear() {
      this.data = new long[0];
   }

   public Optional<long[]> asLongArray() {
      return Optional.of(this.data);
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }

   // $FF: synthetic method
   public Tag get(final int var1) {
      return this.get(var1);
   }

   // $FF: synthetic method
   public Tag remove(final int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
