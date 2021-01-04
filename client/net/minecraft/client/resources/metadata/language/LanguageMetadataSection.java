package net.minecraft.client.resources.metadata.language;

import java.util.Collection;
import net.minecraft.client.resources.language.Language;

public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer SERIALIZER = new LanguageMetadataSectionSerializer();
   private final Collection<Language> languages;

   public LanguageMetadataSection(Collection<Language> var1) {
      super();
      this.languages = var1;
   }

   public Collection<Language> getLanguages() {
      return this.languages;
   }
}
