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
      public LongArrayTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return new LongArrayTag(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static long[] readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(24L);
         int length = input.readInt();
         accounter.accountBytes(8L, (long)length);
         long[] data = new long[length];

         for(int i = 0; i < length; ++i) {
            data[i] = input.readLong();
         }

         return data;
      }

      public void skip(final DataInput input, final NbtAccounter accounter) throws IOException {
         input.skipBytes(input.readInt() * 8);
      }

      public String getName() {
         return "LONG[]";
      }

      public String getPrettyName() {
         return "TAG_Long_Array";
      }
   };
   private long[] data;

   public LongArrayTag(final long[] data) {
      super();
      this.data = data;
   }

   public void write(final DataOutput output) throws IOException {
      output.writeInt(this.data.length);

      for(long i : this.data) {
         output.writeLong(i);
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
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitLongArray(this);
      return visitor.build();
   }

   public LongArrayTag copy() {
      long[] cp = new long[this.data.length];
      System.arraycopy(this.data, 0, cp, 0, this.data.length);
      return new LongArrayTag(cp);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)obj).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitLongArray(this);
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongTag get(final int index) {
      return LongTag.valueOf(this.data[index]);
   }

   public boolean setTag(final int index, final Tag tag) {
      if (tag instanceof NumericTag numeric) {
         this.data[index] = numeric.longValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(final int index, final Tag tag) {
      if (tag instanceof NumericTag numeric) {
         this.data = ArrayUtils.add(this.data, index, numeric.longValue());
         return true;
      } else {
         return false;
      }
   }

   public LongTag remove(final int index) {
      long prev = this.data[index];
      this.data = ArrayUtils.remove(this.data, index);
      return LongTag.valueOf(prev);
   }

   public void clear() {
      this.data = new long[0];
   }

   public Optional<long[]> asLongArray() {
      return Optional.of(this.data);
   }

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      return visitor.visit(this.data);
   }
}
