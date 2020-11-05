package net.minecraft.client.resources.metadata.language;

import java.util.Collection;
import net.minecraft.client.resources.language.LanguageInfo;

public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer SERIALIZER = new LanguageMetadataSectionSerializer();
   private final Collection<LanguageInfo> languages;

   public LanguageMetadataSection(Collection<LanguageInfo> var1) {
      super();
      this.languages = var1;
   }

   public Collection<LanguageInfo> getLanguages() {
      return this.languages;
   }
}
