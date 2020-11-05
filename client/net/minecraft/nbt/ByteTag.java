package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ByteTag extends NumericTag {
   public static final TagType<ByteTag> TYPE = new TagType<ByteTag>() {
      public ByteTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(72L);
         return ByteTag.valueOf(var1.readByte());
      }

      public String getName() {
         return "BYTE";
      }

      public String getPrettyName() {
         return "TAG_Byte";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   public static final ByteTag ZERO = valueOf((byte)0);
   public static final ByteTag ONE = valueOf((byte)1);
   private final byte data;

   private ByteTag(byte var1) {
      super();
      this.data = var1;
   }

   public static ByteTag valueOf(byte var0) {
      return ByteTag.Cache.cache[128 + var0];
   }

   public static ByteTag valueOf(boolean var0) {
      return var0 ? ONE : ZERO;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeByte(this.data);
   }

   public byte getId() {
      return 1;
   }

   public TagType<ByteTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.data + "b";
   }

   public ByteTag copy() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ByteTag && this.data == ((ByteTag)var1).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public Component getPrettyDisplay(String var1, int var2) {
      MutableComponent var3 = (new TextComponent("b")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(var3).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)this.data;
   }

   public byte getAsByte() {
      return this.data;
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

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   ByteTag(byte var1, Object var2) {
      this(var1);
   }

   static class Cache {
      private static final ByteTag[] cache = new ByteTag[256];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new ByteTag((byte)(var0 - 128));
         }

      }
   }
}
