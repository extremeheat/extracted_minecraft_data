package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final TagType<IntTag> TYPE = new TagType.StaticSize<IntTag>() {
      public IntTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return IntTag.valueOf(readAccounted(var1, var2));
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static int readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(12L);
         return var0.readInt();
      }

      @Override
      public int size() {
         return 4;
      }

      @Override
      public String getName() {
         return "INT";
      }

      @Override
      public String getPrettyName() {
         return "TAG_Int";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   private final int data;

   IntTag(int var1) {
      super();
      this.data = var1;
   }

   public static IntTag valueOf(int var0) {
      return var0 >= -128 && var0 <= 1024 ? IntTag.Cache.cache[var0 - -128] : new IntTag(var0);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data);
   }

   @Override
   public int sizeInBytes() {
      return 12;
   }

   @Override
   public byte getId() {
      return 3;
   }

   @Override
   public TagType<IntTag> getType() {
      return TYPE;
   }

   public IntTag copy() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof IntTag && this.data == ((IntTag)var1).data;
      }
   }

   @Override
   public int hashCode() {
      return this.data;
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitInt(this);
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
      return (short)(this.data & 65535);
   }

   @Override
   public byte getAsByte() {
      return (byte)(this.data & 0xFF);
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
      static final IntTag[] cache = new IntTag[1153];

      private Cache() {
         super();
      }

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new IntTag(-128 + var0);
         }
      }
   }
}
