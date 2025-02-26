package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record ByteTag(byte value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 9;
   public static final TagType<ByteTag> TYPE = new TagType.StaticSize<ByteTag>() {
      public ByteTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return ByteTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static byte readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(9L);
         return var0.readByte();
      }

      public int size() {
         return 1;
      }

      public String getName() {
         return "BYTE";
      }

      public String getPrettyName() {
         return "TAG_Byte";
      }

      // $FF: synthetic method
      public Tag load(final DataInput var1, final NbtAccounter var2) throws IOException {
         return this.load(var1, var2);
      }
   };
   public static final ByteTag ZERO = valueOf((byte)0);
   public static final ByteTag ONE = valueOf((byte)1);

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public ByteTag(byte var1) {
      super();
      this.value = var1;
   }

   public static ByteTag valueOf(byte var0) {
      return ByteTag.Cache.cache[128 + var0];
   }

   public static ByteTag valueOf(boolean var0) {
      return var0 ? ONE : ZERO;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeByte(this.value);
   }

   public int sizeInBytes() {
      return 9;
   }

   public byte getId() {
      return 1;
   }

   public TagType<ByteTag> getType() {
      return TYPE;
   }

   public ByteTag copy() {
      return this;
   }

   public void accept(TagVisitor var1) {
      var1.visitByte(this);
   }

   public long longValue() {
      return (long)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public short shortValue() {
      return (short)this.value;
   }

   public byte byteValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
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
      var1.visitByte(this);
      return var1.build();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
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
