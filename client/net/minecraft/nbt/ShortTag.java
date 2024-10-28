package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 10;
   public static final TagType<ShortTag> TYPE = new TagType.StaticSize<ShortTag>() {
      public ShortTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return ShortTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static short readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(10L);
         return var0.readShort();
      }

      public int size() {
         return 2;
      }

      public String getName() {
         return "SHORT";
      }

      public String getPrettyName() {
         return "TAG_Short";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
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

   public void write(DataOutput var1) throws IOException {
      var1.writeShort(this.data);
   }

   public int sizeInBytes() {
      return 10;
   }

   public byte getId() {
      return 2;
   }

   public TagType<ShortTag> getType() {
      return TYPE;
   }

   public ShortTag copy() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ShortTag && this.data == ((ShortTag)var1).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public void accept(TagVisitor var1) {
      var1.visitShort(this);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return this.data;
   }

   public byte getAsByte() {
      return (byte)(this.data & 255);
   }

   public double getAsDouble() {
      return (double)this.data;
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
