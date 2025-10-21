package net.minecraft.client.resources.metadata.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record TextureMetadataSection(boolean blur, boolean clamp, MipmapStrategy mipmapStrategy) {
   public static final boolean DEFAULT_BLUR = false;
   public static final boolean DEFAULT_CLAMP = false;
   public static final Codec<TextureMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("blur", false).forGetter(TextureMetadataSection::blur), Codec.BOOL.optionalFieldOf("clamp", false).forGetter(TextureMetadataSection::clamp), MipmapStrategy.CODEC.optionalFieldOf("mipmap_strategy", MipmapStrategy.AUTO).forGetter(TextureMetadataSection::mipmapStrategy)).apply(var0, TextureMetadataSection::new));
   public static final MetadataSectionType<TextureMetadataSection> TYPE;

   public TextureMetadataSection(boolean var1, boolean var2, MipmapStrategy var3) {
      super();
      this.blur = var1;
      this.clamp = var2;
      this.mipmapStrategy = var3;
   }

   static {
      TYPE = new MetadataSectionType<TextureMetadataSection>("texture", CODEC);
   }
}
