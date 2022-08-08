package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EndTag implements Tag {
   private static final int SELF_SIZE_IN_BITS = 64;
   public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
      public EndTag load(DataInput var1, int var2, NbtAccounter var3) {
         var3.accountBits(64L);
         return EndTag.INSTANCE;
      }

      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) {
         return var2.visitEnd();
      }

      public void skip(DataInput var1, int var2) {
      }

      public void skip(DataInput var1) {
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
      return this.getAsString();
   }

   public EndTag copy() {
      return this;
   }

   public void accept(TagVisitor var1) {
      var1.visitEnd(this);
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visitEnd();
   }

   // $FF: synthetic method
   public Tag copy() {
      return this.copy();
   }
}
