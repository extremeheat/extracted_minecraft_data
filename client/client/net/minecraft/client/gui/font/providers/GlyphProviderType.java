package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.SpaceProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.StringRepresentable;

public enum GlyphProviderType implements StringRepresentable {
   BITMAP("bitmap", BitmapProvider.Definition.CODEC),
   TTF("ttf", TrueTypeGlyphProviderDefinition.CODEC),
   SPACE("space", SpaceProvider.Definition.CODEC),
   UNIHEX("unihex", UnihexProvider.Definition.CODEC),
   REFERENCE("reference", ProviderReferenceDefinition.CODEC);

   public static final Codec<GlyphProviderType> CODEC = StringRepresentable.fromEnum(GlyphProviderType::values);
   private final String name;
   private final MapCodec<? extends GlyphProviderDefinition> codec;

   private GlyphProviderType(final String param3, final MapCodec<? extends GlyphProviderDefinition> param4) {
      this.name = nullxx;
      this.codec = nullxxx;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public MapCodec<? extends GlyphProviderDefinition> mapCodec() {
      return this.codec;
   }
}
