package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ByteTag extends NumericTag {
   private byte data;

   ByteTag() {
      super();
   }

   public ByteTag(byte var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeByte(this.data);
   }

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(72L);
      this.data = var1.readByte();
   }

   public byte getId() {
      return 1;
   }

   public String toString() {
      return this.data + "b";
   }

   public ByteTag copy() {
      return new ByteTag(this.data);
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
      Component var3 = (new TextComponent("b")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
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
}
