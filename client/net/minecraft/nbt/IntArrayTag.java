package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;

public final class IntArrayTag implements CollectionTag {
   private static final int SELF_SIZE_IN_BYTES = 24;
   public static final TagType<IntArrayTag> TYPE = new TagType.VariableSize<IntArrayTag>() {
      public IntArrayTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return new IntArrayTag(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static int[] readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(24L);
         int length = input.readInt();
         accounter.accountBytes(4L, (long)length);
         int[] data = new int[length];

         for(int i = 0; i < length; ++i) {
            data[i] = input.readInt();
         }

         return data;
      }

      public void skip(final DataInput input, final NbtAccounter accounter) throws IOException {
         input.skipBytes(input.readInt() * 4);
      }

      public String getName() {
         return "INT[]";
      }

      public String getPrettyName() {
         return "TAG_Int_Array";
      }
   };
   private int[] data;

   public IntArrayTag(final int[] data) {
      super();
      this.data = data;
   }

   public void write(final DataOutput output) throws IOException {
      output.writeInt(this.data.length);

      for(int i : this.data) {
         output.writeInt(i);
      }

   }

   public int sizeInBytes() {
      return 24 + 4 * this.data.length;
   }

   public byte getId() {
      return 11;
   }

   public TagType<IntArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitIntArray(this);
      return visitor.build();
   }

   public IntArrayTag copy() {
      int[] cp = new int[this.data.length];
      System.arraycopy(this.data, 0, cp, 0, this.data.length);
      return new IntArrayTag(cp);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof IntArrayTag) {
            IntArrayTag intArrayTag = (IntArrayTag)obj;
            if (Arrays.equals(this.data, intArrayTag.data)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public int[] getAsIntArray() {
      return this.data;
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitIntArray(this);
   }

   public int size() {
      return this.data.length;
   }

   public IntTag get(final int index) {
      return IntTag.valueOf(this.data[index]);
   }

   public boolean setTag(final int index, final Tag tag) {
      if (tag instanceof NumericTag numeric) {
         this.data[index] = numeric.intValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(final int index, final Tag tag) {
      if (tag instanceof NumericTag numeric) {
         this.data = ArrayUtils.add(this.data, index, numeric.intValue());
         return true;
      } else {
         return false;
      }
   }

   public IntTag remove(final int index) {
      int prev = this.data[index];
      this.data = ArrayUtils.remove(this.data, index);
      return IntTag.valueOf(prev);
   }

   public void clear() {
      this.data = new int[0];
   }

   public Optional<int[]> asIntArray() {
      return Optional.of(this.data);
   }

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      return visitor.visit(this.data);
   }
}
