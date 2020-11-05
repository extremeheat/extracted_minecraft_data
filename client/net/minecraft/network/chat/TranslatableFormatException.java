package net.minecraft.network.chat;

public class TranslatableFormatException extends IllegalArgumentException {
   public TranslatableFormatException(TranslatableComponent var1, String var2) {
      super(String.format("Error parsing: %s: %s", var1, var2));
   }

   public TranslatableFormatException(TranslatableComponent var1, int var2) {
      super(String.format("Invalid index %d requested for %s", var2, var1));
   }

   public TranslatableFormatException(TranslatableComponent var1, Throwable var2) {
      super(String.format("Error while parsing: %s", var1), var2);
   }
}
