package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag extends CollectionTag<ByteTag> {
   private byte[] data;

   ByteArrayTag() {
      super();
   }

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

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(192L);
      int var4 = var1.readInt();
      var3.accountBits((long)(8 * var4));
      this.data = new byte[var4];
      var1.readFully(this.data);
   }

   public byte getId() {
      return 7;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[B;");

      for(int var2 = 0; var2 < this.data.length; ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.data[var2]).append('B');
      }

      return var1.append(']').toString();
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

   public Component getPrettyDisplay(String var1, int var2) {
      Component var3 = (new TextComponent("B")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      Component var4 = (new TextComponent("[")).append(var3).append(";");

      for(int var5 = 0; var5 < this.data.length; ++var5) {
         Component var6 = (new TextComponent(String.valueOf(this.data[var5]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var4.append(" ").append(var6).append(var3);
         if (var5 != this.data.length - 1) {
            var4.append(",");
         }
      }

      var4.append("]");
      return var4;
   }

   public byte[] getAsByteArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public ByteTag get(int var1) {
      return new ByteTag(this.data[var1]);
   }

   public ByteTag set(int var1, ByteTag var2) {
      byte var3 = this.data[var1];
      this.data[var1] = var2.getAsByte();
      return new ByteTag(var3);
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
      return new ByteTag(var2);
   }

   public void clear() {
      this.data = new byte[0];
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
