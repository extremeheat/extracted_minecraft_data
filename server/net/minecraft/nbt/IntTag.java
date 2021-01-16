package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class IntTag extends NumericTag {
   public static final TagType<IntTag> TYPE = new TagType<IntTag>() {
      public IntTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(96L);
         return IntTag.valueOf(var1.readInt());
      }

      public String getName() {
         return "INT";
      }

      public String getPrettyName() {
         return "TAG_Int";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private final int data;

   private IntTag(int var1) {
      super();
      this.data = var1;
   }

   public static IntTag valueOf(int var0) {
      return var0 >= -128 && var0 <= 1024 ? IntTag.Cache.cache[var0 + 128] : new IntTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data);
   }

   public byte getId() {
      return 3;
   }

   public TagType<IntTag> getType() {
      return TYPE;
   }

   public String toString() {
      return String.valueOf(this.data);
   }

   public IntTag copy() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof IntTag && this.data == ((IntTag)var1).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public Component getPrettyDisplay(String var1, int var2) {
      return (new TextComponent(String.valueOf(this.data))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)(this.data & '\uffff');
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

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   IntTag(int var1, Object var2) {
      this(var1);
   }

   static class Cache {
      static final IntTag[] cache = new IntTag[1153];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new IntTag(-128 + var0);
         }

      }
   }
}
