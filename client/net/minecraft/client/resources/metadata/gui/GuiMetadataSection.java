package net.minecraft.client.resources.metadata.gui;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ExtraCodecs;

public record GuiMetadataSection(GuiSpriteScaling d) {
   private final GuiSpriteScaling scaling;
   public static final GuiMetadataSection DEFAULT = new GuiMetadataSection(GuiSpriteScaling.DEFAULT);
   public static final Codec<GuiMetadataSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ExtraCodecs.strictOptionalField(GuiSpriteScaling.CODEC, "scaling", GuiSpriteScaling.DEFAULT).forGetter(GuiMetadataSection::scaling))
            .apply(var0, GuiMetadataSection::new)
   );
   public static final MetadataSectionType<GuiMetadataSection> TYPE = MetadataSectionType.fromCodec("gui", CODEC);

   public GuiMetadataSection(GuiSpriteScaling var1) {
      super();
      this.scaling = var1;
   }
}
