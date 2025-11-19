package net.minecraft.client.resources.metadata.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record TextureMetadataSection(boolean blur, boolean clamp, MipmapStrategy mipmapStrategy, float alphaCutoffBias) {
   public static final boolean DEFAULT_BLUR = false;
   public static final boolean DEFAULT_CLAMP = false;
   public static final float DEFAULT_ALPHA_CUTOFF_BIAS = 0.0F;
   public static final Codec<TextureMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("blur", false).forGetter(TextureMetadataSection::blur), Codec.BOOL.optionalFieldOf("clamp", false).forGetter(TextureMetadataSection::clamp), MipmapStrategy.CODEC.optionalFieldOf("mipmap_strategy", MipmapStrategy.AUTO).forGetter(TextureMetadataSection::mipmapStrategy), Codec.FLOAT.optionalFieldOf("alpha_cutoff_bias", 0.0F).forGetter(TextureMetadataSection::alphaCutoffBias)).apply(var0, TextureMetadataSection::new));
   public static final MetadataSectionType<TextureMetadataSection> TYPE;

   public TextureMetadataSection(boolean var1, boolean var2, MipmapStrategy var3, float var4) {
      super();
      this.blur = var1;
      this.clamp = var2;
      this.mipmapStrategy = var3;
      this.alphaCutoffBias = var4;
   }

   static {
      TYPE = new MetadataSectionType<TextureMetadataSection>("texture", CODEC);
   }
}
