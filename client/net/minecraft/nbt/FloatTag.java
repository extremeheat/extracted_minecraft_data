package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

public class FloatTag extends NumericTag {
   private float data;

   FloatTag() {
      super();
   }

   public FloatTag(float var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.data);
   }

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(96L);
      this.data = var1.readFloat();
   }

   public byte getId() {
      return 5;
   }

   public String toString() {
      return this.data + "f";
   }

   public FloatTag copy() {
      return new FloatTag(this.data);
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
