package net.minecraft.util.parsing.packrat;

public record ErrorEntry(int cursor, Suggestion suggestion, Object reason) {
   public ErrorEntry {
      super();
   }
}
