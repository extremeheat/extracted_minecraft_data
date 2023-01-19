package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends NumericTag {
   private static final int SELF_SIZE_IN_BITS = 72;
   public static final TagType<ByteTag> TYPE = new TagType.StaticSize<ByteTag>() {
      public ByteTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(72L);
         return ByteTag.valueOf(var1.readByte());
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
         return var2.visit(var1.readByte());
      }

      @Override
      public int size() {
         return 1;
      }

      @Override
      public String getName() {
         return "BYTE";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Byte";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   public static final ByteTag ZERO = valueOf((byte)0);
   public static final ByteTag ONE = valueOf((byte)1);
   private final byte data;

   ByteTag(byte var1) {
      super();
      this.data = var1;
   }

   public static ByteTag valueOf(byte var0) {
      return ByteTag.Cache.cache[128 + var0];
   }

   public static ByteTag valueOf(boolean var0) {
      return var0 ? ONE : ZERO;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeByte(this.data);
   }

   @Override
   public byte getId() {
      return 1;
   }

   @Override
   public TagType<ByteTag> getType() {
      return TYPE;
   }

   public ByteTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ByteTag && this.data == ((ByteTag)var1).data;
      }
   }

   @Override
   public int hashCode() {
      return this.data;
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitByte(this);
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
      return (short)this.data;
   }

   @Override
   public byte getAsByte() {
      return this.data;
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
      static final ByteTag[] cache = new ByteTag[256];

      private Cache() {
         super();
      }

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new ByteTag((byte)(var0 - 128));
         }
      }
   }
}
