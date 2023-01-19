package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends CollectionTag<LongTag> {
   private static final int SELF_SIZE_IN_BITS = 192;
   public static final TagType<LongArrayTag> TYPE = new TagType.VariableSize<LongArrayTag>() {
      public LongArrayTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(192L);
         int var4 = var1.readInt();
         var3.accountBits(64L * (long)var4);
         long[] var5 = new long[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            var5[var6] = var1.readLong();
         }

         return new LongArrayTag(var5);
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
         int var3 = var1.readInt();
         long[] var4 = new long[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var1.readLong();
         }

         return var2.visit(var4);
      }

      @Override
      public void skip(DataInput var1) throws IOException {
         var1.skipBytes(var1.readInt() * 8);
      }

      @Override
      public String getName() {
         return "LONG[]";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Long_Array";
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

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);

      for(long var5 : this.data) {
         var1.writeLong(var5);
      }
   }

   @Override
   public byte getId() {
      return 12;
   }

   @Override
   public TagType<LongArrayTag> getType() {
      return TYPE;
   }

   @Override
   public String toString() {
      return this.getAsString();
   }

   public LongArrayTag copy() {
      long[] var1 = new long[this.data.length];
      System.arraycopy(this.data, 0, var1, 0, this.data.length);
      return new LongArrayTag(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)var1).data);
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitLongArray(this);
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   @Override
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

   @Override
   public boolean setTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data[var1] = ((NumericTag)var2).getAsLong();
         return true;
      } else {
         return false;
      }
   }

   @Override
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

   @Override
   public byte getElementType() {
      return 4;
   }

   @Override
   public void clear() {
      this.data = new long[0];
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }
}
