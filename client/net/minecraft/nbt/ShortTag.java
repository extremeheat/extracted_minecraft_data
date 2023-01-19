package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends NumericTag {
   private static final int SELF_SIZE_IN_BITS = 80;
   public static final TagType<ShortTag> TYPE = new TagType.StaticSize<ShortTag>() {
      public ShortTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(80L);
         return ShortTag.valueOf(var1.readShort());
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
         return var2.visit(var1.readShort());
      }

      @Override
      public int size() {
         return 2;
      }

      @Override
      public String getName() {
         return "SHORT";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Short";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   private final short data;

   ShortTag(short var1) {
      super();
      this.data = var1;
   }

   public static ShortTag valueOf(short var0) {
      return var0 >= -128 && var0 <= 1024 ? ShortTag.Cache.cache[var0 - -128] : new ShortTag(var0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeShort(this.data);
   }

   @Override
   public byte getId() {
      return 2;
   }

   @Override
   public TagType<ShortTag> getType() {
      return TYPE;
   }

   public ShortTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ShortTag && this.data == ((ShortTag)var1).data;
      }
   }

   @Override
   public int hashCode() {
      return this.data;
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitShort(this);
   }

   @Override
   public long getAsLong() {
      return (long)this.data;
   }

   @Override
   public int getAsInt() {
      return this.data;
   }

   @Override
   public short getAsShort() {
      return this.data;
   }

   @Override
   public byte getAsByte() {
      return (byte)(this.data & 255);
   }

   @Override
   public double getAsDouble() {
      return (double)this.data;
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

   static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final ShortTag[] cache = new ShortTag[1153];

      private Cache() {
         super();
      }

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new ShortTag((short)(-128 + var0));
         }
      }
   }
}
