package net.minecraft.client.resources.metadata.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record TextureMetadataSection(boolean blur, boolean clamp, boolean darkenedCutoutMipmap) {
   public static final boolean DEFAULT_BLUR = false;
   public static final boolean DEFAULT_CLAMP = false;
   public static final boolean DEFAULT_DARKENED_CUTOUT_MIPMAP = false;
   public static final Codec<TextureMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("blur", false).forGetter(TextureMetadataSection::blur), Codec.BOOL.optionalFieldOf("clamp", false).forGetter(TextureMetadataSection::clamp), Codec.BOOL.optionalFieldOf("darkened_cutout_mipmap", false).forGetter(TextureMetadataSection::darkenedCutoutMipmap)).apply(var0, TextureMetadataSection::new));
   public static final MetadataSectionType<TextureMetadataSection> TYPE;

   public TextureMetadataSection(boolean var1, boolean var2, boolean var3) {
      super();
      this.blur = var1;
      this.clamp = var2;
      this.darkenedCutoutMipmap = var3;
   }

   static {
      TYPE = new MetadataSectionType<TextureMetadataSection>("texture", CODEC);
   }
}
