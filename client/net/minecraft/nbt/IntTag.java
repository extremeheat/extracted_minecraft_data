package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record IntTag(int value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final TagType<IntTag> TYPE = new TagType.StaticSize<IntTag>() {
      public IntTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return IntTag.valueOf(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static int readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(12L);
         return input.readInt();
      }

      public int size() {
         return 4;
      }

      public String getName() {
         return "INT";
      }

      public String getPrettyName() {
         return "TAG_Int";
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public IntTag(int value) {
      super();
      this.value = value;
   }

   public static IntTag valueOf(final int i) {
      return i >= -128 && i <= 1024 ? IntTag.Cache.cache[i - -128] : new IntTag(i);
   }

   public void write(final DataOutput output) throws IOException {
      output.writeInt(this.value);
   }

   public int sizeInBytes() {
      return 12;
   }

   public byte getId() {
      return 3;
   }

   public TagType<IntTag> getType() {
      return TYPE;
   }

   public IntTag copy() {
      return this;
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitInt(this);
   }

   public long longValue() {
      return (long)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public short shortValue() {
      return (short)(this.value & '\uffff');
   }

   public byte byteValue() {
      return (byte)(this.value & 255);
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
      visitor.visitInt(this);
      return visitor.build();
   }

   private static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final IntTag[] cache = new IntTag[1153];

      private Cache() {
         super();
      }

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new IntTag(-128 + i);
         }

      }
   }
}
