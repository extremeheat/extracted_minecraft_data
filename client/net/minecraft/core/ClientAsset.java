package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface ClientAsset {
   ResourceLocation id();

   public static record ResourceTexture(ResourceLocation id, ResourceLocation texturePath) implements Texture {
      public static final Codec<ResourceTexture> CODEC;
      public static final MapCodec<ResourceTexture> DEFAULT_FIELD_CODEC;
      public static final StreamCodec<ByteBuf, ResourceTexture> STREAM_CODEC;

      public ResourceTexture(ResourceLocation var1) {
         this(var1, var1.withPath((UnaryOperator)((var0) -> "textures/" + var0 + ".png")));
      }

      public ResourceTexture(ResourceLocation var1, ResourceLocation var2) {
         super();
         this.id = var1;
         this.texturePath = var2;
      }

      static {
         CODEC = ResourceLocation.CODEC.xmap(ResourceTexture::new, ResourceTexture::id);
         DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
         STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(ResourceTexture::new, ResourceTexture::id);
      }
   }

   public static record DownloadedTexture(ResourceLocation texturePath, String url) implements Texture {
      public DownloadedTexture(ResourceLocation var1, String var2) {
         super();
         this.texturePath = var1;
         this.url = var2;
      }

      public ResourceLocation id() {
         return this.texturePath;
      }
   }

   public interface Texture extends ClientAsset {
      ResourceLocation texturePath();
   }
}
