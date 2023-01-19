package net.minecraft.world.level.chunk;

public class MissingPaletteEntryException extends RuntimeException {
   public MissingPaletteEntryException(int var1) {
      super("Missing Palette entry for index " + var1 + ".");
   }
}
