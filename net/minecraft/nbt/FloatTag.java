package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

public class FloatTag extends NumericTag {
   public static final FloatTag ZERO = new FloatTag(0.0F);
   public static final TagType TYPE = new TagType() {
      public FloatTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         var3.accountBits(96L);
         return FloatTag.valueOf(var1.readFloat());
      }

      public String getName() {
         return "FLOAT";
      }

      public String getPrettyName() {
         return "TAG_Float";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   private final float data;

   private FloatTag(float var1) {
      this.data = var1;
   }

   public static FloatTag valueOf(float var0) {
      return var0 == 0.0F ? ZERO : new FloatTag(var0);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.data);
   }

   public byte getId() {
      return 5;
   }

   public TagType getType() {
      return TYPE;
   }

   public String toString() {
      return this.data + "f";
   }

   public FloatTag copy() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof FloatTag && this.data == ((FloatTag)var1).data;
      }
   }

   public int hashCode() {
      return Float.floatToIntBits(this.data);
   }

   public Component getPrettyDisplay(String var1, int var2) {
      Component var3 = (new TextComponent("f")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(var3).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return Mth.floor(this.data);
   }

   public short getAsShort() {
      return (short)(Mth.floor(this.data) & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(Mth.floor(this.data) & 255);
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
