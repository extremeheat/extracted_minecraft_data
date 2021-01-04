package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class EndTag implements Tag {
   public EndTag() {
      super();
   }

   public void load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
      var3.accountBits(64L);
   }

   public void write(DataOutput var1) throws IOException {
   }

   public byte getId() {
      return 0;
   }

   public String toString() {
      return "END";
   }

   public EndTag copy() {
      return new EndTag();
   }

   public Component getPrettyDisplay(String var1, int var2) {
      return new TextComponent("");
   }

   public boolean equals(Object var1) {
      return var1 instanceof EndTag;
   }

   public int hashCode() {
      return this.getId();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
