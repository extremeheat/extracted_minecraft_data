package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;

public interface ErrorCollector<S> {
   void store(int var1, SuggestionSupplier<S> var2, Object var3);

   default void store(int var1, Object var2) {
      this.store(var1, SuggestionSupplier.empty(), var2);
   }

   void finish(int var1);

   public static class Nop<S> implements ErrorCollector<S> {
      public Nop() {
         super();
      }

      public void store(int var1, SuggestionSupplier<S> var2, Object var3) {
      }

      public void finish(int var1) {
      }
   }

   public static class LongestOnly<S> implements ErrorCollector<S> {
      private MutableErrorEntry<S>[] entries = new MutableErrorEntry[16];
      private int nextErrorEntry;
      private int lastCursor = -1;

      public LongestOnly() {
         super();
      }

      private void discardErrorsFromShorterParse(int var1) {
         if (var1 > this.lastCursor) {
            this.lastCursor = var1;
            this.nextErrorEntry = 0;
         }

      }

      public void finish(int var1) {
         this.discardErrorsFromShorterParse(var1);
      }

      public void store(int var1, SuggestionSupplier<S> var2, Object var3) {
         this.discardErrorsFromShorterParse(var1);
         if (var1 == this.lastCursor) {
            this.addErrorEntry(var2, var3);
         }

      }

      private void addErrorEntry(SuggestionSupplier<S> var1, Object var2) {
         int var3 = this.entries.length;
         if (this.nextErrorEntry >= var3) {
            int var4 = Util.growByHalf(var3, this.nextErrorEntry + 1);
            MutableErrorEntry[] var5 = new MutableErrorEntry[var4];
            System.arraycopy(this.entries, 0, var5, 0, var3);
            this.entries = var5;
         }

         int var6 = this.nextErrorEntry++;
         MutableErrorEntry var7 = this.entries[var6];
         if (var7 == null) {
            var7 = new MutableErrorEntry();
            this.entries[var6] = var7;
         }

         var7.suggestions = var1;
         var7.reason = var2;
      }

      public List<ErrorEntry<S>> entries() {
         int var1 = this.nextErrorEntry;
         if (var1 == 0) {
            return List.of();
         } else {
            ArrayList var2 = new ArrayList(var1);

            for(int var3 = 0; var3 < var1; ++var3) {
               MutableErrorEntry var4 = this.entries[var3];
               var2.add(new ErrorEntry(this.lastCursor, var4.suggestions, var4.reason));
            }

            return var2;
         }
      }

      public int cursor() {
         return this.lastCursor;
      }

      static class MutableErrorEntry<S> {
         SuggestionSupplier<S> suggestions = SuggestionSupplier.<S>empty();
         Object reason = "empty";

         MutableErrorEntry() {
            super();
         }
      }
   }
}
