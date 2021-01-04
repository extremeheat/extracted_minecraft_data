package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class IntTag extends NumericTag {
   private int data;

   IntTag() {
      super();
   }

   public IntTag(int var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data);
   }

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(96L);
      this.data = var1.readInt();
   }

   public byte getId() {
      return 3;
   }

   public String toString() {
      return String.valueOf(this.data);
   }

   public IntTag copy() {
      return new IntTag(this.data);
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
}
