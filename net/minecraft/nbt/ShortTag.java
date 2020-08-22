package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ShortTag extends NumericTag {
   public static final TagType TYPE = new TagType() {
      public ShortTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(80L);
         return ShortTag.valueOf(var1.readShort());
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
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private final short data;

   private ShortTag(short var1) {
      this.data = var1;
   }

   public static ShortTag valueOf(short var0) {
      return var0 >= -128 && var0 <= 1024 ? ShortTag.Cache.cache[var0 + 128] : new ShortTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeShort(this.data);
   }

   public byte getId() {
      return 2;
   }

   public TagType getType() {
      return TYPE;
   }

   public String toString() {
      return this.data + "s";
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

   public Component getPrettyDisplay(String var1, int var2) {
      Component var3 = (new TextComponent("s")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(var3).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
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

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }

   // $FF: synthetic method
   ShortTag(short var1, Object var2) {
      this(var1);
   }

   static class Cache {
      static final ShortTag[] cache = new ShortTag[1153];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new ShortTag((short)(-128 + var0));
         }

      }
   }
}
