package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.Mth;

public class DoubleTag extends NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 16;
   public static final DoubleTag ZERO = new DoubleTag(0.0);
   public static final TagType<DoubleTag> TYPE = new TagType.StaticSize<DoubleTag>() {
      public DoubleTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return DoubleTag.valueOf(readAccounted(var1, var2));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static double readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(16L);
         return var0.readDouble();
      }

      @Override
      public int size() {
         return 8;
      }

      @Override
      public String getName() {
         return "DOUBLE";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Double";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   private final double data;

   private DoubleTag(double var1) {
      super();
      this.data = var1;
   }

   public static DoubleTag valueOf(double var0) {
      return var0 == 0.0 ? ZERO : new DoubleTag(var0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeDouble(this.data);
   }

   @Override
   public int sizeInBytes() {
      return 16;
   }

   @Override
   public byte getId() {
      return 6;
   }

   @Override
   public TagType<DoubleTag> getType() {
      return TYPE;
   }

   public DoubleTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof DoubleTag && this.data == ((DoubleTag)var1).data;
      }
   }

   @Override
   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.data);
      return (int)(var1 ^ var1 >>> 32);
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitDouble(this);
   }

   @Override
   public long getAsLong() {
      return (long)Math.floor(this.data);
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
      return this.data;
   }

   @Override
   public float getAsFloat() {
      return (float)this.data;
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
