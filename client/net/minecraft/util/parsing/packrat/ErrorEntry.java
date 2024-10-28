package net.minecraft.util.parsing.packrat;

public record ErrorEntry<S>(int cursor, SuggestionSupplier<S> suggestions, Object reason) {
   public ErrorEntry(int cursor, SuggestionSupplier<S> suggestions, Object reason) {
      super();
      this.cursor = cursor;
      this.suggestions = suggestions;
      this.reason = reason;
   }

   public int cursor() {
      return this.cursor;
   }

   public SuggestionSupplier<S> suggestions() {
      return this.suggestions;
   }

   public Object reason() {
      return this.reason;
   }
}
