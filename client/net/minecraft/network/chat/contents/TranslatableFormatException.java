package net.minecraft.network.chat.contents;

import java.util.Locale;

public class TranslatableFormatException extends IllegalArgumentException {
   public TranslatableFormatException(TranslatableContents var1, String var2) {
      super(String.format(Locale.ROOT, "Error parsing: %s: %s", var1, var2));
   }

   public TranslatableFormatException(TranslatableContents var1, int var2) {
      super(String.format(Locale.ROOT, "Invalid index %d requested for %s", var2, var1));
   }

   public TranslatableFormatException(TranslatableContents var1, Throwable var2) {
      super(String.format(Locale.ROOT, "Error while parsing: %s", var1), var2);
   }
}
