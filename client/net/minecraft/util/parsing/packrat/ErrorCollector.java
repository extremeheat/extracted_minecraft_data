package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface ErrorCollector {
   ErrorCollector NOP = new ErrorCollector() {
      public void store(final int cursor, final Suggestion suggestion, final Object reason) {
      }

      public void finish(final int finalCursor) {
      }
   };

   void store(int cursor, Suggestion suggestion, Object reason);

   default void store(final int cursor, final Object reason) {
      this.store(cursor, Suggestion.EMPTY, reason);
   }

   void finish(int finalCursor);

   public static class LongestOnly implements ErrorCollector {
      private @Nullable ErrorCollector.MutableErrorEntry[] entries = new MutableErrorEntry[16];
      private int nextErrorEntry;
      private int lastCursor = -1;

      public LongestOnly() {
         super();
      }

      private void discardErrorsFromShorterParse(final int cursor) {
         if (cursor > this.lastCursor) {
            this.lastCursor = cursor;
            this.nextErrorEntry = 0;
         }

      }

      public void finish(final int finalCursor) {
         this.discardErrorsFromShorterParse(finalCursor);
      }

      public void store(final int cursor, final Suggestion suggestions, final Object reason) {
         this.discardErrorsFromShorterParse(cursor);
         if (cursor == this.lastCursor) {
            this.addErrorEntry(suggestions, reason);
         }

      }

      private void addErrorEntry(final Suggestion suggestions, final Object reason) {
         int currentSize = this.entries.length;
         if (this.nextErrorEntry >= currentSize) {
            int newSize = Util.growByHalf(currentSize, this.nextErrorEntry + 1);
            MutableErrorEntry[] newEntries = new MutableErrorEntry[newSize];
            System.arraycopy(this.entries, 0, newEntries, 0, currentSize);
            this.entries = newEntries;
         }

         int entryIndex = this.nextErrorEntry++;
         MutableErrorEntry entry = this.entries[entryIndex];
         if (entry == null) {
            entry = new MutableErrorEntry();
            this.entries[entryIndex] = entry;
         }

         entry.suggestions = suggestions;
         entry.reason = reason;
      }

      public List<ErrorEntry> entries() {
         int errorCount = this.nextErrorEntry;
         if (errorCount == 0) {
            return List.of();
         } else {
            List<ErrorEntry> result = new ArrayList(errorCount);

            for(int i = 0; i < errorCount; ++i) {
               MutableErrorEntry entry = this.entries[i];
               result.add(new ErrorEntry(this.lastCursor, entry.suggestions, entry.reason));
            }

            return result;
         }
      }

      public int cursor() {
         return this.lastCursor;
      }

      private static class MutableErrorEntry {
         private Suggestion suggestions;
         private Object reason;

         private MutableErrorEntry() {
            super();
            this.suggestions = Suggestion.EMPTY;
            this.reason = "empty";
         }
      }
   }
}
