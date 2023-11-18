package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final TagType<LongTag> TYPE = new TagType.StaticSize<LongTag>() {
      public LongTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return LongTag.valueOf(readAccounted(var1, var2));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static long readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(16L);
         return var0.readLong();
      }

      @Override
      public int size() {
         return 8;
      }

      @Override
      public String getName() {
         return "LONG";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Long";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   private final long data;

   LongTag(long var1) {
      super();
      this.data = var1;
   }

   public static LongTag valueOf(long var0) {
      return var0 >= -128L && var0 <= 1024L ? LongTag.Cache.cache[(int)var0 - -128] : new LongTag(var0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeLong(this.data);
   }

   @Override
   public int sizeInBytes() {
      return 16;
   }

   @Override
   public byte getId() {
      return 4;
   }

   @Override
   public TagType<LongTag> getType() {
      return TYPE;
   }

   public LongTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof LongTag && this.data == ((LongTag)var1).data;
      }
   }

   @Override
   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitLong(this);
   }

   @Override
   public long getAsLong() {
      return this.data;
   }

   @Override
   public int getAsInt() {
      return (int)(this.data & -1L);
   }

   @Override
   public short getAsShort() {
      return (short)((int)(this.data & 65535L));
   }

   @Override
   public byte getAsByte() {
      return (byte)((int)(this.data & 255L));
   }

   @Override
   public double getAsDouble() {
      return (double)this.data;
   }

   @Override
   public float getAsFloat() {
      return (float)this.data;
   }

   @Override
   public Number getAsNumber() {
      return this.data;
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }

   static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final LongTag[] cache = new LongTag[1153];

      private Cache() {
         super();
      }

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new LongTag((long)(-128 + var0));
         }
      }
   }
}
