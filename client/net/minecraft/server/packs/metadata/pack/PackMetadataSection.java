package net.minecraft.server.packs.metadata.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.InclusiveRange;

public record PackMetadataSection(Component c, int d, Optional<InclusiveRange<Integer>> e) {
   private final Component description;
   private final int packFormat;
   private final Optional<InclusiveRange<Integer>> supportedFormats;
   public static final Codec<PackMetadataSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ComponentSerialization.CODEC.fieldOf("description").forGetter(PackMetadataSection::description),
               Codec.INT.fieldOf("pack_format").forGetter(PackMetadataSection::packFormat),
               InclusiveRange.codec(Codec.INT).optionalFieldOf("supported_formats").forGetter(PackMetadataSection::supportedFormats)
            )
            .apply(var0, PackMetadataSection::new)
   );
   public static final MetadataSectionType<PackMetadataSection> TYPE = MetadataSectionType.fromCodec("pack", CODEC);

   public PackMetadataSection(Component var1, int var2, Optional<InclusiveRange<Integer>> var3) {
      super();
      this.description = var1;
      this.packFormat = var2;
      this.supportedFormats = var3;
   }
}
