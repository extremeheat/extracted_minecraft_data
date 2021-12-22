package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public class DoubleTag extends NumericTag {
   private static final int SELF_SIZE_IN_BITS = 128;
   public static final DoubleTag ZERO = new DoubleTag(0.0D);
   public static final TagType<DoubleTag> TYPE = new TagType.StaticSize<DoubleTag>() {
      public DoubleTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(128L);
         return DoubleTag.valueOf(var1.readDouble());
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
         return var2.visit(var1.readDouble());
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

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private final double data;

   private DoubleTag(double var1) {
      super();
      this.data = var1;
   }

   public static DoubleTag valueOf(double var0) {
      return var0 == 0.0D ? ZERO : new DoubleTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeDouble(this.data);
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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof DoubleTag && this.data == ((DoubleTag)var1).data;
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.data);
      return (int)(var1 ^ var1 >>> 32);
   }

   public void accept(TagVisitor var1) {
      var1.visitDouble(this);
   }

   public long getAsLong() {
      return (long)Math.floor(this.data);
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
      return this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visit(this.data);
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
