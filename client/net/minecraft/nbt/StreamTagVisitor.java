package net.minecraft.nbt;

public interface StreamTagVisitor {
   ValueResult visitEnd();

   ValueResult visit(String var1);

   ValueResult visit(byte var1);

   ValueResult visit(short var1);

   ValueResult visit(int var1);

   ValueResult visit(long var1);

   ValueResult visit(float var1);

   ValueResult visit(double var1);

   ValueResult visit(byte[] var1);

   ValueResult visit(int[] var1);

   ValueResult visit(long[] var1);

   ValueResult visitList(TagType<?> var1, int var2);

   EntryResult visitEntry(TagType<?> var1);

   EntryResult visitEntry(TagType<?> var1, String var2);

   EntryResult visitElement(TagType<?> var1, int var2);

   ValueResult visitContainerEnd();

   ValueResult visitRootEntry(TagType<?> var1);

   public static enum EntryResult {
      ENTER,
      SKIP,
      BREAK,
      HALT;

      private EntryResult() {
      }

      // $FF: synthetic method
      private static EntryResult[] $values() {
         return new EntryResult[]{ENTER, SKIP, BREAK, HALT};
      }
   }

   public static enum ValueResult {
      CONTINUE,
      BREAK,
      HALT;

      private ValueResult() {
      }

      // $FF: synthetic method
      private static ValueResult[] $values() {
         return new ValueResult[]{CONTINUE, BREAK, HALT};
      }
   }
}
