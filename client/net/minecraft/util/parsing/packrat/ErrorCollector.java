package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface ErrorCollector<S> {
   void store(int cursor, SuggestionSupplier<S> suggestions, Object reason);

   default void store(final int cursor, final Object reason) {
      this.store(cursor, SuggestionSupplier.empty(), reason);
   }

   void finish(int finalCursor);

   public static class Nop<S> implements ErrorCollector<S> {
      public Nop() {
         super();
      }

      public void store(final int cursor, final SuggestionSupplier<S> suggestions, final Object reason) {
      }

      public void finish(final int finalCursor) {
      }
   }

   public static class LongestOnly<S> implements ErrorCollector<S> {
      private @Nullable ErrorCollector.MutableErrorEntry<S>[] entries = new MutableErrorEntry[16];
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

      public void store(final int cursor, final SuggestionSupplier<S> suggestions, final Object reason) {
         this.discardErrorsFromShorterParse(cursor);
         if (cursor == this.lastCursor) {
            this.addErrorEntry(suggestions, reason);
         }

      }

      private void addErrorEntry(final SuggestionSupplier<S> suggestions, final Object reason) {
         int currentSize = this.entries.length;
         if (this.nextErrorEntry >= currentSize) {
            int newSize = Util.growByHalf(currentSize, this.nextErrorEntry + 1);
            MutableErrorEntry<S>[] newEntries = new MutableErrorEntry[newSize];
            System.arraycopy(this.entries, 0, newEntries, 0, currentSize);
            this.entries = newEntries;
         }

         int entryIndex = this.nextErrorEntry++;
         MutableErrorEntry<S> entry = this.entries[entryIndex];
         if (entry == null) {
            entry = new MutableErrorEntry<S>();
            this.entries[entryIndex] = entry;
         }

         entry.suggestions = suggestions;
         entry.reason = reason;
      }

      public List<ErrorEntry<S>> entries() {
         int errorCount = this.nextErrorEntry;
         if (errorCount == 0) {
            return List.of();
         } else {
            List<ErrorEntry<S>> result = new ArrayList(errorCount);

            for(int i = 0; i < errorCount; ++i) {
               MutableErrorEntry<S> entry = this.entries[i];
               result.add(new ErrorEntry(this.lastCursor, entry.suggestions, entry.reason));
            }

            return result;
         }
      }

      public int cursor() {
         return this.lastCursor;
      }

      private static class MutableErrorEntry<S> {
         private SuggestionSupplier<S> suggestions = SuggestionSupplier.<S>empty();
         private Object reason = "empty";

         private MutableErrorEntry() {
            super();
         }
      }
   }
}
