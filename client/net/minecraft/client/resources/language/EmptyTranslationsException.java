package net.minecraft.client.resources.language;

class EmptyTranslationsException extends RuntimeException {
   private final String languageCode;

   EmptyTranslationsException(final String languageCode) {
      super();
      this.languageCode = languageCode;
   }

   public String getLanguageCode() {
      return this.languageCode;
   }
}
