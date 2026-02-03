package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record LongTag(long value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final TagType<LongTag> TYPE = new TagType.StaticSize<LongTag>() {
      public LongTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return LongTag.valueOf(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static long readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(16L);
         return input.readLong();
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
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public LongTag(long value) {
      super();
      this.value = value;
   }

   public static LongTag valueOf(final long i) {
      return i >= -128L && i <= 1024L ? LongTag.Cache.cache[(int)i - -128] : new LongTag(i);
   }

   public void write(final DataOutput output) throws IOException {
      output.writeLong(this.value);
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

   public void accept(final TagVisitor visitor) {
      visitor.visitLong(this);
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

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      return visitor.visit(this.value);
   }

   public String toString() {
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitLong(this);
      return visitor.build();
   }

   private static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final LongTag[] cache = new LongTag[1153];

      private Cache() {
         super();
      }

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new LongTag((long)(-128 + i));
         }

      }
   }
}
