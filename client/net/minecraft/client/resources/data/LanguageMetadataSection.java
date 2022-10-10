package net.minecraft.client.resources.data;

import java.util.Collection;
import net.minecraft.client.resources.Language;

public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer field_195818_a = new LanguageMetadataSectionSerializer();
   private final Collection<Language> field_135019_a;

   public LanguageMetadataSection(Collection<Language> var1) {
      super();
      this.field_135019_a = var1;
   }

   public Collection<Language> func_135018_a() {
      return this.field_135019_a;
   }
}
