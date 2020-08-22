package net.minecraft.client.resources.metadata.language;

import java.util.Collection;

public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer SERIALIZER = new LanguageMetadataSectionSerializer();
   private final Collection languages;

   public LanguageMetadataSection(Collection var1) {
      this.languages = var1;
   }

   public Collection getLanguages() {
      return this.languages;
   }
}
