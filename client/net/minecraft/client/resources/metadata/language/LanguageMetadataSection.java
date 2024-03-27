package net.minecraft.client.resources.metadata.language;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record LanguageMetadataSection(Map<String, LanguageInfo> d) {
   private final Map<String, LanguageInfo> languages;
   public static final Codec<String> LANGUAGE_CODE_CODEC = Codec.string(1, 16);
   public static final Codec<LanguageMetadataSection> CODEC = Codec.unboundedMap(LANGUAGE_CODE_CODEC, LanguageInfo.CODEC)
      .xmap(LanguageMetadataSection::new, LanguageMetadataSection::languages);
   public static final MetadataSectionType<LanguageMetadataSection> TYPE = MetadataSectionType.fromCodec("language", CODEC);

   public LanguageMetadataSection(Map<String, LanguageInfo> var1) {
      super();
      this.languages = var1;
   }
}
