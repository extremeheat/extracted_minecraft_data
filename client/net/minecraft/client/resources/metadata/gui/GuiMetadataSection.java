package net.minecraft.client.resources.metadata.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record GuiMetadataSection(GuiSpriteScaling scaling) {
   public static final GuiMetadataSection DEFAULT;
   public static final Codec<GuiMetadataSection> CODEC;
   public static final MetadataSectionType<GuiMetadataSection> TYPE;

   public GuiMetadataSection(GuiSpriteScaling var1) {
      super();
      this.scaling = var1;
   }

   static {
      DEFAULT = new GuiMetadataSection(GuiSpriteScaling.DEFAULT);
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(GuiSpriteScaling.CODEC.optionalFieldOf("scaling", GuiSpriteScaling.DEFAULT).forGetter(GuiMetadataSection::scaling)).apply(var0, GuiMetadataSection::new));
      TYPE = new MetadataSectionType<GuiMetadataSection>("gui", CODEC);
   }
}
