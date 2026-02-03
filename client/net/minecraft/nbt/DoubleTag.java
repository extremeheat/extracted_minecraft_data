package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public record DoubleTag(double value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final DoubleTag ZERO = new DoubleTag(0.0);
   public static final TagType<DoubleTag> TYPE = new TagType.StaticSize<DoubleTag>() {
      public DoubleTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return DoubleTag.valueOf(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static double readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(16L);
         return input.readDouble();
      }

      public int size() {
         return 8;
      }

      public String getName() {
         return "DOUBLE";
      }

      public String getPrettyName() {
         return "TAG_Double";
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public DoubleTag(double value) {
      super();
      this.value = value;
   }

   public static DoubleTag valueOf(final double data) {
      return data == 0.0 ? ZERO : new DoubleTag(data);
   }

   public void write(final DataOutput output) throws IOException {
      output.writeDouble(this.value);
   }

   public int sizeInBytes() {
      return 16;
   }

   public byte getId() {
      return 6;
   }

   public TagType<DoubleTag> getType() {
      return TYPE;
   }

   public DoubleTag copy() {
      return this;
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitDouble(this);
   }

   public long longValue() {
      return (long)Math.floor(this.value);
   }

   public int intValue() {
      return Mth.floor(this.value);
   }

   public short shortValue() {
      return (short)(Mth.floor(this.value) & '\uffff');
   }

   public byte byteValue() {
      return (byte)(Mth.floor(this.value) & 255);
   }

   public double doubleValue() {
      return this.value;
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
      visitor.visitDouble(this);
      return visitor.build();
   }
}
