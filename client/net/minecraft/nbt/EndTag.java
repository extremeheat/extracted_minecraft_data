package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class EndTag implements Tag {
   public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
      public EndTag load(DataInput var1, int var2, NbtAccounter var3) {
         var3.accountBits(64L);
         return EndTag.INSTANCE;
      }

      public String getName() {
         return "END";
      }

      public String getPrettyName() {
         return "TAG_End";
      }

      public boolean isValue() {
         return true;
      }

      // $FF: synthetic method
      public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
         return this.load(var1, var2, var3);
      }
   };
   public static final EndTag INSTANCE = new EndTag();

   private EndTag() {
      super();
   }

   public void write(DataOutput var1) throws IOException {
   }

   public byte getId() {
      return 0;
   }

   public TagType<EndTag> getType() {
      return TYPE;
   }

   public String toString() {
      return "END";
   }

   public EndTag copy() {
      return this;
   }

   public Component getPrettyDisplay(String var1, int var2) {
      return TextComponent.EMPTY;
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
