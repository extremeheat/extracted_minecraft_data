package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagType<T extends Tag> {
   T load(DataInput var1, int var2, NbtAccounter var3) throws IOException;

   StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException;

   default void parseRoot(DataInput var1, StreamTagVisitor var2) throws IOException {
      switch (var2.visitRootEntry(this)) {
         case CONTINUE:
            this.parse(var1, var2);
         case HALT:
         default:
            break;
         case BREAK:
            this.skip(var1);
      }

   }

   void skip(DataInput var1, int var2) throws IOException;

   void skip(DataInput var1) throws IOException;

   default boolean isValue() {
      return false;
   }

   String getName();

   String getPrettyName();

   static TagType<EndTag> createInvalid(final int var0) {
      return new TagType<EndTag>() {
         private IOException createException() {
            return new IOException("Invalid tag id: " + var0);
         }

         public EndTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
            throw this.createException();
         }

         public StreamTagVisitor.ValueResult parse(DataInput var1, StreamTagVisitor var2) throws IOException {
            throw this.createException();
         }

         public void skip(DataInput var1, int var2) throws IOException {
            throw this.createException();
         }

         public void skip(DataInput var1) throws IOException {
            throw this.createException();
         }

         public String getName() {
            return "INVALID[" + var0 + "]";
         }

         public String getPrettyName() {
            return "UNKNOWN_" + var0;
         }

         // $FF: synthetic method
         public Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
            return this.load(var1, var2, var3);
         }
      };
   }

   public interface VariableSize<T extends Tag> extends TagType<T> {
      default void skip(DataInput var1, int var2) throws IOException {
         for(int var3 = 0; var3 < var2; ++var3) {
            this.skip(var1);
         }

      }
   }

   public interface StaticSize<T extends Tag> extends TagType<T> {
      default void skip(DataInput var1) throws IOException {
         var1.skipBytes(this.size());
      }

      default void skip(DataInput var1, int var2) throws IOException {
         var1.skipBytes(this.size() * var2);
      }

      int size();
   }
}
