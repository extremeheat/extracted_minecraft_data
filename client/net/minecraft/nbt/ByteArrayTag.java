package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.ArrayUtils;

public final class ByteArrayTag implements CollectionTag {
   private static final int SELF_SIZE_IN_BYTES = 24;
   public static final TagType<ByteArrayTag> TYPE = new TagType.VariableSize<ByteArrayTag>() {
      public ByteArrayTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return new ByteArrayTag(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static byte[] readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(24L);
         int length = input.readInt();
         accounter.accountBytes(1L, (long)length);
         byte[] data = new byte[length];
         input.readFully(data);
         return data;
      }

      public void skip(final DataInput input, final NbtAccounter accounter) throws IOException {
         input.skipBytes(input.readInt() * 1);
      }

      public String getName() {
         return "BYTE[]";
      }

      public String getPrettyName() {
         return "TAG_Byte_Array";
      }
   };
   private byte[] data;

   public ByteArrayTag(final byte[] data) {
      super();
      this.data = data;
   }

   public void write(final DataOutput output) throws IOException {
      output.writeInt(this.data.length);
      output.write(this.data);
   }

   public int sizeInBytes() {
      return 24 + 1 * this.data.length;
   }

   public byte getId() {
      return 7;
   }

   public TagType<ByteArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitByteArray(this);
      return visitor.build();
   }

   public Tag copy() {
      byte[] cp = new byte[this.data.length];
      System.arraycopy(this.data, 0, cp, 0, this.data.length);
      return new ByteArrayTag(cp);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof ByteArrayTag && Arrays.equals(this.data, ((ByteArrayTag)obj).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitByteArray(this);
   }

   public byte[] getAsByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public ByteTag get(final int index) {
      return ByteTag.valueOf(this.data[index]);
   }

   public boolean setTag(final int index, final Tag tag) {
      if (tag instanceof NumericTag numeric) {
         this.data[index] = numeric.byteValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(final int index, final Tag tag) {
      if (tag instanceof NumericTag numeric) {
         this.data = ArrayUtils.add(this.data, index, numeric.byteValue());
         return true;
      } else {
         return false;
      }
   }

   public ByteTag remove(final int index) {
      byte prev = this.data[index];
      this.data = ArrayUtils.remove(this.data, index);
      return ByteTag.valueOf(prev);
   }

   public void clear() {
      this.data = new byte[0];
   }

   public Optional<byte[]> asByteArray() {
      return Optional.of(this.data);
   }

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      return visitor.visit(this.data);
   }
}
