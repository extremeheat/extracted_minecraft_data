package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record IntTag(int value) implements NumericTag {
   private static final int SELF_SIZE_IN_BYTES = 12;
   public static final TagType<IntTag> TYPE = new TagType.StaticSize<IntTag>() {
      public IntTag load(DataInput var1, NbtAccounter var2) throws IOException {
         return IntTag.valueOf(readAccounted(var1, var2));
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) throws IOException {
         return var2.visit(readAccounted(var1, var3));
      }

      private static int readAccounted(DataInput var0, NbtAccounter var1) throws IOException {
         var1.accountBytes(12L);
         return var0.readInt();
      }

      public int size() {
         return 4;
      }

      public String getName() {
         return "INT";
      }

      public String getPrettyName() {
         return "TAG_Int";
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
   public IntTag(int var1) {
      super();
      this.value = var1;
   }

   public static IntTag valueOf(int var0) {
      return var0 >= -128 && var0 <= 1024 ? IntTag.Cache.cache[var0 - -128] : new IntTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.value);
   }

   public int sizeInBytes() {
      return 12;
   }

   public byte getId() {
      return 3;
   }

   public TagType<IntTag> getType() {
      return TYPE;
   }

   public IntTag copy() {
      return this;
   }

   public void accept(TagVisitor var1) {
      var1.visitInt(this);
   }

   public long longValue() {
      return (long)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public short shortValue() {
      return (short)(this.value & '\uffff');
   }

   public byte byteValue() {
      return (byte)(this.value & 255);
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
      var1.visitInt(this);
      return var1.build();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
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
