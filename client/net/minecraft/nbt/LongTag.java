package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumericTag {
   public static final TagType<LongTag> TYPE = new TagType<LongTag>() {
      public LongTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(128L);
         return LongTag.valueOf(var1.readLong());
      }

      public String getName() {
         return "LONG";
      }

      public String getPrettyName() {
         return "TAG_Long";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private final long data;

   private LongTag(long var1) {
      super();
      this.data = var1;
   }

   public static LongTag valueOf(long var0) {
      return var0 >= -128L && var0 <= 1024L ? LongTag.Cache.cache[(int)var0 - -128] : new LongTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeLong(this.data);
   }

   public byte getId() {
      return 4;
   }

   public TagType<LongTag> getType() {
      return TYPE;
   }

   public LongTag copy() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof LongTag && this.data == ((LongTag)var1).data;
      }
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public void accept(TagVisitor var1) {
      var1.visitLong(this);
   }

   public long getAsLong() {
      return this.data;
   }

   public int getAsInt() {
      return (int)(this.data & -1L);
   }

   public short getAsShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getAsByte() {
      return (byte)((int)(this.data & 255L));
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   LongTag(long var1, Object var3) {
      this(var1);
   }

   static class Cache {
      static final LongTag[] cache = new LongTag[1153];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new LongTag((long)(-128 + var0));
         }

      }
   }
}
