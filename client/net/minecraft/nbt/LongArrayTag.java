package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends CollectionTag<LongTag> {
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

   public LongArrayTag(LongSet var1) {
      super();
      this.data = var1.toLongArray();
   }

   public LongArrayTag(List<Long> var1) {
      this(toArray(var1));
   }

   private static long[] toArray(List<Long> var0) {
      long[] var1 = new long[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Long var3 = (Long)var0.get(var2);
         var1[var2] = var3 == null ? 0L : var3;
      }

      return var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);
      long[] var2 = this.data;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         long var5 = var2[var4];
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
      return this.getAsString();
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

   public LongTag set(int var1, LongTag var2) {
      long var3 = this.data[var1];
      this.data[var1] = var2.getAsLong();
      return LongTag.valueOf(var3);
   }

   public void add(int var1, LongTag var2) {
      this.data = ArrayUtils.add(this.data, var1, var2.getAsLong());
   }

   public boolean setTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data[var1] = ((NumericTag)var2).getAsLong();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, var1, ((NumericTag)var2).getAsLong());
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

   public byte getElementType() {
      return 4;
   }

   public void clear() {
      this.data = new long[0];
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }

   // $FF: synthetic method
   public Tag remove(final int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(final int var1, final Tag var2) {
      this.add(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Tag set(final int var1, final Tag var2) {
      return this.set(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   public Object remove(final int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(final int var1, final Object var2) {
      this.add(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Object set(final int var1, final Object var2) {
      return this.set(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Object get(final int var1) {
      return this.get(var1);
   }
}
