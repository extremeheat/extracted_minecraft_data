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
      public ByteArrayTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return new ByteArrayTag(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static byte[] readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(24L);
         int var2 = var0.readInt();
         var1.accountBytes(1L, (long)var2);
         byte[] var3 = new byte[var2];
         var0.readFully(var3);
         return var3;
      }

      public void skip(DataInput var1, NbtAccounter var2) throws IOException {
         var1.skipBytes(var1.readInt() * 1);
      }

      public String getName() {
         return "BYTE[]";
      }

      public String getPrettyName() {
         return "TAG_Byte_Array";
      }

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };
   private byte[] data;

   public ByteArrayTag(byte[] var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);
      var1.write(this.data);
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
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitByteArray(this);
      return var1.build();
   }

   public Tag copy() {
      byte[] var1 = new byte[this.data.length];
      System.arraycopy(this.data, 0, var1, 0, this.data.length);
      return new ByteArrayTag(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ByteArrayTag && Arrays.equals(this.data, ((ByteArrayTag)var1).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public void accept(TagVisitor var1) {
      var1.visitByteArray(this);
   }

   public byte[] getAsByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public ByteTag get(int var1) {
      return ByteTag.valueOf(this.data[var1]);
   }

   public boolean setTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag var3) {
         this.data[var1] = var3.byteValue();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag var3) {
         this.data = ArrayUtils.add(this.data, var1, var3.byteValue());
         return true;
      } else {
         return false;
      }
   }

   public ByteTag remove(int var1) {
      byte var2 = this.data[var1];
      this.data = ArrayUtils.remove(this.data, var1);
      return ByteTag.valueOf(var2);
   }

   public void clear() {
      this.data = new byte[0];
   }

   public Optional<byte[]> asByteArray() {
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
}
