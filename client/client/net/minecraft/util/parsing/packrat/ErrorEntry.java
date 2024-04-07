package net.minecraft.util.parsing.packrat;

public record ErrorEntry<S>(int cursor, SuggestionSupplier<S> suggestions, Object reason) {
   public ErrorEntry(int cursor, SuggestionSupplier<S> suggestions, Object reason) {
      super();
      this.cursor = cursor;
      this.suggestions = suggestions;
      this.reason = reason;
   }
}
