package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;

public interface ErrorCollector<S> {
   void store(int var1, SuggestionSupplier<S> var2, Object var3);

   default void store(int var1, Object var2) {
      this.store(var1, SuggestionSupplier.empty(), var2);
   }

   void finish(int var1);

   public static class LongestOnly<S> implements ErrorCollector<S> {
      private final List<ErrorEntry<S>> entries = new ArrayList();
      private int lastCursor = -1;

      public LongestOnly() {
         super();
      }

      private void discardErrorsFromShorterParse(int var1) {
         if (var1 > this.lastCursor) {
            this.lastCursor = var1;
            this.entries.clear();
         }

      }

      public void finish(int var1) {
         this.discardErrorsFromShorterParse(var1);
      }

      public void store(int var1, SuggestionSupplier<S> var2, Object var3) {
         this.discardErrorsFromShorterParse(var1);
         if (var1 == this.lastCursor) {
            this.entries.add(new ErrorEntry(var1, var2, var3));
         }

      }

      public List<ErrorEntry<S>> entries() {
         return this.entries;
      }

      public int cursor() {
         return this.lastCursor;
      }
   }
}
