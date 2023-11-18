package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EndTag implements Tag {
   private static final int SELF_SIZE_IN_BYTES = 8;
   public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
      public EndTag load(DataInput var1, NbtAccounter var2) {
         var2.accountBytes(8L);
         return EndTag.INSTANCE;
      }

      @Override
      public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2, NbtAccounter var3) {
         var3.accountBytes(8L);
         return var2.visitEnd();
      }

      @Override
      public void skip(DataInput var1, int var2, NbtAccounter var3) {
      }

      @Override
      public void skip(DataInput var1, NbtAccounter var2) {
      }

      @Override
      public String getName() {
         return "END";
      }

      @Override
      public String getPrettyName() {
         return "TAG_End";
      }

      @Override
      public boolean isValue() {
         return true;
      }
   };
   public static final EndTag INSTANCE = new EndTag();

   private EndTag() {
      super();
   }

   @Override
   public void write(DataOutput var1) throws IOException {
   }

   @Override
   public int sizeInBytes() {
      return 8;
   }

   @Override
   public byte getId() {
      return 0;
   }

   @Override
   public TagType<EndTag> getType() {
      return TYPE;
   }

   @Override
   public String toString() {
      return this.getAsString();
   }

   public EndTag copy() {
      return this;
   }

   @Override
   public void accept(TagVisitor var1) {
      var1.visitEnd(this);
   }

   @Override
   public StreamTagVisitor.ValueResult accept(StreamTagVisitor var1) {
      return var1.visitEnd();
   }
}
