package net.minecraft.client.resources.metadata.language;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record LanguageMetadataSection(Map<String, LanguageInfo> languages) {
   public static final Codec<String> LANGUAGE_CODE_CODEC = Codec.string(1, 16);
   public static final Codec<LanguageMetadataSection> CODEC;
   public static final MetadataSectionType<LanguageMetadataSection> TYPE;

   public LanguageMetadataSection(Map<String, LanguageInfo> languages) {
      super();
      this.languages = languages;
   }

   public Map<String, LanguageInfo> languages() {
      return this.languages;
   }

   static {
      CODEC = Codec.unboundedMap(LANGUAGE_CODE_CODEC, LanguageInfo.CODEC).xmap(LanguageMetadataSection::new, LanguageMetadataSection::languages);
      TYPE = MetadataSectionType.fromCodec("language", CODEC);
   }
}
