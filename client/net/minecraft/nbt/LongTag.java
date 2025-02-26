package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record LongTag(long value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final TagType<LongTag> TYPE = new TagType.StaticSize<LongTag>() {
      public LongTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return LongTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static long readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(16L);
         return var0.readLong();
      }

      public int size() {
         return 8;
      }

      public String getName() {
         return "LONG";
      }

      public String getPrettyName() {
         return "TAG_Long";
      }

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public LongTag(long var1) {
      super();
      this.value = var1;
   }

   public static LongTag valueOf(long var0) {
      return var0 >= -128L && var0 <= 1024L ? LongTag.Cache.cache[(int)var0 - -128] : new LongTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeLong(this.value);
   }

   public int sizeInBytes() {
      return 16;
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

   public void accept(TagVisitor var1) {
      var1.visitLong(this);
   }

   public long longValue() {
      return this.value;
   }

   public int intValue() {
      return (int)(this.value & -1L);
   }

   public short shortValue() {
      return (short)((int)(this.value & 65535L));
   }

   public byte byteValue() {
      return (byte)((int)(this.value & 255L));
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public Number box() {
      return this.value;
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.value);
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitLong(this);
      return var1.build();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
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
