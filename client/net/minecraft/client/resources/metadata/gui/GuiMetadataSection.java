package net.minecraft.client.resources.metadata.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record GuiMetadataSection(GuiSpriteScaling scaling) {
   public static final GuiMetadataSection DEFAULT;
   public static final Codec<GuiMetadataSection> CODEC;
   public static final MetadataSectionType<GuiMetadataSection> TYPE;

   public GuiMetadataSection {
      super();
   }

   static {
      DEFAULT = new GuiMetadataSection(GuiSpriteScaling.DEFAULT);
      CODEC = RecordCodecBuilder.create((i) -> i.group(GuiSpriteScaling.CODEC.optionalFieldOf("scaling", GuiSpriteScaling.DEFAULT).forGetter(GuiMetadataSection::scaling)).apply(i, GuiMetadataSection::new));
      TYPE = new MetadataSectionType<GuiMetadataSection>("gui", CODEC);
   }
}
