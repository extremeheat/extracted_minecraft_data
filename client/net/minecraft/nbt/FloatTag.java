package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public class FloatTag extends NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final FloatTag ZERO = new FloatTag(0.0F);
   public static final TagType<FloatTag> TYPE = new TagType.StaticSize<FloatTag>() {
      public FloatTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return FloatTag.valueOf(readAccounted(var1, var2));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static float readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(12L);
         return var0.readFloat();
      }

      @Override
      public int size() {
         return 4;
      }

      @Override
      public String getName() {
         return "FLOAT";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Float";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   private final float data;

   private FloatTag(float var1) {
      super();
      this.data = var1;
   }

   public static FloatTag valueOf(float var0) {
      return var0 == 0.0F ? ZERO : new FloatTag(var0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.data);
   }

   @Override
   public int sizeInBytes() {
      return 12;
   }

   @Override
   public byte getId() {
      return 5;
   }

   @Override
   public TagType<FloatTag> getType() {
      return TYPE;
   }

   public FloatTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof FloatTag && this.data == ((FloatTag)var1).data;
      }
   }

   @Override
   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitFloat(this);
   }

   @Override
   public long getAsLong() {
      return (long)this.data;
   }

   @Override
   public int getAsInt() {
      return Mth.floor(this.data);
   }

   @Override
   public short getAsShort() {
      return (short)(Mth.floor(this.data) & 65535);
   }

   @Override
   public byte getAsByte() {
      return (byte)(Mth.floor(this.data) & 0xFF);
   }

   @Override
   public double getAsDouble() {
      return (double)this.data;
   }

   @Override
   public float getAsFloat() {
      return this.data;
   }

   @Override
   public Number getAsNumber() {
      return this.data;
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }
}
