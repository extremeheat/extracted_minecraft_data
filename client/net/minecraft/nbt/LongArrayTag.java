package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends CollectionTag<LongTag> {
   private long[] data;

   LongArrayTag() {
      super();
   }

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

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(192L);
      int var4 = var1.readInt();
      var3.accountBits((long)(64 * var4));
      this.data = new long[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.data[var5] = var1.readLong();
      }

   }

   public byte getId() {
      return 12;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[L;");

      for(int var2 = 0; var2 < this.data.length; ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.data[var2]).append('L');
      }

      return var1.append(']').toString();
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

   public Component getPrettyDisplay(String var1, int var2) {
      Component var3 = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
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

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongTag get(int var1) {
      return new LongTag(this.data[var1]);
   }

   public LongTag set(int var1, LongTag var2) {
      long var3 = this.data[var1];
      this.data[var1] = var2.getAsLong();
      return new LongTag(var3);
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
      return new LongTag(var2);
   }

   public void clear() {
      this.data = new long[0];
   }

   // $FF: synthetic method
   public Tag remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Tag var2) {
      this.add(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Tag set(int var1, Tag var2) {
      return this.set(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Object var2) {
      this.add(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (LongTag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
