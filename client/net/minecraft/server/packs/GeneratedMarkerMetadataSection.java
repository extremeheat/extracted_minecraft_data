package net.minecraft.server.packs;

import com.mojang.serialization.Codec;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record GeneratedMarkerMetadataSection() {
   public static final Codec<GeneratedMarkerMetadataSection> CODEC = Codec.unit(GeneratedMarkerMetadataSection::new);
   public static final MetadataSectionType<GeneratedMarkerMetadataSection> TYPE;

   public GeneratedMarkerMetadataSection() {
      super();
   }

   static {
      TYPE = new MetadataSectionType<GeneratedMarkerMetadataSection>("generated", CODEC);
   }
}
