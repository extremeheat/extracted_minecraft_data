package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public class FloatTag extends NumericTag {
   public static final FloatTag ZERO = new FloatTag(0.0F);
   public static final TagType<FloatTag> TYPE = new TagType<FloatTag>() {
      public FloatTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(96L);
         return FloatTag.valueOf(var1.readFloat());
      }

      public String getName() {
         return "FLOAT";
      }

      public String getPrettyName() {
         return "TAG_Float";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
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

   public void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.data);
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof FloatTag && this.data == ((FloatTag)var1).data;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   public void accept(TagVisitor var1) {
      var1.visitFloat(this);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return Mth.floor(this.data);
   }

   public short getAsShort() {
      return (short)(Mth.floor(this.data) & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(Mth.floor(this.data) & 255);
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
