package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public record FloatTag(float value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final FloatTag ZERO = new FloatTag(0.0F);
   public static final TagType<FloatTag> TYPE = new TagType.StaticSize<FloatTag>() {
      public FloatTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return FloatTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static float readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(12L);
         return var0.readFloat();
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

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public FloatTag(float var1) {
      super();
      this.value = var1;
   }

   public static FloatTag valueOf(float var0) {
      return var0 == 0.0F ? ZERO : new FloatTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.value);
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

   public void accept(TagVisitor var1) {
      var1.visitFloat(this);
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

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.value);
   }

   public String toString() {
      StringTagVisitor var1 = new StringTagVisitor();
      var1.visitFloat(this);
      return var1.build();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
