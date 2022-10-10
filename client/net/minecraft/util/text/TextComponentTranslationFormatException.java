package net.minecraft.util.text;

public class TextComponentTranslationFormatException extends IllegalArgumentException {
   public TextComponentTranslationFormatException(TextComponentTranslation var1, String var2) {
      super(String.format("Error parsing: %s: %s", var1, var2));
   }

   public TextComponentTranslationFormatException(TextComponentTranslation var1, int var2) {
      super(String.format("Invalid index %d requested for %s", var2, var1));
   }

   public TextComponentTranslationFormatException(TextComponentTranslation var1, Throwable var2) {
      super(String.format("Error while parsing: %s", var1), var2);
   }
}
