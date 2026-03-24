package net.minecraft.nbt;

public interface StreamTagVisitor {
   ValueResult visitEnd();

   ValueResult visit(final String value);

   ValueResult visit(final byte value);

   ValueResult visit(final short value);

   ValueResult visit(final int value);

   ValueResult visit(final long value);

   ValueResult visit(final float value);

   ValueResult visit(final double value);

   ValueResult visit(final byte[] value);

   ValueResult visit(final int[] value);

   ValueResult visit(final long[] value);

   ValueResult visitList(final TagType<?> elementType, final int size);

   EntryResult visitEntry(final TagType<?> type);

   EntryResult visitEntry(final TagType<?> type, final String id);

   EntryResult visitElement(final TagType<?> type, final int index);

   ValueResult visitContainerEnd();

   ValueResult visitRootEntry(final TagType<?> type);

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
}
