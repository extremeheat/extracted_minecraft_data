package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag extends CollectionTag<IntTag> {
   public static final TagType<IntArrayTag> TYPE = new TagType<IntArrayTag>() {
      public IntArrayTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(192L);
         int var4 = var1.readInt();
         var3.accountBits(32L * (long)var4);
         int[] var5 = new int[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            var5[var6] = var1.readInt();
         }

         return new IntArrayTag(var5);
      }

      public String getName() {
         return "INT[]";
      }

      public String getPrettyName() {
         return "TAG_Int_Array";
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private int[] data;

   public IntArrayTag(int[] var1) {
      super();
      this.data = var1;
   }

   public IntArrayTag(List<Integer> var1) {
      this(toArray(var1));
   }

   private static int[] toArray(List<Integer> var0) {
      int[] var1 = new int[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         Integer var3 = (Integer)var0.get(var2);
         var1[var2] = var3 == null ? 0 : var3;
      }

      return var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);
      int[] var2 = this.data;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1.writeInt(var5);
      }

   }

   public byte getId() {
      return 11;
   }

   public TagType<IntArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.getAsString();
   }

   public IntArrayTag copy() {
      int[] var1 = new int[this.data.length];
      System.arraycopy(this.data, 0, var1, 0, this.data.length);
      return new IntArrayTag(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof IntArrayTag && Arrays.equals(this.data, ((IntArrayTag)var1).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public int[] getAsIntArray() {
      return this.data;
   }

   public void accept(TagVisitor var1) {
      var1.visitIntArray(this);
   }

   public int size() {
      return this.data.length;
   }

   public IntTag get(int var1) {
      return IntTag.valueOf(this.data[var1]);
   }

   public IntTag set(int var1, IntTag var2) {
      int var3 = this.data[var1];
      this.data[var1] = var2.getAsInt();
      return IntTag.valueOf(var3);
   }

   public void add(int var1, IntTag var2) {
      this.data = ArrayUtils.add(this.data, var1, var2.getAsInt());
   }

   public boolean setTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data[var1] = ((NumericTag)var2).getAsInt();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int var1, Tag var2) {
      if (var2 instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, var1, ((NumericTag)var2).getAsInt());
         return true;
      } else {
         return false;
      }
   }

   public IntTag remove(int var1) {
      int var2 = this.data[var1];
      this.data = ArrayUtils.remove(this.data, var1);
      return IntTag.valueOf(var2);
   }

   public byte getElementType() {
      return 3;
   }

   public void clear() {
      this.data = new int[0];
   }

   // $FF: synthetic method
   public Tag remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public void add(int var1, Tag var2) {
      this.add(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Tag set(int var1, Tag var2) {
      return this.set(var1, (IntTag)var2);
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
      this.add(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (IntTag)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }
}
