package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class LongTag extends NumericTag {
   private long data;

   LongTag() {
      super();
   }

   public LongTag(long var1) {
      super();
      this.data = var1;
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeLong(this.data);
   }

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(128L);
      this.data = var1.readLong();
   }

   public byte getId() {
      return 4;
   }

   public String toString() {
      return this.data + "L";
   }

   public LongTag copy() {
      return new LongTag(this.data);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof LongTag && this.data == ((LongTag)var1).data;
      }
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public Component getPrettyDisplay(String var1, int var2) {
      Component var3 = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      return (new TextComponent(String.valueOf(this.data))).append(var3).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public long getAsLong() {
      return this.data;
   }

   public int getAsInt() {
      return (int)(this.data & -1L);
   }

   public short getAsShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getAsByte() {
      return (byte)((int)(this.data & 255L));
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
