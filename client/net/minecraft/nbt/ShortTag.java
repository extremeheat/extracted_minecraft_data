package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ShortTag extends NumericTag {
   private short data;

   public ShortTag() {
      super();
   }

   public ShortTag(short var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeShort(this.data);
   }

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(80L);
      this.data = var1.readShort();
   }

   public byte getId() {
      return 2;
   }

   public String toString() {
      return this.data + "s";
   }

   public ShortTag copy() {
      return new ShortTag(this.data);
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
}
