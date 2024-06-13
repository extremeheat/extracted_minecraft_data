package net.minecraft.client.resources.metadata.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record GuiMetadataSection(GuiSpriteScaling scaling) {
   public static final GuiMetadataSection DEFAULT = new GuiMetadataSection(GuiSpriteScaling.DEFAULT);
   public static final Codec<GuiMetadataSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(GuiSpriteScaling.CODEC.optionalFieldOf("scaling", GuiSpriteScaling.DEFAULT).forGetter(GuiMetadataSection::scaling))
            .apply(var0, GuiMetadataSection::new)
   );
   public static final MetadataSectionType<GuiMetadataSection> TYPE = MetadataSectionType.fromCodec("gui", CODEC);

   public GuiMetadataSection(GuiSpriteScaling scaling) {
      super();
      this.scaling = scaling;
   }
}
