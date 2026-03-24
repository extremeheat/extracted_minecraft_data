package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public record FloatTag(float value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final FloatTag ZERO = new FloatTag(0.0F);
   public static final TagType<FloatTag> TYPE = new TagType.StaticSize<FloatTag>() {
      public FloatTag load(final DataInput input, final NbtAccounter accounter) throws IOException {
         return FloatTag.valueOf(readAccounted(input, accounter));
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) throws IOException {
         return output.visit(readAccounted(input, accounter));
      }

      private static float readAccounted(final DataInput input, final NbtAccounter accounter) throws IOException {
         accounter.accountBytes(12L);
         return input.readFloat();
      }

      public int size() {
         return 4;
      }

      public String getName() {
         return "FLOAT";
      }

      public String getPrettyName() {
         return "TAG_Float";
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public FloatTag(float value) {
      super();
      this.value = value;
   }

   public static FloatTag valueOf(final float data) {
      return data == 0.0F ? ZERO : new FloatTag(data);
   }

   public void write(final DataOutput output) throws IOException {
      output.writeFloat(this.value);
   }

   public int sizeInBytes() {
      return 12;
   }

   public byte getId() {
      return 5;
   }

   public TagType<FloatTag> getType() {
      return TYPE;
   }

   public FloatTag copy() {
      return this;
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitFloat(this);
   }

   public long longValue() {
      return (long)this.value;
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
      return (double)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public Number box() {
      return this.value;
   }

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      return visitor.visit(this.value);
   }

   public String toString() {
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitFloat(this);
      return visitor.build();
   }
}
