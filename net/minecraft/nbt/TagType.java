package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagType {
   Tag load(DataInput var1, int var2, NbtAccounter var3) throws IOException;

   default boolean isValue() {
      return false;
   }

   String getName();

   String getPrettyName();

   static TagType createInvalid(final int var0) {
      return new TagType() {
         public EndTag load(DataInput var1, int var2, NbtAccounter var3) throws IOException {
            throw new IllegalArgumentException("Invalid tag id: " + var0);
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
}
