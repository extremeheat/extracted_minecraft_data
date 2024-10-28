package net.minecraft.util.parsing.packrat;

public record ErrorEntry<S>(int cursor, SuggestionSupplier<S> suggestions, Object reason) {
   public ErrorEntry(int var1, SuggestionSupplier<S> var2, Object var3) {
      super();
      this.cursor = var1;
      this.suggestions = var2;
      this.reason = var3;
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
