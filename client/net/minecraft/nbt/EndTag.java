package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class EndTag implements Tag {
   private static final int SELF_SIZE_IN_BYTES = 8;
   public static final TagType<EndTag> TYPE = new TagType<EndTag>() {
      public EndTag load(final DataInput input, final NbtAccounter accounter) {
         accounter.accountBytes(8L);
         return EndTag.INSTANCE;
      }

      public StreamTagVisitor.ValueResult parse(final DataInput input, final StreamTagVisitor output, final NbtAccounter accounter) {
         accounter.accountBytes(8L);
         return output.visitEnd();
      }

      public void skip(final DataInput input, final int count, final NbtAccounter accounter) {
      }

      public void skip(final DataInput input, final NbtAccounter accounter) {
      }

      public String getName() {
         return "END";
      }

      public String getPrettyName() {
         return "TAG_End";
      }
   };
   public static final EndTag INSTANCE = new EndTag();

   private EndTag() {
      super();
   }

   public void write(final DataOutput output) throws IOException {
   }

   public int sizeInBytes() {
      return 8;
   }

   public byte getId() {
      return 0;
   }

   public TagType<EndTag> getType() {
      return TYPE;
   }

   public String toString() {
      StringTagVisitor visitor = new StringTagVisitor();
      visitor.visitEnd(this);
      return visitor.build();
   }

   public EndTag copy() {
      return this;
   }

   public void accept(final TagVisitor visitor) {
      visitor.visitEnd(this);
   }

   public StreamTagVisitor.ValueResult accept(final StreamTagVisitor visitor) {
      return visitor.visitEnd();
   }
}
