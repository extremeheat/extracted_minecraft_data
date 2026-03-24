package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record ByteTag(byte value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 9;
   public static final TagType<ByteTag> TYPE = new TagType.StaticSize<ByteTag>() {
      public ByteTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return ByteTag.valueOf(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static byte readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(9L);
         return input.readByte();
      }

      public int size() {
         return 1;
      }

      public String getName() {
         return "BYTE";
      }

      public String getPrettyName() {
         return "TAG_Byte";
      }
   };
   public static final ByteTag ZERO = valueOf((byte)0);
   public static final ByteTag ONE = valueOf((byte)1);

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public ByteTag(byte value) {
      super();
      this.value = value;
   }

   public static ByteTag valueOf(final byte data) {
      return ByteTag.Cache.cache[128 + data];
   }

   public static ByteTag valueOf(final boolean data) {
      return data ? ONE : ZERO;
   }

   public void write(final DataOutput output) throws IOException {
      output.writeByte(this.value);
   }

   public int sizeInBytes() {
      return 9;
   }

   public byte getId() {
      return 1;
   }

   public TagType<ByteTag> getType() {
      return TYPE;
   }

   public ByteTag copy() {
      return this;
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitByte(this);
   }

   public long longValue() {
      return (long)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public short shortValue() {
      return (short)this.value;
   }

   public byte byteValue() {
      return this.value;
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
      visitor.visitByte(this);
      return visitor.build();
   }

   private static class Cache {
      private static final ByteTag[] cache = new ByteTag[256];

      private Cache() {
         super();
      }

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new ByteTag((byte)(i - 128));
         }

      }
   }
}
