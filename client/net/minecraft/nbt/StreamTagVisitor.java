package net.minecraft.nbt;

public interface StreamTagVisitor {
   StreamTagVisitor.ValueResult visitEnd();

   StreamTagVisitor.ValueResult visit(String var1);

   StreamTagVisitor.ValueResult visit(byte var1);

   StreamTagVisitor.ValueResult visit(short var1);

   StreamTagVisitor.ValueResult visit(int var1);

   StreamTagVisitor.ValueResult visit(long var1);

   StreamTagVisitor.ValueResult visit(float var1);

   StreamTagVisitor.ValueResult visit(double var1);

   StreamTagVisitor.ValueResult visit(byte[] var1);

   StreamTagVisitor.ValueResult visit(int[] var1);

   StreamTagVisitor.ValueResult visit(long[] var1);

   StreamTagVisitor.ValueResult visitList(TagType<?> var1, int var2);

   StreamTagVisitor.EntryResult visitEntry(TagType<?> var1);

   StreamTagVisitor.EntryResult visitEntry(TagType<?> var1, String var2);

   StreamTagVisitor.EntryResult visitElement(TagType<?> var1, int var2);

   StreamTagVisitor.ValueResult visitContainerEnd();

   StreamTagVisitor.ValueResult visitRootEntry(TagType<?> var1);

   public static enum EntryResult {
      ENTER,
      SKIP,
      BREAK,
      HALT;

      private EntryResult() {
      }

      // $FF: synthetic method
      private static StreamTagVisitor.EntryResult[] $values() {
         return new StreamTagVisitor.EntryResult[]{ENTER, SKIP, BREAK, HALT};
      }
   }

   public static enum ValueResult {
      CONTINUE,
      BREAK,
      HALT;

      private ValueResult() {
      }

      // $FF: synthetic method
      private static StreamTagVisitor.ValueResult[] $values() {
         return new StreamTagVisitor.ValueResult[]{CONTINUE, BREAK, HALT};
      }
   }
}
