package net.minecraft.util.parsing.packrat;

public record ErrorEntry<S>(int a, SuggestionSupplier<S> b, Object c) {
   private final int cursor;
   private final SuggestionSupplier<S> suggestions;
   private final Object reason;

   public ErrorEntry(int var1, SuggestionSupplier<S> var2, Object var3) {
      super();
      this.cursor = var1;
      this.suggestions = var2;
      this.reason = var3;
   }
}
