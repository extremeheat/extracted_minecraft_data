package net.minecraft.server.packs.metadata.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.InclusiveRange;

public record PackMetadataSection(Component description, InclusiveRange<PackFormat> supportedFormats) {
   private static final Codec<PackMetadataSection> FALLBACK_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("description").forGetter(PackMetadataSection::description)).apply(var0, (var0x) -> new PackMetadataSection(var0x, new InclusiveRange(PackFormat.of(2147483647)))));
   public static final MetadataSectionType<PackMetadataSection> CLIENT_TYPE;
   public static final MetadataSectionType<PackMetadataSection> SERVER_TYPE;
   public static final MetadataSectionType<PackMetadataSection> FALLBACK_TYPE;

   public PackMetadataSection(Component var1, InclusiveRange<PackFormat> var2) {
      super();
      this.description = var1;
      this.supportedFormats = var2;
   }

   private static Codec<PackMetadataSection> codecForPackType(PackType var0) {
      return RecordCodecBuilder.create((var1) -> var1.group(ComponentSerialization.CODEC.fieldOf("description").forGetter(PackMetadataSection::description), PackFormat.packCodec(var0).forGetter(PackMetadataSection::supportedFormats)).apply(var1, PackMetadataSection::new));
   }

   public static MetadataSectionType<PackMetadataSection> forPackType(PackType var0) {
      MetadataSectionType var10000;
      switch (var0) {
         case CLIENT_RESOURCES -> var10000 = CLIENT_TYPE;
         case SERVER_DATA -> var10000 = SERVER_TYPE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static {
      CLIENT_TYPE = new MetadataSectionType<PackMetadataSection>("pack", codecForPackType(PackType.CLIENT_RESOURCES));
      SERVER_TYPE = new MetadataSectionType<PackMetadataSection>("pack", codecForPackType(PackType.SERVER_DATA));
      FALLBACK_TYPE = new MetadataSectionType<PackMetadataSection>("pack", FALLBACK_CODEC);
   }
}
