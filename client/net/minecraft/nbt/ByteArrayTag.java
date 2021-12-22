package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag extends CollectionTag<ByteTag> {
   private static final int SELF_SIZE_IN_BITS = 192;
   public static final TagType<ByteArrayTag> TYPE = new TagType.VariableSize<ByteArrayTag>() {
      public ByteArrayTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(192L);
         int var4 = var1.readInt();
         var3.accountBits(8L * (long)var4);
         byte[] var5 = new byte[var4];
         var1.readFully(var5);
         return new ByteArrayTag(var5);
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
         int var3 = var1.readInt();
         byte[] var4 = new byte[var3];
         var1.readFully(var4);
         return var2.visit(var4);
      }

      public void skip(DataInput var1) throws IOException {
         var1.skipBytes(var1.readInt() * 1);
      }

      public String getName() {
         return "BYTE[]";
      }

      public String getPrettyName() {
         return "TAG_Byte_Array";
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private byte[] data;

   public ByteArrayTag(byte[] var1) {
      super();
      this.data = var1;
   }

   public ByteArrayTag(List<Byte> var1) {
      this(toArray(var1));
   }

   private static byte[] toArray(List<Byte> var0) {
      byte[] var1 = new byte[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Byte var3 = (Byte)var0.get(var2);
         var1[var2] = var3 == null ? 0 : var3;
      }

      return var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);
      var1.write(this.data);
   }

   public byte getId() {
      return 7;
   }

   public TagType<ByteArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.getAsString();
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

   public ByteTag set(int var1, ByteTag var2) {
      byte var3 = this.data[var1];
      this.data[var1] = var2.getAsByte();
      return ByteTag.valueOf(var3);
   }

   public void add(int var1, ByteTag var2) {
      this.data = ArrayUtils.add(this.data, var1, var2.getAsByte());
   }

   public boolean setTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data[var1] = ((NumericTag)var2).getAsByte();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, var1, ((NumericTag)var2).getAsByte());
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

   public byte getElementType() {
      return 1;
   }

   public void clear() {
      this.data = new byte[0];
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }

   // $FF: synthetic method
   public Tag remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Tag var2) {
      this.add(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Tag set(int var1, Tag var2) {
      return this.set(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Object var2) {
      this.add(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (ByteTag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
