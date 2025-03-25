package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public record DoubleTag(double value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final DoubleTag ZERO = new DoubleTag(0.0);
   public static final TagType<DoubleTag> TYPE = new TagType.StaticSize<DoubleTag>() {
      public DoubleTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return DoubleTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static double readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(16L);
         return var0.readDouble();
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

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public DoubleTag(double var1) {
      super();
      this.value = var1;
   }

   public static DoubleTag valueOf(double var0) {
      return var0 == 0.0 ? ZERO : new DoubleTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeDouble(this.value);
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

   public void accept(TagVisitor var1) {
      var1.visitDouble(this);
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

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.value);
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitDouble(this);
      return var1.build();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
